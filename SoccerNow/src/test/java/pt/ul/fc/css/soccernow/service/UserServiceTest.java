// src/test/java/pt/ul/fc/css/soccernow/service/UserServiceTest.java
package pt.ul.fc.css.soccernow.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.Referee;
import pt.ul.fc.css.soccernow.domain.User;
import pt.ul.fc.css.soccernow.dto.UserDTO;
import pt.ul.fc.css.soccernow.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testCreateUser_Player() {
        // dado
        UserDTO dto = new UserDTO("Alice", "alice@test.com", UserDTO.Role.PLAYER, "Forward", null);
        when(userRepository.save(any(Player.class))).thenAnswer(inv -> {
            Player p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        // cuando
        User created = userService.createUser(dto);

        // entonces
        assertTrue(created instanceof Player);
        assertEquals(1L, created.getId());
        assertEquals("Alice", created.getName());
        assertEquals("alice@test.com", created.getEmail());
        assertEquals("Forward", ((Player) created).getPreferredPosition());
        verify(userRepository).save(any(Player.class));
    }

    @Test
    void testCreateUser_Referee() {
        UserDTO dto = new UserDTO("Bob", "bob@test.com", UserDTO.Role.REFEREE, null, Boolean.TRUE);
        when(userRepository.save(any(Referee.class))).thenAnswer(inv -> {
            Referee r = inv.getArgument(0);
            r.setId(2L);
            return r;
        });

        User created = userService.createUser(dto);

        assertTrue(created instanceof Referee);
        assertEquals(2L, created.getId());
        assertEquals("Bob", created.getName());
        assertEquals("bob@test.com", created.getEmail());
        assertTrue(((Referee) created).isCertified());
        verify(userRepository).save(any(Referee.class));
    }

    @Test
    void testGetUserById_Found() {
        User u = new Player(); u.setId(10L);
        when(userRepository.findById(10L)).thenReturn(Optional.of(u));

        Optional<User> opt = userService.getUserById(10L);

        assertTrue(opt.isPresent());
        assertEquals(10L, opt.get().getId());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<User> opt = userService.getUserById(99L);

        assertFalse(opt.isPresent());
    }

    @Test
    void testUpdateUser_Player() {
        Player existing = new Player(); existing.setId(5L);
        existing.setName("Old"); existing.setEmail("old@e");
        existing.setPreferredPosition("Mid");
        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(Player.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDTO dto = new UserDTO("New", "new@e", UserDTO.Role.PLAYER, "Defender", null);
        Optional<User> updated = userService.updateUser(5L, dto);

        assertTrue(updated.isPresent());
        Player p = (Player) updated.get();
        assertEquals("New", p.getName());
        assertEquals("new@e", p.getEmail());
        assertEquals("Defender", p.getPreferredPosition());
        verify(userRepository).save(p);
    }

    @Test
    void testUpdateUser_NotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        Optional<User> updated = userService.updateUser(1L, new UserDTO());
        assertFalse(updated.isPresent());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteUser_Exists() {
        when(userRepository.existsById(7L)).thenReturn(true);
        boolean res = userService.deleteUser(7L);
        assertTrue(res);
        verify(userRepository).deleteById(7L);
    }

    @Test
    void testDeleteUser_NotExists() {
        when(userRepository.existsById(8L)).thenReturn(false);
        boolean res = userService.deleteUser(8L);
        assertFalse(res);
        verify(userRepository, never()).deleteById(any());
    }
}
