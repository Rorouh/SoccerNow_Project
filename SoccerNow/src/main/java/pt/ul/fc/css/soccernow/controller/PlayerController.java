// src/main/java/pt/ul/fc/css/soccernow/controller/PlayerController.java
package pt.ul.fc.css.soccernow.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.User.PreferredPosition;
import pt.ul.fc.css.soccernow.dto.PlayerCreateDTO;
import pt.ul.fc.css.soccernow.dto.PlayerDTO;
import pt.ul.fc.css.soccernow.dto.PlayerUpdateDTO;
import pt.ul.fc.css.soccernow.service.PlayerService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    /**
     * POST /api/players
     * Crea un jugador a partir de PlayerCreateDTO.
     */
    @PostMapping
    public ResponseEntity<?> createPlayer(@Valid @RequestBody PlayerCreateDTO dto) {
        try {
            Player created = playerService.createPlayer(dto);
            PlayerDTO out = PlayerDTO.fromEntity(created);
            return ResponseEntity
                    .created(URI.create("/api/players/" + created.getId()))
                    .body(out);
        } catch (ApplicationException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * GET /api/players/{id}
     * Devuelve un jugador por id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable Long id) {
        Optional<Player> opt = playerService.getPlayerById(id);
        return opt
            .map(p -> ResponseEntity.ok(PlayerDTO.fromEntity(p)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/players/{id}
     * Actualiza los campos que vienen en el JSON a través de PlayerUpdateDTO.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlayer(
            @PathVariable Long id,
            @Valid @RequestBody PlayerUpdateDTO dto) {
        try {
            Optional<Player> opt = playerService.updatePlayer(id, dto);
            if (opt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(PlayerDTO.fromEntity(opt.get()));
        } catch (ApplicationException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * DELETE /api/players/{id}
     * Elimina un jugador por id.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        return playerService.deletePlayer(id)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.notFound().build();
    }

    /**
     * GET /api/players/filter
     * Filtros avanzados: name, preferredPosition, minGoals, minCards, minGames.
     */
    @GetMapping("/filter")
    public ResponseEntity<List<PlayerDTO>> filterPlayers(
            @RequestParam(value = "name",             required = false) String name,
            @RequestParam(value = "preferredPosition", required = false) PreferredPosition preferredPosition,
            @RequestParam(value = "minGoals",         required = false) Integer minGoals,
            @RequestParam(value = "minCards",         required = false) Integer minCards,
            @RequestParam(value = "minGames",         required = false) Integer minGames
    ) {
        List<Player> results = playerService.searchPlayers(name, preferredPosition, minGoals, minCards, minGames);
        List<PlayerDTO> dtos = results.stream()
                                       .map(PlayerDTO::fromEntity)
                                       .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/players
     * Listado con filtros por posición, goles, tarjetas o juegos jugados.
     */
    @GetMapping
    public ResponseEntity<List<PlayerDTO>> listPlayers(
            @RequestParam(value = "position",        required = false) PreferredPosition position,
            @RequestParam(value = "minGoals",        required = false) Long minGoals,
            @RequestParam(value = "minRedCards",     required = false) Long minRedCards,
            @RequestParam(value = "minGamesPlayed",  required = false) Long minGames
    ) {
        List<Player> results;
        if (position != null) {
            results = playerService.findByPosition(position);
        } else if (minGoals != null) {
            results = playerService.findByMinGoals(minGoals);
        } else if (minRedCards != null) {
            results = playerService.findByMinRedCards(minRedCards);
        } else if (minGames != null) {
            results = playerService.findByMinGames(minGames);
        } else {
            results = playerService.findAllPlayers();
        }
        List<PlayerDTO> dtos = results.stream()
                                       .map(PlayerDTO::fromEntity)
                                       .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/players/by-name/{name}
     * Busca jugadores por fragmento de nombre.
     */
    @GetMapping("/by-name/{name}")
    public ResponseEntity<List<PlayerDTO>> findPlayersByName(@PathVariable String name) {
        List<Player> players = playerService.findPlayersByName(name);
        List<PlayerDTO> dtos = players.stream()
                                      .map(PlayerDTO::fromEntity)
                                      .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
