// src/main/java/pt/ul/fc/css/soccernow/config/BootstrapData.java
package pt.ul.fc.css.soccernow.config;

import java.time.LocalDateTime;
import java.util.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pt.ul.fc.css.soccernow.domain.Campeonato;
import pt.ul.fc.css.soccernow.domain.User.PreferredPosition;
import pt.ul.fc.css.soccernow.dto.PlayerCreateDTO;
import pt.ul.fc.css.soccernow.dto.TeamDTO;
import pt.ul.fc.css.soccernow.repository.CampeonatoRepository;
import pt.ul.fc.css.soccernow.service.*;

/**
 * Semilla de datos de demostración EXTENDIDA.
 * – 16 jugadores   – 4 equipos equilibrados (1 x posición cada uno)
 * – 3 árbitros     – 2 campeonatos con 4 partidos oficiales
 * – 2 amistosos    – 1 partido pasado con resultado en cada tipo
 */
@Configuration
public class BootstrapData {

  /* Tabla auxiliar de posiciones para el ciclo 0-1-2-3 */
  private static final PreferredPosition[] POS = {
      PreferredPosition.PORTERO,
      PreferredPosition.DEFENSA,
      PreferredPosition.CENTROCAMPISTA,
      PreferredPosition.DELANTERO
  };

  @Bean
  CommandLineRunner seedDatabase(
      PlayerService        playerService,
      TeamService          teamService,
      RefereeService       refereeService,
      JogoService          jogoService,
      CampeonatoRepository campeonatoRepo) {

    return args -> {

      /* ─────────────── 1. JUGADORES ─────────────── */
      record Spec(String name, PreferredPosition pos) {}
      List<Spec> specs = List.of(
          new Spec("Alice", POS[0]), new Spec("Bob",   POS[1]),
          new Spec("Cathy", POS[2]), new Spec("Dan",   POS[3]),

          new Spec("Eve",   POS[0]), new Spec("Frank", POS[1]),
          new Spec("Grace", POS[2]), new Spec("Hugo",  POS[3]),

          new Spec("Ivy",   POS[0]), new Spec("Jack",  POS[1]),
          new Spec("Kate",  POS[2]), new Spec("Leo",   POS[3]),

          new Spec("Mia",   POS[0]), new Spec("Nick",  POS[1]),
          new Spec("Olga",  POS[2]), new Spec("Pete",  POS[3])
      );

      /* Se guardan EXACTAMENTE en este orden → id 1,2,3,4,… */
      Map<String,Long> id = new LinkedHashMap<>();
      for (Spec s : specs) {
        Long pid = buildPlayer(
            s.name(),
            s.name().toLowerCase() + "@mail.com",
            s.pos(),
            playerService);
        id.put(s.name().toLowerCase(), pid);
      }

      /* ─────────────── 2. EQUIPOS (4 jugadores) ─────────────── */
      Long dragones = teamService.createTeam(new TeamDTO(
          "Dragones", Set.of(id.get("alice"), id.get("bob"),
                             id.get("cathy"), id.get("dan")))).getId();

      Long tigres = teamService.createTeam(new TeamDTO(
          "Tigres",   Set.of(id.get("eve"),   id.get("frank"),
                             id.get("grace"), id.get("hugo")))).getId();

      Long aguilas = teamService.createTeam(new TeamDTO(
          "Águilas",  Set.of(id.get("ivy"),  id.get("jack"),
                             id.get("kate"), id.get("leo")))).getId();

      Long lobos = teamService.createTeam(new TeamDTO(
          "Lobos",    Set.of(id.get("mia"),  id.get("nick"),
                             id.get("olga"), id.get("pete")))).getId();

      /* ─────────────── 3. ÁRBITROS ─────────────── */
      Long laura = refereeService.createReferee("Laura", "laura@mail.com", true ).getId();
      Long mike  = refereeService.createReferee("Mike",  "mike@mail.com",  false).getId();
      Long sara  = refereeService.createReferee("Sara",  "sara@mail.com",  true ).getId();

      /* ─────────────── 4. CAMPEONATOS ─────────────── */
      Campeonato primavera = new Campeonato(
          "Liga Primavera", "Liga", "Fútbol",
          Set.of(teamService.getTeamById(dragones).orElseThrow(),
                 teamService.getTeamById(tigres).orElseThrow(),
                 teamService.getTeamById(aguilas).orElseThrow()));
      campeonatoRepo.save(primavera);

      Campeonato verano = new Campeonato(
          "Copa Verano", "Copa", "Fútbol-7",
          Set.of(teamService.getTeamById(lobos).orElseThrow(),
                 teamService.getTeamById(dragones).orElseThrow(),
                 teamService.getTeamById(tigres).orElseThrow()));
      campeonatoRepo.save(verano);

      /* ─────────────── 5. PARTIDOS ─────────────── */

      // Liga Primavera
      jogoService.criarJogo(LocalDateTime.now().plusDays(2), "Estadio Nacional",
          false, dragones, tigres, Set.of(laura), laura);

      var jp1 = jogoService.criarJogo(LocalDateTime.now().minusDays(4), "Complejo Sur",
          false, aguilas, dragones, Set.of(laura), laura);
      jogoService.registrarResultado(jp1.getId(), 0, 3, dragones);

      // Copa Verano
      jogoService.criarJogo(LocalDateTime.now().plusDays(7), "Arena Playa",
          false, lobos, tigres, Set.of(sara), sara);

      var cv1 = jogoService.criarJogo(LocalDateTime.now().minusDays(1), "Mini-Stadium",
          false, tigres, dragones, Set.of(sara), sara);
      jogoService.registrarResultado(cv1.getId(), 1, 1, null);   // empate

      // Amistosos
      jogoService.criarJogo(LocalDateTime.now().plusWeeks(2), "Campo Municipal",
          true, aguilas, lobos, Set.of(mike), null);

      var amistosoPrevio = jogoService.criarJogo(LocalDateTime.now().minusWeeks(3), "Viejo Coliseo",
          true, lobos, aguilas, Set.of(mike), null);
      jogoService.registrarResultado(amistosoPrevio.getId(), 2, 0, lobos);

      System.out.println("✅  Datos de demo extendidos cargados.");
    };
  }

  /* ─────────── Helper para crear jugador ─────────── */
  private static Long buildPlayer(String name,
                                  String email,
                                  PreferredPosition pos,
                                  PlayerService playerService) {

    PlayerCreateDTO dto = new PlayerCreateDTO();
    dto.setName(name);
    dto.setEmail(email);
    dto.setPassword("pass");
    dto.setPreferredPosition(pos.name());
    dto.setGoals(0);
    dto.setCards(0);
    return playerService.createPlayer(dto).getId();
  }
}
