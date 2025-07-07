package pt.ul.fc.css.soccernow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.soccernow.domain.*;
import pt.ul.fc.css.soccernow.dto.JogoCreateDTO;
import pt.ul.fc.css.soccernow.dto.JogoUpdateDTO;
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
import java.util.HashSet;
import java.util.Objects;
import java.util.Iterator;


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

    /**
     * Crea un juego a partir de un DTO.
     */
    @Transactional
    public Jogo criarJogo(LocalDateTime dataHora,
                          String local,
                          boolean amigavel,
                          Long homeTeamId,
                          Long awayTeamId,
                          Set<Long> refereeIds,
                          Long primaryRefereeId)
            throws NotFoundException, ApplicationException {

        if (homeTeamId.equals(awayTeamId)) {
            throw new ApplicationException("Las dos equipas deben ser diferentes.");
        }
        Team home = teamRepository.findById(homeTeamId)
                .orElseThrow(() -> new NotFoundException("Equipo local no encontrado: " + homeTeamId));
        Team away = teamRepository.findById(awayTeamId)
                .orElseThrow(() -> new NotFoundException("Equipo visitante no encontrado: " + awayTeamId));

        Set<Referee> referees = new HashSet<>(refereeRepository.findAllById(refereeIds));
        if (referees.size() != refereeIds.size()) {
            throw new NotFoundException("Algún árbitro no existe.");
        }
        if (!amigavel && referees.stream().anyMatch(r -> !r.isCertified())) {
            throw new ApplicationException("Todos los árbitros de campeonato deben estar certificados.");
        }

        Jogo jogo = new Jogo(dataHora, local, amigavel);
        jogo.setHomeTeam(home);
        jogo.setAwayTeam(away);
        jogo.setReferees(referees);

        if (primaryRefereeId != null) {
            Referee primary = referees.stream()
                    .filter(r -> r.getId().equals(primaryRefereeId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Árbitro principal no pertenece al listado."));
            jogo.setPrimaryReferee(primary);
        }

        return jogoRepository.save(jogo);
    }

    @Transactional
    public Jogo criarJogo(JogoCreateDTO dto) throws NotFoundException, ApplicationException {
        var it = dto.getTeamIds().iterator();
        Long homeId = it.next(), awayId = it.next();
        return criarJogo(
            dto.getDateTime(),
            dto.getLocation(),
            dto.getAmigavel(),
            homeId,
            awayId,
            dto.getRefereeIds(),
            dto.getPrimaryRefereeId()
        );
    }

    @Transactional
    public Resultado registrarResultado(Long jogoId,
                                        int golosCasa,
                                        int golosFora,
                                        Long vencedoraId)
            throws NotFoundException, ApplicationException {

        Jogo j = jogoRepository.findById(jogoId)
                 .orElseThrow(() -> new NotFoundException("Juego no encontrado: " + jogoId));

        if (j.getResultado() != null)
            throw new ApplicationException("El resultado ya estaba registrado.");

        // -------- validaciones sencillas ----------
        if (vencedoraId != null &&
            !vencedoraId.equals(j.getHomeTeam().getId()) &&
            !vencedoraId.equals(j.getAwayTeam().getId())) {
            throw new ApplicationException("El vencedor no participó en este juego.");
        }
        if (vencedoraId != null) {                 // coherencia marcador-vencedor
            boolean homeGanó = golosCasa > golosFora;
            if (homeGanó && !vencedoraId.equals(j.getHomeTeam().getId()) ||
               !homeGanó &&  vencedoraId.equals(j.getHomeTeam().getId())) {
                throw new ApplicationException("El marcador no coincide con el vencedor.");
            }
        }

        // -------- crear Resultado ----------
        Resultado res = new Resultado();
        res.setGolosCasa(golosCasa);
        res.setGolosFora(golosFora);
        res.setPlacar(golosCasa + "-" + golosFora);
        res.setJogo(j);
        if (vencedoraId != null) {
            Team v = teamRepository.findById(vencedoraId)
                       .orElseThrow(() -> new NotFoundException("Equipo vencedor no existe"));
            res.setEquipaVitoriosa(v);
        }

        // -------- sincronizar con Jogo ----------
        j.setHomeScore(golosCasa);
        j.setAwayScore(golosFora);
        j.setResultado(res);

        jogoRepository.save(j);           // cascade guarda Resultado
        return res;
    }
    

    public Optional<Jogo> obterJogo(Long id) {
        return jogoRepository.findById(id);
    }
    /* ------------------------------------------------------------------
        ELIMINAR PARTIDO (borrado real, no «cancelado»)
    ------------------------------------------------------------------ */
    @Transactional
    public void eliminarJogo(Long id) throws NotFoundException, ApplicationException {
        Jogo j = jogoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Juego no encontrado: " + id));

        if (j.getResultado() != null)
            throw new ApplicationException("No se puede borrar un juego con resultado registrado.");

        j.getReferees().clear();          // limpia tabla puente jogo_arbitros
        jogoRepository.delete(j);
    }


    public JogoService(JogoRepository jogoRepository) {
        this.jogoRepository = jogoRepository;
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

    /**
     * Centraliza la lógica de búsqueda para /api/jogos
     */
    @Transactional(readOnly = true)
    public List<Jogo> searchGames(String status, String location, String timeSlot, Integer minGoals) {
        if ("played".equalsIgnoreCase(status)) {
            return findPlayedGames();
        }
        if ("cancelled".equalsIgnoreCase(status)) {
            return findCancelledGames();
        }
        if ("pending".equalsIgnoreCase(status)) {
            return findPendingGames();
        }
        if (location != null && !location.isBlank()) {
            return findByLocation(location);
        }
        if (timeSlot != null && !timeSlot.isBlank()) {
            return findByTimeSlot(timeSlot);
        }
        if (minGoals != null) {
            return findByMinGoals(minGoals);
        }
        return findAllJogos();
    }

    /**
     * Filtro avanzado de jogos:
     *   – realizados  (played)   → resultado ≠ null
     *   – aRealizar  (pending)  → resultado == null y !cancelado
     *   – minGoals               → suma goles ≥ X
     *   – location               → contiene ignore-case
     *   – timeSlot (manhã|tarde|noite)
     *
     * Si el usuario marca AMBOS check-box se muestran todos los partidos
     * (el resto de filtros siguen aplicándose).
     */
    @Transactional(readOnly = true)
    public List<Jogo> filterJogos(Boolean realizados, Boolean aRealizar,
            Integer minGoals, String location, String timeSlot) {

        return jogoRepository.findAll().stream()
            .filter(j -> realizados == null || (realizados && j.getResultado() != null)
                        || (aRealizar != null && aRealizar && j.getResultado() == null))
            .filter(j -> minGoals == null || somaGolsMin(j, minGoals))        //  <<< usa helper
            .filter(j -> location == null || (j.getLocal() != null
            && j.getLocal().toLowerCase().contains(location.toLowerCase())))
            .filter(j -> timeSlot == null || pertenceAoTurno(j.getDataHora(), timeSlot))
            .toList();
        }


    // Função auxiliar para somar gols do placar (ex: "3-2" -> 5)
    private boolean somaGolsMin(Jogo j, int threshold) {
        Integer h = j.getHomeScore();
        Integer a = j.getAwayScore();
        return h != null && a != null && (h + a) >= threshold;
    }

    // Função auxiliar para verificar turno (manhã, tarde, noite)
    private boolean pertenceAoTurno(java.time.LocalDateTime dataHora, String turno) {
        if (dataHora == null || turno == null) return false;
        int hora = dataHora.getHour();
        switch (turno.toLowerCase()) {
            case "manha":
            case "manhã":
            case "morning":
                return hora >= 6 && hora < 12;
            case "tarde":
            case "afternoon":
                return hora >= 12 && hora < 18;
            case "noite":
            case "noche":
            case "night":
                return hora >= 18 || hora < 6;
            default:
                return false;
        }
    }

    /**
     * Actualiza un juego existente a partir de un DTO.
     */
    @Transactional(readOnly = true)
    public Jogo atualizarJogo(Long id, JogoUpdateDTO dto) throws NotFoundException, ApplicationException {
        Jogo existing = jogoRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Jogo com ID " + id + " não encontrado."));
        // campos mutables
        if (dto.getDateTime() != null) {
            existing.setDataHora(dto.getDateTime());
        }
        if (dto.getLocation() != null && !dto.getLocation().isBlank()) {
            existing.setLocal(dto.getLocation());
        }
        if (dto.getAmigavel() != null) {
            existing.setAmigavel(dto.getAmigavel());
        }
        // equipos
        if (dto.getTeamIds() != null) {
            if (dto.getTeamIds().size() != 2) {
                throw new ApplicationException("Se deben especificar exactamente 2 equipos.");
            }
            Iterator<Long> it = dto.getTeamIds().iterator();
            Long homeId = it.next(), awayId = it.next();
            Team home = teamRepository.findById(homeId)
                .orElseThrow(() -> new NotFoundException("Team com ID " + homeId + " não encontrado."));
            Team away = teamRepository.findById(awayId)
                .orElseThrow(() -> new NotFoundException("Team com ID " + awayId + " não encontrado."));
            existing.setHomeTeam(home);
            existing.setAwayTeam(away);
        }
        // árbitros
        if (dto.getRefereeIds() != null) {
            var refs = refereeRepository.findAllById(dto.getRefereeIds());
            if (refs.size() != dto.getRefereeIds().size()) {
                throw new NotFoundException("Um ou mais árbitros não encontrados.");
            }
            // si es campeonato, validar certificados
            if (!existing.isAmigavel() && refs.stream().anyMatch(r -> !r.isCertified())) {
                throw new ApplicationException("Todos los árbitros deben estar certificados para partidos de campeonato.");
            }
            existing.setReferees(Set.copyOf(refs));
        }
        // árbitro principal
        if (dto.getPrimaryRefereeId() != null) {
            Referee primary = existing.getReferees().stream()
                .filter(r -> r.getId().equals(dto.getPrimaryRefereeId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Árbitro principal no forma parte de la lista."));
            existing.setPrimaryReferee(primary);
        }
        return jogoRepository.save(existing);
    }

    @Transactional
    public Jogo criarJogo(Jogo jogo) throws ApplicationException {
        // ✅  aquí podrías reutilizar validaciones o simplemente guardar
        return jogoRepository.save(jogo);
    }
}
