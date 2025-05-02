package pt.ul.fc.css.soccernow.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.soccernow.domain.Player;
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
        return teamRepository.findById(id).map(existing -> {
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
        Optional<Team> optionalTeam = teamRepository.findById(id);
        if (optionalTeam.isEmpty()) return false;

        Team team = optionalTeam.get();

        /*boolean hasMatches = matchRepository.hasMatches(team);
        if (hasMatches) return false;*/

        teamRepository.delete(team);
        return true;
    }


    @Transactional(readOnly = true)
    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Team> getAllTeams() {
        return teamRepository.findAll(); // Não há necessidade de Optional aqui.
    }


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

}
