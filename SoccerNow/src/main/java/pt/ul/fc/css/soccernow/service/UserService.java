// src/main/java/pt/ul/fc/css/soccernow/service/UserService.java
package pt.ul.fc.css.soccernow.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.Referee;
import pt.ul.fc.css.soccernow.domain.User;
import pt.ul.fc.css.soccernow.dto.UserDTO;
import pt.ul.fc.css.soccernow.dto.UserDTO.Role;
import pt.ul.fc.css.soccernow.repository.UserRepository;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.util.Optional;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User createUser(UserDTO dto) {
        // 1) Validar que no exista otro usuario con el mismo email
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ApplicationException("Ya existe un usuario con ese email.");
        }

        // 2) Según el role, construir la subclase correcta
        User entidad;
        if (dto.getRole() == Role.PLAYER) {
            // 'preferredPosition' *DEBE* venir no nulo
            if (dto.getPreferredPosition() == null) {
                throw new ApplicationException("El campo 'preferredPosition' es obligatorio para un PLAYER.");
            }
            Player jugador = new Player(
                dto.getName(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getPreferredPosition()
            );
            entidad = jugador;
        } else {
            // REFEREE
            // 'certified' *DEBE* venir no nulo
            if (dto.getCertified() == null) {
                throw new ApplicationException("El campo 'certified' es obligatorio para un REFEREE.");
            }
            Referee arbitro = new Referee(
                dto.getName(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getCertified()
            );
            entidad = arbitro;
        }

        // 3) Guardamos en BD. JPA asigna id automáticamente.
        return userRepository.save(entidad);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public Optional<User> updateUser(Long id, UserDTO dto) {
        return userRepository.findById(id).map(existing -> {
            // --- 1) Campos básicos “fusión” solo si vienen en el DTO (no nulos) ---
            if (dto.getName() != null && !dto.getName().isBlank()) {
                existing.setName(dto.getName());
            }
            if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
                existing.setEmail(dto.getEmail());
            }
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                existing.setPassword(dto.getPassword());
            }

            // --- 2) ¿Cambia o mantiene el rol? ---
            if (dto.getRole() != null) {
                if (!dto.getRole().name().equals(existing.getRole())) {
                    // Prohibimos cambiar tipo de usuario con un simple PUT
                    throw new ApplicationException("No está permitido cambiar el tipo de usuario (Player <-> Referee) con PUT.");
                }
            }

            // --- 3) Campos específicos según tipo existente ---
            if (existing instanceof Player) {
                Player p = (Player) existing;
                // Si mandan preferredPosition, lo actualizamos:
                if (dto.getPreferredPosition() != null) {
                    p.setPreferredPosition(dto.getPreferredPosition());
                }
                // Si mandan certified (no aplica a Player), ignoramos.
            } else if (existing instanceof Referee) {
                Referee r = (Referee) existing;
                // Si mandan certified, lo actualizamos:
                if (dto.getCertified() != null) {
                    r.setCertified(dto.getCertified());
                }
                // Si mandan preferredPosition (no aplica a Referee), ignoramos.
            }

            // --- 4) Guardamos cambios ---
            return userRepository.save(existing);
        });
    }

    @Transactional
    public boolean deleteUser(Long id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) {
            return false;
        }
        userRepository.delete(opt.get());
        return true;
    }
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
