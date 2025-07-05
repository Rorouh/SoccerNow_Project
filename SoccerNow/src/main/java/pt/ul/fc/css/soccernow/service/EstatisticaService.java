// src/main/java/pt/ul/fc/css/soccernow/service/EstatisticaService.java
package pt.ul.fc.css.soccernow.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.soccernow.domain.Estatisticas;
import pt.ul.fc.css.soccernow.dto.EstatisticaCreateDTO;
import pt.ul.fc.css.soccernow.repository.EstatisticasRepository;
import pt.ul.fc.css.soccernow.repository.PlayerRepository;
import pt.ul.fc.css.soccernow.repository.JogoRepository;

import java.util.List;

@Service
public class EstatisticaService {

    private final EstatisticasRepository estatRepo;
    private final PlayerRepository        playerRepo;
    private final JogoRepository          jogoRepo;

    public EstatisticaService(EstatisticasRepository estatRepo,
                              PlayerRepository playerRepo,
                              JogoRepository jogoRepo) {
        this.estatRepo  = estatRepo;
        this.playerRepo = playerRepo;
        this.jogoRepo   = jogoRepo;
    }

    @Transactional
    public Estatisticas create(EstatisticaCreateDTO dto) {
        Estatisticas e = new Estatisticas();
        e.setGols(dto.getGols());
        e.setPlayer(playerRepo.findById(dto.getPlayerId())
            .orElseThrow(() -> new RuntimeException("Player no encontrado: " + dto.getPlayerId())));
        e.setJogo(jogoRepo.findById(dto.getJogoId())
            .orElseThrow(() -> new RuntimeException("Jogo no encontrado: " + dto.getJogoId())));
        return estatRepo.save(e);
    }

    @Transactional(readOnly = true)
    public List<Estatisticas> findByJogo(Long jogoId) {
        return estatRepo.findByJogoId(jogoId);
    }

    @Transactional(readOnly = true)
    public List<Estatisticas> findByPlayer(Long playerId) {
        return estatRepo.findByPlayerId(playerId);
    }
}
