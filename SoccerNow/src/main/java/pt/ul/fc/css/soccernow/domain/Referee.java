package pt.ul.fc.css.soccernow.domain;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class Referee extends User {

    @Column(nullable = false)
    private boolean certified;
        
    /**
     * Partidos donde fue árbitro principal.
     */
    @OneToMany(mappedBy = "primaryReferee") 
    private Set<Jogo> gamesAsPrimary = new HashSet<>();

    /**
     * Partidos donde fue árbitro asistente (en el set `referees`).
     */
    @ManyToMany(mappedBy = "referees") 
    private Set<Jogo> gamesAsAssistant = new HashSet<>();

    

    /** Constructor vacío (necesario para JPA) */
    public Referee() {
        super();
    }

    public Referee(String name, String email, String password, boolean certified) {
        super(name, email, password, "REFEREE", null);
        this.certified = certified;
    }

    /**
     * Constructor “de conveniencia” que usan los tests (o código manual)
     * para crear un árbitro sin especificar id. JPA asignará el id al hacer save().
     */
    public Referee(int id, String name, String email, String password, boolean certified) {
        super(name, email, password, "REFEREE", null);
        this.certified = certified;
    }


    public boolean isCertified() {
        return certified;
    }

    public void setCertified(boolean certified) {
        this.certified = certified;
    }


    public Set<Jogo> getGamesAsPrimary() {
        return gamesAsPrimary;
    }
    public void setGamesAsPrimary(Set<Jogo> gamesAsPrimary) {
        this.gamesAsPrimary = gamesAsPrimary;
    }

    public Set<Jogo> getGamesAsAssistant() {
        return gamesAsAssistant;
    }
    public void setGamesAsAssistant(Set<Jogo> gamesAsAssistant) {
        this.gamesAsAssistant = gamesAsAssistant;
    }
}
