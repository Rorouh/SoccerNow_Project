// src/main/java/pt/ul/fc/css/soccernow/dto/UserCreateDTO.java
package pt.ul.fc.css.soccernow.dto;

import pt.ul.fc.css.soccernow.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserCreateDTO {
    /** Para que Thymeleaf pueda leer userDTO.id sin fallar */
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser un email válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;

    @NotNull(message = "El rol no puede ser nulo")
    private pt.ul.fc.css.soccernow.dto.UserDTO.Role role;

    /** Obligatorio si role=PLAYER */
    private User.PreferredPosition preferredPosition;

    /** Obligatorio si role=REFEREE */
    private Boolean certified;

    public UserCreateDTO() {
        // id queda a null aquí
    }

    // --- Getters & Setters ---

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

    public pt.ul.fc.css.soccernow.dto.UserDTO.Role getRole() {
        return role;
    }

    public void setRole(pt.ul.fc.css.soccernow.dto.UserDTO.Role role) {
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
