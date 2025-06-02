package pt.ul.fc.css.soccernow.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Referee extends User {

    @Column(nullable = false)
    private boolean certified;

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
}
