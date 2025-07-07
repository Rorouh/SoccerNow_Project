// src/main/java/pt/ul/fc/css/soccernow/controller/JogoWebController.java
package pt.ul.fc.css.soccernow.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ul.fc.css.soccernow.domain.Jogo;
import pt.ul.fc.css.soccernow.domain.Referee;
import pt.ul.fc.css.soccernow.domain.Team;
import pt.ul.fc.css.soccernow.dto.JogoDTO;
import pt.ul.fc.css.soccernow.service.JogoService;
import pt.ul.fc.css.soccernow.service.RefereeService;
import pt.ul.fc.css.soccernow.service.TeamService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;
import pt.ul.fc.css.soccernow.service.exceptions.NotFoundException;

@Controller
@RequestMapping("/web/jogos")
public class JogoWebController {

    private final JogoService   jogoService;
    private final TeamService   teamService;
    private final RefereeService refereeService;

    public JogoWebController(JogoService jogoService,
                             TeamService teamService,
                             RefereeService refereeService) {
        this.jogoService   = jogoService;
        this.teamService   = teamService;
        this.refereeService = refereeService;
    }

    /* Listar + filrar*/
    @GetMapping
    public String listJogos(
            @RequestParam(value = "realizados", required = false) Boolean realizados,
            @RequestParam(value = "aRealizar",  required = false) Boolean aRealizar,
            @RequestParam(value = "minGoals",   required = false) Integer minGoals,
            @RequestParam(value = "location",   required = false) String location,
            @RequestParam(value = "timeSlot",   required = false) String timeSlot,
            Model model) {

        /* Cuando no se manda ningún parámetro devolvemos todo. */
        boolean noFilters = realizados == null && aRealizar == null &&
                            minGoals == null && (location == null || location.isBlank()) &&
                            (timeSlot == null || timeSlot.isBlank());

        List<Jogo> jogos = noFilters
                ? jogoService.findAllJogos()
                : jogoService.filterJogos(realizados, aRealizar, minGoals, location, timeSlot);

        model.addAttribute("jogos", jogos.stream().map(this::toDTO).toList());

        /*  Guardamos los valores en el modelo para que el formulario
            mantenga el estado de los filtros.*/
        model.addAttribute("paramRealizados", realizados);
        model.addAttribute("paramARealizar",  aRealizar);
        model.addAttribute("paramMinGoals",   minGoals);
        model.addAttribute("paramLocation",   location);
        model.addAttribute("paramTimeSlot",   timeSlot);

        return "jogos/list";
    }

    /* ─────────────  CRUD NORMAL (create-save-edit-update-delete) ───────────── */
    /* No hay cambios en estos métodos — solo se han movido debajo por claridad */

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("jogoDTO", new JogoDTO());
        model.addAttribute("teams",    teamService.getAllTeams());
        model.addAttribute("arbitros", refereeService.findAllReferees());
        return "jogos/form";
    }

    /* ========== NUEVO: registrar Resultado ========== */
    @PostMapping("/resultado/{id}")
    public String saveResultado(@PathVariable Long id,
                                @RequestParam int homeScore,
                                @RequestParam int awayScore,
                                @RequestParam(required = false) Long winnerId,
                                RedirectAttributes flash) {
        try {
            jogoService.registrarResultado(id, homeScore, awayScore, winnerId);
            return "redirect:/web/jogos";
        } catch (ApplicationException | NotFoundException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
            return "redirect:/web/jogos";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Jogo jogo = jogoService.obterJogo(id)
                     .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado: " + id));
        model.addAttribute("jogoDTO", toDTO(jogo));
        model.addAttribute("teams",    teamService.getAllTeams());
        model.addAttribute("arbitros", refereeService.findAllReferees());
        return "jogos/form";
    }

    @PostMapping("/update/{id}")
    public String updateJogo(@PathVariable Long id,
                             @ModelAttribute("jogoDTO") JogoDTO dto,
                             Model model) {
        try {
            Jogo existing = jogoService.obterJogo(id)
                               .orElseThrow(() -> new NotFoundException("Juego no encontrado: " + id));

            if (dto.getDateTime() != null)                     existing.setDataHora(dto.getDateTime());
            if (dto.getLocation() != null && !dto.getLocation().isBlank())
                                                             existing.setLocal(dto.getLocation());
            existing.setAmigavel(dto.isAmigavel());

            Team home = teamService.getTeamById(dto.getHomeTeamId())
                         .orElseThrow(() -> new NotFoundException("Equipo local no encontrado"));
            Team away = teamService.getTeamById(dto.getAwayTeamId())
                         .orElseThrow(() -> new NotFoundException("Equipo visitante no encontrado"));
            existing.setHomeTeam(home);
            existing.setAwayTeam(away);

            Set<Referee> refs = refereeService.findAllByIds(dto.getArbitroIds());
            existing.setReferees(refs);

            if (dto.getPrimaryRefereeId() != null) {
                Referee principal = refereeService.getRefereeById(dto.getPrimaryRefereeId())
                                   .orElseThrow(() -> new NotFoundException("Árbitro principal no encontrado"));
                existing.setPrimaryReferee(principal);
            }

            jogoService.criarJogo(existing);   // reutiliza validaciones
            return "redirect:/web/jogos";
        } catch (ApplicationException | NotFoundException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("teams",    teamService.getAllTeams());
            model.addAttribute("arbitros", refereeService.findAllReferees());
            return "jogos/form";
        }
    }

    /* ========== NUEVO: mostrar formulario Resultado ========== */
    @GetMapping("/resultado/{id}")
    public String showResultadoForm(@PathVariable Long id, Model model) {
        Jogo j = jogoService.obterJogo(id)
                 .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado"));
        if (j.getResultado() != null)            // ya tiene marcador → vuelve al listado
            return "redirect:/web/jogos";

        // reutilizamos el mismo DTO pero solo usaremos homeScore / awayScore
        JogoDTO dto = toDTO(j);
        model.addAttribute("jogoDTO", dto);
        model.addAttribute("teams", List.of(j.getHomeTeam(), j.getAwayTeam()));
        return "jogos/resultado-form";           // (plantilla muy pequeña, ver abajo)
    }

    /* ======= MODIFICADO: borrar definitivamente ======= */
    @GetMapping("/delete/{id}")
    public String deleteJogo(@PathVariable Long id, RedirectAttributes flash) {
        try {
            jogoService.eliminarJogo(id);
        } catch (ApplicationException | NotFoundException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/web/jogos";
    }

    /* ═══ Helper DTO ═══ */
    private JogoDTO toDTO(Jogo j) {
        JogoDTO dto = new JogoDTO();
        dto.setId(j.getId());
        dto.setDateTime(j.getDateTime());
        dto.setLocation(j.getLocation());
        dto.setAmigavel(j.isAmigavel());
        dto.setHomeScore(j.getHomeScore());
        dto.setAwayScore(j.getAwayScore());
        dto.setHomeTeamId(j.getHomeTeam().getId());
        dto.setHomeTeamName(j.getHomeTeam().getName());
        dto.setAwayTeamId(j.getAwayTeam().getId());
        dto.setAwayTeamName(j.getAwayTeam().getName());
        dto.setCampeonatoId(j.getCampeonato() != null ? j.getCampeonato().getId() : null);
        dto.setArbitroIds(j.getReferees().stream().map(Referee::getId).collect(Collectors.toSet()));
        dto.setPrimaryRefereeId(j.getPrimaryReferee() != null ? j.getPrimaryReferee().getId() : null);
        return dto;
    }
}
