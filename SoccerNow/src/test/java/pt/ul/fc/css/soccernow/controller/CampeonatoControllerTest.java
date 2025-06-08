package pt.ul.fc.css.soccernow.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import pt.ul.fc.css.soccernow.domain.Campeonato;
import pt.ul.fc.css.soccernow.dto.CampeonatoDTO;
import pt.ul.fc.css.soccernow.service.CampeonatoService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CampeonatoControllerTest {

    @Mock
    private CampeonatoService campeonatoService;

    @InjectMocks
    private CampeonatoController campeonatoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFilterCampeonatos_AllFilters() {
        Campeonato c1 = new Campeonato();
        c1.setNome("Liga Principal");
        Campeonato c2 = new Campeonato();
        c2.setNome("Liga Secundária");
        List<Campeonato> campeonatos = Arrays.asList(c1, c2);
        when(campeonatoService.filterCampeonatos(eq("Liga"), eq("TimeA"), eq(2), eq(1))).thenReturn(campeonatos);

        ResponseEntity<List<CampeonatoDTO>> response = campeonatoController.filterCampeonatos("Liga", "TimeA", 2, 1);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Liga Principal", response.getBody().get(0).getNome());
    }

    @Test
    void testFilterCampeonatos_NoResults() {
        when(campeonatoService.filterCampeonatos(eq("X"), eq(null), eq(null), eq(null))).thenReturn(List.of());
        ResponseEntity<List<CampeonatoDTO>> response = campeonatoController.filterCampeonatos("X", null, null, null);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testFilterCampeonatos_Exception() {
        when(campeonatoService.filterCampeonatos(any(), any(), any(), any())).thenThrow(new ApplicationException("Erro"));
        assertThrows(ApplicationException.class, () -> {
            campeonatoController.filterCampeonatos("A", null, null, null);
        });
    }
}
