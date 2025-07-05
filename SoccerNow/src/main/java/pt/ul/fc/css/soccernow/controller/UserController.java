// src/main/java/pt/ul/fc/css/soccernow/controller/UserController.java
package pt.ul.fc.css.soccernow.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.domain.User;
import pt.ul.fc.css.soccernow.dto.UserCreateDTO;
import pt.ul.fc.css.soccernow.dto.UserDTO;
import pt.ul.fc.css.soccernow.dto.UserUpdateDTO;
import pt.ul.fc.css.soccernow.service.UserService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** POST /api/users */
    @PostMapping
    public ResponseEntity<?> createUser(
            @Valid @RequestBody UserCreateDTO dto) {
        try {
            User creado = userService.createUser(dto);
            UserDTO salida = UserDTO.fromEntity(creado);
            return ResponseEntity
                    .created(URI.create("/api/users/" + creado.getId()))
                    .body(salida);

        } catch (ApplicationException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /** GET /api/users/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<User> opt = userService.getUserById(id);
        return opt
            .map(u -> ResponseEntity.ok(UserDTO.fromEntity(u)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** PUT /api/users/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO dto) {
        try {
            Optional<User> opt = userService.updateUser(id, dto);
            if (opt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(UserDTO.fromEntity(opt.get()));
        } catch (ApplicationException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /** DELETE /api/users/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.notFound().build();
    }
}
