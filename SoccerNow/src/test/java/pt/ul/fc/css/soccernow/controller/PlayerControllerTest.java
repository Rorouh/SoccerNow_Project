package pt.ul.fc.css.soccernow.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.dto.PlayerDTO;
import pt.ul.fc.css.soccernow.dto.PlayerUpdateDTO;
import pt.ul.fc.css.soccernow.service.PlayerService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PlayerControllerTest {

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private PlayerController playerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPlayerById_found() {
        Player player = new Player();
        player.setId(1L);
        player.setName("John");
        player.setEmail("john@example.com");
        player.setPassword("pass123");
        player.setPreferredPosition(Player.PreferredPosition.DEFENSA);

        when(playerService.getPlayerById(1L)).thenReturn(Optional.of(player));

        ResponseEntity<PlayerDTO> response = playerController.getPlayerById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("John", response.getBody().getName());
        assertEquals("DEFENSA", response.getBody().getPreferredPosition());
    }

    @Test
    void testGetPlayerById_notFound() {
        when(playerService.getPlayerById(1L)).thenReturn(Optional.empty());

        ResponseEntity<PlayerDTO> response = playerController.getPlayerById(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testUpdatePlayer_success() {
        // Jugador que devuelve el servicio
        Player updatedEntity = new Player();
        updatedEntity.setId(1L);
        updatedEntity.setName("UpdatedName");
        updatedEntity.setEmail("updated@example.com");
        updatedEntity.setPassword("newpass");
        updatedEntity.setPreferredPosition(Player.PreferredPosition.DELANTERO);

        // DTO que pasamos al controlador
        PlayerUpdateDTO dto = new PlayerUpdateDTO();
        dto.setName("UpdatedName");
        dto.setEmail("updated@example.com");
        dto.setPassword("newpass");
        dto.setPreferredPosition("DELANTERO");

        when(playerService.updatePlayer(eq(1L), any(PlayerUpdateDTO.class)))
            .thenReturn(Optional.of(updatedEntity));

        ResponseEntity<PlayerDTO> response = playerController.updatePlayer(1L, dto);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("UpdatedName", response.getBody().getName());
        assertEquals("DELANTERO", response.getBody().getPreferredPosition());
    }

    @Test
    void testUpdatePlayer_notFound() {
        PlayerUpdateDTO dto = new PlayerUpdateDTO();
        dto.setName("NoExiste");
        dto.setEmail("no@exist.com");
        dto.setPassword("pass");
        dto.setPreferredPosition("PORTERO");

        when(playerService.updatePlayer(eq(1L), any(PlayerUpdateDTO.class)))
            .thenReturn(Optional.empty());

        ResponseEntity<PlayerDTO> response = playerController.updatePlayer(1L, dto);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testUpdatePlayer_invalidPreferredPosition() {
        PlayerUpdateDTO dto = new PlayerUpdateDTO();
        dto.setName("Name");
        dto.setEmail("email@example.com");
        dto.setPassword("pass");
        dto.setPreferredPosition("INVALID");

        when(playerService.updatePlayer(eq(1L), any(PlayerUpdateDTO.class)))
            .thenThrow(new ApplicationException("Posição inválida"));

        ResponseEntity<PlayerDTO> response = playerController.updatePlayer(1L, dto);

        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testDeletePlayer_success() {
        when(playerService.deletePlayer(1L)).thenReturn(true);

        ResponseEntity<Void> response = playerController.deletePlayer(1L);

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testDeletePlayer_notFound() {
        when(playerService.deletePlayer(1L)).thenReturn(false);

        ResponseEntity<Void> response = playerController.deletePlayer(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testListPlayers_noFilters() {
        Player p1 = new Player();
        p1.setId(1L);
        p1.setName("Player1");
        p1.setEmail("p1@example.com");
        p1.setPassword("pass1");
        p1.setPreferredPosition(Player.PreferredPosition.PORTERO);

        Player p2 = new Player();
        p2.setId(2L);
        p2.setName("Player2");
        p2.setEmail("p2@example.com");
        p2.setPassword("pass2");
        p2.setPreferredPosition(Player.PreferredPosition.DELANTERO);

        when(playerService.findAllPlayers()).thenReturn(List.of(p1, p2));

        ResponseEntity<List<PlayerDTO>> response = playerController.listPlayers(null, null, null, null);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testListPlayers_byPosition() {
        Player p1 = new Player();
        p1.setId(1L);
        p1.setName("Player1");
        p1.setEmail("p1@example.com");
        p1.setPassword("pass1");
        p1.setPreferredPosition(Player.PreferredPosition.DEFENSA);

        when(playerService.findByPosition(Player.PreferredPosition.DEFENSA))
            .thenReturn(List.of(p1));

        ResponseEntity<List<PlayerDTO>> response =
            playerController.listPlayers(Player.PreferredPosition.DEFENSA, null, null, null);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("DEFENSA", response.getBody().get(0).getPreferredPosition());
    }

    @Test
    void testListPlayers_byMinGoals() {
        Player p1 = new Player();
        p1.setId(1L);
        p1.setName("Player1");
        p1.setEmail("p1@example.com");
        p1.setPassword("pass1");
        p1.setPreferredPosition(Player.PreferredPosition.DELANTERO);

        when(playerService.findByMinGoals(5L)).thenReturn(List.of(p1));

        ResponseEntity<List<PlayerDTO>> response =
            playerController.listPlayers(null, 5L, null, null);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testFindPlayersByName() {
        Player p1 = new Player();
        p1.setId(1L);
        p1.setName("Carlos");
        p1.setEmail("carlos@example.com");
        p1.setPassword("pass1");
        p1.setPreferredPosition(Player.PreferredPosition.DELANTERO);
        p1.setGoals(10);
        p1.setCards(1);

        when(playerService.findPlayersByName("Carlos"))
            .thenReturn(List.of(p1));

        ResponseEntity<List<PlayerDTO>> response =
            playerController.findPlayersByName("Carlos");

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        PlayerDTO dto = response.getBody().get(0);
        assertEquals("Carlos", dto.getName());
        assertEquals(10, dto.getGoals());
        assertEquals(1, dto.getCards());
    }
}
