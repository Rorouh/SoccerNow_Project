package pt.ul.fc.css.soccernow.repository;

import pt.ul.fc.css.soccernow.domain.Referee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefereeRepository extends JpaRepository<Referee, Long> {
}
