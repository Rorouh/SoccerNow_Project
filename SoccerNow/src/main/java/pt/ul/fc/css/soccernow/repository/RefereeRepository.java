// src/main/java/pt/ul/fc/css/soccernow/repository/RefereeRepository.java
package pt.ul.fc.css.soccernow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pt.ul.fc.css.soccernow.domain.Referee;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public interface RefereeRepository extends JpaRepository<Referee, Long> {

    /**
     * Buscar árbitros por nombre (contenga, ignore mayúsculas/minúsculas).
     */
    List<Referee> findByNameContainingIgnoreCase(String name);

    /**
     * Filtrar árbitros con al menos `minGames` partidos arbitrados.
     * Asume que en Referee existen mapeos inversos:
     *   @OneToMany(mappedBy = "primaryReferee") Set<Jogo> gamesAsPrimary;
     *   @ManyToMany(mappedBy = "referees")   Set<Jogo> gamesAsAssistant;
     */
    @Query("""
           SELECT r 
           FROM Referee r 
           LEFT JOIN r.gamesAsPrimary gp 
           LEFT JOIN r.gamesAsAssistant ga 
           GROUP BY r 
           HAVING (COALESCE(COUNT(gp),0) + COALESCE(COUNT(ga),0)) >= :minGames
           """)
    List<Referee> findRefereesWithMinGames(@Param("minGames") long minGames);
}
