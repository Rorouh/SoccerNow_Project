// src/test/java/pt/ul/fc/css/soccernow/service/UserServiceTest.java
package pt.ul.fc.css.soccernow.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.Referee;
import pt.ul.fc.css.soccernow.domain.User;
import pt.ul.fc.css.soccernow.domain.User.PreferredPosition;
import pt.ul.fc.css.soccernow.dto.UserDTO;
import pt.ul.fc.css.soccernow.dto.UserDTO.Role;
import pt.ul.fc.css.soccernow.repository.UserRepository;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

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
        // Dado un UserDTO con role = PLAYER (preferredPosition obligatorio)
        UserDTO dto = new UserDTO(
                "Alice",
                "alice@test.com",
                "securePass",
                Role.PLAYER,
                PreferredPosition.DELANTERO, // Usamos el enum interno de User
                null  // certified = null para PLAYER
        );

        // Simulamos que no existe aún ese email
        when(userRepository.existsByEmail("alice@test.com")).thenReturn(false);
        // Simulamos que el repositorio guarda un Player y le asigna ID = 1
        when(userRepository.save(any(Player.class))).thenAnswer(invocation -> {
            Player p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        // Cuando llamamos a createUser
        var created = userService.createUser(dto);

        // Entonces esperamos un objeto Player con los campos correctamente rellenados
        assertTrue(created instanceof Player, "Debe devolverse una instancia de Player");
        assertEquals(1L, created.getId());
        assertEquals("Alice", created.getName());
        assertEquals("alice@test.com", created.getEmail());
        assertEquals("securePass", created.getPassword());
        assertEquals("PLAYER", created.getRole());
        assertEquals(PreferredPosition.DELANTERO, ((Player) created).getPreferredPosition());

        // Verificamos que se haya invocado userRepository.save(...) con un Player
        ArgumentCaptor<Player> captor = ArgumentCaptor.forClass(Player.class);
        verify(userRepository, times(1)).save(captor.capture());
        Player saved = captor.getValue();
        assertEquals("Alice", saved.getName());
        assertEquals("alice@test.com", saved.getEmail());
        assertEquals("securePass", saved.getPassword());
        assertEquals(PreferredPosition.DELANTERO, saved.getPreferredPosition());
    }

    @Test
    void testCreateUser_Referee() {
        // Dado un UserDTO con role = REFEREE (preferredPosition = null)
        UserDTO dto = new UserDTO(
                "Bob",
                "bob@test.com",
                "refPass",
                Role.REFEREE,
                null,      // preferredPosition = null para REFEREE
                false      // certified por defecto
        );

        // Simulamos que no existe ese email
        when(userRepository.existsByEmail("bob@test.com")).thenReturn(false);
        // Simulamos que al guardar le asigna ID = 2
        when(userRepository.save(any(Referee.class))).thenAnswer(invocation -> {
            Referee r = invocation.getArgument(0);
            r.setId(2L);
            return r;
        });

        // Cuando llamamos a createUser
        var created = userService.createUser(dto);

        // Entonces esperamos un objeto Referee con los campos rellenados
        assertTrue(created instanceof Referee, "Debe devolverse una instancia de Referee");
        assertEquals(2L, created.getId());
        assertEquals("Bob", created.getName());
        assertEquals("bob@test.com", created.getEmail());
        assertEquals("refPass", created.getPassword());
        assertEquals("REFEREE", created.getRole());
        assertFalse(((Referee) created).isCertified(),
                    "Por defecto, un nuevo Referee debe venir con certified = false");

        // Verificamos que se haya invocado userRepository.save(...) con un Referee
        ArgumentCaptor<Referee> captor = ArgumentCaptor.forClass(Referee.class);
        verify(userRepository, times(1)).save(captor.capture());
        Referee saved = captor.getValue();
        assertEquals("Bob", saved.getName());
        assertEquals("bob@test.com", saved.getEmail());
        assertEquals("refPass", saved.getPassword());
        assertFalse(saved.isCertified());
    }

    @Test
    void testCreateUser_DuplicateEmail_ThrowsException() {
        // Dado un email ya existente
        UserDTO dto = new UserDTO(
                "Carlos",
                "carlos@test.com",
                "pass123",
                Role.PLAYER,
                PreferredPosition.CENTROCAMPISTA, // Usamos el enum
                null
        );
        when(userRepository.existsByEmail("carlos@test.com")).thenReturn(true);

        // Cuando llamamos a createUser => esperamos ApplicationException
        ApplicationException ex = assertThrows(
                ApplicationException.class,
                () -> userService.createUser(dto)
        );
        assertEquals("Ya existe un usuario con ese email.", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void testGetUserById_Found() {
        // Dado un Player en el repositorio con ID = 10
        Player existing = new Player();
        existing.setId(10L);
        existing.setName("Diana");
        existing.setEmail("diana@test.com");
        existing.setPassword("pwd");
        existing.setRole("PLAYER");
        existing.setPreferredPosition(PreferredPosition.PORTERO); // Enum en lugar de texto

        when(userRepository.findById(10L)).thenReturn(Optional.of(existing));

        // Cuando llamamos a getUserById
        Optional<User> opt = userService.getUserById(10L);

        // Entonces debe devolver el mismo objeto
        assertTrue(opt.isPresent());
        User u = opt.get();
        assertEquals(10L, u.getId());
        assertTrue(u instanceof Player);
        assertEquals("Diana", u.getName());
        assertEquals(PreferredPosition.PORTERO, ((Player) u).getPreferredPosition());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<User> opt = userService.getUserById(99L);
        assertFalse(opt.isPresent());
    }

    @Test
    void testUpdateUser_Player() {
        // Dado un Player existente en BD
        Player existing = new Player();
        existing.setId(5L);
        existing.setName("OldName");
        existing.setEmail("old@test.com");
        existing.setPassword("oldPwd");
        existing.setRole("PLAYER");
        existing.setPreferredPosition(PreferredPosition.CENTROCAMPISTA); // Antes "Midfielder"

        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(Player.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // DTO con valores nuevos
        UserDTO dto = new UserDTO(
                "NewName",
                "new@test.com",
                "newPwd",
                Role.PLAYER,
                PreferredPosition.DEFENSA, // Antes "Defender"
                null
        );

        Optional<User> updatedOpt = userService.updateUser(5L, dto);
        assertTrue(updatedOpt.isPresent());

        Player updated = (Player) updatedOpt.get();
        assertEquals(5L, updated.getId());
        assertEquals("NewName", updated.getName());
        assertEquals("new@test.com", updated.getEmail());
        assertEquals("newPwd", updated.getPassword());
        assertEquals("PLAYER", updated.getRole());
        assertEquals(PreferredPosition.DEFENSA, updated.getPreferredPosition());

        verify(userRepository).save(updated);
    }

    @Test
    void testUpdateUser_Referee() {
        // Dado un Referee existente en BD
        Referee existing = new Referee();
        existing.setId(8L);
        existing.setName("RefOld");
        existing.setEmail("refold@test.com");
        existing.setPassword("refPwd");
        existing.setRole("REFEREE");
        existing.setCertified(true);

        when(userRepository.findById(8L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(Referee.class))).thenAnswer(inv -> inv.getArgument(0));

        // DTO: role sigue siendo REFEREE, no se envía preferredPosition
        UserDTO dto = new UserDTO(
                "RefNew",
                "refnew@test.com",
                "newRefPwd",
                Role.REFEREE,
                null,
                true
        );

        Optional<User> updatedOpt = userService.updateUser(8L, dto);
        assertTrue(updatedOpt.isPresent());

        Referee updated = (Referee) updatedOpt.get();
        assertEquals(8L, updated.getId());
        assertEquals("RefNew", updated.getName());
        assertEquals("refnew@test.com", updated.getEmail());
        assertEquals("newRefPwd", updated.getPassword());
        assertEquals("REFEREE", updated.getRole());
        assertTrue(updated.isCertified());

        verify(userRepository).save(updated);
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
        // Dado un usuario en BD
        Player existing = new Player();
        existing.setId(7L);

        when(userRepository.findById(7L)).thenReturn(Optional.of(existing));
        // Cuando llamo a deleteUser(7)
        boolean result = userService.deleteUser(7L);

        assertTrue(result);
        // Se espera que se llame a userRepository.delete(u)
        verify(userRepository).delete(existing);
    }

    @Test
    void testDeleteUser_NotExists() {
        when(userRepository.findById(8L)).thenReturn(Optional.empty());
        boolean result = userService.deleteUser(8L);
        assertFalse(result);
        verify(userRepository, never()).delete(any());
    }
}
