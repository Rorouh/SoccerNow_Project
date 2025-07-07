package pt.ul.fc.css.soccernow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import pt.ul.fc.css.soccernow.domain.Team;
import pt.ul.fc.css.soccernow.domain.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class TeamDTO {

    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @NotEmpty(message = "Debe especificar al menos un jugador")
    private Set<Long> playerIds;

    /* ───── NUEVOS CAMPOS ───── */
    private int playerCount;   // nº de jugadores
    private int wins;
    private int draws;
    private int losses;
    private int achievements;
    /* ───────────────────────── */

    /* ---------- Constructores ---------- */
    public TeamDTO() {}

    /** Constructor completo (respuesta) */
    public TeamDTO(Long id, String name, Set<Long> playerIds,
                   int playerCount, int wins, int draws,
                   int losses, int achievements) {
        this.id = id;
        this.name = name;
        this.playerIds = playerIds;
        this.playerCount = playerCount;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.achievements = achievements;
    }
    public TeamDTO(Long id, String name, Set<Long> playerIds) {
        // delega en el constructor largo pasando “0” en las estadísticas
        this(id, name, playerIds,                 // datos básicos
        playerIds != null ? playerIds.size() : 0,  // playerCount
        0, 0, 0, 0);                          // wins, draws, losses, achievements
    }
    /** Constructor simple (crear/editar) */
    public TeamDTO(String name, Set<Long> playerIds) {
        this(null, name, playerIds, 0, 0, 0, 0, 0);
    }

    /* ---------- Getters / Setters ---------- */
    public Long getId()                { return id; }
    public void setId(Long id)         { this.id = id; }

    public String getName()            { return name; }
    public void setName(String name)   { this.name = name; }

    public Set<Long> getPlayerIds()           { return playerIds; }
    public void setPlayerIds(Set<Long> pIds)  { this.playerIds = pIds; }

    public int getPlayerCount()        { return playerCount; }
    public void setPlayerCount(int c)  { this.playerCount = c; }

    public int getWins()               { return wins; }
    public void setWins(int wins)      { this.wins = wins; }

    public int getDraws()              { return draws; }
    public void setDraws(int draws)    { this.draws = draws; }

    public int getLosses()             { return losses; }
    public void setLosses(int losses)  { this.losses = losses; }

    public int getAchievements()                 { return achievements; }
    public void setAchievements(int achievements){ this.achievements = achievements; }

    /* ---------- Helper de mapeo ---------- */
    public static TeamDTO fromEntity(Team t) {
        return new TeamDTO(
            t.getId(),
            t.getName(),
            t.getPlayers() != null
                ? t.getPlayers().stream().map(Player::getId).collect(Collectors.toSet())
                : Set.of(),
            t.getPlayers() == null ? 0 : t.getPlayers().size(),
            t.getWins(),
            t.getDraws(),
            t.getLosses(),
            t.getAchievements()
        );
    }
}
