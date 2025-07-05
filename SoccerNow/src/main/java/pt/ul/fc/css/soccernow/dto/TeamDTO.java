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

    public TeamDTO() { }

    // Para respuestas (con id y lista de jugadores)
    public TeamDTO(Long id, String name, Set<Long> playerIds) {
        this.id = id;
        this.name = name;
        this.playerIds = playerIds;
    }

    // Para creación o actualización (sin id)
    public TeamDTO(String name, Set<Long> playerIds) {
        this(null, name, playerIds);
    }

    // --- Getters / Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<Long> getPlayerIds() { return playerIds; }
    public void setPlayerIds(Set<Long> playerIds) { this.playerIds = playerIds; }

    /**
     * Helper para convertir entidad Team → TeamDTO
     */
    public static TeamDTO fromEntity(Team t) {
        return new TeamDTO(
            t.getId(),
            t.getName(),
            t.getPlayers() != null
                ? t.getPlayers().stream().map(Player::getId).collect(Collectors.toSet())
                : Set.of()
        );
    }
}
