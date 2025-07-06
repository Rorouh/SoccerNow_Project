// src/main/java/pt/ul/fc/css/soccernow/dto/UserUpdateDTO.java
package pt.ul.fc.css.soccernow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import pt.ul.fc.css.soccernow.domain.User;

public class UserUpdateDTO {

    private Long id;
    private String name;

    @Email(message = "Debe ser un email válido")
    private String email;

    private String password;

    /** Ahora usamos el Role de UserDTO */
    @NotNull(message = "El rol no puede ser nulo")
    private UserDTO.Role role;

    private User.PreferredPosition preferredPosition;
    private Boolean certified;

    public UserUpdateDTO() {}

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public UserDTO.Role getRole() {
        return role;
    }

    public void setRole(UserDTO.Role role) {
        this.role = role;
    }

    public User.PreferredPosition getPreferredPosition() {
        return preferredPosition;
    }

    public void setPreferredPosition(User.PreferredPosition preferredPosition) {
        this.preferredPosition = preferredPosition;
    }

    public Boolean getCertified() {
        return certified;
    }

    public void setCertified(Boolean certified) {
        this.certified = certified;
    }
}
