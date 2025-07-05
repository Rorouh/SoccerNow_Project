package pt.ul.fc.css.soccernow.repository;

import pt.ul.fc.css.soccernow.domain.Cartao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CartaoRepository extends JpaRepository<Cartao, Long> {
    List<Cartao> findByJogoId(Long jogoId);
    List<Cartao> findByPlayerId(Long playerId);
}
