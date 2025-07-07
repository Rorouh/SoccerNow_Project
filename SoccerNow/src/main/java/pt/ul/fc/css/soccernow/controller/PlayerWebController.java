package pt.ul.fc.css.soccernow.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.User;
import pt.ul.fc.css.soccernow.dto.PlayerCreateDTO;
import pt.ul.fc.css.soccernow.dto.PlayerDTO;
import pt.ul.fc.css.soccernow.dto.PlayerUpdateDTO;
import pt.ul.fc.css.soccernow.service.PlayerService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

@Controller
@RequestMapping("/web/players")
public class PlayerWebController {

  private final PlayerService playerService;

  public PlayerWebController(PlayerService playerService) {
    this.playerService = playerService;
  }

  /**
   * GET /web/players
   * Filtra por nombre, posición, minGoals, minCards, minGames
   */
  @GetMapping
  public String listPlayers(
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "preferredPosition", required = false) User.PreferredPosition preferredPosition,
      @RequestParam(value = "minGoals", required = false) String minGoalsStr,
      @RequestParam(value = "minCards", required = false) String minCardsStr,
      @RequestParam(value = "minGames", required = false) String minGamesStr,
      Model model) {

    Integer minGoals = null;
    Integer minCards = null;
    Integer minGames = null;
    try {
      if (minGoalsStr != null && !minGoalsStr.isBlank()) {
        minGoals = Integer.valueOf(minGoalsStr);
      }
    } catch (NumberFormatException e) {
      // Ignorar valor no numérico
    }
    try {
      if (minCardsStr != null && !minCardsStr.isBlank()) {
        minCards = Integer.valueOf(minCardsStr);
      }
    } catch (NumberFormatException e) {
      // Ignorar valor no numérico
    }
    try {
      if (minGamesStr != null && !minGamesStr.isBlank()) {
        minGames = Integer.valueOf(minGamesStr);
      }
    } catch (NumberFormatException e) {
      // Ignorar valor no numérico
    }

    List<Player> players = playerService.searchPlayers(
        name,
        preferredPosition,
        minGoals,
        minCards,
        minGames
    );

    List<PlayerDTO> dtos = players.stream()
        .map(PlayerDTO::fromEntity)
        .collect(Collectors.toList());

    model.addAttribute("nameFilter", name == null ? "" : name);
    model.addAttribute("posFilter", preferredPosition == null ? "" : preferredPosition.name());
    model.addAttribute("minGoalsFilter",  minGoals == null  ? "" : minGoals);
    model.addAttribute("minCardsFilter",  minCards == null  ? "" : minCards);
    model.addAttribute("minGamesFilter",  minGames == null  ? "" : minGames);
    model.addAttribute("players", dtos);
    model.addAttribute("positions", User.PreferredPosition.values());
    return "players/list";
  }

  @GetMapping("/create")
  public String showCreateForm(Model model) {
    model.addAttribute("playerDTO", new PlayerDTO());
    model.addAttribute("positions", User.PreferredPosition.values());
    return "players/form";
  }

  @PostMapping("/save")
  public String savePlayer(@Valid @ModelAttribute("playerDTO") PlayerCreateDTO dto, Model model) {
    try {
      playerService.createPlayer(dto);
      return "redirect:/web/players";
    } catch (ApplicationException ex) {
      model.addAttribute("error", ex.getMessage());
      model.addAttribute("positions", User.PreferredPosition.values());
      return "players/form";
    }
  }

  @GetMapping("/edit/{id}")
  public String showEditForm(@PathVariable Long id, Model model) {
    var opt = playerService.getPlayerById(id);
    if (opt.isEmpty()) {
      return "redirect:/web/players";
    }
    Player p = opt.get();
    PlayerDTO dto = PlayerDTO.fromEntity(p);
    model.addAttribute("playerDTO", dto);
    model.addAttribute("positions", User.PreferredPosition.values());
    return "players/form";
  }

  @PostMapping("/update/{id}")
  public String updatePlayer(
      @PathVariable Long id,
      @ModelAttribute("playerDTO") PlayerUpdateDTO dto,
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

  @GetMapping("/delete/{id}")
  public String deletePlayer(@PathVariable Long id) {
    playerService.deletePlayer(id);
    return "redirect:/web/players";
  }
}
