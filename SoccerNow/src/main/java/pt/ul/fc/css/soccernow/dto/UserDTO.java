package pt.ul.fc.css.soccernow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserDTO {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser un email válido")
    private String email;

    @NotNull(message = "El role no puede ser null")
    private Role role;

    // Sólo se usará si role == PLAYER
    private String preferredPosition;

    // Sólo se usará si role == REFEREE
    private Boolean certified;

    public enum Role {
        PLAYER, REFEREE
    }

    public UserDTO() {}

    public UserDTO(String name, String email, Role role,
                   String preferredPosition, Boolean certified) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.preferredPosition = preferredPosition;
        this.certified = certified;
    }

    // getters y setters

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

    // <-- CAMBIO AQUÍ: devuelve y recibe Role, no String
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPreferredPosition() {
        return preferredPosition;
    }

    public void setPreferredPosition(String preferredPosition) {
        this.preferredPosition = preferredPosition;
    }

    public Boolean getCertified() {
        return certified;
    }

    public void setCertified(Boolean certified) {
        this.certified = certified;
    }
}
