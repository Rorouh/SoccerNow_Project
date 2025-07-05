// src/main/java/pt/ul/fc/css/soccernow/controller/CartaoController.java
package pt.ul.fc.css.soccernow.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.dto.CartaoDTO;
import pt.ul.fc.css.soccernow.dto.CartaoCreateDTO;
import pt.ul.fc.css.soccernow.domain.Cartao;
import pt.ul.fc.css.soccernow.service.CartaoService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cartoes")
public class CartaoController {

    private final CartaoService service;

    public CartaoController(CartaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CartaoDTO> create(@Valid @RequestBody CartaoCreateDTO dto) {
        Cartao c = service.create(dto);
        return ResponseEntity
            .created(URI.create("/api/cartoes/" + c.getId()))
            .body(CartaoDTO.fromEntity(c));
    }

    @GetMapping("/jogo/{jogoId}")
    public List<CartaoDTO> getByJogo(@PathVariable Long jogoId) {
        return service.findByJogo(jogoId).stream()
                      .map(CartaoDTO::fromEntity)
                      .collect(Collectors.toList());
    }

    @GetMapping("/player/{playerId}")
    public List<CartaoDTO> getByPlayer(@PathVariable Long playerId) {
        return service.findByPlayer(playerId).stream()
                      .map(CartaoDTO::fromEntity)
                      .collect(Collectors.toList());
    }
}
