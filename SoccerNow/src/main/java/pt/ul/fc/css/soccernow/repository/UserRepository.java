// src/main/java/pt/ul/fc/css/soccernow/repository/ResultadoRepository.java
package pt.ul.fc.css.soccernow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ul.fc.css.soccernow.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Spring Data JPA generará automáticamente esta consulta para comprobar si ya existe un usuario
     * con ese email.
     */
    boolean existsByEmail(String email);
    
    /** Busca usuarios cuyo nombre contenga (ignore mayúsculas/minúsculas) */
    List<User> findByNameContainingIgnoreCase(String name);

    /** Busca usuarios por rol ("PLAYER" o "REFEREE") */
    List<User> findByRole(String role);
}
