package pt.ul.fc.css.soccernow.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.Team;
import pt.ul.fc.css.soccernow.dto.TeamDTO;
import pt.ul.fc.css.soccernow.service.TeamService;

import java.util.*;

public class TeamControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TeamService teamService;

    @InjectMocks
    private TeamController teamController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(teamController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateTeam() throws Exception {
        TeamDTO dto = new TeamDTO(null, "Team A", Set.of(1L, 2L));
        Team saved = new Team();
        saved.setId(1L);
        saved.setName("Team A");
        saved.setPlayers(new HashSet<>(Arrays.asList(
                createPlayer(1L),
                createPlayer(2L)
        )));

        when(teamService.createTeam(any(TeamDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/teams/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Team A"))
                .andExpect(jsonPath("$.playerIds").isArray())
                .andExpect(jsonPath("$.playerIds.length()").value(2));
    }

    @Test
    void testGetTeamById_found() throws Exception {
        Team team = new Team();
        team.setId(1L);
        team.setName("Team A");
        team.setPlayers(Set.of(createPlayer(1L)));

        when(teamService.getTeamById(1L)).thenReturn(Optional.of(team));

        mockMvc.perform(get("/api/teams/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Team A"))
                .andExpect(jsonPath("$.playerIds[0]").value(1));
    }

    @Test
    void testGetTeamById_notFound() throws Exception {
        when(teamService.getTeamById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/teams/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testListTeams_withoutName() throws Exception {
        Team t1 = new Team();
        t1.setId(1L);
        t1.setName("Team A");
        t1.setPlayers(Set.of(createPlayer(1L)));

        Team t2 = new Team();
        t2.setId(2L);
        t2.setName("Team B");
        t2.setPlayers(Set.of(createPlayer(2L)));

        when(teamService.getAllTeams()).thenReturn(List.of(t1, t2));

        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testUpdateTeam_found() throws Exception {
        TeamDTO dto = new TeamDTO(null, "Updated Team", Set.of(1L));
        Team updated = new Team();
        updated.setId(1L);
        updated.setName("Updated Team");
        updated.setPlayers(Set.of(createPlayer(1L)));

        when(teamService.updateTeam(eq(1L), any(TeamDTO.class))).thenReturn(Optional.of(updated));

        mockMvc.perform(put("/api/teams/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Team"))
                .andExpect(jsonPath("$.playerIds[0]").value(1));
    }

    @Test
    void testUpdateTeam_notFound() throws Exception {
        TeamDTO dto = new TeamDTO(null, "Updated Team", Set.of(1L));

        when(teamService.updateTeam(eq(999L), any(TeamDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/teams/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTeam_success() throws Exception {
        when(teamService.deleteTeam(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/teams/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteTeam_notFound() throws Exception {
        when(teamService.deleteTeam(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/teams/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTeam_illegalState() throws Exception {
        when(teamService.deleteTeam(1L)).thenThrow(new IllegalStateException());

        mockMvc.perform(delete("/api/teams/1"))
                .andExpect(status().isBadRequest());
    }

    private Player createPlayer(Long id) {
        Player p = new Player();
        p.setId(id);
        return p;
    }
}
