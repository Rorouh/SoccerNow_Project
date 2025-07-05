// src/main/java/pt/ul/fc/css/soccernow/dto/PlayerUpdateDTO.java
package pt.ul.fc.css.soccernow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;

public class PlayerUpdateDTO {

    /** Todos opcionales para PUT parcial */
    private String name;

    @Email(message = "Debe ser un email válido")
    private String email;

    private String password;

    private String preferredPosition;

    @Min(value = 0, message = "Los goles no pueden ser negativos")
    private Integer goals;

    @Min(value = 0, message = "Las tarjetas no pueden ser negativas")
    private Integer cards;

    public PlayerUpdateDTO() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPreferredPosition() {
        return preferredPosition;
    }

    public void setPreferredPosition(String preferredPosition) {
        this.preferredPosition = preferredPosition;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getCards() {
        return cards;
    }

    public void setCards(int cards) {
        this.cards = cards;
    }
}
