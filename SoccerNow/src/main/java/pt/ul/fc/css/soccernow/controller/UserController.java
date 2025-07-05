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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** POST /api/users */
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateDTO dto) {
        try {
            User creado = userService.createUser(dto);
            return ResponseEntity
                    .created(URI.create("/api/users/" + creado.getId()))
                    .body(UserDTO.fromEntity(creado));
        } catch (ApplicationException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /** GET /api/users/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
            .map(u -> ResponseEntity.ok(UserDTO.fromEntity(u)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** PUT /api/users/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO dto) {
        try {
            return userService.updateUser(id, dto)
                .map(u -> ResponseEntity.ok(UserDTO.fromEntity(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
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

    /**
     * GET /api/users/filter
     * Búsqueda avanzada por nombre y/o rol.
     */
    @GetMapping("/filter")
    public ResponseEntity<List<UserDTO>> filterUsers(
            @RequestParam(value = "name", required = false) UserDTO.Role name,
            @RequestParam(value = "role", required = false) UserDTO.Role role
    ) {
        var results = userService.filterUsers(name, role);
        var dtos = results.stream()
                          .map(UserDTO::fromEntity)
                          .toList();
        return ResponseEntity.ok(dtos);
    }
}
