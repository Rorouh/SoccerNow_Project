package pt.ul.fc.css.soccernow.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.Referee;
import pt.ul.fc.css.soccernow.domain.User;
import pt.ul.fc.css.soccernow.dto.UserDTO;
import pt.ul.fc.css.soccernow.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User createUser(UserDTO dto) {
        User user;
        if (dto.getRole() == UserDTO.Role.PLAYER) {
            Player p = new Player();
            p.setPreferredPosition(dto.getPreferredPosition());
            user = p;
        } else {
            Referee r = new Referee();
            r.setCertified(Boolean.TRUE.equals(dto.getCertified()));
            user = r;
        }
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public Optional<User> updateUser(Long id, UserDTO dto) {
        return userRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            existing.setEmail(dto.getEmail());
            if (existing instanceof Player && dto.getRole() == UserDTO.Role.PLAYER) {
                ((Player) existing).setPreferredPosition(dto.getPreferredPosition());
            }
            if (existing instanceof Referee && dto.getRole() == UserDTO.Role.REFEREE) {
                ((Referee) existing).setCertified(Boolean.TRUE.equals(dto.getCertified()));
            }
            return userRepository.save(existing);
        });
    }

    @Transactional
    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) return false;
        userRepository.deleteById(id);
        return true;
    }
}
