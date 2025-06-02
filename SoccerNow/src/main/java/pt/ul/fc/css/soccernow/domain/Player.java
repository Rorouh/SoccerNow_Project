package pt.ul.fc.css.soccernow.domain;

import jakarta.persistence.*;
import java.util.Set;

@Entity
public class Player extends User {

    @Column(nullable = false)
    private String preferredPosition;

    @ManyToMany(mappedBy = "players")
    private Set<Team> teams;

    public Player() {}

    public Player(String name, String email, String password, PreferredPosition preferredPosition) {
        super(name, email, password, "PLAYER", preferredPosition);
    }

    @Override
    public PreferredPosition getPreferredPosition() {
        return super.getPreferredPosition();
    }

    @Override
    public void setPreferredPosition(PreferredPosition preferredPosition) {
        super.setPreferredPosition(preferredPosition);
    }
    
    public Set<Team> getTeams() {
        return teams;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }
}
