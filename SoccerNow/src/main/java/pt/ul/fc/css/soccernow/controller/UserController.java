package pt.ul.fc.css.soccernow.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.domain.Player;
import pt.ul.fc.css.soccernow.domain.Referee;
import pt.ul.fc.css.soccernow.domain.User;
import pt.ul.fc.css.soccernow.dto.UserDTO;
import pt.ul.fc.css.soccernow.service.UserService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** POST: creación completa. Se valida con @Valid. */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO dto) {
        try {
            User creado = userService.createUser(dto);

            // Convertir entidad a DTO de salida
            UserDTO salida;
            if (creado instanceof Player p) {
                salida = new UserDTO(
                    p.getId(),
                    p.getName(),
                    p.getEmail(),
                    p.getPassword(),
                    UserDTO.Role.PLAYER,
                    p.getPreferredPosition(),
                    null
                );
            } else {
                Referee r = (Referee) creado;
                salida = new UserDTO(
                    r.getId(),
                    r.getName(),
                    r.getEmail(),
                    r.getPassword(),
                    UserDTO.Role.REFEREE,
                    null,
                    r.isCertified()
                );
            }

            return ResponseEntity
                    .created(URI.create("/api/users/" + creado.getId()))
                    .body(salida);

        } catch (ApplicationException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** GET: devolver un usuario por id */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<User> opt = userService.getUserById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User u = opt.get();
        UserDTO salida;
        if (u instanceof Player p) {
            salida = new UserDTO(
                p.getId(),
                p.getName(),
                p.getEmail(),
                p.getPassword(),
                UserDTO.Role.PLAYER,
                p.getPreferredPosition(),
                null
            );
        } else {
            Referee r = (Referee) u;
            salida = new UserDTO(
                r.getId(),
                r.getName(),
                r.getEmail(),
                r.getPassword(),
                UserDTO.Role.REFEREE,
                null,
                r.isCertified()
            );
        }
        return ResponseEntity.ok(salida);
    }

    /**
     * PUT /api/users/{id}
     * Actualiza SOLO los campos que vengan en el JSON. Por eso no usamos @Valid,
     * y dejamos que el service fusione con el objeto existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserDTO dto /* sin @Valid */) {

        try {
            Optional<User> opt = userService.updateUser(id, dto);
            if (opt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            User u = opt.get();
            UserDTO salida;
            if (u instanceof Player p) {
                salida = new UserDTO(
                    p.getId(),
                    p.getName(),
                    p.getEmail(),
                    p.getPassword(),
                    UserDTO.Role.PLAYER,
                    p.getPreferredPosition(),
                    null
                );
            } else {
                Referee r = (Referee) u;
                salida = new UserDTO(
                    r.getId(),
                    r.getName(),
                    r.getEmail(),
                    r.getPassword(),
                    UserDTO.Role.REFEREE,
                    null,
                    r.isCertified()
                );
            }
            return ResponseEntity.ok(salida);

        } catch (ApplicationException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** DELETE /api/users/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
