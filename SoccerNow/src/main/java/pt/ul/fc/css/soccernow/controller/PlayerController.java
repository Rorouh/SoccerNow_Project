// src/main/java/pt/ul/fc/css/soccernow/controller/PlayerController.java
package pt.ul.fc.css.soccernow.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.domain.Player;

import pt.ul.fc.css.soccernow.dto.PlayerDTO;

import pt.ul.fc.css.soccernow.service.PlayerService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;


import java.net.URI;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    /**
     * POST /api/players
     * Crea un jugador. El DTO viene con preferredPosition como String, 
     * lo convertimos a User.PreferredPosition internamente.
     */
    @PostMapping
    public ResponseEntity<PlayerDTO> createPlayer(@Valid @RequestBody PlayerDTO dto) {
        try {
            Player created = playerService.createPlayer(dto);

            // Convertimos entidad Player a PlayerDTO (preferredPosition -> String)
            PlayerDTO salida = new PlayerDTO(
                created.getId(),
                created.getName(),
                created.getEmail(),
                created.getPassword(),
                created.getPreferredPosition().name()
            );

            return ResponseEntity
                    .created(URI.create("/api/players/" + created.getId()))
                    .body(salida);

        } catch (ApplicationException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/players/{id}
     * Devuelve un jugador por id, si existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable Long id) {
        Optional<Player> opt = playerService.getPlayerById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Player p = opt.get();
        PlayerDTO salida = new PlayerDTO(
            p.getId(),
            p.getName(),
            p.getEmail(),
            p.getPassword(),
            p.getPreferredPosition().name()
        );
        return ResponseEntity.ok(salida);
    }

    /**
     * PUT /api/players/{id}
     * Actualiza solo los campos que vienen en el JSON. 
     * En este caso aceptamos preferredPosition como String y lo convertimos a enum.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PlayerDTO> updatePlayer(
            @PathVariable Long id,
            @RequestBody PlayerDTO dto /* sin @Valid */) {
        try {
            Optional<Player> opt = playerService.updatePlayer(id, dto);
            if (opt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Player p = opt.get();
            PlayerDTO salida = new PlayerDTO(
                p.getId(),
                p.getName(),
                p.getEmail(),
                p.getPassword(),
                p.getPreferredPosition().name()
            );
            return ResponseEntity.ok(salida);
        } catch (ApplicationException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE /api/players/{id}
     * Elimina un jugador por id.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        if (playerService.deletePlayer(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/players")
    public ResponseEntity<List<PlayerDTO>> listPlayers(
            @RequestParam(value = "position", required = false) Player.PreferredPosition position,
            @RequestParam(value = "minGoals", required = false) Long minGoals,
            @RequestParam(value = "minRedCards", required = false) Long minRedCards,
            @RequestParam(value = "minGamesPlayed", required = false) Long minGames) {

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
                .map(p -> new PlayerDTO(p.getId(), p.getName(), p.getEmail(), p.getPassword(),
                                    p.getPreferredPosition().name()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

}
