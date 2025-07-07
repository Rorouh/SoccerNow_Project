// src/main/java/pt/ul/fc/css/soccernow/config/BootstrapData.java
package pt.ul.fc.css.soccernow.config;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pt.ul.fc.css.soccernow.domain.Team;
import pt.ul.fc.css.soccernow.domain.User.PreferredPosition;
import pt.ul.fc.css.soccernow.dto.PlayerCreateDTO;
import pt.ul.fc.css.soccernow.dto.TeamDTO;
import pt.ul.fc.css.soccernow.repository.CampeonatoRepository;
import pt.ul.fc.css.soccernow.domain.Campeonato;
import pt.ul.fc.css.soccernow.service.*;

/**
 * Genera datos de demo completos al arrancar la aplicación.
 * – 8 jugadores   – 3 equipos  
 * – 2 árbitros (1 sin certificar)  
 * – 1 campeonato con un partido oficial  
 * – 1 amistoso  
 * – 1 partido pasado con resultado
 */
@Configuration
public class BootstrapData {

  @Bean
  CommandLineRunner seedDatabase(
      PlayerService      playerService,
      TeamService        teamService,
      RefereeService     refereeService,
      JogoService        jogoService,
      CampeonatoRepository campeonatoRepo) {

    return args -> {

      /* ─────────────  Jugadores  ───────────── */
      List<Long> playerIds = List.of(
          buildPlayer("Alice" , "alice@mail.com" , PreferredPosition.DELANTERO , playerService),
          buildPlayer("Bob"   , "bob@mail.com"   , PreferredPosition.CENTROCAMPISTA , playerService),
          buildPlayer("Cathy" , "cathy@mail.com" , PreferredPosition.DEFENSA   , playerService),
          buildPlayer("Dan"   , "dan@mail.com"   , PreferredPosition.PORTERO   , playerService),
          buildPlayer("Eve"   , "eve@mail.com"   , PreferredPosition.DELANTERO , playerService),
          buildPlayer("Frank" , "frank@mail.com" , PreferredPosition.DEFENSA   , playerService),
          buildPlayer("Grace" , "grace@mail.com" , PreferredPosition.CENTROCAMPISTA, playerService),
          buildPlayer("Hugo"  , "hugo@mail.com"  , PreferredPosition.DEFENSA   , playerService)
      );

      /* ─────────────  Equipos  ───────────── */
      Long dragons = teamService.createTeam(
              new TeamDTO("Dragones", Set.of(playerIds.get(0), playerIds.get(1), playerIds.get(2))))
              .getId();

      Long tigers  = teamService.createTeam(
              new TeamDTO("Tigres", Set.of(playerIds.get(3), playerIds.get(4), playerIds.get(5))))
              .getId();

      Long eagles  = teamService.createTeam(
              new TeamDTO("Águilas", Set.of(playerIds.get(6), playerIds.get(7))))
              .getId();

      /* ─────────────  Árbitros  ───────────── */
      Long refLaura = refereeService.createReferee("Laura", "laura@mail.com", true ).getId(); // certificado
      Long refMike  = refereeService.createReferee("Mike" , "mike@mail.com" , false).getId(); // NO certificado

      /* ─────────────  Campeonato  ───────────── */
      Campeonato liga = new Campeonato(
              "Liga Primavera",         // nome
              "Liga",                   // formato
              "Fútbol",                 // modalidade
              Set.of(                    // equipos participantes
                      teamService.getTeamById(dragons).orElseThrow(),
                      teamService.getTeamById(tigers ).orElseThrow())
      );
      campeonatoRepo.save(liga);

      /* ─────────────  Partidos  ───────────── */

      // 1) Partido de campeonato (oficial) – sólo árbitros certificados
      jogoService.criarJogo(
          LocalDateTime.now().plusDays(3),
          "Estadio Nacional",
          false,                       // amigavel = false  → campeonato
          dragons,
          tigers,
          Set.of(refLaura),            // árbitro certificado
          refLaura
      );

      // 2) Amistoso futuro con árbitro sin certificar permitido
      jogoService.criarJogo(
          LocalDateTime.now().plusWeeks(1),
          "Campo Municipal",
          true,                        // amistoso
          eagles,
          dragons,
          Set.of(refMike),
          null
      );

      // 3) Partido pasado (hace 5 días) con resultado registrado
      var jogoPasado = jogoService.criarJogo(
          LocalDateTime.now().minusDays(5),
          "Arena Central",
          false,
          dragons,
          eagles,
          Set.of(refLaura),
          refLaura
      );
      jogoService.registrarResultado(
          jogoPasado.getId(),
          2, 1,                         // 2-1
          dragons                       // vencedor
      );

      System.out.println("✅  Datos de demo cargados correctamente");
    };
  }

  /* helper para crear jugador */
  private static Long buildPlayer(
      String name,
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
