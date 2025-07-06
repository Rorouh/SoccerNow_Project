// src/main/java/pt/ul/fc/css/soccernow/controller/CampeonatoWebController.java
package pt.ul.fc.css.soccernow.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.domain.Campeonato;
import pt.ul.fc.css.soccernow.domain.Team;
import pt.ul.fc.css.soccernow.dto.CampeonatoDTO;
import pt.ul.fc.css.soccernow.service.CampeonatoService;
import pt.ul.fc.css.soccernow.service.TeamService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web/campeonatos")
public class CampeonatoWebController {

    private final CampeonatoService campeonatoService;
    private final TeamService teamService;

    public CampeonatoWebController(CampeonatoService campeonatoService,
                                  TeamService teamService) {
        this.campeonatoService = campeonatoService;
        this.teamService = teamService;
    }

    /**
     * GET /web/campeonatos
     * Lista todos los campeonatos (sin filtros).
     */
    @GetMapping
    public String listCampeonatos(Model model) {
        List<Campeonato> campeonatos = campeonatoService.getAllCampeonatos();
        List<CampeonatoDTO> dtos = campeonatos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        model.addAttribute("campeonatos", dtos);
        return "campeonatos/list";
    }

    /**
     * GET /web/campeonatos/create
     * Muestra formulario vacío para crear un campeonato.
     * Añade lista de equipos disponibles al modelo.
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("campeonatoDTO", new CampeonatoDTO());
        model.addAttribute("teams", teamService.getAllTeams());
        return "campeonatos/form";
    }

    /**
     * POST /web/campeonatos/save
     * Crea un nuevo campeonato con la lista de equipos seleccionada.
     */
    @PostMapping("/save")
    public String saveCampeonato(@Valid @ModelAttribute("campeonatoDTO") CampeonatoDTO dto,
                                 Model model) {
        try {
            campeonatoService.createCampeonato(dto);
            return "redirect:/web/campeonatos";
        } catch (ApplicationException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("teams", teamService.getAllTeams());
            return "campeonatos/form";
        }
    }

    /**
     * GET /web/campeonatos/edit/{id}
     * Muestra formulario con datos de un campeonato para editar.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Campeonato c = campeonatoService.getCampeonatoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Campeonato no encontrado: " + id));
        CampeonatoDTO dto = toDTO(c);
        model.addAttribute("campeonatoDTO", dto);
        model.addAttribute("teams", teamService.getAllTeams());
        return "campeonatos/form";
    }

    /**
     * POST /web/campeonatos/update/{id}
     * Actualiza un campeonato existente.
     */
    @PostMapping("/update/{id}")
    public String updateCampeonato(@PathVariable Long id,
                                   @ModelAttribute("campeonatoDTO") CampeonatoDTO dto,
                                   Model model) {
        try {
            campeonatoService.updateCampeonato(id, dto);
            return "redirect:/web/campeonatos";
        } catch (ApplicationException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("teams", teamService.getAllTeams());
            return "campeonatos/form";
        }
    }

    /**
     * GET /web/campeonatos/delete/{id}
     * Elimina un campeonato si no tiene juegos asociados.
     */
    @GetMapping("/delete/{id}")
    public String deleteCampeonato(@PathVariable Long id, Model model) {
        try {
            campeonatoService.deleteCampeonato(id);
            return "redirect:/web/campeonatos";
        } catch (ApplicationException ex) {
            model.addAttribute("error", ex.getMessage());
            List<CampeonatoDTO> dtos = campeonatoService.getAllCampeonatos()
                    .stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            model.addAttribute("campeonatos", dtos);
            return "campeonatos/list";
        }
    }

    /**
     * GET /web/campeonatos/filters
     * Filtros avançados: nome, team, minGamesPlayed, minGamesPending
     * Parâmetros opcionais: nome, team, minGamesPlayed, minGamesPending.
     */
    @GetMapping("/filters")
    public String filterCampeonatos(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "team", required = false) String team,
            @RequestParam(value = "minGamesPlayed", required = false) Integer minGamesPlayed,
            @RequestParam(value = "minGamesPending", required = false) Integer minGamesPending,
            Model model
    ) {
        List<Campeonato> results = campeonatoService.filterCampeonatos(nome, team, minGamesPlayed, minGamesPending);
        List<CampeonatoDTO> dtos = results.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        model.addAttribute("campeonatos", dtos);
        return "campeonatos/list";
    }

    // Helper: convierte Campeonato a CampeonatoDTO
    private CampeonatoDTO toDTO(Campeonato c) {
        return new CampeonatoDTO(
            c.getId(),
            c.getNome(),
            c.getModalidade(),
            c.getFormato(),
            c.getParticipantes().stream().map(Team::getId).collect(Collectors.toSet())
        );
    }
}
