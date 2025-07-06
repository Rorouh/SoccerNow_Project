package pt.ul.fc.css.soccernow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pt.ul.fc.css.soccernow.domain.Jogo;
import pt.ul.fc.css.soccernow.domain.Resultado;
import pt.ul.fc.css.soccernow.dto.JogoCreateDTO;
import pt.ul.fc.css.soccernow.dto.JogoDTO;
import pt.ul.fc.css.soccernow.service.JogoService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;
import pt.ul.fc.css.soccernow.service.exceptions.NotFoundException;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JogoController.class)
class JogoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JogoService jogoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void criarJogo_DeveRetornarCreated() throws Exception {
        // Preparamos el Jogo que queremos que el servicio devuelva
        Jogo jogo = new Jogo();
        jogo.setId(1L);
        jogo.setDataHora(LocalDateTime.of(2050, 1, 1, 15, 0));
        jogo.setLocal("Lisboa");
        jogo.setAmigavel(false);
        // simulamos DTO de creación
        JogoCreateDTO dto = new JogoCreateDTO();
        dto.setDateTime(jogo.getDataHora());
        dto.setLocation(jogo.getLocal());
        dto.setAmigavel(jogo.isAmigavel());
        dto.setTeamIds(Set.of(10L, 20L));
        dto.setRefereeIds(Set.of(100L));
        dto.setPrimaryRefereeId(null);

        Mockito.when(jogoService.criarJogo(any(JogoCreateDTO.class)))
               .thenReturn(jogo);

        mockMvc.perform(post("/api/jogos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
               .andExpect(status().isCreated())
               .andExpect(header().string("Location", "/api/jogos/1"))
               .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void criarJogo_DeveRetornarBadRequest_OnError() throws Exception {
        JogoCreateDTO dto = new JogoCreateDTO();
        dto.setDateTime(LocalDateTime.of(2050, 1, 1, 15, 0));
        dto.setLocation("Porto");
        dto.setAmigavel(false);
        dto.setTeamIds(Set.of(5L, 5L));           // mismo equipo -> provoca ApplicationException
        dto.setRefereeIds(Set.of(100L));
        dto.setPrimaryRefereeId(null);

        Mockito.when(jogoService.criarJogo(any(JogoCreateDTO.class)))
               .thenThrow(new ApplicationException("Las dos equipas deben ser diferentes."));

        mockMvc.perform(post("/api/jogos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value("Las dos equipas deben ser diferentes."));
    }

    @Test
    void registarResultado_DeveRetornarOK() throws Exception {
        Resultado resultado = new Resultado();
        resultado.setGolosCasa(2);
        resultado.setGolosFora(1);
        // stub del servicio
        Mockito.when(jogoService.registarResultado(eq(1L), any(Resultado.class)))
               .thenReturn(resultado);

        mockMvc.perform(post("/api/jogos/1/resultado")
                    .contentType(MediaType.APPLICATION_JSON)
                    // enviamos algún JSON válido; el stub no usa los campos
                    .content(objectMapper.writeValueAsString(resultado)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.golosCasa").value(2))
               .andExpect(jsonPath("$.golosFora").value(1));
    }

    @Test
    void registarResultado_DeveRetornarBadRequest_OnError() throws Exception {
        Mockito.when(jogoService.registarResultado(eq(1L), any(Resultado.class)))
               .thenThrow(new ApplicationException("Formato inválido, use “golesCasa-golesFuera”."));

        mockMvc.perform(post("/api/jogos/1/resultado")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"placar\":\"abc\"}"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value("Formato inválido, use “golesCasa-golesFuera”."));
    }

    @Test
    void obterJogo_DeveRetornarJogoSeExistir() throws Exception {
        Jogo jogo = new Jogo();
        jogo.setId(42L);
        jogo.setLocal("Coimbra");
        jogo.setDataHora(LocalDateTime.now());
        Mockito.when(jogoService.obterJogo(42L))
               .thenReturn(Optional.of(jogo));

        mockMvc.perform(get("/api/jogos/42"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void obterJogo_DeveRetornarNotFoundSeNaoExistir() throws Exception {
        Mockito.when(jogoService.obterJogo(99L))
               .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/jogos/99"))
               .andExpect(status().isNotFound());
    }

    @Test
    void cancelarJogo_DeveRetornarNoContentSeSucesso() throws Exception {
        // Sin excepción -> 204
        mockMvc.perform(put("/api/jogos/1/cancelar"))
               .andExpect(status().isNoContent());
    }

    @Test
    void cancelarJogo_DeveRetornarNotFound() throws Exception {
        Mockito.doThrow(new NotFoundException("Juego no encontrado: 999"))
               .when(jogoService).cancelarJogo(999L);

        mockMvc.perform(put("/api/jogos/999/cancelar"))
               .andExpect(status().isNotFound());
    }

    @Test
    void cancelarJogo_DeveRetornarBadRequest() throws Exception {
        Mockito.doThrow(new ApplicationException("No se puede cancelar un juego con resultado."))
               .when(jogoService).cancelarJogo(5L);

        mockMvc.perform(put("/api/jogos/5/cancelar"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value("No se puede cancelar un juego con resultado."));
    }

    @Test
    void listJogos_DeveRetornarListaVazia() throws Exception {
        // El controlador llama a searchGames(...)
        Mockito.when(jogoService.searchGames(isNull(), isNull(), isNull(), isNull()))
               .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/jogos/jogos"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(0));
    }
}
