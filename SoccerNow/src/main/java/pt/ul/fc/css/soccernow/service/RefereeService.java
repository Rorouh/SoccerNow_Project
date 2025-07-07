// src/main/java/pt/ul/fc/css/soccernow/service/RefereeService.java
package pt.ul.fc.css.soccernow.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.soccernow.domain.Referee;
import pt.ul.fc.css.soccernow.repository.RefereeRepository;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Lógica de negocio asociada a la gestión de árbitros.
 */
@Service
public class RefereeService {

  private final RefereeRepository refereeRepository;

  public RefereeService(RefereeRepository refereeRepository) {
    this.refereeRepository = refereeRepository;
  }

  /* ════════════════  NUEVO  — creación rápida  ════════════════ */

  /**
   * Crea y persiste un árbitro partiendo de datos mínimos.
   *
   * @param name      Nombre
   * @param email     Email (único en la tabla <code>users</code>)
   * @param certified <code>true</code> si dispone de certificación oficial
   * @return árbitro guardado (con <code>id</code> generado)
   * @throws ApplicationException si el email ya existe
   */
  @Transactional
  public Referee createReferee(String name, String email, boolean certified) {

    boolean emailTaken = refereeRepository.findAll().stream()
        .anyMatch(r -> r.getEmail().equalsIgnoreCase(email));

    if (emailTaken) {
      throw new ApplicationException("Ya existe un árbitro con el email " + email);
    }

    Referee ref = new Referee();
    ref.setName(name);
    ref.setEmail(email);
    ref.setPassword("pass");      // en producción usar hashing
    ref.setRole("REFEREE");
    ref.setCertified(certified);

    return refereeRepository.save(ref);
  }

  /* ════════════════  CONSULTAS / FILTROS  ════════════════ */

  @Transactional(readOnly = true)
  public List<Referee> findAllReferees() {
    return refereeRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<Referee> findByName(String name) {
    return refereeRepository.findByNameContainingIgnoreCase(name);
  }

  @Transactional(readOnly = true)
  public List<Referee> findByMinGames(long minGames) {
    if (minGames < 0) {
      throw new ApplicationException("minGames no puede ser negativo.");
    }
    return refereeRepository.findRefereesWithMinGames(minGames);
  }

  @Transactional(readOnly = true)
  public Optional<Referee> getRefereeById(Long id) {
    return refereeRepository.findById(id);
  }

  public Set<Referee> findAllByIds(Set<Long> ids) {
    return refereeRepository.findAllById(ids).stream().collect(Collectors.toSet());
  }

  /**
   * Filtro combinado utilizado por la capa web.
   */
  @Transactional(readOnly = true)
  public List<Referee> filterReferees(String name, Integer minGames, Integer /*ignored*/ minCards) {
    List<Referee> all = refereeRepository.findAll();
    return all.stream()
        .filter(r -> name == null || r.getName().toLowerCase().contains(name.toLowerCase()))
        .filter(r -> minGames == null
                     || (r.getGamesAsPrimary().size()
                         + r.getGamesAsAssistant().size()) >= minGames)
        // minCards no está implementado todavía
        .toList();
  }
}
