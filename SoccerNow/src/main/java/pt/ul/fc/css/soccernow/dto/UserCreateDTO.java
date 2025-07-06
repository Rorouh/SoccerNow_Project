// src/main/java/pt/ul/fc/css/soccernow/dto/UserCreateDTO.java
package pt.ul.fc.css.soccernow.dto;

import pt.ul.fc.css.soccernow.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pt.ul.fc.css.soccernow.dto.UserDTO;

public class UserCreateDTO {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser un email válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;

    //public enum Role { PLAYER, REFEREE }
    @NotNull(message = "El rol no puede ser nulo")
    private UserDTO.Role Role;

    /** Obligatorio si role=PLAYER */
    private User.PreferredPosition preferredPosition;

    /** Obligatorio si role=REFEREE */
    private Boolean certified;

    public UserCreateDTO() {}
    
    // Getters e Setters
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
        return Role;
    }

    public void setRole(UserDTO.Role role) {
        this.Role = role;
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
