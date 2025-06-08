package pt.ul.fc.css.soccernow.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import java.util.Set;
import java.util.stream.Stream;

@Entity
public class Player extends User {

    // ... outros campos e métodos ...

    /**
     * Retorna o número de jogos únicos em que o jogador participou
     */
    public int getGames() {
        if (teams == null || teams.isEmpty()) return 0;
        return teams.stream()
            .flatMap(team -> {
                // Recupera todos os jogos como mandante e visitante
                Stream<Jogo> jogosMandante = team.getJogosComoVisitada() != null ? team.getJogosComoVisitada().stream() : Stream.empty();
                Stream<Jogo> jogosVisitante = team.getJogosComoVisitante() != null ? team.getJogosComoVisitante().stream() : Stream.empty();
                return Stream.concat(jogosMandante, jogosVisitante);
            })
            .filter(jogo -> jogo.getEstatisticas() != null && jogo.getEstatisticas().stream().anyMatch(est -> est.getPlayer() != null && est.getPlayer().getId().equals(this.getId())))
            .map(Jogo::getId)
            .distinct()
            .toList()
            .size();
    }


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
