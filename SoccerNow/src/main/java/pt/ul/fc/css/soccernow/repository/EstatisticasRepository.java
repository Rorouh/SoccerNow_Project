package pt.ul.fc.css.soccernow.repository;

import pt.ul.fc.css.soccernow.domain.Estatisticas;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EstatisticasRepository extends JpaRepository<Estatisticas, Long> {
    List<Estatisticas> findByJogoId(Long jogoId);
    List<Estatisticas> findByPlayerId(Long playerId);
}
