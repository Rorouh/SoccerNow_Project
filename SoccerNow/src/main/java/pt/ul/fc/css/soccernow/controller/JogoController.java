// src/main/java/pt/ul/fc/css/soccernow/controller/JogoController.java
package pt.ul.fc.css.soccernow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.domain.Jogo;
import pt.ul.fc.css.soccernow.domain.Referee;
import pt.ul.fc.css.soccernow.domain.Resultado;
import pt.ul.fc.css.soccernow.dto.JogoDTO;
import pt.ul.fc.css.soccernow.service.JogoService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;
import pt.ul.fc.css.soccernow.service.exceptions.NotFoundException;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/jogos")
public class JogoController {

    private final JogoService jogoService;

    @Autowired
    public JogoController(JogoService jogoService) {
        this.jogoService = jogoService;
    }

    /**
     * GET /api/jogos/filter
     * Filtros avançados: realizados, aRealizar, minGoals, location, timeSlot
     */
    @GetMapping("/filter")
    public ResponseEntity<List<JogoDTO>> filterJogos(
            @RequestParam(required = false) Boolean realizados,
            @RequestParam(required = false) Boolean aRealizar,
            @RequestParam(required = false) Integer minGoals,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String timeSlot
    ) {
        var resultados = jogoService.filterJogos(realizados, aRealizar, minGoals, location, timeSlot);
        var dtos = resultados.stream().map(JogoDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<?> criarJogo(@Valid @RequestBody JogoCreateDTO dto) {
        try {
            Jogo criado = jogoService.criarJogo(dto);
            return ResponseEntity
                .created(URI.create("/api/jogos/" + criado.getId()))
                .body(JogoDTO.fromEntity(criado));
        } catch (NotFoundException | ApplicationException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

     @PostMapping("/{id}/resultado")
    public ResponseEntity<?> registarResultado(
            @PathVariable Long id,
            @RequestBody Resultado resultado
    ) {
        try {
            Resultado res = jogoService.registarResultado(id, resultado);
            return ResponseEntity.ok(res);
        } catch (NotFoundException | ApplicationException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<JogoDTO> obterJogo(@PathVariable Long id) {
        return jogoService.obterJogo(id)
                .map(j -> ResponseEntity.ok(JogoDTO.fromEntity(j)))
                .orElse(ResponseEntity.notFound().build());
    }

    /** -------------- NUEVO ENDPOINT: Cancelar Jogo -------------- */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarJogo(@PathVariable Long id) {
        try {
            jogoService.cancelarJogo(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ApplicationException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    /** ------------------------------------------------------------ */

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJogo(
            @PathVariable Long id,
            @Valid @RequestBody JogoUpdateDTO dto
    ) {
        try {
            var opt = jogoService.updateJogo(id, dto);
            return opt
                .map(j -> ResponseEntity.ok(JogoDTO.fromEntity(j)))
                .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (ApplicationException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * GET /api/jogos
     * Parámetros opcionales:
     * - status = played|cancelled|pending
     * - location = fragmento de texto
     * - timeSlot = morning|afternoon|night
     * - minGoals = mínimo total de goles
     *
     * Internamente delega en searchGames(...) del servicio.
     */
    @GetMapping("/jogos")
    public ResponseEntity<List<JogoDTO>> listJogos(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String timeSlot,
            @RequestParam(required = false) Integer minGoals
    ) {
        var jogos = jogoService.searchGames(status, location, timeSlot, minGoals);
        var dtos = jogos.stream().map(JogoDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
