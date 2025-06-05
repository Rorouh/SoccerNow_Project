// src/main/java/pt/ul/fc/css/soccernow/controller/PlayerWebController.java
package pt.ul.fc.css.soccernow.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.domain.User;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.dto.PlayerDTO;
import pt.ul.fc.css.soccernow.service.PlayerService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web/players")
public class PlayerWebController {

    private final PlayerService playerService;

    public PlayerWebController(PlayerService playerService) {
        this.playerService = playerService;
    }

    /**
     * GET /web/players
     * Lista todos los jugadores. Si viene parámetro "name", filtra por nombre.
     * Si viene "position", filtra por posición.
     * Si viene "minGoals" o "minCards", filtra respectivamente.
     */
    @GetMapping
    public String listPlayers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "position", required = false) String position,
            @RequestParam(value = "minGoals", required = false) Integer minGoals,
            @RequestParam(value = "minCards", required = false) Integer minCards,
            Model model) {

        // Filtro avançado: permite combinar todos os filtros ao mesmo tempo
        List<Player> players = playerService.filterPlayers(name, position, minGoals, minCards);

        List<PlayerDTO> dtos = players.stream()
                .map(p -> new PlayerDTO(
                        p.getId(),
                        p.getName(),
                        p.getEmail(),
                        p.getPassword(),
                        p.getPreferredPosition() != null ? p.getPreferredPosition().name() : null,
                        p.getGoals(),
                        p.getCards()
                ))
                .collect(Collectors.toList());

        model.addAttribute("players", dtos);
        model.addAttribute("positions", User.PreferredPosition.values());
        return "players/list";
    }

    /**
     * GET /web/players/create
     * Muestra formulario para crear un jugador.
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("playerDTO", new PlayerDTO());
        model.addAttribute("positions", User.PreferredPosition.values());
        return "players/form";  // templates/players/form.html
    }

    /**
     * POST /web/players/save
     * Crea un jugador. Luego redirige a la lista.
     */
    @PostMapping("/save")
    public String savePlayer(@Valid @ModelAttribute("playerDTO") PlayerDTO dto,
                             Model model) {
        try {
            playerService.createPlayer(dto);
            return "redirect:/web/players";
        } catch (ApplicationException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("positions", User.PreferredPosition.values());
            return "players/form";
        }
    }

    /**
     * GET /web/players/edit/{id}
     * Muestra formulario con datos para editar un jugador.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        var opt = playerService.getPlayerById(id);
        if (opt.isEmpty()) {
            return "redirect:/web/players";
        }
        Player p = opt.get();
        PlayerDTO dto = new PlayerDTO(
                p.getId(),
                p.getName(),
                p.getEmail(),
                p.getPassword(),
                p.getPreferredPosition().name()
        );
        dto.setGoals(p.getGoals());
        dto.setCards(p.getCards());

        model.addAttribute("playerDTO", dto);
        model.addAttribute("positions", User.PreferredPosition.values());
        return "players/form";
    }

    /**
     * POST /web/players/update/{id}
     * Actualiza un jugador existente.
     */
    @PostMapping("/update/{id}")
    public String updatePlayer(@PathVariable Long id,
                               @ModelAttribute("playerDTO") PlayerDTO dto,
                               Model model) {
        try {
            var opt = playerService.updatePlayer(id, dto);
            if (opt.isEmpty()) {
                return "redirect:/web/players";
            }
            return "redirect:/web/players";
        } catch (ApplicationException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("positions", User.PreferredPosition.values());
            return "players/form";
        }
    }

    /**
     * GET /web/players/delete/{id}
     * Elimina un jugador.
     */
    @GetMapping("/delete/{id}")
    public String deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return "redirect:/web/players";
    }
}
