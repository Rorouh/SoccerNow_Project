// src/main/java/pt/ul/fc/css/soccernow/repository/CampeonatoRepository.java
package pt.ul.fc.css.soccernow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.ul.fc.css.soccernow.domain.Campeonato;

import java.util.List;

@Repository
public interface CampeonatoRepository extends JpaRepository<Campeonato, Long> {

    /**
     * Buscar campeonatos cuyo nombre contenga (ignorando mayúsculas/minúsculas) el texto dado.
     * Esto cubre el caso K (búsqueda/filtrado por nombre).
     */
    List<Campeonato> findByNomeContainingIgnoreCase(String nome);

    @Query("""
       SELECT c 
       FROM Campeonato c 
       JOIN c.jogos j 
       WHERE j.resultado IS NOT NULL
       GROUP BY c
       HAVING COUNT(j) >= :minPlayed
       """)
    List<Campeonato> findByMinGamesPlayed(@Param("minPlayed") long minPlayed);

    @Query("""
       SELECT c 
       FROM Campeonato c 
       JOIN c.jogos j 
       WHERE j.resultado IS NULL AND j.cancelado = false
       GROUP BY c
       HAVING COUNT(j) >= :minPending
       """)
    List<Campeonato> findByMinGamesPending(@Param("minPending") long minPending);


}
