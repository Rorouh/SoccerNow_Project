package pt.ul.fc.css.soccernow.domain;

import jakarta.persistence.*;

@Entity
public class Cartao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;

    @ManyToOne 
    Player player;

    //Si queremos saber quien saco la tarjeta, podemos hacer:
    //@ManyToOne Referee referee;

    @ManyToOne
    private Jogo jogo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Jogo getJogo() { return jogo; }
    public void setJogo(Jogo jogo) { this.jogo = jogo; }
}
