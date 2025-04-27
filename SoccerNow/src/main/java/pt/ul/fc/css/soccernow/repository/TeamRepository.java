package pt.ul.fc.css.soccernow.repository;

import pt.ul.fc.css.soccernow.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
}
