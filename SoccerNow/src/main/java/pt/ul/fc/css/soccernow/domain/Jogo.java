// src/main/java/pt/ul/fc/css/soccernow/domain/Jogo.java
package pt.ul.fc.css.soccernow.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
public class Jogo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateTime;
    private String location;
    private boolean amigavel;
    private Integer homeScore;
    private Integer awayScore;

    @ManyToOne(optional = false)
    private Team homeTeam;

    @ManyToOne(optional = false)
    private Team awayTeam;

    @ManyToOne
    private Campeonato campeonato;

    @ManyToMany
    @JoinTable(name = "jogo_arbitros",
        joinColumns = @JoinColumn(name = "jogo_id"),
        inverseJoinColumns = @JoinColumn(name = "arbitro_id"))
    private Set<Referee> referees;

    @ManyToOne
    private Referee primaryReferee;

    @OneToMany(mappedBy = "jogo", cascade = CascadeType.ALL)
    private Set<Cartao> cartoes;

    @OneToMany(mappedBy = "jogo", cascade = CascadeType.ALL)
    private Set<Estatisticas> estatisticas;

    @OneToOne(mappedBy = "jogo", cascade = CascadeType.ALL)
    private Resultado resultado;

    /** --------------------- NUEVO CAMPO --------------------- */
    @Column(nullable = false)
    private boolean cancelado = false;
    /** -------------------------------------------------------- */

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public boolean isAmigavel() { return amigavel; }
    public void setAmigavel(boolean amigavel) { this.amigavel = amigavel; }
    public Integer getHomeScore() { return homeScore; }
    public void setHomeScore(Integer homeScore) { this.homeScore = homeScore; }
    public Integer getAwayScore() { return awayScore; }
    public void setAwayScore(Integer awayScore) { this.awayScore = awayScore; }
    public Team getHomeTeam() { return homeTeam; }
    public void setHomeTeam(Team homeTeam) { this.homeTeam = homeTeam; }
    public Team getAwayTeam() { return awayTeam; }
    public void setAwayTeam(Team awayTeam) { this.awayTeam = awayTeam; }
    public Campeonato getCampeonato() { return campeonato; }
    public void setCampeonato(Campeonato campeonato) { this.campeonato = campeonato; }
    public Set<Referee> getReferees() { return referees; }
    public void setReferees(Set<Referee> referees) { this.referees = referees; }
    public Referee getPrimaryReferee() { return primaryReferee; }
    public void setPrimaryReferee(Referee primaryReferee) { this.primaryReferee = primaryReferee; }
    public Set<Cartao> getCartoes() { return cartoes; }
    public void setCartoes(Set<Cartao> cartoes) { this.cartoes = cartoes; }
    public Set<Estatisticas> getEstatisticas() { return estatisticas; }
    public void setEstatisticas(Set<Estatisticas> estatisticas) { this.estatisticas = estatisticas; }
    public Resultado getResultado() { return resultado; }
    public void setResultado(Resultado resultado) { this.resultado = resultado; }

    // Getter y setter del nuevo campo cancelado:
    public boolean isCancelado() {
        return cancelado;
    }
    public void setCancelado(boolean cancelado) {
        this.cancelado = cancelado;
    }

    // Alias para compatibilidad con tests
    public LocalDateTime getDataHora() { return dateTime; }
    public void setDataHora(LocalDateTime dataHora) { this.dateTime = dataHora; }
    public String getLocal() { return location; }
    public void setLocal(String local) { this.location = local; }

    // Helper para equipas
    @Transient
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Set<Team> getEquipas() {
        return Set.of(homeTeam, awayTeam);
    }
    public void setEquipas(Set<Team> equipas) {
        if (equipas == null || equipas.size() != 2) return;
        var iter = equipas.iterator();
        this.homeTeam = iter.next();
        this.awayTeam = iter.next();
    }

    // Helper para árbitros
    @Transient
    public Set<Referee> getArbitros() { return referees; }
    public void setArbitros(Set<Referee> arbitros) { this.referees = arbitros; }

    // Construtor auxiliar
    public Jogo() {}
    public Jogo(LocalDateTime dataHora, String local, boolean amigavel) {
        this.dateTime = dataHora;
        this.location = local;
        this.amigavel = amigavel;
    }
}
