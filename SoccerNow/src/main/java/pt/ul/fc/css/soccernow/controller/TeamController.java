package pt.ul.fc.css.soccernow.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.domain.Team;
import pt.ul.fc.css.soccernow.domain.Player.PreferredPosition;
import pt.ul.fc.css.soccernow.dto.TeamDTO;
import pt.ul.fc.css.soccernow.service.TeamService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * POST /api/teams
     * Crea un equipo nuevo.
     */
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TeamDTO dto) {
        try {
            Team saved = teamService.createTeam(dto);
            return ResponseEntity
                    .created(URI.create("/api/teams/" + saved.getId()))
                    .body(TeamDTO.fromEntity(saved));
        } catch (ApplicationException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * GET /api/teams/{id}
     * Recupera un equipo por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getById(@PathVariable Long id) {
        Optional<Team> opt = teamService.getTeamById(id);
        return opt
            .map(team -> ResponseEntity.ok(TeamDTO.fromEntity(team)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/teams/{id}
     * Actualiza nombre y jugadores de un equipo existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody TeamDTO dto) {
        try {
            Optional<Team> opt = teamService.updateTeam(id, dto);
            return opt
                .map(team -> ResponseEntity.ok(TeamDTO.fromEntity(team)))
                .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (ApplicationException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * DELETE /api/teams/{id}
     * Elimina un equipo (si no tiene partidos asociados).
     */
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

    /**
     * GET /api/teams
     * Soporta múltiples modos de búsqueda vía query‐params:
     *   ?name=XYZ
     *   ?minPlayers=3
     *   ?minWins=5
     *   ?missingPosition=DEFENSA
     * Si no se pasa nada, retorna todos.
     */
    @GetMapping
    public ResponseEntity<List<TeamDTO>> list(
            @RequestParam(value = "name",            required = false) String name,
            @RequestParam(value = "minPlayers",      required = false) Integer minPlayers,
            @RequestParam(value = "minWins",         required = false) Long    minWins,
            @RequestParam(value = "missingPosition", required = false) PreferredPosition missingPosition
    ) {
        List<Team> results;
        if (name != null && !name.isBlank()) {
            results = teamService.findByName(name);
        } else if (minPlayers != null) {
            results = teamService.findByMinPlayers(minPlayers);
        } else if (minWins != null) {
            results = teamService.findByMinWins(minWins);
        } else if (missingPosition != null) {
            results = teamService.findWithNoPlayerInPosition(missingPosition);
        } else {
            results = teamService.getAllTeams();
        }

        List<TeamDTO> dtos = results.stream()
                                    .map(TeamDTO::fromEntity)
                                    .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/teams/filter
     * (Opcional) buscador avanzado combinando todos los criterios a la vez.
     */
    @GetMapping("/filter")
    public ResponseEntity<List<TeamDTO>> filter(
            @RequestParam(value = "name",            required = false) String name,
            @RequestParam(value = "minPlayers",      required = false) Integer minPlayers,
            @RequestParam(value = "minWins",         required = false) Integer minWins,
            @RequestParam(value = "minDraws",        required = false) Integer minDraws,
            @RequestParam(value = "minLosses",       required = false) Integer minLosses,
            @RequestParam(value = "minAchievements", required = false) Integer minAchievements,
            @RequestParam(value = "missingPosition", required = false) String missingPosition
    ) {
        var filtered = teamService.filterTeams(
            name, minPlayers, minWins, minDraws, minLosses, minAchievements, missingPosition
        );
        return ResponseEntity.ok(
            filtered.stream().map(TeamDTO::fromEntity).collect(Collectors.toList())
        );
    }

    /**
     * POST /api/teams/{teamId}/players/{playerId}
     * Caso de uso “añadir jugador a equipo” (solo si no estaba ya):
     */
    @PostMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<?> addPlayer(
            @PathVariable Long teamId,
            @PathVariable Long playerId
    ) {
        Optional<Team> opt = teamService.addPlayerToTeam(teamId, playerId);
        return opt
            .map(team -> ResponseEntity.ok(TeamDTO.fromEntity(team)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
