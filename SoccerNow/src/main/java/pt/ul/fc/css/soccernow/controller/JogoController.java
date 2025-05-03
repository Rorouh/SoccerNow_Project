package pt.ul.fc.css.soccernow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.domain.Jogo;
import pt.ul.fc.css.soccernow.domain.Resultado;
import pt.ul.fc.css.soccernow.service.JogoService;

import java.net.URI;

@RestController
@RequestMapping("/api/jogos")
public class JogoController {
    @Autowired
    private JogoService jogoService;

    @PostMapping
    public ResponseEntity<Jogo> criarJogo(@RequestBody Jogo jogo) {
        Jogo criado = jogoService.criarJogo(jogo);
        return ResponseEntity.created(URI.create("/api/jogos/" + criado.getId())).body(criado);
    }

    @PostMapping("/{id}/resultado")
    public ResponseEntity<Resultado> registarResultado(@PathVariable Long id, @RequestBody Resultado resultado) {
        Resultado res = jogoService.registarResultado(id, resultado);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Jogo> obterJogo(@PathVariable Long id) {
        return jogoService.obterJogo(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
