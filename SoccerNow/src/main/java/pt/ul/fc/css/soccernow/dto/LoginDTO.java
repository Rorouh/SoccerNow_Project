package pt.ul.fc.css.soccernow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para la petición de login (solo email y password).
 * Validamos que el email tenga formato y que la contraseña no esté vacía.
 */
public class LoginDTO {

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser un email válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;

    public LoginDTO() { }

    public LoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // --- Getters y setters ---
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
}
