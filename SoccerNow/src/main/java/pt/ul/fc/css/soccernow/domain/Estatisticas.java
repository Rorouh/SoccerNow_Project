package pt.ul.fc.css.soccernow.domain;

import jakarta.persistence.*;

@Entity
public class Estatisticas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer gols;   

    @ManyToOne 
    Player player;

    @ManyToOne
    private Jogo jogo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getGols() { return gols; }
    public void setGols(Integer gols) { this.gols = gols; }
    public Jogo getJogo() { return jogo; }
    public void setJogo(Jogo jogo) { this.jogo = jogo; }
}
