package pt.ul.fc.css.soccernow.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ul.fc.css.soccernow.domain.*;
import pt.ul.fc.css.soccernow.repository.*;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;
import pt.ul.fc.css.soccernow.service.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JogoServiceTest {

    @Mock
    private JogoRepository jogoRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private RefereeRepository refereeRepository;
    @Mock
    private ResultadoRepository resultadoRepository;

    @InjectMocks
    private JogoService jogoService;

    @Test
    void testCriarJogo_Amigavel_Success() throws NotFoundException, ApplicationException {
        LocalDateTime dataHora = LocalDateTime.now().plusDays(1);
        String local = "Estádio Teste";
        Long equipa1Id = 1L;
        Long equipa2Id = 2L;
        Set<Long> arbitroIds = Set.of(10L, 11L, 12L);

        Team team1 = new Team("Team A"); team1.setId(equipa1Id);
        Team team2 = new Team("Team B"); team2.setId(equipa2Id);
        Referee r1 = new Referee("Ref 1", "r1@test.com", "p", true); r1.setId(10L);
        Referee r2 = new Referee("Ref 2", "r2@test.com", "p", true); r2.setId(11L);
        Referee r3 = new Referee("Ref 3", "r3@test.com", "p", true); r3.setId(12L);

        when(teamRepository.findById(equipa1Id)).thenReturn(Optional.of(team1));
        when(teamRepository.findById(equipa2Id)).thenReturn(Optional.of(team2));
        when(refereeRepository.findAllById(arbitroIds)).thenReturn(List.of(r1, r2, r3));
        when(jogoRepository.save(any(Jogo.class))).thenAnswer(inv -> { Jogo j = inv.getArgument(0); j.setId(100L); return j; });

        Jogo result = jogoService.criarJogo(dataHora, local, true, equipa1Id, equipa2Id, arbitroIds, null);

        assertNotNull(result);
        assertEquals(dataHora, result.getDataHora());
        assertEquals(local, result.getLocal());
        assertTrue(result.isAmigavel());
        assertEquals(2, result.getEquipas().size());
        assertEquals(3, result.getArbitros().size());

        verify(teamRepository, times(2)).findById(anyLong());
        verify(refereeRepository, times(1)).findAllById(arbitroIds);
        verify(jogoRepository, times(1)).save(any(Jogo.class));
    }

    // Teste Caso de Uso H: Criar jogo de campeonato - Sucesso
    @Test
    void testCriarJogo_Campeonato_Success() throws NotFoundException, ApplicationException {
        LocalDateTime dataHora = LocalDateTime.now().plusDays(1);
        String local = "Estádio Teste";
        Long equipa1Id = 1L;
        Long equipa2Id = 2L;
        Set<Long> arbitroIds = Set.of(10L, 11L, 12L);

        Team team1 = new Team("Team A"); team1.setId(equipa1Id);
        Team team2 = new Team("Team B"); team2.setId(equipa2Id);
        Referee r1 = new Referee("Ref 1", "r1@test.com", "p", true); r1.setId(10L);
        Referee r2 = new Referee("Ref 2", "r2@test.com", "p", true); r2.setId(11L);
        Referee r3 = new Referee("Ref 3", "r3@test.com", "p", true); r3.setId(12L);

        when(teamRepository.findById(equipa1Id)).thenReturn(Optional.of(team1));
        when(teamRepository.findById(equipa2Id)).thenReturn(Optional.of(team2));
        when(refereeRepository.findAllById(arbitroIds)).thenReturn(List.of(r1, r2, r3));
        when(jogoRepository.save(any(Jogo.class))).thenAnswer(inv -> { Jogo j = inv.getArgument(0); j.setId(101L); return j; });

        Jogo result = jogoService.criarJogo(dataHora, local, false, equipa1Id, equipa2Id, arbitroIds, r1.getId());

        assertNotNull(result);
        assertFalse(result.isAmigavel());
        verify(teamRepository, times(2)).findById(anyLong());
        verify(refereeRepository).findAllById(arbitroIds);
        verify(jogoRepository).save(any(Jogo.class));
    }

    // Team não encontrada
    @Test
    void testCriarJogo_EquipaNotFound_ThrowsException() {
        Long equipa1Id = 1L;
        Long equipa2Id = 99L;
        Set<Long> arbitroIds = Set.of(10L, 11L, 12L);

        Team team1 = new Team("Team A"); team1.setId(equipa1Id);

        when(teamRepository.findById(equipa1Id)).thenReturn(Optional.of(team1));
        when(teamRepository.findById(equipa2Id)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                jogoService.criarJogo(LocalDateTime.now(), "Local", true, equipa1Id, equipa2Id, arbitroIds, null));

        assertEquals("Team com ID " + equipa2Id + " não encontrada.", ex.getMessage());
        verify(jogoRepository, never()).save(any());
    }

    // Árbitro não encontrado
    @Test
    void testCriarJogo_ArbitroNotFound_ThrowsException() {
        Long equipa1Id = 1L;
        Long equipa2Id = 2L;
        Set<Long> arbitroIds = Set.of(10L, 99L);

        Team team1 = new Team("Team A"); team1.setId(equipa1Id);
        Team team2 = new Team("Team B"); team2.setId(equipa2Id);
        Referee r1 = new Referee("Ref 1", "r1@test.com", "p", true); r1.setId(10L);

        when(teamRepository.findById(equipa1Id)).thenReturn(Optional.of(team1));
        when(teamRepository.findById(equipa2Id)).thenReturn(Optional.of(team2));
        when(refereeRepository.findAllById(arbitroIds)).thenReturn(List.of(r1));

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                jogoService.criarJogo(LocalDateTime.now(), "Local", true, equipa1Id, equipa2Id, arbitroIds, null));

        assertTrue(ex.getMessage().contains("Árbitro(s) não encontrado(s)"));
        verify(jogoRepository, never()).save(any());
    }

    // Equipas iguais
    @Test
    void testCriarJogo_EquipasIguais_ThrowsException() {
        Long equipaId = 1L;
        Set<Long> arbitroIds = Set.of(10L, 11L, 12L);

        ApplicationException ex = assertThrows(ApplicationException.class, () ->
                jogoService.criarJogo(LocalDateTime.now(), "Local", true, equipaId, equipaId, arbitroIds, null));

        assertEquals("As equipas têm de ser diferentes.", ex.getMessage());
        verify(jogoRepository, never()).save(any());
    }

    // Árbitro não certificado em campeonato
    @Test
    void testCriarJogo_Campeonato_ArbitroNaoCertificado_ThrowsException() {
        Long equipa1Id = 1L;
        Long equipa2Id = 2L;
        Set<Long> arbitroIds = Set.of(10L, 11L, 12L);

        Team t1 = new Team("A"); t1.setId(equipa1Id);
        Team t2 = new Team("B"); t2.setId(equipa2Id);
        Referee r1 = new Referee("R1", "r1@test.com", "p", true); r1.setId(10L);
        Referee r2 = new Referee("R2", "r2@test.com", "p", true); r2.setId(11L);
        Referee r3 = new Referee("R3", "r3@test.com", "p", false); r3.setId(12L);

        when(teamRepository.findById(equipa1Id)).thenReturn(Optional.of(t1));
        when(teamRepository.findById(equipa2Id)).thenReturn(Optional.of(t2));
        when(refereeRepository.findAllById(arbitroIds)).thenReturn(List.of(r1, r2, r3));

        ApplicationException ex = assertThrows(ApplicationException.class, () ->
                jogoService.criarJogo(LocalDateTime.now(), "Loc", false, equipa1Id, equipa2Id, arbitroIds, null));

        assertEquals("Todos os árbitros têm de ser certificados para jogos de campeonato.", ex.getMessage());
        verify(jogoRepository, never()).save(any());
    }

    // Registrar resultado - sucesso com vencedor
    @Test
    void testRegistarResultado_Success_ComVencedor() throws NotFoundException, ApplicationException {
        Long jogoId = 100L;
        String placar = "3-2";
        Long equipaVitoriosaId = 1L;

        Jogo jogo = new Jogo(LocalDateTime.now().minusHours(1), "Local", true);
        jogo.setId(jogoId);
        Team t1 = new Team("A"); t1.setId(1L);
        Team t2 = new Team("B"); t2.setId(2L);
        jogo.setEquipas(Set.of(t1, t2));

        when(jogoRepository.findById(jogoId)).thenReturn(Optional.of(jogo));
        when(teamRepository.findById(equipaVitoriosaId)).thenReturn(Optional.of(t1));
        when(resultadoRepository.save(any(Resultado.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jogoRepository.save(any(Jogo.class))).thenReturn(jogo);

        Resultado res = jogoService.registarResultado(jogoId, placar, equipaVitoriosaId);

        assertNotNull(res);
        assertEquals(placar, res.getPlacar());
        assertEquals(equipaVitoriosaId, res.getEquipaVitoriosa().getId());
        assertEquals(res, jogo.getResultado());
    }

    // Registrar resultado - empate
    @Test
    void testRegistarResultado_Success_Empate() throws NotFoundException, ApplicationException {
        Long jogoId = 101L;
        String placar = "1-1";

        Jogo jogo = new Jogo(LocalDateTime.now().minusHours(1), "Loc", true);
        jogo.setId(jogoId);
        Team t1 = new Team("C"); t1.setId(3L);
        Team t2 = new Team("D"); t2.setId(4L);
        jogo.setEquipas(Set.of(t1, t2));

        when(jogoRepository.findById(jogoId)).thenReturn(Optional.of(jogo));
        when(resultadoRepository.save(any(Resultado.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jogoRepository.save(any(Jogo.class))).thenReturn(jogo);

        Resultado res = jogoService.registarResultado(jogoId, placar, null);

        assertNotNull(res);
        assertNull(res.getEquipaVitoriosa());
        assertEquals(placar, res.getPlacar());
    }

    // Registrar resultado - jogo não encontrado
    @Test
    void testRegistarResultado_JogoNotFound_ThrowsException() {
        when(jogoRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                jogoService.registarResultado(999L, "1-0", 1L));

        assertEquals("Jogo com ID 999 não encontrado.", ex.getMessage());
    }

    // Registrar resultado - jogo já tem resultado
    @Test
    void testRegistarResultado_JogoJaTemResultado_ThrowsException() {
        Long jogoId = 110L;
        Jogo jogo = new Jogo(LocalDateTime.now().minusHours(2), "Loc", true);
        jogo.setId(jogoId);
        jogo.setResultado(new Resultado("2-1", null));

        when(jogoRepository.findById(jogoId)).thenReturn(Optional.of(jogo));

        ApplicationException ex = assertThrows(ApplicationException.class, () ->
                jogoService.registarResultado(jogoId, "3-0", 1L));

        assertEquals("Jogo já tem um resultado registado.", ex.getMessage());
    }

    // Registrar resultado - equipe vencedora não encontrada
    @Test
    void testRegistarResultado_EquipaVitoriosaNotFound_ThrowsException() {
        Long jogoId = 120L;
        Long victId = 99L;
        Jogo jogo = new Jogo(LocalDateTime.now().minusHours(1), "Loc", true);
        jogo.setId(jogoId);
        Team t1 = new Team("A"); t1.setId(1L);
        Team t2 = new Team("B"); t2.setId(2L);
        jogo.setEquipas(Set.of(t1, t2));

        when(jogoRepository.findById(jogoId)).thenReturn(Optional.of(jogo));
        when(teamRepository.findById(victId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                jogoService.registarResultado(jogoId, "1-0", victId));

        assertEquals("Team vitoriosa com ID " + victId + " não encontrada.", ex.getMessage());
    }

    // Registrar resultado - equipe vencedora não participou
    @Test
    void testRegistarResultado_EquipaVitoriosaNaoParticipou_ThrowsException() {
        Long jogoId = 130L;
        Long victId = 3L;

        Jogo jogo = new Jogo(LocalDateTime.now().minusHours(1), "Loc", true);
        jogo.setId(jogoId);
        Team t1 = new Team("A"); t1.setId(1L);
        Team t2 = new Team("B"); t2.setId(2L);
        Team externo = new Team("C"); externo.setId(victId);
        jogo.setEquipas(Set.of(t1, t2));

        when(jogoRepository.findById(jogoId)).thenReturn(Optional.of(jogo));
        when(teamRepository.findById(victId)).thenReturn(Optional.of(externo));

        ApplicationException ex = assertThrows(ApplicationException.class, () ->
                jogoService.registarResultado(jogoId, "1-0", victId));

        assertEquals("Team vitoriosa indicada não participou neste jogo.", ex.getMessage());
    }
}
