// src/test/java/pt/ul/fc/css/soccernow/service/PlayerServiceTest.java
package pt.ul.fc.css.soccernow.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.Player.PreferredPosition;
import pt.ul.fc.css.soccernow.dto.PlayerCreateDTO;
import pt.ul.fc.css.soccernow.dto.PlayerUpdateDTO;
import pt.ul.fc.css.soccernow.repository.PlayerRepository;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    private PlayerCreateDTO createDto;
    private PlayerUpdateDTO updateDto;

    @BeforeEach
    void setUp() {
        createDto = new PlayerCreateDTO();
        createDto.setName("Juan");
        createDto.setEmail("juan@example.com");
        createDto.setPassword("pass123");
        createDto.setPreferredPosition("DELANTERO");
        createDto.setGoals(2);
        createDto.setCards(1);

        updateDto = new PlayerUpdateDTO();
    }

    @Test
    void crearJugador_ok() {
        when(playerRepository.existsByEmail("juan@example.com")).thenReturn(false);
        ArgumentCaptor<Player> captor = ArgumentCaptor.forClass(Player.class);
        Player saved = new Player();
        saved.setId(7L);
        when(playerRepository.save(any())).thenReturn(saved);

        Player result = playerService.createPlayer(createDto);

        assertEquals(7L, result.getId());
        verify(playerRepository).save(captor.capture());
        Player toSave = captor.getValue();
        assertEquals("Juan", toSave.getName());
        assertEquals("juan@example.com", toSave.getEmail());
        assertEquals(PreferredPosition.DELANTERO, toSave.getPreferredPosition());
        assertEquals(2, toSave.getGoals());
        assertEquals(1, toSave.getCards());
    }

    @Test
    void crearJugador_errorEmailRepetido() {
        when(playerRepository.existsByEmail("juan@example.com")).thenReturn(true);
        ApplicationException ex = assertThrows(ApplicationException.class,
            () -> playerService.createPlayer(createDto));
        assertEquals("Ya existe un jugador con ese email.", ex.getMessage());
    }

    @Test
    void crearJugador_errorPositionVacio() {
        createDto.setPreferredPosition("");
        when(playerRepository.existsByEmail("juan@example.com")).thenReturn(false);
        ApplicationException ex = assertThrows(ApplicationException.class,
            () -> playerService.createPlayer(createDto));
        assertTrue(ex.getMessage().contains("obligatorio para un PLAYER"));
    }

    @Test
    void crearJugador_errorPositionInvalido() {
        createDto.setPreferredPosition("INVALIDA");
        when(playerRepository.existsByEmail("juan@example.com")).thenReturn(false);
        ApplicationException ex = assertThrows(ApplicationException.class,
            () -> playerService.createPlayer(createDto));
        assertTrue(ex.getMessage().contains("PreferredPosition inválido"));
    }

    @Test
    void actualizarJugador_ok() {
        Player existing = new Player();
        existing.setId(10L);
        existing.setName("Ana");
        existing.setEmail("ana@old.com");
        existing.setPreferredPosition(PreferredPosition.PORTERO);
        when(playerRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(playerRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        updateDto.setName("Ana María");
        updateDto.setEmail("ana@new.com");
        updateDto.setPreferredPosition("DEFENSA");
        updateDto.setGoals(5);
        updateDto.setCards(0);

        var opt = playerService.updatePlayer(10L, updateDto);
        assertTrue(opt.isPresent());
        Player updated = opt.get();
        assertEquals("Ana María", updated.getName());
        assertEquals("ana@new.com", updated.getEmail());
        assertEquals(PreferredPosition.DEFENSA, updated.getPreferredPosition());
        assertEquals(5, updated.getGoals());
    }

    @Test
    void actualizarJugador_noExiste() {
        when(playerRepository.findById(99L)).thenReturn(Optional.empty());
        var opt = playerService.updatePlayer(99L, updateDto);
        assertTrue(opt.isEmpty());
    }

    @Test
    void actualizarJugador_errorPositionInvalido() {
        Player existing = new Player();
        existing.setId(11L);
        when(playerRepository.findById(11L)).thenReturn(Optional.of(existing));
        updateDto.setPreferredPosition("XX");
        ApplicationException ex = assertThrows(ApplicationException.class,
            () -> playerService.updatePlayer(11L, updateDto));
        assertTrue(ex.getMessage().contains("PreferredPosition inválido"));
    }

    @Test
    void borrarJugador_ok() {
        Player existing = new Player();
        existing.setId(20L);
        when(playerRepository.findById(20L)).thenReturn(Optional.of(existing));
        boolean result = playerService.deletePlayer(20L);
        assertTrue(result);
        verify(playerRepository).delete(existing);
    }

    @Test
    void borrarJugador_noExiste() {
        when(playerRepository.findById(999L)).thenReturn(Optional.empty());
        boolean result = playerService.deletePlayer(999L);
        assertFalse(result);
        verify(playerRepository, never()).delete(any());
    }
}
