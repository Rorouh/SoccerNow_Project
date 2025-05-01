package pt.ul.fc.css.soccernow.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.Team;
import pt.ul.fc.css.soccernow.dto.TeamDTO;
import pt.ul.fc.css.soccernow.repository.PlayerRepository;
import pt.ul.fc.css.soccernow.repository.TeamRepository;
import pt.ul.fc.css.soccernow.repository.MatchRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;

    public TeamService(TeamRepository teamRepository, PlayerRepository playerRepository, MatchRepository matchRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
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

    @Transactional(readOnly = true)
    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findById(id);
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

        boolean hasMatches = matchRepository.hasMatches(team);
        if (hasMatches) return false;

        teamRepository.delete(team);
        return true;
    }


    @Transactional(readOnly = true)
    public Optional<Team> getTeamByName(String name) {
        return teamRepository.findByName(name).stream().findFirst();
    }


    @Transactional(readOnly = true)
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }
}
