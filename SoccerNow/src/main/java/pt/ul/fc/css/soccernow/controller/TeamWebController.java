// src/main/java/pt/ul/fc/css/soccernow/controller/TeamWebController.java
package pt.ul.fc.css.soccernow.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.Team;
import pt.ul.fc.css.soccernow.dto.TeamDTO;
import pt.ul.fc.css.soccernow.service.TeamService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web/teams")
public class TeamWebController {

    private final TeamService teamService;

    public TeamWebController(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * GET /web/teams
     * Lista todos los equipos. Si viene “name”, filtra por nombre.
     * Si viene “minPlayers”, filtra por mínimo de jugadores.
     * Si viene “minWins”, filtra por ganador de X partidos.
     * Si viene “noPosition”, filtra equipos que no tienen jugador con cierta posición.
     */
    @GetMapping
    public String listTeams(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "minPlayers", required = false) Integer minPlayers,
            @RequestParam(value = "minWins", required = false) Integer minWins,
            @RequestParam(value = "minDraws", required = false) Integer minDraws,
            @RequestParam(value = "minLosses", required = false) Integer minLosses,
            @RequestParam(value = "minAchievements", required = false) Integer minAchievements,
            @RequestParam(value = "missingPosition", required = false) String missingPosition,
            @RequestParam(value = "noPosition", required = false) String noPosition,
            Model model) {

        List<Team> teams;

        // Se algum filtro avançado for usado, aplica filtro avançado
        if (name != null || minPlayers != null || minWins != null || minDraws != null || minLosses != null || minAchievements != null || (missingPosition != null && !missingPosition.isBlank())) {
            teams = teamService.filterTeams(name, minPlayers, minWins, minDraws, minLosses, minAchievements, missingPosition);
        } else if (noPosition != null && !noPosition.isBlank()) {
            Player.PreferredPosition posEnum;
            try {
                posEnum = Player.PreferredPosition.valueOf(noPosition);
                teams = teamService.findWithNoPlayerInPosition(posEnum);
            } catch (IllegalArgumentException ex) {
                model.addAttribute("error", "Posição inválida. Valores: PORTERO, DEFENSA, CENTROCAMPISTA, DELANTERO.");
                teams = teamService.getAllTeams();
                model.addAttribute("positions", Player.PreferredPosition.values());
                model.addAttribute("teams", teams);
                return "teams/list";
            }
        } else {
            teams = teamService.getAllTeams();
        }

        List<TeamDTO> dtos = teams.stream()
                .map(t -> new TeamDTO(
                        t.getId(),
                        t.getName(),
                        t.getPlayers().stream().map(Player::getId).collect(Collectors.toSet())
                ))
                .collect(Collectors.toList());

        model.addAttribute("teams", dtos);
        model.addAttribute("positions", Player.PreferredPosition.values());
        return "teams/list";  // templates/teams/list.html
    }

    /**
     * GET /web/teams/create
     * Muestra formulario para crear un equipo.
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("teamDTO", new TeamDTO());
        // Para el formulario, necesitaremos cargar todos los jugadores disponibles.
        // Suponemos que hay método findAllPlayers() en PlayerService. Si no existe, añádelo:
        // List<Player> all = playerService.findAllPlayers();
        // model.addAttribute("allPlayers", all);
        return "teams/form";  // templates/teams/form.html
    }

    /**
     * POST /web/teams/save
     * Crea un equipo. Luego redirige a lista.
     */
    @PostMapping("/save")
    public String saveTeam(@Valid @ModelAttribute("teamDTO") TeamDTO dto,
                           Model model) {
        try {
            teamService.createTeam(dto);
            return "redirect:/web/teams";
        } catch (ApplicationException ex) {
            model.addAttribute("error", ex.getMessage());
            return "teams/form";
        }
    }

    /**
     * GET /web/teams/edit/{id}
     * Muestra formulario para editar equipo.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        var opt = teamService.getTeamById(id);
        if (opt.isEmpty()) {
            return "redirect:/web/teams";
        }
        Team t = opt.get();
        TeamDTO dto = new TeamDTO(
                t.getId(),
                t.getName(),
                t.getPlayers().stream().map(Player::getId).collect(Collectors.toSet())
        );
        model.addAttribute("teamDTO", dto);
        return "teams/form";
    }

    /**
     * POST /web/teams/update/{id}
     * Actualiza los datos del equipo.
     */
    @PostMapping("/update/{id}")
    public String updateTeam(@PathVariable Long id,
                             @ModelAttribute("teamDTO") TeamDTO dto,
                             Model model) {
        try {
            var opt = teamService.updateTeam(id, dto);
            if (opt.isEmpty()) {
                return "redirect:/web/teams";
            }
            return "redirect:/web/teams";
        } catch (ApplicationException ex) {
            model.addAttribute("error", ex.getMessage());
            return "teams/form";
        }
    }

    /**
     * GET /web/teams/delete/{id}
     * Elimina un equipo.
     */
    @GetMapping("/delete/{id}")
    public String deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return "redirect:/web/teams";
    }

    /**
     * GET /web/teams/addPlayer/{teamId}/{playerId}
     * Añade un jugador al equipo y redirige mostrando error si ya estaba.
     */
    @GetMapping("/addPlayer/{teamId}/{playerId}")
    public String addPlayerToTeam(@PathVariable Long teamId,
                                  @PathVariable Long playerId,
                                  RedirectAttributes redirectAttrs) {
        try {
            teamService.addPlayerToTeam(teamId, playerId);
        } catch (ApplicationException ex) {
            redirectAttrs.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/web/teams";
    }

    /**
     * GET /web/teams/filter
     * Filtros avançados: nome, minPlayers, minWins, minDraws, minLosses, minAchievements, missingPosition
     */
    @GetMapping("/filter")
    public String filterTeams(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "minPlayers", required = false) Integer minPlayers,
            @RequestParam(value = "minWins", required = false) Integer minWins,
            @RequestParam(value = "minDraws", required = false) Integer minDraws,
            @RequestParam(value = "minLosses", required = false) Integer minLosses,
            @RequestParam(value = "minAchievements", required = false) Integer minAchievements,
            @RequestParam(value = "missingPosition", required = false) String missingPosition,
            Model model) {
        List<Team> teams = teamService.filterTeams(name, minPlayers, minWins, minDraws, minLosses, minAchievements, missingPosition);
        List<TeamDTO> dtos = teams.stream()
                .map(t -> new TeamDTO(
                        t.getId(),
                        t.getName(),
                        t.getPlayers().stream().map(Player::getId).collect(Collectors.toSet())
                ))
                .collect(Collectors.toList());
        model.addAttribute("teams", dtos);
        model.addAttribute("positions", Player.PreferredPosition.values());
        return "teams/list";
    }
}
