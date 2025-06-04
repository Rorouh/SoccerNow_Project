package pt.ul.fc.css.soccernow.dto;

import pt.ul.fc.css.soccernow.domain.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser un email válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;

    public enum Role {
        PLAYER,
        REFEREE
    }

    @NotNull(message = "El rol no puede ser nulo")
    private Role role;

    private User.PreferredPosition preferredPosition;

    private Boolean certified;


    public UserDTO() {
    }


    public UserDTO(Long id,
                   String name,
                   String email,
                   String password,
                   Role role,
                   User.PreferredPosition preferredPosition,
                   Boolean certified) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.preferredPosition = preferredPosition;
        this.certified = certified;
    }


    public UserDTO(String name,
                   String email,
                   String password,
                   Role role,
                   User.PreferredPosition preferredPosition,
                   Boolean certified) {
        this(null, name, email, password, role, preferredPosition, certified);
    }

    // Getters e Setters
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
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
