// src/main/java/pt/ul/fc/css/soccernow/controller/RefereeController.java
package pt.ul.fc.css.soccernow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.domain.Referee;
import pt.ul.fc.css.soccernow.dto.RefereeDTO;
import pt.ul.fc.css.soccernow.service.RefereeService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/referees")
public class RefereeController {

    private final RefereeService refereeService;

    public RefereeController(RefereeService refereeService) {
        this.refereeService = refereeService;
    }

    /**
     * GET /api/referees
     * Si no se envía ningún parámetro, devuelve todos.
     * Opciones de filtro:
     *   ?name=XYZ
     *   ?minGames=5
     */
    @GetMapping
    public ResponseEntity<List<RefereeDTO>> listReferees(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "minGames", required = false) Long minGames) {
        List<Referee> results;

        if (name != null) {
            results = refereeService.findByName(name);
        } else if (minGames != null) {
            try {
                results = refereeService.findByMinGames(minGames);
            } catch (ApplicationException ex) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            results = refereeService.findAllReferees();
        }

        List<RefereeDTO> dtos = results.stream()
                .map(r -> new RefereeDTO(
                        r.getId(),
                        r.getName(),
                        r.getEmail(),
                        r.isCertified()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
