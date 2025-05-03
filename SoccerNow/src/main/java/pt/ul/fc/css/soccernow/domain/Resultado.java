package pt.ul.fc.css.soccernow.domain;

import jakarta.persistence.*;

@Entity
public class Resultado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String placar;

    @ManyToOne
    private Team equipaVitoriosa;

    @OneToOne
    @JoinColumn(name = "jogo_id")
    private Jogo jogo;

    public Resultado() {}

    public Resultado(String placar, Team vitoriosa) {
        this.placar = placar;
        this.equipaVitoriosa = vitoriosa;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlacar() { return placar; }
    public void setPlacar(String placar) { this.placar = placar; }
    public Team getEquipaVitoriosa() { return equipaVitoriosa; }
    public void setEquipaVitoriosa(Team equipaVitoriosa) { this.equipaVitoriosa = equipaVitoriosa; }
    public Jogo getJogo() { return jogo; }
    public void setJogo(Jogo jogo) { this.jogo = jogo; }
}
