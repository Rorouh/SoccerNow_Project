package pt.ul.fc.css.soccernow.repository;

import pt.ul.fc.css.soccernow.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; 
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByName(String name);
    // Nueva búsqueda por fragmento de nombre:
    List<Team> findByNameContainingIgnoreCase(String name);
}
