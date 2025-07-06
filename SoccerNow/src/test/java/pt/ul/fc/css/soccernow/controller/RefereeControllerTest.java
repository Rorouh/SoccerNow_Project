package pt.ul.fc.css.soccernow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pt.ul.fc.css.soccernow.domain.Referee;
import pt.ul.fc.css.soccernow.dto.RefereeDTO;
import pt.ul.fc.css.soccernow.service.RefereeService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RefereeController.class)
public class RefereeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RefereeService refereeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Referee mockReferee(Long id, String name, String email, boolean certified) {
        Referee r = new Referee(name, email, "pass", certified);
        r.setId(id);
        return r;
    }

    @Test
    void testListAllReferees() throws Exception {
        List<Referee> list = List.of(
            mockReferee(1L, "João", "joao@teste.com", true),
            mockReferee(2L, "Ana", "ana@teste.com", false)
        );

        when(refereeService.findAllReferees()).thenReturn(list);

        mockMvc.perform(get("/api/referees"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].name").value("João"))
               .andExpect(jsonPath("$[1].certified").value(false));
    }

    @Test
    void testListRefereesByName() throws Exception {
        List<Referee> list = List.of(
            mockReferee(1L, "Carlos", "carlos@teste.com", true)
        );

        when(refereeService.findByName("carl")).thenReturn(list);

        mockMvc.perform(get("/api/referees")
                        .param("name", "carl"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.length()").value(1))
               .andExpect(jsonPath("$[0].name").value("Carlos"));
    }

    @Test
    void testListRefereesByMinGames() throws Exception {
        List<Referee> list = List.of(
            mockReferee(1L, "Tiago", "tiago@teste.com", true)
        );

        when(refereeService.findByMinGames(5L)).thenReturn(list);

        mockMvc.perform(get("/api/referees")
                        .param("minGames", "5"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.length()").value(1))
               .andExpect(jsonPath("$[0].name").value("Tiago"));
    }

    @Test
    void testListRefereesByMinGames_Negative_returnsBadRequest() throws Exception {
        when(refereeService.findByMinGames(anyLong()))
            .thenThrow(new ApplicationException("minGames no puede ser negativo."));

        mockMvc.perform(get("/api/referees")
                        .param("minGames", "-1"))
               .andExpect(status().isBadRequest());
    }

    @Test
    void testFilterReferees_AllFilters() throws Exception {
        // Cobertura endpoint /api/referees/filter
        List<Referee> list = List.of(
            mockReferee(1L, "Pedro", "pedro@teste.com", true)
        );

        when(refereeService.filterReferees("ped", 3, 1)).thenReturn(list);

        mockMvc.perform(get("/api/referees/filter")
                        .param("name", "ped")
                        .param("minGames", "3")
                        .param("minCards", "1"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.length()").value(1))
               .andExpect(jsonPath("$[0].id").value(1))
               .andExpect(jsonPath("$[0].certified").value(true));
    }
}
