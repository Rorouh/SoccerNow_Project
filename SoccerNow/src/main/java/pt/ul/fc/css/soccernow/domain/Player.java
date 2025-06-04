package pt.ul.fc.css.soccernow.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import java.util.Set;

@Entity
public class Player extends User {

    @Column(nullable = false)
    private int goals = 0;

    @Column(nullable = false)
    private int cards = 0;

    @ManyToMany(mappedBy = "players")
    private Set<Team> teams;

    public Player() {}

    public Player(String name, String email, String password, PreferredPosition preferredPosition) {
        super(name, email, password, "PLAYER", preferredPosition);
    }

    public Set<Team> getTeams() {
        return teams;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getCards() {
        return cards;
    }

    public void setCards(int cards) {
        this.cards = cards;
    }
}
