package pt.ul.fc.css.soccernow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pt.ul.fc.css.soccernow.dto.TeamDTO;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TeamControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveBuscarTimesPorFragmentoNome() throws Exception {
        TeamDTO dto = new TeamDTO("Azul FC", Set.of());
        mockMvc.perform(post("/api/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        mockMvc.perform(get("/api/teams/search?name=azul"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Azul FC"));
    }

    @Test
    void deveValidarCamposObrigatoriosAoCriarTime() throws Exception {
        TeamDTO dto = new TeamDTO("", Set.of());
        mockMvc.perform(post("/api/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void deveCriarAtualizarBuscarEDeletarTime() throws Exception {
        // Criação
        TeamDTO dto = new TeamDTO("Time Teste", Set.of());
        String response = mockMvc.perform(post("/api/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        TeamDTO criado = objectMapper.readValue(response, TeamDTO.class);

        // Atualização (adiciona jogador fictício de id 99)
        TeamDTO update = new TeamDTO(criado.getId(), "Time Teste Atualizado", Set.of(99L));
        mockMvc.perform(put("/api/teams/" + criado.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Time Teste Atualizado"));

        // Busca por fragmento
        mockMvc.perform(get("/api/teams/search?name=teste"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Time Teste Atualizado"));

        // Deleção
        mockMvc.perform(delete("/api/teams/" + criado.getId()))
                .andExpect(status().isNoContent());

        // Busca após deleção
        mockMvc.perform(get("/api/teams/" + criado.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarErroAoBuscarTimeInexistente() throws Exception {
        mockMvc.perform(get("/api/teams/99999"))
                .andExpect(status().isNotFound());
    }
}
