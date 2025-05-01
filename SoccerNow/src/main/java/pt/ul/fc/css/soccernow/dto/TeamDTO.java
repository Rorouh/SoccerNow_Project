package pt.ul.fc.css.soccernow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class TeamDTO {

    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private Set<Long> playerIds;

    public TeamDTO() {}

    public TeamDTO(Long id, String name, Set<Long> playerIds) {
        this.id = id;
        this.name = name;
        this.playerIds = playerIds;
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
