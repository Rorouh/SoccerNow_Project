package pt.ul.fc.css.soccernow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ul.fc.css.soccernow.domain.Resultado;

public interface ResultadoRepository extends JpaRepository<Resultado, Long> {
}
