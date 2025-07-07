package pt.ul.fc.css.soccernow.service;

import org.springframework.stereotype.Service;
import pt.ul.fc.css.soccernow.domain.User;
import pt.ul.fc.css.soccernow.repository.UserRepository;

/**
 * Servicio de autenticación.
 * Comprueba que (a) el e-mail existe y (b) la contraseña coincide.
 * (En esta demo las contraseñas se guardan en texto plano;
 *  si las tienes con hash, haz el `matches()` correspondiente aquí).
 */
@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Devuelve true si email + password son válidos. */
    public boolean login(String email, String password) {
        return userRepository.findByEmailIgnoreCase(email)
               .map(u -> u.getPassword().equals(password))  // TODO hash/BCrypt si procede
               .orElse(false);
    }

    /* ------------------------------------------------------------------
       (¡opcional!) deja este método sólo si aún lo usan otros tests)      */
    @Deprecated
    public boolean loginMock(String email, String password) {
        return userRepository.existsByEmail(email);
    }
}
