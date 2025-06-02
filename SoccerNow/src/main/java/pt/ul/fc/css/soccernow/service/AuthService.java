package pt.ul.fc.css.soccernow.service;

import org.springframework.stereotype.Service;
import pt.ul.fc.css.soccernow.repository.UserRepository;

/**
 * Servicio que implementa un login “mock”. 
 * Simplemente comprobamos si existe un usuario con ese email en la BD.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * “Logueo mock”:
     * - Devuelve true si existe un usuario con ese email (ignoramos la contraseña).
     * - Devuelve false en caso contrario.
     */
    public boolean loginMock(String email, String password) {
        // No validamos password: basta con que exista el email.
        return userRepository.existsByEmail(email);
    }
}
