// src/main/java/pt/ul/fc/css/soccernow/controller/JogoWebController.java
package pt.ul.fc.css.soccernow.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.domain.Jogo;
import pt.ul.fc.css.soccernow.domain.Referee;
import pt.ul.fc.css.soccernow.domain.Team;
import pt.ul.fc.css.soccernow.dto.JogoDTO;
import pt.ul.fc.css.soccernow.service.JogoService;
import pt.ul.fc.css.soccernow.service.TeamService;
import pt.ul.fc.css.soccernow.service.RefereeService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;
import pt.ul.fc.css.soccernow.service.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web/jogos")
public class JogoWebController {

    private final JogoService jogoService;
    private final TeamService teamService;
    private final RefereeService refereeService;

    public JogoWebController(JogoService jogoService,
                             TeamService teamService,
                             RefereeService refereeService) {
        this.jogoService = jogoService;
        this.teamService = teamService;
        this.refereeService = refereeService;
    }

    /**
     * GET /web/jogos
     * Muestra la lista completa de juegos (sin filtros).
     */
    @GetMapping
    public String listJogos(Model model) {
        List<Jogo> jogos = jogoService.findAllJogos();
        List<JogoDTO> dtos = jogos.stream().map(this::toDTO).collect(Collectors.toList());
        model.addAttribute("jogos", dtos);
        return "jogos/list";  // templates/jogos/list.html
    }

    /**
     * GET /web/jogos/create
     * Muestra formulario vacío para crear un juego.
     * Pone en el modelo la lista de equipos y árbitros.
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("jogoDTO", new JogoDTO());
        model.addAttribute("teams", teamService.getAllTeams());
        model.addAttribute("arbitros", refereeService.getAllReferees());
        return "jogos/form";  // templates/jogos/form.html
    }

    /**
     * POST /web/jogos/save
     * Recibe el JogoDTO del formulario y crea el juego.
     */
    @PostMapping("/save")
    public String saveJogo(@ModelAttribute("jogoDTO") JogoDTO dto, Model model) {
        try {
            // Construir objeto Jogo para enviar a servicio
            Jogo jogo = new Jogo();
            jogo.setDataHora(dto.getDateTime());
            jogo.setLocal(dto.getLocation());
            jogo.setAmigavel(dto.isAmigavel());

            // Cargar equipos y árbitros:
            Team home = teamService.getTeamById(dto.getHomeTeamId())
                    .orElseThrow(() -> new NotFoundException("Equipo local no encontrado"));
            Team away = teamService.getTeamById(dto.getAwayTeamId())
                    .orElseThrow(() -> new NotFoundException("Equipo visitante no encontrado"));
            jogo.setHomeTeam(home);
            jogo.setAwayTeam(away);

            Set<Referee> arbitros = refereeService.findAllByIds(dto.getArbitroIds());
            jogo.setReferees(arbitros);

            if (dto.getPrimaryRefereeId() != null) {
                Referee primary = refereeService.getRefereeById(dto.getPrimaryRefereeId())
                        .orElseThrow(() -> new NotFoundException("Árbitro principal no encontrado"));
                jogo.setPrimaryReferee(primary);
            }

            // Llamada a servicio:
            jogoService.criarJogo(jogo);
            return "redirect:/web/jogos";

        } catch (ApplicationException | NotFoundException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("teams", teamService.getAllTeams());
            model.addAttribute("arbitros", refereeService.getAllReferees());
            return "jogos/form";
        }
    }

    /**
     * GET /web/jogos/edit/{id}
     * Muestra formulario con datos del juego para editar (excepto resultado).
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Jogo jogo = jogoService.obterJogo(id)
                .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado: " + id));
        JogoDTO dto = toDTO(jogo);
        model.addAttribute("jogoDTO", dto);
        model.addAttribute("teams", teamService.getAllTeams());
        model.addAttribute("arbitros", refereeService.getAllReferees());
        return "jogos/form";
    }

    /**
     * POST /web/jogos/update/{id}
     * Actualiza un juego existente (solo campos mutables).
     */
    @PostMapping("/update/{id}")
    public String updateJogo(@PathVariable Long id,
                             @ModelAttribute("jogoDTO") JogoDTO dto,
                             Model model) {
        try {
            Jogo existing = jogoService.obterJogo(id)
                    .orElseThrow(() -> new NotFoundException("Juego no encontrado: " + id));

            // Solo permitimos cambiar fecha, local, amigable, equipos, árbitros
            if (dto.getDateTime() != null) existing.setDataHora(dto.getDateTime());
            if (dto.getLocation() != null && !dto.getLocation().isBlank()) existing.setLocal(dto.getLocation());
            existing.setAmigavel(dto.isAmigavel());

            Team home = teamService.getTeamById(dto.getHomeTeamId())
                    .orElseThrow(() -> new NotFoundException("Equipo local no encontrado"));
            Team away = teamService.getTeamById(dto.getAwayTeamId())
                    .orElseThrow(() -> new NotFoundException("Equipo visitante no encontrado"));
            existing.setHomeTeam(home);
            existing.setAwayTeam(away);

            Set<Referee> arbitros = refereeService.findAllByIds(dto.getArbitroIds());
            existing.setReferees(arbitros);

            if (dto.getPrimaryRefereeId() != null) {
                Referee primary = refereeService.getRefereeById(dto.getPrimaryRefereeId())
                        .orElseThrow(() -> new NotFoundException("Árbitro principal no encontrado"));
                existing.setPrimaryReferee(primary);
            }

            jogoService.criarJogo(existing); // reutilizamos lógica de validaciones
            return "redirect:/web/jogos";

        } catch (ApplicationException | NotFoundException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("teams", teamService.getAllTeams());
            model.addAttribute("arbitros", refereeService.getAllReferees());
            return "jogos/form";
        }
    }

    /**
     * GET /web/jogos/delete/{id}
     * Elimina un juego si no tiene resultado. Redirige a la lista.
     */
    @GetMapping("/delete/{id}")
    public String deleteJogo(@PathVariable Long id, Model model) {
        try {
            jogoService.cancelarJogo(id);
            return "redirect:/web/jogos";
        } catch (ApplicationException | NotFoundException ex) {
            model.addAttribute("error", ex.getMessage());
            List<Jogo> jogos = jogoService.findAllJogos();
            model.addAttribute("jogos", jogos.stream().map(this::toDTO).collect(Collectors.toList()));
            return "jogos/list";
        }
    }

    /**
     * GET /web/jogos/filters
     * Lista juegos aplicando filtros: status (played, pending, cancelled), location, minGoals, timeSlot.
     * Parámetros opcionales: status, location, minGoals, timeSlot.
     */
    @GetMapping("/filters")
    public String filterJogos(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "minGoals", required = false) Integer minGoals,
            @RequestParam(value = "timeSlot", required = false) String timeSlot,
            Model model
    ) {
        List<Jogo> results;

        if (status != null) {
            switch (status.toLowerCase()) {
                case "played":
                    results = jogoService.findPlayedGames();
                    break;
                case "pending":
                    results = jogoService.findPendingGames();
                    break;
                case "cancelled":
                    results = jogoService.findCancelledGames();
                    break;
                default:
                    results = jogoService.findAllJogos();
            }
        } else if (location != null && !location.isBlank()) {
            results = jogoService.findByLocation(location);
        } else if (minGoals != null) {
            results = jogoService.findByMinGoals(minGoals);
        } else if (timeSlot != null && !timeSlot.isBlank()) {
            results = jogoService.findByTimeSlot(timeSlot);
        } else {
            results = jogoService.findAllJogos();
        }

        List<JogoDTO> dtos = results.stream().map(this::toDTO).collect(Collectors.toList());
        model.addAttribute("jogos", dtos);
        return "jogos/list";
    }

    // Helper: convierte Jogo a JogoDTO para mostrar en vista
    private JogoDTO toDTO(Jogo j) {
        JogoDTO dto = new JogoDTO();
        dto.setId(j.getId());
        dto.setDateTime(j.getDateTime());
        dto.setLocation(j.getLocation());
        dto.setAmigavel(j.isAmigavel());
        dto.setHomeScore(j.getHomeScore());
        dto.setAwayScore(j.getAwayScore());
        dto.setHomeTeamId(j.getHomeTeam().getId());
        dto.setAwayTeamId(j.getAwayTeam().getId());
        dto.setCampeonatoId(j.getCampeonato() != null ? j.getCampeonato().getId() : null);
        dto.setArbitroIds(j.getReferees().stream().map(Referee::getId).collect(Collectors.toSet()));
        dto.setPrimaryRefereeId(j.getPrimaryReferee() != null ? j.getPrimaryReferee().getId() : null);
        return dto;
    }
}
