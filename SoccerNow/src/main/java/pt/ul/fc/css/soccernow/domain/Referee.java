package pt.ul.fc.css.soccernow.domain;

import jakarta.persistence.Entity;

@Entity
public class Referee extends User {

    private boolean certified;

    // Getters y setters
    public boolean isCertified() {
        return certified;
    }

    public void setCertified(boolean certified) {
        this.certified = certified;
    }
}
