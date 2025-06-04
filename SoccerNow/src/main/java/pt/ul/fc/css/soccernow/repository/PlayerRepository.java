package pt.ul.fc.css.soccernow.repository;

import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.User.PreferredPosition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    /**
     * Busca todos los jugadores cuyo nombre contenga 'name', ignorando mayúsculas/minúsculas
     */
    List<Player> findByNameContainingIgnoreCase(String name);

    /**
     * Comprueba si existe un jugador con ese email.
     * Spring Data JPA generará la consulta automáticamente.
     */
    boolean existsByEmail(String email);

    /**
     * Filtra por posicion
     * @param position
     * @return
     */
    List<Player> findByPreferredPosition(PreferredPosition position);

    /**
     * Busca jogadores com pelo menos `minGames` jogos disputados.
     * Considera jogos com resultado OU amistosos não cancelados.
     */
    @Query("""
       SELECT p 
       FROM Player p 
       JOIN p.teams t 
       JOIN Jogo j ON (j.homeTeam = t OR j.awayTeam = t)
       WHERE j.cancelado = false
         AND (j.resultado IS NOT NULL OR j.amigavel = true)
       GROUP BY p
       HAVING COUNT(DISTINCT j) >= :minGames
       """)
    List<Player> findPlayersWithMinGames(@Param("minGames") long minGames);

    @Query("""
       SELECT p 
       FROM Player p 
       JOIN Estatisticas e ON e.player = p
       GROUP BY p
       HAVING SUM(e.gols) >= :minGoals
       """)
    List<Player> findPlayersWithMinGoals(@Param("minGoals") long minGoals);

    @Query("""
       SELECT p 
       FROM Player p 
       JOIN Cartao c ON c.player = p
       WHERE c.tipo = 'ROJA'
       GROUP BY p
       HAVING COUNT(c) >= :minRedCards
       """)
    List<Player> findPlayersWithMinRedCards(@Param("minRedCards") long minRedCards);
}
