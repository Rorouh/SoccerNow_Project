package pt.ul.fc.css.soccernow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public class TeamDTO {

    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @NotEmpty(message = "Debe especificar al menos un jugador")
    private Set<Long> playerIds;

    public TeamDTO() { }

    // Para respuestas (con id)
    public TeamDTO(Long id, String name, Set<Long> playerIds) {
        this.id = id;
        this.name = name;
        this.playerIds = playerIds;
    }

    // Para creación (sin id)
    public TeamDTO(String name, Set<Long> playerIds) {
        this(null, name, playerIds);
    }

    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getName() { 
        return name;
    }
    public void setName(String name) { 
        this.name = name; 
    }

    public Set<Long> getPlayerIds() { 
        return playerIds; 
    }
    public void setPlayerIds(Set<Long> playerIds) { 
        this.playerIds = playerIds;
    }
}
