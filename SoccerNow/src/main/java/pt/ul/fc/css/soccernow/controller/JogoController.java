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
import java.util.stream.Collectors;

import java.util.List;

@RestController
@RequestMapping("/api/jogos")
public class JogoController {

    /**
     * GET /api/jogos/filter
     * Filtros avançados: realizados, aRealizar, minGoals, location, timeSlot
     */
    @GetMapping("/filter")
    public ResponseEntity<List<JogoDTO>> filterJogos(
            @RequestParam(value = "realizados", required = false) Boolean realizados,
            @RequestParam(value = "aRealizar", required = false) Boolean aRealizar,
            @RequestParam(value = "minGoals", required = false) Integer minGoals,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "timeSlot", required = false) String timeSlot
    ) {
        List<Jogo> results = jogoService.filterJogos(realizados, aRealizar, minGoals, location, timeSlot);
        List<JogoDTO> dtos = results.stream()
                .map(j -> new JogoDTO(
                        j.getId(),
                        j.getDataHora(),
                        j.getLocal()
                        // Adicione outros campos necessários do DTO
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
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

    /** -------------- NUEVO ENDPOINT: Cancelar Jogo -------------- */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarJogo(@PathVariable Long id) {
        try {
            jogoService.cancelarJogo(id);
            // 204 No Content cuando se cancela correctamente
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            // 404 si no existe el juego
            return ResponseEntity.notFound().build();
        } catch (ApplicationException e) {
            // 400 si ya tiene resultado o ya está cancelado
            return ResponseEntity.badRequest().build();
        }
    }
    /** ------------------------------------------------------------ */

    @GetMapping("/jogos")
    public ResponseEntity<List<JogoDTO>> listJogos(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "timeSlot", required = false) String timeSlot,
            @RequestParam(value = "minGoals", required = false) Integer minGoals) {

        List<Jogo> results;

        if ("played".equalsIgnoreCase(status)) {
            results = jogoService.findPlayedGames();
        } else if ("cancelled".equalsIgnoreCase(status)) {
            results = jogoService.findCancelledGames();
        } else if ("pending".equalsIgnoreCase(status)) {
            results = jogoService.findPendingGames();
        } else if (location != null) {
            results = jogoService.findByLocation(location);
        } else if (timeSlot != null) {
            results = jogoService.findByTimeSlot(timeSlot);
        } else if (minGoals != null) {
            results = jogoService.findByMinGoals(minGoals);
        } else {
            results = jogoService.findAllJogos();
        }

        List<JogoDTO> dtos = results.stream()
                .map(j -> {
                    JogoDTO d = new JogoDTO();
                    d.setId(j.getId());
                    d.setDateTime(j.getDateTime());
                    d.setLocation(j.getLocation());
                    d.setAmigavel(j.isAmigavel());
                    d.setHomeScore(j.getHomeScore());
                    d.setAwayScore(j.getAwayScore());
                    d.setHomeTeamId(j.getHomeTeam().getId());
                    d.setAwayTeamId(j.getAwayTeam().getId());
                    d.setCampeonatoId(j.getCampeonato() != null ? j.getCampeonato().getId() : null);
                    d.setArbitroIds(j.getReferees().stream().map(Referee::getId).collect(Collectors.toSet()));
                    d.setPrimaryRefereeId(j.getPrimaryReferee() != null ? j.getPrimaryReferee().getId() : null);
                    return d;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
    
}
