package pt.ul.fc.css.soccernow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.service.PlayerService;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<Player> createPlayer(@Valid @RequestBody Player player) {
        Player saved = playerService.save(player);
        return ResponseEntity
                .created(URI.create("/api/players/" + saved.getId()))
                .body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Player>> getPlayers(
            @RequestParam(required = false) String name) {
        List<Player> list;
        if (name != null && !name.isBlank()) {
            list = playerService.findPlayersByName(name);
        } else {
            list = playerService.findAllPlayers();
        }
        return ResponseEntity.ok(list);
    }
}
