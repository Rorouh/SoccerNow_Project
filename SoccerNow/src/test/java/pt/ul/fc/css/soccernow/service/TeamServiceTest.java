// src/test/java/pt/ul/fc/css/soccernow/service/TeamServiceTest.java
package pt.ul.fc.css.soccernow.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.Team;
import pt.ul.fc.css.soccernow.dto.TeamDTO;
import pt.ul.fc.css.soccernow.repository.PlayerRepository;
import pt.ul.fc.css.soccernow.repository.TeamRepository;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private TeamService teamService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTeam_shouldSaveWithGivenPlayers() {
        // dado
        TeamDTO dto = new TeamDTO("EquipoX", Set.of(1L, 2L));
        Player p1 = new Player(); p1.setId(1L);
        Player p2 = new Player(); p2.setId(2L);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(playerRepository.findById(2L)).thenReturn(Optional.of(p2));

        Team saved = new Team(); saved.setId(10L); saved.setName("EquipoX");
        saved.setPlayers(Set.of(p1, p2));
        when(teamRepository.save(any())).thenReturn(saved);

        // cuando
        Team result = teamService.createTeam(dto);

        // entonces
        assertEquals(10L, result.getId());
        assertEquals("EquipoX", result.getName());
        assertTrue(result.getPlayers().containsAll(List.of(p1, p2)));
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void updateTeam_existingId_shouldUpdateNameAndPlayers() {
        // dado
        Team existing = new Team(); existing.setId(5L); existing.setName("Old");
        Player oldP = new Player(); oldP.setId(9L);
        existing.setPlayers(Set.of(oldP));
        when(teamRepository.findById(5L)).thenReturn(Optional.of(existing));

        TeamDTO dto = new TeamDTO("Nuevo", Set.of(7L));
        Player newP = new Player(); newP.setId(7L);
        when(playerRepository.findById(7L)).thenReturn(Optional.of(newP));

        Team updated = new Team(); updated.setId(5L);
        updated.setName("Nuevo");
        updated.setPlayers(Set.of(newP));
        when(teamRepository.save(any())).thenReturn(updated);

        // cuando
        Optional<Team> opt = teamService.updateTeam(5L, dto);

        // entonces
        assertTrue(opt.isPresent());
        Team result = opt.get();
        assertEquals("Nuevo", result.getName());
        assertEquals(1, result.getPlayers().size());
        assertTrue(result.getPlayers().contains(newP));
    }

    @Test
    void updateTeam_nonExistingId_shouldReturnEmpty() {
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Team> opt = teamService.updateTeam(99L, new TeamDTO("X", Set.of()));
        assertTrue(opt.isEmpty());
    }

    @Test
    void deleteTeam_nonExistingId_returnsFalse() {
        when(teamRepository.findById(123L)).thenReturn(Optional.empty());
        assertFalse(teamService.deleteTeam(123L));
    }

    @Test
    void deleteTeam_withGames_shouldThrowIllegalState() {
        Team t = new Team(); t.setId(2L); t.setName("T");
        // simulamos que tiene juegos en casa
        t.setJogosComoVisitada(Set.of(mock(pt.ul.fc.css.soccernow.domain.Jogo.class)));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(t));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> teamService.deleteTeam(2L));
        assertEquals("No es posible eliminar un equipo con juegos asociados.", ex.getMessage());
    }

    @Test
    void deleteTeam_noGames_clearsPlayersAndDeletes() {
        Team t = new Team(); t.setId(3L);
        t.setPlayers(new HashSet<>(List.of(new Player())));
        when(teamRepository.findById(3L)).thenReturn(Optional.of(t));

        boolean res = teamService.deleteTeam(3L);

        assertTrue(res);
        assertTrue(t.getPlayers().isEmpty());
        verify(teamRepository).delete(t);
    }

    @Test
    void addPlayerToTeam_missingTeamOrPlayer_returnsEmpty() {
        when(teamRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Team> r1 = teamService.addPlayerToTeam(1L, 42L);
        assertTrue(r1.isEmpty());

        Team t = new Team(); t.setId(1L);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(t));
        when(playerRepository.findById(42L)).thenReturn(Optional.empty());
        Optional<Team> r2 = teamService.addPlayerToTeam(1L, 42L);
        assertTrue(r2.isEmpty());
    }

    @Test
    void addPlayerToTeam_alreadyMember_shouldThrowApplicationException() {
        Team t = new Team(); t.setId(5L);
        Player p = new Player(); p.setId(8L);
        t.setPlayers(new HashSet<>(List.of(p)));
        when(teamRepository.findById(5L)).thenReturn(Optional.of(t));
        when(playerRepository.findById(8L)).thenReturn(Optional.of(p));

        ApplicationException ex = assertThrows(ApplicationException.class,
            () -> teamService.addPlayerToTeam(5L, 8L));
        assertEquals("El jugador ya forma parte del equipo.", ex.getMessage());
    }

    @Test
    void addPlayerToTeam_happyPath_returnsReFetchedTeam() {
        Team t = new Team(); t.setId(6L); t.setPlayers(new HashSet<>());
        Player p = new Player(); p.setId(11L);
        when(teamRepository.findById(6L)).thenReturn(Optional.of(t));
        when(playerRepository.findById(11L)).thenReturn(Optional.of(p));

        Team reloaded = new Team(); reloaded.setId(6L);
        reloaded.setPlayers(Set.of(p));
        when(teamRepository.findById(6L)).thenReturn(Optional.of(reloaded));

        Optional<Team> opt = teamService.addPlayerToTeam(6L, 11L);
        assertTrue(opt.isPresent());
        Team result = opt.get();
        assertTrue(result.getPlayers().contains(p));
    }
}
