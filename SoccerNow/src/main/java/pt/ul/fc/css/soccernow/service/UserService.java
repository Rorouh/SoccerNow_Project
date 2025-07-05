// src/main/java/pt/ul/fc/css/soccernow/service/UserService.java
package pt.ul.fc.css.soccernow.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.Referee;
import pt.ul.fc.css.soccernow.domain.User;
import pt.ul.fc.css.soccernow.dto.UserDTO;
import pt.ul.fc.css.soccernow.dto.UserCreateDTO;
import pt.ul.fc.css.soccernow.dto.UserUpdateDTO;
import pt.ul.fc.css.soccernow.repository.UserRepository;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User createUser(UserCreateDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ApplicationException("Ya existe un usuario con ese email.");
        }
        User entidad;
        if (dto.getRole() == UserDTO.Role.PLAYER) {
            if (dto.getPreferredPosition() == null) {
                throw new ApplicationException("El campo 'preferredPosition' es obligatorio para un PLAYER.");
            }
            entidad = new Player(
                dto.getName(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getPreferredPosition()
            );
        } else {
            if (dto.getCertified() == null) {
                throw new ApplicationException("El campo 'certified' es obligatorio para un REFEREE.");
            }
            entidad = new Referee(
                dto.getName(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getCertified()
            );
        }
        return userRepository.save(entidad);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public Optional<User> updateUser(Long id, UserUpdateDTO dto) {
        return userRepository.findById(id).map(existing -> {
            if (dto.getName() != null)     existing.setName(dto.getName());
            if (dto.getEmail() != null)    existing.setEmail(dto.getEmail());
            if (dto.getPassword() != null) existing.setPassword(dto.getPassword());
            // No permitimos cambiar rol...
            if (existing instanceof Player p && dto.getPreferredPosition() != null) {
                p.setPreferredPosition(dto.getPreferredPosition());
            }
            if (existing instanceof Referee r && dto.getCertified() != null) {
                r.setCertified(dto.getCertified());
            }
            return userRepository.save(existing);
        });
    }

    @Transactional
    public boolean deleteUser(Long id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) return false;
        userRepository.delete(opt.get());
        return true;
    }

    // --- Nuevos métodos de búsqueda ---

    @Transactional(readOnly = true)
    public List<User> findByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public List<User> findByRole(UserDTO.Role role) {
        return userRepository.findByRole(role.name());
    }

    @Transactional(readOnly = true)
    public List<User> filterUsers(String name, UserDTO.Role role) {
        if (name != null && role != null) {
            return userRepository.findByNameContainingIgnoreCase(name)
                                 .stream()
                                 .filter(u -> u.getRole().equals(role.name()))
                                 .collect(Collectors.toList());
        } else if (name != null) {
            return findByName(name);
        } else if (role != null) {
            return findByRole(role);
        } else {
            return getAllUsers();
        }
    }
}
