package pt.ul.fc.css.soccernow.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.domain.Team;
import pt.ul.fc.css.soccernow.dto.TeamDTO;
import pt.ul.fc.css.soccernow.service.TeamService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public ResponseEntity<Team> createTeam(@Valid @RequestBody TeamDTO teamDTO) {
        Team team = teamService.createTeam(teamDTO);
        return ResponseEntity
                .created(URI.create("/api/teams/" + team.getId()))
                .body(team);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable Long id) {
        return teamService.getTeamById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Team> updateTeam(@PathVariable Long id, @Valid @RequestBody TeamDTO teamDTO) {
        return teamService.updateTeam(id, teamDTO)
                .map(updatedTeam -> ResponseEntity.ok(updatedTeam))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        if (teamService.deleteTeam(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(400).build();
        }
    }


    @GetMapping("/search")
    public ResponseEntity<List<Team>> searchTeams(@RequestParam String name) {
        List<Team> teams = teamService.getTeamsByName(name);
        return ResponseEntity.ok(teams);
    }

    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        List<Team> teams = teamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }
}
