package pt.ul.fc.css.soccernow.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser un email válido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Column(nullable = false)
    private String password;

    /** En la BD guardamos el role como texto (“PLAYER” o “REFEREE”). */
    @Column(nullable = false)
    private String role;


    /**
     * Sólo será obligatorio si role = "PLAYER".
     * Si role = "REFEREE", puedes guardarlo nulo o ignorarlo.
     */
    public enum PreferredPosition {
        PORTERO,
        DEFENSA,
        CENTROCAMPISTA,
        DELANTERO
    } 
    private PreferredPosition preferredPosition;

    public User() { }

    public User(String name, String email, String password, String role, PreferredPosition preferredPosition) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.preferredPosition = preferredPosition;
    }

    // --- Getters y setters ---

    public Long getId() {
        return id;
    }

    // NOTA: normalmente no expondríamos setId(Long) en producción,
    // pero lo dejamos público para facilitar pruebas unitarias si hiciera falta:
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public PreferredPosition getPreferredPosition() {
        return preferredPosition;
    }

    public void setPreferredPosition(PreferredPosition preferredPosition) {
        this.preferredPosition = preferredPosition;
    }
}
