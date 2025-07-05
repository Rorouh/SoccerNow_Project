// src\main\java\pt\ul\fc\css\soccernow\repository\TeamRepository.java
package pt.ul.fc.css.soccernow.repository;

import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List; 
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByName(String name);

    // Nueva búsqueda por fragmento de nombre:
    List<Team> findByNameContainingIgnoreCase(String name);

    @Query("SELECT t FROM Team t WHERE size(t.players) >= :minPlayers")
    List<Team> findByMinPlayers(@Param("minPlayers") int minPlayers);

    @Query("""
       SELECT t 
       FROM Team t 
       JOIN Jogo j 
         ON (j.homeTeam = t AND j.homeScore > j.awayScore) 
         OR (j.awayTeam = t AND j.awayScore > j.homeScore)
       GROUP BY t
       HAVING COUNT(j) >= :minWins
       """)
    List<Team> findTeamsWithMinWins(@Param("minWins") long minWins);

    @Query("""
       SELECT t
       FROM Team t
       WHERE NOT EXISTS (
         SELECT p FROM Player p
         WHERE p MEMBER OF t.players 
           AND p.preferredPosition = :position
       )
       """)
    List<Team> findTeamsWithNoPlayerInPosition(@Param("position") Player.PreferredPosition position);

}
