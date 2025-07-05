// src/main/java/pt/ul/fc/css/soccernow/service/CartaoService.java
package pt.ul.fc.css.soccernow.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.soccernow.domain.Cartao;
import pt.ul.fc.css.soccernow.dto.CartaoCreateDTO;
import pt.ul.fc.css.soccernow.repository.CartaoRepository;
import pt.ul.fc.css.soccernow.repository.PlayerRepository;
import pt.ul.fc.css.soccernow.repository.JogoRepository;

import java.util.List;

@Service
public class CartaoService {

    private final CartaoRepository cartaoRepo;
    private final PlayerRepository  playerRepo;
    private final JogoRepository    jogoRepo;

    public CartaoService(CartaoRepository cartaoRepo,
                         PlayerRepository playerRepo,
                         JogoRepository jogoRepo) {
        this.cartaoRepo = cartaoRepo;
        this.playerRepo = playerRepo;
        this.jogoRepo   = jogoRepo;
    }

    @Transactional
    public Cartao create(CartaoCreateDTO dto) {
        Cartao c = new Cartao();
        c.setTipo(dto.getTipo());
        c.setPlayer(playerRepo.findById(dto.getPlayerId())
            .orElseThrow(() -> new RuntimeException("Player no encontrado: " + dto.getPlayerId())));
        c.setJogo(jogoRepo.findById(dto.getJogoId())
            .orElseThrow(() -> new RuntimeException("Jogo no encontrado: " + dto.getJogoId())));
        return cartaoRepo.save(c);
    }

    @Transactional(readOnly = true)
    public List<Cartao> findByJogo(Long jogoId) {
        return cartaoRepo.findByJogoId(jogoId);
    }

    @Transactional(readOnly = true)
    public List<Cartao> findByPlayer(Long playerId) {
        return cartaoRepo.findByPlayerId(playerId);
    }
}
