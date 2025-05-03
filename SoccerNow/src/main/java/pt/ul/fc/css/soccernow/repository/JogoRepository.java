package pt.ul.fc.css.soccernow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ul.fc.css.soccernow.domain.Jogo;

public interface JogoRepository extends JpaRepository<Jogo, Long> {
}
