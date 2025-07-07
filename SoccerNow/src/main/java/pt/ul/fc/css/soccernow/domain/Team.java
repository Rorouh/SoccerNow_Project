package pt.ul.fc.css.soccernow.domain;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /** NUEVOS CAMPOS persistentes -------------------- */
    private int wins;
    private int draws;
    private int losses;
    private int achievements;
    /** ------------------------------------------------ */

    @ManyToMany
    @JoinTable(
        name = "team_players",
        joinColumns = @JoinColumn(name = "team_id"),
        inverseJoinColumns = @JoinColumn(name = "player_id"))
    private Set<Player> players;

    @OneToMany(mappedBy = "homeTeam")  private Set<Jogo> jogosComoVisitada;
    @OneToMany(mappedBy = "awayTeam")  private Set<Jogo> jogosComoVisitante;

    /* ---------- getters / setters ---------- */
    public Long getId()                { return id; }
    public void setId(Long id)         { this.id = id; }

    public String getName()            { return name; }
    public void setName(String name)   { this.name = name; }

    public int getWins()               { return wins; }
    public void setWins(int wins)      { this.wins = wins; }

    public int getDraws()              { return draws; }
    public void setDraws(int draws)    { this.draws = draws; }

    public int getLosses()             { return losses; }
    public void setLosses(int losses)  { this.losses = losses; }

    public int getAchievements()       { return achievements; }
    public void setAchievements(int achievements) { this.achievements = achievements; }

    public Set<Player> getPlayers()    { return players; }
    public void setPlayers(Set<Player> players) { this.players = players; }

    public Set<Jogo> getJogosComoVisitada() {
        return jogosComoVisitada;
    }

    public void setJogosComoVisitada(Set<Jogo> jogosComoVisitada) {
        this.jogosComoVisitada = jogosComoVisitada;
    }

    public Set<Jogo> getJogosComoVisitante() {
        return jogosComoVisitante;
    }

    
    public void setJogosComoVisitante(Set<Jogo> jogosComoVisitante) {
        this.jogosComoVisitante = jogosComoVisitante;
    }
 
    /** Utilidad: nº jugadores */
    @Transient                         // no se persiste; solo para UI
    public int getPlayerCount() {
        return players == null ? 0 : players.size();
    } 
    public boolean isMissingPosition(Player.PreferredPosition pos) {
        return players.stream().noneMatch(p -> p.getPreferredPosition() == pos);
    }
}
