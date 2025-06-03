// src/main/java/pt/ul/fc/css/soccernow/dto/PlayerDTO.java
package pt.ul.fc.css.soccernow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class PlayerDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser un email válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;

    /** preferredPosition vendrá como String (p.ej. "DELANTERO") */
    @NotBlank(message = "La posición preferida no puede estar vacía")
    private String preferredPosition;


    private int goals;

    private int cards;

    //public PlayerDTO() { }

    /** Constructor completo (incluye id) para respuestas */
    public PlayerDTO(Long id, String name, String email, String password, String preferredPosition) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.preferredPosition = preferredPosition;
        this.goals = goals;
        this.cards = cards;
    }

    /** Constructor sin id (para crear) */
    public PlayerDTO(String name, String email, String password, String preferredPosition) {
        this(null, name, email, password, preferredPosition);
    }

    // --- Getters y setters ---
   /** public Long getId() {
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

    public String getPreferredPosition() {
        return preferredPosition;
    }
    public void setPreferredPosition(String preferredPosition) {
        this.preferredPosition = preferredPosition;
    }
    **/
}
