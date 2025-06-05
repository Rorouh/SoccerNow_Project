package pt.ul.fc.css.soccernow.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.Team;
import pt.ul.fc.css.soccernow.dto.TeamDTO;
import pt.ul.fc.css.soccernow.service.TeamService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    /**
     * GET /api/teams/filter
     * Filtros avançados: nome, minPlayers, minWins, minDraws, minLosses, minAchievements, missingPosition
     */
    @GetMapping("/filter")
    public ResponseEntity<List<TeamDTO>> filterTeams(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "minPlayers", required = false) Integer minPlayers,
            @RequestParam(value = "minWins", required = false) Integer minWins,
            @RequestParam(value = "minDraws", required = false) Integer minDraws,
            @RequestParam(value = "minLosses", required = false) Integer minLosses,
            @RequestParam(value = "minAchievements", required = false) Integer minAchievements,
            @RequestParam(value = "missingPosition", required = false) String missingPosition
    ) {
        List<Team> results = teamService.filterTeams(name, minPlayers, minWins, minDraws, minLosses, minAchievements, missingPosition);
        List<TeamDTO> dtos = results.stream()
                .map(t -> new TeamDTO(
                        t.getId(),
                        t.getName()
                        // Adicione outros campos necessários do DTO
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public ResponseEntity<TeamDTO> create(@Valid @RequestBody TeamDTO dto) {
        Team saved = teamService.createTeam(dto);
        TeamDTO out = new TeamDTO(
            saved.getId(),
            saved.getName(),
            saved.getPlayers().stream().map(p -> p.getId()).collect(Collectors.toSet())
        );
        return ResponseEntity
            .created(URI.create("/api/teams/" + saved.getId()))
            .body(out);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getById(@PathVariable Long id) {
        return teamService.getTeamById(id)
            .map(team -> new TeamDTO(
                team.getId(),
                team.getName(),
                team.getPlayers().stream().map(p -> p.getId()).collect(Collectors.toSet())
            ))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TeamDTO>> list(@RequestParam(required = false) String name) {
        List<Team> teams = (name == null || name.isBlank())
            ? teamService.getAllTeams()
            : teamService.getTeamByName(name)
                         .map(List::of)
                         .orElse(List.of());
        List<TeamDTO> dtos = teams.stream()
            .map(team -> new TeamDTO(
                team.getId(),
                team.getName(),
                team.getPlayers().stream().map(p -> p.getId()).collect(Collectors.toSet())
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody TeamDTO dto) {

        return teamService.updateTeam(id, dto)
            .map(team -> new TeamDTO(
                team.getId(),
                team.getName(),
                team.getPlayers().stream().map(p -> p.getId()).collect(Collectors.toSet())
            ))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            return teamService.deleteTeam(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/teams")
    public ResponseEntity<List<TeamDTO>> listTeams(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "minPlayers", required = false) Integer minPlayers,
            @RequestParam(value = "minWins", required = false) Long minWins,
            @RequestParam(value = "noPlayerInPosition", required = false) Player.PreferredPosition pos) {

        List<Team> results;

        if (name != null) {
            results = teamService.findByName(name);
        } else if (minPlayers != null) {
            results = teamService.findByMinPlayers(minPlayers);
        } else if (minWins != null) {
            results = teamService.findByMinWins(minWins);
        } else if (pos != null) {
            results = teamService.findWithNoPlayerInPosition(pos);
        } else {
            results = teamService.getAllTeams();
        }

        List<TeamDTO> dtos = results.stream()
                .map(t -> new TeamDTO(t.getId(), t.getName(),
                                    t.getPlayers().stream().map(Player::getId).collect(Collectors.toSet())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

}
