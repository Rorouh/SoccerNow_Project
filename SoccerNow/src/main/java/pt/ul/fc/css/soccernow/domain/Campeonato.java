// src/main/java/pt/ul/fc/css/soccernow/domain/Campeonato.java
package pt.ul.fc.css.soccernow.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "campeonatos")
public class Campeonato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private String modalidade;

    @Column(nullable = false)
    private String formato;

    /** 
     * Equipos participantes en este campeonato. 
     * Relación M:N a través de la tabla intermedia "campeonato_team". 
     */
    @ManyToMany
    @JoinTable(
        name = "campeonato_team",
        joinColumns = @JoinColumn(name = "campeonato_id"),
        inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private Set<Team> participantes = new HashSet<>();

    /**
     * Juegos que forman parte de este campeonato.
     * El campo “campeonato” ya existe en Jogo.java (ManyToOne).
     */
    @OneToMany(mappedBy = "campeonato", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Jogo> jogos = new HashSet<>();

    public Campeonato() { }

    public Campeonato(String nome, String modalidade, String formato, Set<Team> participantes) {
        this.nome = nome;
        this.modalidade = modalidade;
        this.formato = formato;
        this.participantes = participantes;
    }
    
    /** ➜ NUEVO: sin lista de equipos (útil en BootstrapData) */
    public Campeonato(String nome, String modalidade, String formato) {
        this(nome, modalidade, formato, null);
    }
    // --- Getters y setters ---

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getModalidade() {
        return modalidade;
    }
    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public String getFormato() {
        return formato;
    }
    public void setFormato(String formato) {
        this.formato = formato;
    }

    public Set<Team> getParticipantes() {
        return participantes;
    }
    public void setParticipantes(Set<Team> participantes) {
        this.participantes = participantes;
    }

    public Set<Jogo> getJogos() {
        return jogos;
    }
    public void setJogos(Set<Jogo> jogos) {
        this.jogos = jogos;
    }
}
