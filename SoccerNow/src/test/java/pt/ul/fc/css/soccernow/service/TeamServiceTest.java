// src/test/java/pt/ul/fc/css/soccernow/service/TeamServiceTest.java
package pt.ul.fc.css.soccernow.service;

import pt.ul.fc.css.soccernow.domain.Jogo;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.Team;
import pt.ul.fc.css.soccernow.domain.User.PreferredPosition;
import pt.ul.fc.css.soccernow.dto.TeamDTO;
import pt.ul.fc.css.soccernow.repository.PlayerRepository;
import pt.ul.fc.css.soccernow.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private TeamService teamService;

    private Team team;
    private Player player;

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setId(1L);
        team.setName("Test Team");

        player = new Player();
        player.setId(1L);
        // Cambiamos esto:
        player.setPreferredPosition(PreferredPosition.DELANTERO);

        team.setPlayers(new HashSet<>());
        team.getPlayers().add(player);
    }

    @Test
    void testCreateTeam() {
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setName("New Team");
        teamDTO.setPlayerIds(Set.of(1L));

        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));

        Team newTeam = new Team();
        newTeam.setId(2L);
        newTeam.setName("New Team");
        newTeam.setPlayers(new HashSet<>());
        newTeam.getPlayers().add(player);

        when(teamRepository.save(any(Team.class))).thenReturn(newTeam);

        Team createdTeam = teamService.createTeam(teamDTO);

        assertNotNull(createdTeam);
        assertEquals("New Team", createdTeam.getName());
        assertEquals(1, createdTeam.getPlayers().size());
    }

    @Test
    void testUpdateTeam() {
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setName("Updated Team");
        teamDTO.setPlayerIds(Set.of(1L));

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        Optional<Team> updatedTeam = teamService.updateTeam(1L, teamDTO);

        assertTrue(updatedTeam.isPresent());
        assertEquals("Updated Team", updatedTeam.get().getName());
        assertEquals(1, updatedTeam.get().getPlayers().size());
    }

    @Test
    void testDeleteTeamWithGamesThrowsException() {
        Long id = 1L;
        Set<Jogo> jogos = new HashSet<>();
        jogos.add(new Jogo());

        team.setJogosComoVisitada(jogos);
        team.setJogosComoVisitante(new HashSet<>());

        when(teamRepository.findById(id)).thenReturn(Optional.of(team));

        assertThrows(IllegalStateException.class, () -> teamService.deleteTeam(id));
    }

    @Test
    void testDeleteTeamWithoutGames() {
        Long id = 1L;

        team.setJogosComoVisitada(new HashSet<>());
        team.setJogosComoVisitante(new HashSet<>());

        when(teamRepository.findById(id)).thenReturn(Optional.of(team));

        boolean result = teamService.deleteTeam(id);

        assertTrue(result);
        verify(teamRepository, times(1)).delete(team);
    }

    @Test
    void testGetTeamById() {
        Long id = 1L;
        when(teamRepository.findById(id)).thenReturn(Optional.of(team));

        Optional<Team> foundTeam = teamService.getTeamById(id);

        assertTrue(foundTeam.isPresent());
        assertEquals(id, foundTeam.get().getId());
    }

    @Test
    void testGetAllTeams() {
        List<Team> teams = new ArrayList<>();
        teams.add(team);

        when(teamRepository.findAll()).thenReturn(teams);

        List<Team> allTeams = teamService.getAllTeams();

        assertFalse(allTeams.isEmpty());
        assertEquals(1, allTeams.size());
    }

    @Test
    void testAddPlayerToTeam() {
        Long teamId = 1L;
        Long playerId = 1L;

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        Optional<Team> updatedTeam = teamService.addPlayerToTeam(teamId, playerId);

        assertTrue(updatedTeam.isPresent());
        assertTrue(updatedTeam.get().getPlayers().contains(player));
    }
}
