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
import pt.ul.fc.css.soccernow.dto.UserCreateDTO;
import pt.ul.fc.css.soccernow.dto.UserDTO;
import pt.ul.fc.css.soccernow.dto.UserUpdateDTO;
import pt.ul.fc.css.soccernow.service.UserService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;
import pt.ul.fc.css.soccernow.service.exceptions.NotFoundException;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
        // Payload de creación
        UserCreateDTO createDto = new UserCreateDTO();
        createDto.setName("João");
        createDto.setEmail("joao@example.com");
        createDto.setPassword("pass");
        createDto.setRole(UserDTO.Role.PLAYER);
        createDto.setPreferredPosition(Player.PreferredPosition.DELANTERO);

        // Entidad que devolverá el servicio
        Player created = new Player("João","joao@example.com","pass", Player.PreferredPosition.DELANTERO);
        created.setId(1L);

        when(userService.createUser(any(UserCreateDTO.class))).thenReturn(created);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location","/api/users/1"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.role").value("PLAYER"))
            .andExpect(jsonPath("$.preferredPosition").value("DELANTERO"));
    }

    @Test
    void testCreateReferee() throws Exception {
        UserCreateDTO createDto = new UserCreateDTO();
        createDto.setName("Ana");
        createDto.setEmail("ana@example.com");
        createDto.setPassword("pass");
        createDto.setRole(UserDTO.Role.REFEREE);
        createDto.setCertified(true);

        Referee created = new Referee("Ana","ana@example.com","pass", true);
        created.setId(2L);

        when(userService.createUser(any(UserCreateDTO.class))).thenReturn(created);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location","/api/users/2"))
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.role").value("REFEREE"))
            .andExpect(jsonPath("$.certified").value(true));
    }

    @Test
    void testCreateUser_BadRequestOnDuplicateEmail() throws Exception {
        UserCreateDTO createDto = new UserCreateDTO();
        createDto.setName("Dup");
        createDto.setEmail("dup@example.com");
        createDto.setPassword("pass");
        createDto.setRole(UserDTO.Role.PLAYER);
        createDto.setPreferredPosition(Player.PreferredPosition.PORTERO);

        when(userService.createUser(any(UserCreateDTO.class)))
            .thenThrow(new ApplicationException("Ya existe un usuario con ese email."));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserById_Player() throws Exception {
        Player p = new Player("Carlos","carlos@example.com","pass", Player.PreferredPosition.DEFENSA);
        p.setId(3L);

        when(userService.getUserById(3L)).thenReturn(Optional.of(p));

        mockMvc.perform(get("/api/users/3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.role").value("PLAYER"))
            .andExpect(jsonPath("$.preferredPosition").value("DEFENSA"));
    }

    @Test
    void testGetUserById_Referee() throws Exception {
        Referee r = new Referee("Rita","rita@example.com","pass", true);
        r.setId(4L);

        when(userService.getUserById(4L)).thenReturn(Optional.of(r));

        mockMvc.perform(get("/api/users/4"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(4))
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
    void testUpdateUser_Success() throws Exception {
        UserUpdateDTO updateDto = new UserUpdateDTO();
        updateDto.setName("CarlosUpd");
        updateDto.setPreferredPosition(Player.PreferredPosition.CENTROCAMPISTA);

        Player updated = new Player("CarlosUpd","carlos@example.com","pass", Player.PreferredPosition.CENTROCAMPISTA);
        updated.setId(3L);

        when(userService.updateUser(eq(3L), any(UserUpdateDTO.class)))
            .thenReturn(Optional.of(updated));

        mockMvc.perform(put("/api/users/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.name").value("CarlosUpd"))
            .andExpect(jsonPath("$.preferredPosition").value("CENTROCAMPISTA"));
    }

    @Test
    void testUpdateUser_NotFound() throws Exception {
        UserUpdateDTO updateDto = new UserUpdateDTO();
        updateDto.setName("NoExiste");

        when(userService.updateUser(eq(99L), any(UserUpdateDTO.class)))
            .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
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
