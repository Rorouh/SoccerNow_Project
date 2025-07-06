package pt.ul.fc.css.soccernow.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import pt.ul.fc.css.soccernow.domain.Campeonato;
import pt.ul.fc.css.soccernow.domain.Team;
import pt.ul.fc.css.soccernow.dto.CampeonatoDTO;
import pt.ul.fc.css.soccernow.service.CampeonatoService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    // -------------------- filterCampeonatos --------------------

    @Test
    void testFilterCampeonatos_AllFilters() {
        Campeonato c1 = new Campeonato();
        c1.setId(1L);
        c1.setNome("Liga A");
        Campeonato c2 = new Campeonato();
        c2.setId(2L);
        c2.setNome("Liga B");
        when(campeonatoService.filterCampeonatos("Liga", "TeamX", 3, 1))
            .thenReturn(List.of(c1, c2));

        ResponseEntity<List<CampeonatoDTO>> resp = campeonatoController.filterCampeonatos(
            "Liga", "TeamX", 3, 1
        );

        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertEquals(2, resp.getBody().size());
        assertEquals(1L, resp.getBody().get(0).getId());
        assertEquals("Liga A", resp.getBody().get(0).getNome());
    }

    @Test
    void testFilterCampeonatos_NoResults() {
        when(campeonatoService.filterCampeonatos(eq("X"), isNull(), isNull(), isNull()))
            .thenReturn(List.of());

        ResponseEntity<List<CampeonatoDTO>> resp = campeonatoController.filterCampeonatos(
            "X", null, null, null
        );

        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody().isEmpty());
    }

    @Test
    void testFilterCampeonatos_Exception() {
        when(campeonatoService.filterCampeonatos(any(), any(), any(), any()))
            .thenThrow(new ApplicationException("error"));

        // Al no haber catch, propagará la excepción
        assertThrows(ApplicationException.class, () ->
            campeonatoController.filterCampeonatos("A", null, null, null)
        );
    }

    // -------------------- create --------------------

    @Test
    void testCreateCampeonato_Success() {
        CampeonatoDTO input = new CampeonatoDTO("Liga", "Mod", "Form", Set.of(10L, 20L));
        // montamos la entidad retornada
        Campeonato created = new Campeonato();
        created.setId(5L);
        created.setNome("Liga");
        created.setModalidade("Mod");
        created.setFormato("Form");
        Team t1 = new Team(); t1.setId(10L);
        Team t2 = new Team(); t2.setId(20L);
        created.setParticipantes(Set.of(t1, t2));

        when(campeonatoService.createCampeonato(any(CampeonatoDTO.class)))
            .thenReturn(created);

        ResponseEntity<CampeonatoDTO> resp = campeonatoController.create(input);

        assertEquals(201, resp.getStatusCodeValue());
        assertEquals(URI.create("/api/campeonatos/5"), resp.getHeaders().getLocation());
        CampeonatoDTO body = resp.getBody();
        assertNotNull(body);
        assertEquals(5L, body.getId());
        assertEquals("Liga", body.getNome());
        assertEquals("Mod", body.getModalidade());
        assertEquals("Form", body.getFormato());
        assertEquals(2, body.getParticipanteIds().size());
        assertTrue(body.getParticipanteIds().containsAll(Set.of(10L, 20L)));
    }

    @Test
    void testCreateCampeonato_BusinessError() {
        CampeonatoDTO input = new CampeonatoDTO("X", "M", "F", Set.of(1L));
        when(campeonatoService.createCampeonato(any())).thenThrow(new ApplicationException("dup"));

        ResponseEntity<CampeonatoDTO> resp = campeonatoController.create(input);

        assertEquals(400, resp.getStatusCodeValue());
        assertNull(resp.getBody());
    }

    // -------------------- getById --------------------

    @Test
    void testGetById_Found() {
        Campeonato c = new Campeonato();
        c.setId(7L);
        c.setNome("Camp");
        c.setModalidade("M");
        c.setFormato("F");
        Team t = new Team(); t.setId(30L);
        c.setParticipantes(Set.of(t));

        when(campeonatoService.getCampeonatoById(7L)).thenReturn(Optional.of(c));

        ResponseEntity<CampeonatoDTO> resp = campeonatoController.getById(7L);

        assertEquals(200, resp.getStatusCodeValue());
        CampeonatoDTO dto = resp.getBody();
        assertNotNull(dto);
        assertEquals(7L, dto.getId());
        assertEquals("Camp", dto.getNome());
        assertEquals(1, dto.getParticipanteIds().size());
        assertTrue(dto.getParticipanteIds().contains(30L));
    }

    @Test
    void testGetById_NotFound() {
        when(campeonatoService.getCampeonatoById(99L)).thenReturn(Optional.empty());

        ResponseEntity<CampeonatoDTO> resp = campeonatoController.getById(99L);

        assertEquals(404, resp.getStatusCodeValue());
        assertNull(resp.getBody());
    }

    // -------------------- findByNome (GET /api/campeonatos) --------------------

    @Test
    void testFindByNome_WithParam() {
        Campeonato c = new Campeonato();
        c.setId(8L);
        c.setNome("X League");
        when(campeonatoService.findCampeonatosByNome("X")).thenReturn(List.of(c));

        ResponseEntity<List<CampeonatoDTO>> resp = campeonatoController.findByNome("X");

        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(1, resp.getBody().size());
        assertEquals("X League", resp.getBody().get(0).getNome());
    }

    @Test
    void testFindByNome_WithoutParam() {
        Campeonato c = new Campeonato();
        c.setId(9L);
        c.setNome("All League");
        // cuando nombre es null, el controlador llama con ""
        when(campeonatoService.findCampeonatosByNome("")).thenReturn(List.of(c));

        ResponseEntity<List<CampeonatoDTO>> resp = campeonatoController.findByNome(null);

        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(1, resp.getBody().size());
        assertEquals("All League", resp.getBody().get(0).getNome());
    }

    // -------------------- update --------------------

    @Test
    void testUpdate_Success() {
        CampeonatoDTO input = new CampeonatoDTO( "New", "M", "F", Set.of(2L) );
        Campeonato updated = new Campeonato();
        updated.setId(11L);
        updated.setNome("New");
        updated.setModalidade("M");
        updated.setFormato("F");
        Team t = new Team(); t.setId(2L);
        updated.setParticipantes(Set.of(t));

        when(campeonatoService.updateCampeonato(eq(11L), any(CampeonatoDTO.class)))
            .thenReturn(Optional.of(updated));

        ResponseEntity<CampeonatoDTO> resp = campeonatoController.update(11L, input);

        assertEquals(200, resp.getStatusCodeValue());
        CampeonatoDTO dto = resp.getBody();
        assertNotNull(dto);
        assertEquals(11L, dto.getId());
        assertEquals("New", dto.getNome());
    }

    @Test
    void testUpdate_NotFound() {
        CampeonatoDTO input = new CampeonatoDTO("X", "M", "F", Set.of(1L));
        when(campeonatoService.updateCampeonato(eq(99L), any())).thenReturn(Optional.empty());

        ResponseEntity<CampeonatoDTO> resp = campeonatoController.update(99L, input);

        assertEquals(404, resp.getStatusCodeValue());
        assertNull(resp.getBody());
    }

    @Test
    void testUpdate_BusinessError() {
        CampeonatoDTO input = new CampeonatoDTO("X", "M", "F", Set.of(1L));
        when(campeonatoService.updateCampeonato(eq(5L), any()))
            .thenThrow(new ApplicationException("err"));

        ResponseEntity<CampeonatoDTO> resp = campeonatoController.update(5L, input);

        assertEquals(400, resp.getStatusCodeValue());
        assertNull(resp.getBody());
    }

    // -------------------- delete --------------------

    @Test
    void testDelete_Success() {
        when(campeonatoService.deleteCampeonato(3L)).thenReturn(true);

        ResponseEntity<Void> resp = campeonatoController.delete(3L);

        assertEquals(204, resp.getStatusCodeValue());
        assertNull(resp.getBody());
    }

    @Test
    void testDelete_NotFound() {
        when(campeonatoService.deleteCampeonato(99L)).thenReturn(false);

        ResponseEntity<Void> resp = campeonatoController.delete(99L);

        assertEquals(404, resp.getStatusCodeValue());
    }

    @Test
    void testDelete_BusinessError() {
        when(campeonatoService.deleteCampeonato(4L))
            .thenThrow(new ApplicationException("cannot delete"));

        ResponseEntity<Void> resp = campeonatoController.delete(4L);

        assertEquals(400, resp.getStatusCodeValue());
    }

    // -------------------- listCampeonatos (/campeonatos) --------------------

    @Test
    void testListCampeonatos_ByNome() {
        Campeonato c = new Campeonato();
        c.setId(12L);
        c.setNome("ByName");
        when(campeonatoService.findByNome("ByName")).thenReturn(List.of(c));

        ResponseEntity<List<CampeonatoDTO>> resp =
            campeonatoController.listCampeonatos("ByName", null, null);

        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(1, resp.getBody().size());
        assertEquals(12L, resp.getBody().get(0).getId());
    }

    @Test
    void testListCampeonatos_ByMinPlayed() {
        Campeonato c = new Campeonato();
        c.setId(13L);
        c.setNome("Played");
        when(campeonatoService.findByMinGamesPlayed(5L)).thenReturn(List.of(c));

        ResponseEntity<List<CampeonatoDTO>> resp =
            campeonatoController.listCampeonatos(null, 5L, null);

        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(1, resp.getBody().size());
        assertEquals("Played", resp.getBody().get(0).getNome());
    }

    @Test
    void testListCampeonatos_ByMinPending() {
        Campeonato c = new Campeonato();
        c.setId(14L);
        c.setNome("Pending");
        when(campeonatoService.findByMinGamesPending(2L)).thenReturn(List.of(c));

        ResponseEntity<List<CampeonatoDTO>> resp =
            campeonatoController.listCampeonatos(null, null, 2L);

        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(1, resp.getBody().size());
        assertEquals("Pending", resp.getBody().get(0).getNome());
    }

    @Test
    void testListCampeonatos_AllDefault() {
        Campeonato c = new Campeonato();
        c.setId(15L);
        c.setNome("All");
        when(campeonatoService.getAllCampeonatos()).thenReturn(List.of(c));

        ResponseEntity<List<CampeonatoDTO>> resp =
            campeonatoController.listCampeonatos(null, null, null);

        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(1, resp.getBody().size());
        assertEquals("All", resp.getBody().get(0).getNome());
    }
}
