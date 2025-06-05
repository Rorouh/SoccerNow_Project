package pt.ul.fc.css.soccernow.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.soccernow.domain.Player;       // ← Import necesario
import pt.ul.fc.css.soccernow.domain.Team;
import pt.ul.fc.css.soccernow.dto.TeamDTO;
import pt.ul.fc.css.soccernow.repository.PlayerRepository;
import pt.ul.fc.css.soccernow.repository.TeamRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        Set<Player> players = new HashSet<>();
        for (Long playerId : dto.getPlayerIds()) {
            playerRepository.findById(playerId).ifPresent(players::add);
        }
        Team team = new Team();
        team.setName(dto.getName());
        team.setPlayers(players);
        return teamRepository.save(team);
    }

    @Transactional
    public Optional<Team> updateTeam(Long id, TeamDTO dto) {
        return teamRepository.findById(id)
            .map(existing -> {
                existing.setName(dto.getName());
                Set<Player> players = new HashSet<>();
                for (Long playerId : dto.getPlayerIds()) {
                    playerRepository.findById(playerId).ifPresent(players::add);
                }
                existing.setPlayers(players);
                return teamRepository.save(existing);
            });
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

    // Este método es requerido por TeamServiceTest.java
    @Transactional
    public Optional<Team> addPlayerToTeam(Long teamId, Long playerId) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        Optional<Player> playerOpt = playerRepository.findById(playerId);
        if (teamOpt.isEmpty() || playerOpt.isEmpty()) {
            return Optional.empty();
        }
        Team team = teamOpt.get();
        Player player = playerOpt.get();
        team.getPlayers().add(player);
        return Optional.of(teamRepository.save(team));
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
     * Filtro avançado de equipas: nome, minPlayers, minWins, minDraws, minLosses, minAchievements, missingPosition
     */
    @Transactional(readOnly = true)
    public List<Team> filterTeams(String name, Integer minPlayers, Integer minWins, Integer minDraws, Integer minLosses, Integer minAchievements, String missingPosition) {
        List<Team> all = teamRepository.findAll();
        return all.stream()
            .filter(t -> name == null || t.getName().toLowerCase().contains(name.toLowerCase()))
            .filter(t -> minPlayers == null || (t.getPlayers() != null && t.getPlayers().size() >= minPlayers))
            // Métodos getWins(), getDraws(), getLosses(), getAchievements() agora retornam int
            .filter(t -> minWins == null || t.getWins() >= minWins)
            .filter(t -> minDraws == null || t.getDraws() >= minDraws)
            .filter(t -> minLosses == null || t.getLosses() >= minLosses)
            .filter(t -> minAchievements == null || t.getAchievements() >= minAchievements)
            .filter(t -> missingPosition == null || t.getPlayers().stream().noneMatch(p -> p.getPreferredPosition().name().equalsIgnoreCase(missingPosition)))
            .toList();
    }
}
