package pt.ul.fc.css.soccernow.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;

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

    private int golosCasa;
    private int golosFora;

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

    public int getGolosCasa() { return golosCasa; }
    public void setGolosCasa(int golosCasa) { this.golosCasa = golosCasa; }

    public int getGolosFora() { return golosFora; }
    public void setGolosFora(int golosFora) { this.golosFora = golosFora; }
}
