// src/main/java/pt/ul/fc/css/soccernow/controller/EstatisticaController.java
package pt.ul.fc.css.soccernow.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.dto.EstatisticaDTO;
import pt.ul.fc.css.soccernow.dto.EstatisticaCreateDTO;
import pt.ul.fc.css.soccernow.domain.Estatisticas;
import pt.ul.fc.css.soccernow.service.EstatisticaService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/estatisticas")
public class EstatisticaController {

    private final EstatisticaService service;

    public EstatisticaController(EstatisticaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EstatisticaDTO> create(@Valid @RequestBody EstatisticaCreateDTO dto) {
        Estatisticas e = service.create(dto);
        return ResponseEntity
            .created(URI.create("/api/estatisticas/" + e.getId()))
            .body(EstatisticaDTO.fromEntity(e));
    }

    @GetMapping("/jogo/{jogoId}")
    public List<EstatisticaDTO> getByJogo(@PathVariable Long jogoId) {
        return service.findByJogo(jogoId).stream()
                      .map(EstatisticaDTO::fromEntity)
                      .collect(Collectors.toList());
    }

    @GetMapping("/player/{playerId}")
    public List<EstatisticaDTO> getByPlayer(@PathVariable Long playerId) {
        return service.findByPlayer(playerId).stream()
                      .map(EstatisticaDTO::fromEntity)
                      .collect(Collectors.toList());
    }
}
