// src/main/java/pt/ul/fc/css/soccernow/service/RefereeService.java
package pt.ul.fc.css.soccernow.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.soccernow.domain.Referee;
import pt.ul.fc.css.soccernow.repository.RefereeRepository;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.util.List;

@Service
public class RefereeService {

    private final RefereeRepository refereeRepository;

    public RefereeService(RefereeRepository refereeRepository) {
        this.refereeRepository = refereeRepository;
    }

    /**
     * Devuelve todos los árbitros.
     */
    @Transactional(readOnly = true)
    public List<Referee> findAllReferees() {
        return refereeRepository.findAll();
    }

    /**
     * Filtrar por nombre (contenga, ignorando mayúsculas/minúsculas).
     */
    @Transactional(readOnly = true)
    public List<Referee> findByName(String name) {
        return refereeRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Filtrar por número mínimo de partidos arbitrados (suma de roles “primario” y “asistente”).
     * Lanza ApplicationException si el parámetro es negativo.
     */
    @Transactional(readOnly = true)
    public List<Referee> findByMinGames(long minGames) {
        if (minGames < 0) {
            throw new ApplicationException("minGames no puede ser negativo.");
        }
        return refereeRepository.findRefereesWithMinGames(minGames);
    }

    // (Opcional) si luego agregas tarjetas a Referee, podrías añadir aquí:
    // public List<Referee> findByMinCards(long minCards) { ... }
}
