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
import java.util.Optional;
import java.util.Set;

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
        // Validações de negócio podem ser adicionadas aqui
        return jogoRepository.save(jogo);
    }

    @Transactional
    public Resultado registarResultado(Long jogoId, Resultado resultado) {
        Optional<Jogo> jogoOpt = jogoRepository.findById(jogoId);
        if (jogoOpt.isEmpty()) throw new IllegalArgumentException("Jogo não encontrado");
        Jogo jogo = jogoOpt.get();
        resultado.setJogo(jogo);
        jogo.setResultado(resultado);
        jogoRepository.save(jogo); // Garante persistência bidirecional
        return resultadoRepository.save(resultado);
    }

    public Optional<Jogo> obterJogo(Long id) {
        return jogoRepository.findById(id);
    }

    // Novo caso de uso completo – criar jogo amigável ou de campeonato
    @Transactional
    public Jogo criarJogo(LocalDateTime dataHora,
                          String local,
                          boolean amigavel,
                          Long equipa1Id,
                          Long equipa2Id,
                          Set<Long> arbitroIds,
                          Long primaryRefereeId) throws NotFoundException, ApplicationException {

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

    // Novo registar resultado
    @Transactional
    public Resultado registarResultado(Long jogoId, String placar, Long equipaVitoriosaId)
            throws NotFoundException, ApplicationException {

        Jogo jogo = jogoRepository.findById(jogoId)
                .orElseThrow(() -> new NotFoundException("Jogo com ID " + jogoId + " não encontrado."));

        if (jogo.getResultado() != null) {
            throw new ApplicationException("Jogo já tem um resultado registado.");
        }

        Resultado resultado = new Resultado();
        resultado.setPlacar(placar);
        resultado.setJogo(jogo);

        if (equipaVitoriosaId != null) {
            Team vencedora = teamRepository.findById(equipaVitoriosaId)
                    .orElseThrow(() -> new NotFoundException("Team vitoriosa com ID " + equipaVitoriosaId + " não encontrada."));
            if (!Set.of(jogo.getHomeTeam().getId(), jogo.getAwayTeam().getId()).contains(equipaVitoriosaId)) {
                throw new ApplicationException("Team vitoriosa indicada não participou neste jogo.");
            }
            resultado.setEquipaVitoriosa(vencedora);
        }

        jogo.setResultado(resultado);
        jogoRepository.save(jogo);
        return resultadoRepository.save(resultado);
    }
}
