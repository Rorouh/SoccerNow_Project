// src/main/java/pt/ul/fc/css/soccernow/repository/JogoRepository.java
package pt.ul.fc.css.soccernow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.ul.fc.css.soccernow.domain.Jogo;

import java.util.List;

@Repository
public interface JogoRepository extends JpaRepository<Jogo, Long> { 

    List<Jogo> findByResultadoIsNotNull();             // played
    
    List<Jogo> findByCanceladoTrue();                  // cancelled

    @Query("SELECT j FROM Jogo j WHERE j.resultado IS NULL AND j.cancelado = false")
    List<Jogo> findPendingGames();

    /**
     * Filtrar por location
     */
    List<Jogo> findByLocationContainingIgnoreCase(String location);

    @Query("""
       SELECT j 
       FROM Jogo j 
       WHERE FUNCTION('HOUR', j.dateTime) BETWEEN :startHour AND :endHour
       """)
    List<Jogo> findByHourBetween(@Param("startHour") int startHour, @Param("endHour")   int endHour);

    @Query("""
       SELECT j 
       FROM Jogo j 
       WHERE COALESCE(j.homeScore,0) + COALESCE(j.awayScore,0) >= :minGoals
       """)
    List<Jogo> findGamesWithMinGoals(@Param("minGoals") int minGoals);

}
