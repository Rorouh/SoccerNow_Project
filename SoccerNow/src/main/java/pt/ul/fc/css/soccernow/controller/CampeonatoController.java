// src/main/java/pt/ul/fc/css/soccernow/controller/CampeonatoController.java
package pt.ul.fc.css.soccernow.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.domain.Campeonato;
import pt.ul.fc.css.soccernow.domain.Team;
import pt.ul.fc.css.soccernow.dto.CampeonatoDTO;
import pt.ul.fc.css.soccernow.service.CampeonatoService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;




@RestController
@RequestMapping("/api/campeonatos")
public class CampeonatoController {

    /**
     * GET /api/campeonatos/filter
     * Filtros avançados: nome, team, minGamesPlayed, minGamesPending
     */
    @GetMapping("/filter")
    public ResponseEntity<List<CampeonatoDTO>> filterCampeonatos(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "team", required = false) String team,
            @RequestParam(value = "minGamesPlayed", required = false) Integer minGamesPlayed,
            @RequestParam(value = "minGamesPending", required = false) Integer minGamesPending
    ) {
        List<Campeonato> results = campeonatoService.filterCampeonatos(nome, team, minGamesPlayed, minGamesPending);
        List<CampeonatoDTO> dtos = results.stream()
                .map(c -> new CampeonatoDTO(
                        c.getId(),
                        c.getNome()
                        // Adicione outros campos necessários do DTO
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private final CampeonatoService campeonatoService;

    public CampeonatoController(CampeonatoService campeonatoService) {
        this.campeonatoService = campeonatoService;
    }

    /**
     * POST /api/campeonatos
     * Crea un nuevo campeonato.
     */
    @PostMapping
    public ResponseEntity<CampeonatoDTO> create(@Valid @RequestBody CampeonatoDTO dto) {
        try {
            Campeonato creado = campeonatoService.createCampeonato(dto);
            CampeonatoDTO salida = new CampeonatoDTO(
                    creado.getId(),
                    creado.getNome(),
                    creado.getModalidade(),
                    creado.getFormato(),
                    creado.getParticipantes().stream().map(team -> team.getId()).collect(Collectors.toSet())
            );
            return ResponseEntity
                    .created(URI.create("/api/campeonatos/" + creado.getId()))
                    .body(salida);
        } catch (ApplicationException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/campeonatos/{id}
     * Busca un campeonato por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CampeonatoDTO> getById(@PathVariable Long id) {
        Optional<Campeonato> opt = campeonatoService.getCampeonatoById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Campeonato c = opt.get();
        CampeonatoDTO salida = new CampeonatoDTO(
                c.getId(),
                c.getNome(),
                c.getModalidade(),
                c.getFormato(),
                c.getParticipantes().stream().map(team -> team.getId()).collect(Collectors.toSet())
        );
        return ResponseEntity.ok(salida);
    }

    /**
     * GET /api/campeonatos?nome=XYZ
     * Busca campeonatos cuyo nombre contenga “XYZ”. Si no se pasa “nome”, devuelve todos.
     */
    @GetMapping
    public ResponseEntity<List<CampeonatoDTO>> findByNome(
            @RequestParam(value = "nome", required = false) String nome) {
        List<Campeonato> encontrados;
        if (nome != null && !nome.isBlank()) {
            encontrados = campeonatoService.findCampeonatosByNome(nome);
        } else {
            encontrados = campeonatoService.findCampeonatosByNome("");
        }
        List<CampeonatoDTO> dtos = encontrados.stream()
                .map(c -> new CampeonatoDTO(
                        c.getId(),
                        c.getNome(),
                        c.getModalidade(),
                        c.getFormato(),
                        c.getParticipantes().stream().map(team -> team.getId()).collect(Collectors.toSet())
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * PUT /api/campeonatos/{id}
     * Actualiza nombre, modalidad, formato o participantes de un campeonato.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CampeonatoDTO> update(
            @PathVariable Long id,
            @RequestBody CampeonatoDTO dto) {
        try {
            Optional<Campeonato> opt = campeonatoService.updateCampeonato(id, dto);
            if (opt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Campeonato c = opt.get();
            CampeonatoDTO salida = new CampeonatoDTO(
                    c.getId(),
                    c.getNome(),
                    c.getModalidade(),
                    c.getFormato(),
                    c.getParticipantes().stream().map(team -> team.getId()).collect(Collectors.toSet())
            );
            return ResponseEntity.ok(salida);
        } catch (ApplicationException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE /api/campeonatos/{id}
     * Elimina un campeonato (solo si no tiene juegos asociados).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            boolean eliminado = campeonatoService.deleteCampeonato(id);
            if (eliminado) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ApplicationException ex) {
            // Por ejemplo: “No se puede eliminar un campeonato con juegos asociados.”
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/campeonatos")
    public ResponseEntity<List<CampeonatoDTO>> listCampeonatos(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "minGamesPlayed", required = false) Long minGamesPlayed,
            @RequestParam(value = "minGamesPending", required = false) Long minGamesPending) {

        List<Campeonato> results;

        if (nome != null) {
            results = campeonatoService.findByNome(nome);
        } else if (minGamesPlayed != null) {
            results = campeonatoService.findByMinGamesPlayed(minGamesPlayed);
        } else if (minGamesPending != null) {
            results = campeonatoService.findByMinGamesPending(minGamesPending);
        } else {
            results = campeonatoService.getAllCampeonatos();
        }

        List<CampeonatoDTO> dtos = results.stream()
                .map(c -> new CampeonatoDTO(
                        c.getId(),
                        c.getNome(),
                        c.getModalidade(),
                        c.getFormato(),
                        c.getParticipantes().stream().map(Team::getId).collect(Collectors.toSet())
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

}
