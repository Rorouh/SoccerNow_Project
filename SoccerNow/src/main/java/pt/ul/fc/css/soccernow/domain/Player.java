package pt.ul.fc.css.soccernow.domain;

import jakarta.persistence.*;
import java.util.Set;

@Entity
public class Player extends User {

    private String preferredPosition;

    @ManyToMany(mappedBy = "players")
    private Set<Team> teams;

    // Getters y setters
    public String getPreferredPosition() {
        return preferredPosition;
    }

    public void setPreferredPosition(String preferredPosition) {
        this.preferredPosition = preferredPosition;
    }

    public Set<Team> getTeams() {
        return teams;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }
}
