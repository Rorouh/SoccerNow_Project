// src/main/java/pt/ul/fc/css/soccernow/config/BootstrapData.java
package pt.ul.fc.css.soccernow.config;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pt.ul.fc.css.soccernow.domain.User.PreferredPosition;
import pt.ul.fc.css.soccernow.dto.PlayerCreateDTO;
import pt.ul.fc.css.soccernow.dto.TeamDTO;
import pt.ul.fc.css.soccernow.service.JogoService;
import pt.ul.fc.css.soccernow.service.PlayerService;
import pt.ul.fc.css.soccernow.service.TeamService;

@Configuration
public class BootstrapData {

    @Bean
    CommandLineRunner seedDatabase(PlayerService playerService,
                                   TeamService   teamService,
                                   JogoService   jogoService) {

        return args -> {
            /* 1️⃣ ───────── jugadores / usuarios ───────── */
            Long g1 = playerService.createPlayer(buildPlayer("Alice", "alice@mail.com", PreferredPosition.DELANTERO)).getId();
            Long g2 = playerService.createPlayer(buildPlayer("Bob",   "bob@mail.com",   PreferredPosition.CENTROCAMPISTA)).getId();
            Long g3 = playerService.createPlayer(buildPlayer("Cathy", "cathy@mail.com", PreferredPosition.DEFENSA)).getId();
            Long g4 = playerService.createPlayer(buildPlayer("Dan",   "dan@mail.com",   PreferredPosition.PORTERO)).getId();

            /* 2️⃣ ───────── equipos ───────── */
            Long teamA = teamService.createTeam(new TeamDTO("Dragones", Set.of(g1, g2))).getId();
            Long teamB = teamService.createTeam(new TeamDTO("Tigres",   Set.of(g3, g4))).getId();

            /* 3️⃣ ───────── partido YA JUGADO (para filtros “realizados” + “noche”) ───────── */
            var played = jogoService.criarJogo(
                    LocalDateTime.now().minusDays(3).withHour(21),   // hace 3 días a las 21 h
                    "Estadio Central",
                    true,                                           // amistoso
                    teamA,
                    teamB,
                    Set.of(),                                       // sin árbitros
                    null
            );
            jogoService.registrarResultado(played.getId(), 3, 2, teamA);   // Dragones 3-2 Tigres

            /* 4️⃣ ───────── partido PENDIENTE (para filtros “a realizar” + “mañana”) ───────── */
            jogoService.criarJogo(
                    LocalDateTime.now().plusDays(7).withHour(10),   // dentro de 7 días a las 10 h
                    "Campo Anexo",
                    true,
                    teamA,
                    teamB,
                    Set.of(),
                    null
            );

            System.out.println("✅  Datos de prueba creados correctamente");
        };
    }

    /* helper */
    private static PlayerCreateDTO buildPlayer(String name, String email, PreferredPosition pos) {
        PlayerCreateDTO dto = new PlayerCreateDTO();
        dto.setName(name);
        dto.setEmail(email);
        dto.setPassword("pass");
        dto.setPreferredPosition(pos.name());
        dto.setGoals(0);
        dto.setCards(0);
        return dto;
    }
}
