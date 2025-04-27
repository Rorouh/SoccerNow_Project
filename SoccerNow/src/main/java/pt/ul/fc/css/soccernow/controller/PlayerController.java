package pt.ul.fc.css.soccernow.controller;

import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.service.PlayerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public Player createPlayer(@RequestBody Player player) {
        return playerService.save(player);
    }

    @GetMapping
    public List<Player> getAllPlayers() {
        return playerService.findAll();
    }
}
