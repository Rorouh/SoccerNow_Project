package pt.ul.fc.css.soccernow.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.service.PlayerService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PlayerWebControllerTest {

    @Mock
    private PlayerService playerService;

    @Mock
    private Model model;

    @InjectMocks
    private PlayerWebController playerWebController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListPlayers_FiltersApplied() {
        Player p1 = new Player();
        p1.setId(1L);
        p1.setName("Ronaldo");
        p1.setEmail("ronaldo@example.com");
        p1.setPassword("123");
        p1.setPreferredPosition(pt.ul.fc.css.soccernow.domain.User.PreferredPosition.DELANTERO);
        p1.setGoals(10);
        p1.setCards(2);
        Player p2 = new Player();
        p2.setId(2L);
        p2.setName("Messi");
        p2.setEmail("messi@example.com");
        p2.setPassword("456");
        p2.setPreferredPosition(pt.ul.fc.css.soccernow.domain.User.PreferredPosition.DELANTERO);
        p2.setGoals(15);
        p2.setCards(1);
        List<Player> players = Arrays.asList(p1, p2);
        when(playerService.filterPlayers(eq("Ron"), anyString(), eq(10), eq(2), eq(5))).thenReturn(players);

        String view = playerWebController.listPlayers("Ron", "ATACANTE", 10, 2, 5, model);

        assertEquals("players/list", view);
        verify(model).addAttribute(eq("players"), argThat(list -> {
            if (!(list instanceof List<?> l) || l.size() != 2) return false;
            Object dto = l.get(0);
            if (!(dto instanceof pt.ul.fc.css.soccernow.dto.PlayerDTO pdto)) return false;
            return "Ronaldo".equals(pdto.getName());
        }));
        verify(model, atLeastOnce()).addAttribute(eq("positions"), any());
    }

    @Test
    void testListPlayers_NoFilters() {
        when(playerService.filterPlayers(isNull(), isNull(), isNull(), isNull(), isNull())).thenReturn(List.of());
        String view = playerWebController.listPlayers(null, null, null, null, null, model);
        assertEquals("players/list", view);
        verify(model).addAttribute(eq("players"), eq(List.of()));
    }
}
