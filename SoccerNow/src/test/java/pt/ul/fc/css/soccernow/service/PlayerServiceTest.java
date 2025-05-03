// src/test/java/pt/ul/fc/css/soccernow/service/PlayerServiceTest.java
package pt.ul.fc.css.soccernow.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.repository.PlayerRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    @Test
    void testFindAllPlayers() {
        Player p1 = new Player(); p1.setName("X");
        Player p2 = new Player(); p2.setName("Y");
        when(playerRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Player> all = playerService.findAllPlayers();

        assertEquals(2, all.size());
        verify(playerRepository).findAll();
    }

    @Test
    void testFindPlayersByName() {
        Player p = new Player(); p.setName("Alice");
        when(playerRepository.findByNameContainingIgnoreCase("ali")).thenReturn(List.of(p));

        List<Player> found = playerService.findPlayersByName("ali");

        assertEquals(1, found.size());
        assertEquals("Alice", found.get(0).getName());
        verify(playerRepository).findByNameContainingIgnoreCase("ali");
    }
}
