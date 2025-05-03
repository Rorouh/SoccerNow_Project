package pt.ul.fc.css.soccernow.domain;

import jakarta.persistence.*;
import java.util.Set;

@Entity
public class Campeonato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String modalidade;
    private String formato;

    @OneToMany(mappedBy = "campeonato")
    private Set<Jogo> jogos;

    public Long getId() { return id; }
public void setId(Long id) { this.id = id; }
public String getNome() { return nome; }
public void setNome(String nome) { this.nome = nome; }
public String getModalidade() { return modalidade; }
public void setModalidade(String modalidade) { this.modalidade = modalidade; }
public String getFormato() { return formato; }
public void setFormato(String formato) { this.formato = formato; }
public Set<Jogo> getJogos() { return jogos; }
public void setJogos(Set<Jogo> jogos) { this.jogos = jogos; }
}
