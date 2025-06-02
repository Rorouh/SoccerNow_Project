package pt.ul.fc.css.soccernow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.soccernow.domain.*;
import pt.ul.fc.css.soccernow.repository.JogoRepository;
import pt.ul.fc.css.soccernow.repository.ResultadoRepository;
import pt.ul.fc.css.soccernow.repository.TeamRepository;
import pt.ul.fc.css.soccernow.repository.RefereeRepository;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;
import pt.ul.fc.css.soccernow.service.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JogoService {
    @Autowired
    private JogoRepository jogoRepository;
    @Autowired
    private ResultadoRepository resultadoRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private RefereeRepository refereeRepository;

    @Transactional
    public Jogo criarJogo(Jogo jogo) {
        // Reutiliza validações completas ao criar jogo
        return criarJogo(
            jogo.getDataHora(),
            jogo.getLocal(),
            jogo.isAmigavel(),
            jogo.getHomeTeam().getId(),
            jogo.getAwayTeam().getId(),
            jogo.getReferees().stream().map(Referee::getId).collect(Collectors.toSet()),
            jogo.getPrimaryReferee() != null ? jogo.getPrimaryReferee().getId() : null
        );
    }

    @Transactional
    public Resultado registarResultado(Long jogoId, Resultado resultado) {
        // Reutiliza validações completas ao registar resultado
        return registarResultado(
            jogoId,
            resultado.getPlacar(),
            resultado.getEquipaVitoriosa() != null ? resultado.getEquipaVitoriosa().getId() : null
        );
    }

    public Optional<Jogo> obterJogo(Long id) {
        return jogoRepository.findById(id);
    }

    // Cria jogo amigável ou de campeonato com validações de negócio
    @Transactional
    public Jogo criarJogo(LocalDateTime dataHora,
                          String local,
                          boolean amigavel,
                          Long equipa1Id,
                          Long equipa2Id,
                          Set<Long> arbitroIds,
                          Long primaryRefereeId)
            throws NotFoundException, ApplicationException {

        // 1) Uma equipa não pode jogar contra si própria
        if (equipa1Id.equals(equipa2Id)) {
            throw new ApplicationException("As equipas têm de ser diferentes.");
        }

        Team home = teamRepository.findById(equipa1Id)
                .orElseThrow(() -> new NotFoundException("Team com ID " + equipa1Id + " não encontrada."));
        Team away = teamRepository.findById(equipa2Id)
                .orElseThrow(() -> new NotFoundException("Team com ID " + equipa2Id + " não encontrada."));

        var referees = refereeRepository.findAllById(arbitroIds);
        if (referees.size() != arbitroIds.size()) {
            throw new NotFoundException("Árbitro(s) não encontrado(s)");
        }

        // 2) Em campeonato, todos árbitros devem ser certificados
        if (!amigavel) {
            boolean allCertified = referees.stream().allMatch(Referee::isCertified);
            if (!allCertified) {
                throw new ApplicationException("Todos os árbitros têm de ser certificados para jogos de campeonato.");
            }
        }

        Jogo jogo = new Jogo(dataHora, local, amigavel);
        jogo.setHomeTeam(home);
        jogo.setAwayTeam(away);
        jogo.setReferees(Set.copyOf(referees));

        if (primaryRefereeId != null) {
            Referee primary = referees.stream()
                    .filter(r -> r.getId().equals(primaryRefereeId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Primary referee indicado não faz parte da lista."));
            jogo.setPrimaryReferee(primary);
        }

        return jogoRepository.save(jogo);
    }

    // Registra resultado com validações de negócio
    @Transactional
    public Resultado registarResultado(Long jogoId, String placar, Long equipaVitoriosaId)
            throws NotFoundException, ApplicationException {

        // 1) cargar Jogo
        Jogo jogo = jogoRepository.findById(jogoId)
                .orElseThrow(() -> new NotFoundException("Jogo com ID " + jogoId + " não encontrado."));

        if (jogo.getResultado() != null) {
            throw new ApplicationException("Jogo já tem um resultado registado.");
        }

        // 2) parsear marcador
        String[] parts = placar.split("-");
        if (parts.length != 2) {
            throw new ApplicationException("Formato de placar inválido, use \"golosCasa-golosFora\".");
        }
        int gCasa = Integer.parseInt(parts[0].trim());
        int gFora = Integer.parseInt(parts[1].trim());

        // 3) crear objeto Resultado
        Resultado resultado = new Resultado();
        resultado.setPlacar(placar);
        resultado.setJogo(jogo);

        if (equipaVitoriosaId != null) {
            // 4) cargar Team vencedora — primero NotFoundException
            Team vencedora = teamRepository.findById(equipaVitoriosaId)
                .orElseThrow(() -> new NotFoundException(
                    "Team vitoriosa com ID " + equipaVitoriosaId + " não encontrada."));

            // 5) comprobar participación
            Long homeId = jogo.getHomeTeam().getId();
            Long awayId = jogo.getAwayTeam().getId();
            if (!equipaVitoriosaId.equals(homeId) && !equipaVitoriosaId.equals(awayId)) {
                throw new ApplicationException("Team vitoriosa indicada não participou neste jogo.");
            }

            // 6) validar que marcó mais goles que el adversario
            boolean ganhou;
            if (equipaVitoriosaId.equals(homeId)) {
                ganhou = gCasa > gFora;
            } else {
                ganhou = gFora > gCasa;
            }
            if (!ganhou) {
                throw new ApplicationException("Autogolo não permitido: equipa vencedora não bate o adversário.");
            }

            resultado.setEquipaVitoriosa(vencedora);
        }

        // 7) persistir todo
        jogo.setResultado(resultado);
        jogoRepository.save(jogo);
        return resultadoRepository.save(resultado);
    }

    @Transactional
    public void cancelarJogo(Long jogoId) throws NotFoundException, ApplicationException {
        Jogo jogo = jogoRepository.findById(jogoId)
                .orElseThrow(() -> new NotFoundException("Jogo com ID " + jogoId + " não encontrado."));

        if (jogo.getResultado() != null) {
            throw new ApplicationException("Não se pode cancelar um jogo que já tem resultado registrado.");
        }
        if (jogo.isCancelado()) {
            throw new ApplicationException("Jogo já está cancelado.");
        }

        jogo.setCancelado(true);
        jogoRepository.save(jogo);
    }
    


    public List<Jogo> findPlayedGames() {
        return jogoRepository.findByResultadoIsNotNull();
    }

    public List<Jogo> findCancelledGames() {
        return jogoRepository.findByCanceladoTrue();
    }

    public List<Jogo> findPendingGames() {
        return jogoRepository.findPendingGames();
    }

    public List<Jogo> findByLocation(String location) {
        return jogoRepository.findByLocationContainingIgnoreCase(location);
    }

    public List<Jogo> findByTimeSlot(String slot) {
        switch (slot.toLowerCase()) {
            case "mañana", "manhã", "morning":
                return jogoRepository.findByHourBetween(6, 11);
            case "tarde", "afternoon":
                return jogoRepository.findByHourBetween(12, 17);
            case "noche", "noite", "night":
                return jogoRepository.findByHourBetween(18, 23);
            default:
                throw new ApplicationException("TimeSlot inválido. Valores válidos: mañAna/tarde/noche.");
        }
    }

    public List<Jogo> findByMinGoals(int minGoals) {
        return jogoRepository.findGamesWithMinGoals(minGoals);
    }

    @Transactional(readOnly = true)
    public List<Jogo> findAllJogos() {
        return jogoRepository.findAll();
    }
}