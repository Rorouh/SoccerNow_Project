package pt.ul.fc.css.soccernow.repository;

import pt.ul.fc.css.soccernow.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    // Busca todos los jugadores cuyo nombre contenga 'name', ignorando mayúsculas/minúsculas
    List<Player> findByNameContainingIgnoreCase(String name);
}
