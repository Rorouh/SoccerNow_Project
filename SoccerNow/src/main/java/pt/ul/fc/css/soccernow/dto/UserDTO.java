// src/main/java/pt/ul/fc/css/soccernow/dto/UserDTO.java
package pt.ul.fc.css.soccernow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.Referee;
import pt.ul.fc.css.soccernow.domain.User;

public class UserDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String email;

    /** Nunca devolvemos la contraseña en la salida */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public enum Role {
        PLAYER,
        REFEREE
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Role role;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private User.PreferredPosition preferredPosition;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean certified;

    public UserDTO() {}

    private UserDTO(Long id,
                    String name,
                    String email,
                    Role role,
                    User.PreferredPosition preferredPosition,
                    Boolean certified) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.preferredPosition = preferredPosition;
        this.certified = certified;
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

    /** Factory para mapear entidad → DTO de salida */
    public static UserDTO fromEntity(User u) {
        if (u instanceof Player p) {
            return new UserDTO(
                p.getId(),
                p.getName(),
                p.getEmail(),
                Role.PLAYER,
                p.getPreferredPosition(),
                null
            );
        } else {
            Referee r = (Referee) u;
            return new UserDTO(
                r.getId(),
                r.getName(),
                r.getEmail(),
                Role.REFEREE,
                null,
                r.isCertified()
            );
        }
    }
}
