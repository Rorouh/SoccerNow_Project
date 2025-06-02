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

    @ManyToMany
    @JoinTable(
            name = "team_players",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private Set<Player> players;

    @OneToMany(mappedBy = "homeTeam")
    private Set<Jogo> jogosComoVisitada;

    @OneToMany(mappedBy = "awayTeam")
    private Set<Jogo> jogosComoVisitante;

    public Team() {}

    public Team(String name) {
        this.name = name;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public void setPlayers(Set<Player> players) {
        this.players = players;
    }

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

    //SI quisiera acceder a los campeonatos que esta un equipo inscrito desde equipo, utilizariamos esto:
    //@ManyToMany(mappedBy = "participantes")
    //private Set<Campeonato> campeonatos = new HashSet<>();

}
