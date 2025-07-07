package pt.ul.fc.css.soccernow.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.dto.LoginDTO;
import pt.ul.fc.css.soccernow.service.AuthService;

/**
 * Controlador para el login “mock”.
 * Endpoint: POST /api/login
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/login
     * Recibe { "email": "...", "password": "..." }.
     * Si existe un usuario con ese email, devuelve 200 OK (login exitoso).
     * Si no existe, devuelve 404 Not Found.
     */
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginDTO dto) {

        boolean ok = authService.login(dto.getEmail(), dto.getPassword());

        if (ok) {
            return ResponseEntity.ok().build();                // 200 → login correcto
        }

        /* averiguamos si el e-mail existe para dar respuesta adecuada */
        boolean emailExiste = authService.loginMock(dto.getEmail(), "");
        return emailExiste
            ? ResponseEntity.status(401).build()               // contraseña incorrecta
            : ResponseEntity.status(404).build();              // e-mail no encontrado
    }

}
