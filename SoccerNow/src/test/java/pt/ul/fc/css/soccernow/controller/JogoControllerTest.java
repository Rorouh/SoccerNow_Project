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
import pt.ul.fc.css.soccernow.service.JogoService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;
import pt.ul.fc.css.soccernow.service.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JogoController.class)
public class JogoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JogoService jogoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void criarJogo_DeveRetornarCreated() throws Exception {
        Jogo jogo = new Jogo();
        jogo.setId(1L);
        jogo.setLocation("Lisboa");
        jogo.setDateTime(LocalDateTime.now());

        Mockito.when(jogoService.criarJogo(any(Jogo.class))).thenReturn(jogo);

        mockMvc.perform(post("/api/jogos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jogo)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/jogos/1"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void registarResultado_DeveRetornarOK() throws Exception {
        Resultado resultado = new Resultado();
        resultado.setGolosCasa(2);
        resultado.setGolosFora(1);

        Mockito.when(jogoService.registarResultado(eq(1L), any(Resultado.class))).thenReturn(resultado);

        mockMvc.perform(post("/api/jogos/1/resultado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resultado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.golosCasa").value(2));
    }

    @Test
    void obterJogo_DeveRetornarJogoSeExistir() throws Exception {
        Jogo jogo = new Jogo();
        jogo.setId(1L);
        jogo.setLocation("Lisboa");
        jogo.setDateTime(LocalDateTime.now());

        Mockito.when(jogoService.obterJogo(1L)).thenReturn(Optional.of(jogo));

        mockMvc.perform(get("/api/jogos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void obterJogo_DeveRetornarNotFoundSeNaoExistir() throws Exception {
        Mockito.when(jogoService.obterJogo(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/jogos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelarJogo_DeveRetornarNoContentSeSucesso() throws Exception {
        mockMvc.perform(put("/api/jogos/1/cancelar"))
                .andExpect(status().isNoContent());
    }

    @Test
    void cancelarJogo_DeveRetornarNotFound() throws Exception {
        Mockito.doThrow(new NotFoundException("Not found")).when(jogoService).cancelarJogo(999L);

        mockMvc.perform(put("/api/jogos/999/cancelar"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelarJogo_DeveRetornarBadRequest() throws Exception {
        Mockito.doThrow(new ApplicationException("Já tem resultado")).when(jogoService).cancelarJogo(5L);

        mockMvc.perform(put("/api/jogos/5/cancelar"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listJogos_DeveRetornarListaVazia() throws Exception {
        Mockito.when(jogoService.findAllJogos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/jogos/jogos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
