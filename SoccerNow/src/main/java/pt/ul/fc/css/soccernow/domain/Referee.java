package pt.ul.fc.css.soccernow.domain;

import jakarta.persistence.Entity;

@Entity
public class Referee extends User {

    private boolean certified;

    public Referee() {}

    public Referee(String name, String email, String password, boolean certified) {
        super(name, email, password);
        this.certified = certified;
    }

    // Getters y setters
    public boolean isCertified() {
        return certified;
    }

    public void setCertified(boolean certified) {
        this.certified = certified;
    }
}
