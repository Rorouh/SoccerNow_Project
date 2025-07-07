// src/main/java/pt/ul/fc/css/soccernow/service/TeamService.java
package pt.ul.fc.css.soccernow.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.Team;
import pt.ul.fc.css.soccernow.dto.TeamDTO;
import pt.ul.fc.css.soccernow.repository.PlayerRepository;
import pt.ul.fc.css.soccernow.repository.TeamRepository;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;



@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public TeamService(TeamRepository teamRepository, PlayerRepository playerRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    @Transactional
    public Team createTeam(TeamDTO dto) {
        Team team = new Team();
        mapBasicFields(dto, team);
        return teamRepository.save(team);
    }

    @Transactional
    public Optional<Team> updateTeam(Long id, TeamDTO dto) {
        return teamRepository.findById(id).map(existing -> {
            mapBasicFields(dto, existing);
            return teamRepository.save(existing);
        });
    }

    /* ===== helper privado que rellena todos los campos ===== */
    private void mapBasicFields(TeamDTO dto, Team team) {
        team.setName(dto.getName());

        // jugadores
        Set<Player> players = new HashSet<>();
        for (Long playerId : dto.getPlayerIds()) {
            playerRepository.findById(playerId).ifPresent(players::add);
        }
        team.setPlayers(players);

        // nuevas estadísticas
        team.setWins(dto.getWins());
        team.setDraws(dto.getDraws());
        team.setLosses(dto.getLosses());
        team.setAchievements(dto.getAchievements());
    }

    @Transactional
    public boolean deleteTeam(Long id) {
        Optional<Team> teamOpt = teamRepository.findById(id);
        if (teamOpt.isEmpty()) {
            return false;
        }
        Team team = teamOpt.get();
        if ((team.getJogosComoVisitada() != null && !team.getJogosComoVisitada().isEmpty()) ||
            (team.getJogosComoVisitante() != null && !team.getJogosComoVisitante().isEmpty())) {
            throw new IllegalStateException("No es posible eliminar un equipo con juegos asociados.");
        }
        team.getPlayers().clear();
        teamRepository.delete(team);
        return true;
    }

    @Transactional(readOnly = true)
    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Team> getTeamByName(String name) {
        return teamRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    /**
     * Añade un jugador al equipo (si no estaba ya),
     * guarda y recarga el equipo entero para devolver la lista actualizada.
     */
    @Transactional
    public Optional<Team> addPlayerToTeam(Long teamId, Long playerId) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        Optional<Player> playerOpt = playerRepository.findById(playerId);
        if (teamOpt.isEmpty() || playerOpt.isEmpty()) {
            return Optional.empty();
        }
        Team team = teamOpt.get();
        Player player = playerOpt.get();

        if (team.getPlayers().contains(player)) {
            throw new ApplicationException("El jugador ya forma parte del equipo.");
        }

        team.getPlayers().add(player);
        teamRepository.save(team);

        // recarga para asegurar que la colección está inicializada
        return teamRepository.findById(teamId);
    }
    
    public List<Team> findByName(String name) {
        return teamRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Team> findByMinPlayers(int minPlayers) {
        return teamRepository.findByMinPlayers(minPlayers);
    }

    public List<Team> findByMinWins(long minWins) {
        return teamRepository.findTeamsWithMinWins(minWins);
    }

    public List<Team> findWithNoPlayerInPosition(Player.PreferredPosition pos) {
        return teamRepository.findTeamsWithNoPlayerInPosition(pos);
    }
    
    /**
     * Búsqueda avanzada de equipos: name, minPlayers, minWins, minDraws,
     * minLosses, minAchievements, missingPosition (posición que NO debe
     * estar cubierta).  Sigue el mismo patrón que searchPlayers().
     */
    @Transactional(readOnly = true)
    public List<Team> filterTeams(String name,
                                Integer minPlayers,
                                Integer minWins,
                                Integer minDraws,
                                Integer minLosses,
                                Integer minAchievements,
                                String missingPosition) {

        // 1) lista base
        List<Team> result = teamRepository.findAll();

        // 2) nombre (usamos el repo dedicado)
        if (name != null && !name.isBlank()) {
            result = teamRepository.findByNameContainingIgnoreCase(name);
        }

        // 3) min-Players (filtrado en memoria ―es rápido―)
        if (minPlayers != null) {
            final int threshold = minPlayers;
            result = result.stream()
                        .filter(t -> t.getPlayers() != null
                                    && t.getPlayers().size() >= threshold)
                        .toList();
        }

        // 4) min-Victorias – hay JPQL optimizado
        if (minWins != null) {
            result = teamRepository.findTeamsWithMinWins(minWins);
        }

        // 5) min-Empates (en memoria)
        if (minDraws != null) {
            final int threshold = minDraws;
            result = result.stream()
                        .filter(t -> t.getDraws() >= threshold)
                        .toList();
        }

        // 6) min-Derrotas (en memoria)
        if (minLosses != null) {
            final int threshold = minLosses;
            result = result.stream()
                        .filter(t -> t.getLosses() >= threshold)
                        .toList();
        }

        // 7) min-Conquistas / Logros (en memoria)
        if (minAchievements != null) {
            final int threshold = minAchievements;
            result = result.stream()
                        .filter(t -> t.getAchievements() >= threshold)
                        .toList();
        }

        // 8) posición ausente (igual que players.missingPosition)
        if (missingPosition != null && !missingPosition.isBlank()) {
            try {
                var posEnum = Player.PreferredPosition.valueOf(missingPosition.trim().toUpperCase());
                result = result.stream()
                            .filter(t -> t.getPlayers()
                                            .stream()
                                            .noneMatch(p -> p.getPreferredPosition() == posEnum))
                            .toList();
            } catch (IllegalArgumentException ex) {
                throw new ApplicationException(
                    "Posición inválida. Valores: PORTERO, DEFENSA, CENTROCAMPISTA, DELANTERO."
                );
            }
        }

        return result;
    }
}
