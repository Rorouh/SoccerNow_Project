package pt.ul.fc.css.soccernow.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ul.fc.css.soccernow.domain.*;
import pt.ul.fc.css.soccernow.repository.*;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;
import pt.ul.fc.css.soccernow.service.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JogoServiceTest {

    @InjectMocks
    private JogoService jogoService;
    @Mock
    private JogoRepository jogoRepository;
    @Mock
    private ResultadoRepository resultadoRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private RefereeRepository refereeRepository;

    private LocalDateTime now;
    private Team home;
    private Team away;
    private Referee refCertified;
    private Referee refNotCertified;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().plusDays(1);
        home = new Team(); home.setId(1L);
        away = new Team(); away.setId(2L);
        refCertified = new Referee(); refCertified.setId(10L); refCertified.setCertified(true);
        refNotCertified = new Referee(); refNotCertified.setId(11L); refNotCertified.setCertified(false);
    }

    @Test
    void crearJogo_mismoEquipo_deberiaLanzarApplicationException() {
        assertThrows(ApplicationException.class, () ->
            jogoService.criarJogo(now, "loc", false, 1L, 1L,
                Set.of(10L), 10L)
        );
    }

    @Test
    void crearJogo_equipoNoExiste_deberiaLanzarNotFoundException() {
        when(teamRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
            jogoService.criarJogo(now, "loc", true, 1L, 2L,
                Set.of(10L), 10L)
        );
    }

    @Test
    void crearJogo_arbitroNoExiste_deberiaLanzarNotFoundException() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(home));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(away));
        when(refereeRepository.findAllById(Set.of(10L))).thenReturn(Collections.emptyList());
        assertThrows(NotFoundException.class, () ->
            jogoService.criarJogo(now, "loc", true, 1L, 2L,
                Set.of(10L), null)
        );
    }

    @Test
    void crearJogo_amistoso_conArbitroNoCertificado_deberiaLanzarApplicationException() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(home));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(away));
        when(refereeRepository.findAllById(Set.of(11L))).thenReturn(List.of(refNotCertified));
        assertThrows(ApplicationException.class, () ->
            jogoService.criarJogo(now, "loc", false, 1L, 2L,
                Set.of(11L), null)
        );
    }

    @Test
    void crearJogo_exitoso_deberiaGuardarYRetornarJogo() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(home));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(away));
        when(refereeRepository.findAllById(Set.of(10L))).thenReturn(List.of(refCertified));
        when(jogoRepository.save(any(Jogo.class))).thenAnswer(inv -> inv.getArgument(0));

        Jogo result = jogoService.criarJogo(now, "loc", true, 1L, 2L,
            Set.of(10L), 10L);

        assertEquals(home, result.getHomeTeam());
        assertEquals(away, result.getAwayTeam());
        assertTrue(result.getReferees().contains(refCertified));
        assertEquals(refCertified, result.getPrimaryReferee());
    }

    @Test
    void registarResultado_formatoInvalido_deberiaLanzarApplicationException() {
        when(jogoRepository.findById(5L)).thenReturn(Optional.of(new Jogo()));
        assertThrows(ApplicationException.class, () ->
            jogoService.registarResultado(5L, "badformat", null)
        );
    }

    @Test
    void registarResultado_juegoNoExiste_deberiaLanzarNotFoundException() {
        when(jogoRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
            jogoService.registarResultado(5L, "1-0", null)
        );
    }

    @Test
    void registarResultado_yaRegistrado_deberiaLanzarApplicationException() {
        Jogo j = new Jogo(); j.setResultado(new Resultado());
        when(jogoRepository.findById(5L)).thenReturn(Optional.of(j));
        assertThrows(ApplicationException.class, () ->
            jogoService.registarResultado(5L, "1-0", null)
        );
    }

    @Test
    void registarResultado_ganadorNoParticipa_deberiaLanzarApplicationException() {
        Jogo j = new Jogo(); j.setHomeTeam(home); j.setAwayTeam(away);
        when(jogoRepository.findById(5L)).thenReturn(Optional.of(j));
        when(teamRepository.findById(99L)).thenReturn(Optional.of(new Team()));
        assertThrows(ApplicationException.class, () ->
            jogoService.registarResultado(5L, "2-1", 99L)
        );
    }

    @Test
    void registarResultado_exitoso_deberiaGuardarYRetornarResultado() {
        Jogo j = new Jogo(); j.setHomeTeam(home); j.setAwayTeam(away);
        when(jogoRepository.findById(5L)).thenReturn(Optional.of(j));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(home));
        when(jogoRepository.save(any(Jogo.class))).thenReturn(j);
        when(resultadoRepository.save(any(Resultado.class))).thenAnswer(inv -> inv.getArgument(0));

        Resultado res = jogoService.registarResultado(5L, "3-1", 1L);
        assertEquals("3-1", res.getPlacar());
        assertEquals(home, res.getEquipaVitoriosa());
    }

    @Test
    void cancelarJogo_juegoNoExiste_deberiaLanzarNotFoundException() {
        when(jogoRepository.findById(7L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
            jogoService.cancelarJogo(7L)
        );
    }

    @Test
    void cancelarJogo_conResultado_deberiaLanzarApplicationException() {
        Jogo j = new Jogo(); j.setResultado(new Resultado());
        when(jogoRepository.findById(7L)).thenReturn(Optional.of(j));
        assertThrows(ApplicationException.class, () ->
            jogoService.cancelarJogo(7L)
        );
    }

    @Test
    void cancelarJogo_yaCancelado_deberiaLanzarApplicationException() {
        Jogo j = new Jogo(); j.setCancelado(true);
        when(jogoRepository.findById(7L)).thenReturn(Optional.of(j));
        assertThrows(ApplicationException.class, () ->
            jogoService.cancelarJogo(7L)
        );
    }

    @Test
    void cancelarJogo_exitoso_deberiaMarcarCanceladoYGuardar() throws Exception {
        Jogo j = new Jogo();
        when(jogoRepository.findById(7L)).thenReturn(Optional.of(j));
        jogoService.cancelarJogo(7L);
        assertTrue(j.isCancelado());
        verify(jogoRepository).save(j);
    }
}
