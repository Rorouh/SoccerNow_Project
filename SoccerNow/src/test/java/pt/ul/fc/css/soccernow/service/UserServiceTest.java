// src/test/java/pt/ul/fc/css/soccernow/service/UserServiceTest.java
package pt.ul.fc.css.soccernow.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.Referee;
import pt.ul.fc.css.soccernow.domain.User;
import pt.ul.fc.css.soccernow.dto.UserCreateDTO;
import pt.ul.fc.css.soccernow.dto.UserUpdateDTO;
import pt.ul.fc.css.soccernow.dto.UserDTO;
import pt.ul.fc.css.soccernow.repository.UserRepository;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    //--- CREATE PLAYER ---

    @Test
    void createPlayer_success() {
        var dto = new UserCreateDTO();
        dto.setName("Juan");
        dto.setEmail("juan@example.com");
        dto.setPassword("pass");
        dto.setRole(UserDTO.Role.PLAYER);
        dto.setPreferredPosition(User.PreferredPosition.DELANTERO);

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        // el save debería devolver un Player con id simulado
        ArgumentCaptor<User> capt = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(capt.capture())).thenAnswer(inv -> {
            Player p = (Player) inv.getArgument(0);
            p.setId(42L);
            return p;
        });

        var created = userService.createUser(dto);

        assertTrue(created instanceof Player);
        assertEquals(42L, created.getId());
        assertEquals("Juan", created.getName());
        verify(userRepository).existsByEmail("juan@example.com");
        verify(userRepository).save(any(Player.class));
    }

    @Test
    void createPlayer_missingPreferredPosition_throws() {
        var dto = new UserCreateDTO();
        dto.setName("Pedro");
        dto.setEmail("pedro@example.com");
        dto.setPassword("pass");
        dto.setRole(UserDTO.Role.PLAYER);
        // dto.setPreferredPosition(null);

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);

        var ex = assertThrows(ApplicationException.class,
            () -> userService.createUser(dto));
        assertEquals("El campo 'preferredPosition' es obligatorio para un PLAYER.", ex.getMessage());
        verify(userRepository).existsByEmail("pedro@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void createPlayer_duplicateEmail_throws() {
        var dto = new UserCreateDTO();
        dto.setEmail("dup@example.com");
        dto.setRole(UserDTO.Role.PLAYER);
        dto.setPreferredPosition(User.PreferredPosition.DEFENSA);

        when(userRepository.existsByEmail("dup@example.com")).thenReturn(true);

        var ex = assertThrows(ApplicationException.class,
            () -> userService.createUser(dto));
        assertEquals("Ya existe un usuario con ese email.", ex.getMessage());
        verify(userRepository).existsByEmail("dup@example.com");
        verify(userRepository, never()).save(any());
    }

    //--- CREATE REFEREE ---

    @Test
    void createReferee_success() {
        var dto = new UserCreateDTO();
        dto.setName("Ana");
        dto.setEmail("ana@example.com");
        dto.setPassword("pass");
        dto.setRole(UserDTO.Role.REFEREE);
        dto.setCertified(true);

        when(userRepository.existsByEmail("ana@example.com")).thenReturn(false);
        when(userRepository.save(any(Referee.class))).thenAnswer(inv -> {
            Referee r = inv.getArgument(0);
            r.setId(7L);
            return r;
        });

        var created = userService.createUser(dto);

        assertTrue(created instanceof Referee);
        assertEquals(7L, created.getId());
        assertTrue(((Referee) created).isCertified());
        verify(userRepository).save(any(Referee.class));
    }

    @Test
    void createReferee_missingCertified_throws() {
        var dto = new UserCreateDTO();
        dto.setEmail("ref@example.com");
        dto.setRole(UserDTO.Role.REFEREE);
        // dto.setCertified(null);

        when(userRepository.existsByEmail("ref@example.com")).thenReturn(false);

        var ex = assertThrows(ApplicationException.class,
            () -> userService.createUser(dto));
        assertEquals("El campo 'certified' es obligatorio para un REFEREE.", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createReferee_duplicateEmail_throws() {
        var dto = new UserCreateDTO();
        dto.setEmail("dup2@example.com");
        dto.setRole(UserDTO.Role.REFEREE);
        dto.setCertified(false);

        when(userRepository.existsByEmail("dup2@example.com")).thenReturn(true);

        var ex = assertThrows(ApplicationException.class,
            () -> userService.createUser(dto));
        assertEquals("Ya existe un usuario con ese email.", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    //--- UPDATE USER ---

    @Test
    void updateUser_success_changesFields() {
        // simulamos un Player existente
        var existing = new Player("Old", "old@example.com", "oldpass", User.PreferredPosition.PORTERO);
        existing.setId(5L);
        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var dto = new UserUpdateDTO();
        dto.setName("Nuevo");
        dto.setEmail("nuevo@example.com");
        dto.setPassword("newpass");
        dto.setPreferredPosition(User.PreferredPosition.CENTROCAMPISTA);

        var opt = userService.updateUser(5L, dto);
        assertTrue(opt.isPresent());
        var updated = opt.get();
        assertEquals("Nuevo", updated.getName());
        assertEquals("nuevo@example.com", updated.getEmail());
        assertEquals("newpass", updated.getPassword());
        assertEquals(User.PreferredPosition.CENTROCAMPISTA, ((Player)updated).getPreferredPosition());
        verify(userRepository).save(updated);
    }

    @Test
    void updateUser_notFound_returnsEmpty() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        var opt = userService.updateUser(999L, new UserUpdateDTO());
        assertFalse(opt.isPresent());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateReferee_certifiedField() {
        var existing = new Referee("Rita", "rita@example.com", "pass", true);
        existing.setId(8L);
        when(userRepository.findById(8L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var dto = new UserUpdateDTO();
        dto.setCertified(false);

        var updated = userService.updateUser(8L, dto).get();
        assertFalse(((Referee)updated).isCertified());
    }
}
