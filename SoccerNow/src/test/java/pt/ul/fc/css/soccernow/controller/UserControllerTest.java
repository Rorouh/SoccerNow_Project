package pt.ul.fc.css.soccernow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.Referee;
import pt.ul.fc.css.soccernow.domain.User;
import pt.ul.fc.css.soccernow.dto.UserDTO;
import pt.ul.fc.css.soccernow.service.UserService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreatePlayer() throws Exception {
        UserDTO dto = new UserDTO("João", "joao@example.com", "pass", UserDTO.Role.PLAYER, User.PreferredPosition.FORWARD, null);
        Player created = new Player("João", "joao@example.com", "pass", User.PreferredPosition.FORWARD);
        created.setId(1L);

        when(userService.createUser(any(UserDTO.class))).thenReturn(created);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/users/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.role").value("PLAYER"));
    }

    @Test
    void testCreateReferee() throws Exception {
        UserDTO dto = new UserDTO("Ana", "ana@example.com", "pass", UserDTO.Role.REFEREE, null, true);
        Referee created = new Referee("Ana", "ana@example.com", "pass", true);
        created.setId(2L);

        when(userService.createUser(any(UserDTO.class))).thenReturn(created);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/users/2"))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.role").value("REFEREE"));
    }

    @Test
    void testGetUserById_Player() throws Exception {
        Player p = new Player("Carlos", "carlos@example.com", "pass", User.PreferredPosition.DEFENDER);
        p.setId(3L);

        when(userService.getUserById(3L)).thenReturn(Optional.of(p));

        mockMvc.perform(get("/api/users/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.role").value("PLAYER"))
                .andExpect(jsonPath("$.preferredPosition").value("DEFENDER"));
    }

    @Test
    void testGetUserById_Referee() throws Exception {
        Referee r = new Referee("Rita", "rita@example.com", "pass", true);
        r.setId(4L);

        when(userService.getUserById(4L)).thenReturn(Optional.of(r));

        mockMvc.perform(get("/api/users/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4L))
                .andExpect(jsonPath("$.role").value("REFEREE"))
                .andExpect(jsonPath("$.certified").value(true));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDTO dto = new UserDTO("Carlos Atualizado", "carlos@example.com", "nova", UserDTO.Role.PLAYER, User.PreferredPosition.MIDFIELDER, null);
        Player updated = new Player("Carlos Atualizado", "carlos@example.com", "nova", User.PreferredPosition.MIDFIELDER);
        updated.setId(3L);

        when(userService.updateUser(Mockito.eq(3L), any(UserDTO.class))).thenReturn(Optional.of(updated));

        mockMvc.perform(put("/api/users/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Carlos Atualizado"))
                .andExpect(jsonPath("$.preferredPosition").value("MIDFIELDER"));
    }

    @Test
    void testUpdateUser_NotFound() throws Exception {
        UserDTO dto = new UserDTO("Inexistente", "no@email.com", "pass", UserDTO.Role.PLAYER, User.PreferredPosition.DEFENDER, null);
        when(userService.updateUser(Mockito.eq(99L), any(UserDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        when(userService.deleteUser(5L)).thenReturn(true);

        mockMvc.perform(delete("/api/users/5"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        when(userService.deleteUser(100L)).thenReturn(false);

        mockMvc.perform(delete("/api/users/100"))
                .andExpect(status().isNotFound());
    }
}
