// src/main/java/pt/ul/fc/css/soccernow/dto/PlayerCreateDTO.java
package pt.ul.fc.css.soccernow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class PlayerCreateDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser un email válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;

    @NotBlank(message = "La posición preferida no puede estar vacía")
    private String preferredPosition;

    @Min(value = 0, message = "Los goles no pueden ser negativos")
    private int goals = 0;

    @Min(value = 0, message = "Las tarjetas no pueden ser negativas")
    private int cards = 0;

    public PlayerCreateDTO() {}

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
