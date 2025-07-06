package pt.ul.fc.css.soccernow.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import pt.ul.fc.css.soccernow.domain.User;
import pt.ul.fc.css.soccernow.dto.UserCreateDTO;
import pt.ul.fc.css.soccernow.dto.UserDTO;
import pt.ul.fc.css.soccernow.dto.UserUpdateDTO;
import pt.ul.fc.css.soccernow.service.UserService;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;

@Controller
@RequestMapping("/web/users")
public class UserWebController {

  private final UserService userService;

  public UserWebController(UserService userService) {
    this.userService = userService;
  }

  /** GET /web/users → lista todos los usuarios */
  @GetMapping
  public String listUsers(Model model) {
    List<User> usuarios = userService.getAllUsers(); // suponer que agregaste método getAllUsers()
    List<UserDTO> dtos = usuarios.stream().map(this::toDTO).collect(Collectors.toList());
    model.addAttribute("users", dtos);
    return "users/list";
  }

  /** GET /web/users/create → muestra formulario vacío para crear usuario */
  @GetMapping("/create")
  public String showCreateForm(Model model) {
    model.addAttribute("userDTO", new UserDTO());
    return "users/form";
  }

  /** POST /web/users/save → guarda nuevo usuario */
  @PostMapping("/save")
  public String saveUser(@Valid @ModelAttribute("userDTO") UserCreateDTO dto, Model model) {
    try {
      userService.createUser(dto);
      return "redirect:/web/users";
    } catch (ApplicationException ex) {
      model.addAttribute("error", ex.getMessage());
      return "users/form";
    }
  }

  /** GET /web/users/edit/{id} → formulario para editar usuario existente */
  @GetMapping("/edit/{id}")
  public String showEditForm(@PathVariable Long id, Model model) {
    Optional<User> opt = userService.getUserById(id);
    if (opt.isEmpty()) {
      return "redirect:/web/users";
    }
    User u = opt.get();
    UserDTO dto = toDTO(u);
    model.addAttribute("userDTO", dto);
    return "users/form";
  }

  /** POST /web/users/update/{id} → actualiza usuario */
  @PostMapping("/update/{id}")
  public String updateUser(
      @PathVariable Long id, @ModelAttribute("userDTO") UserUpdateDTO dto, Model model) {
    try {
      userService
          .updateUser(id, dto)
          .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
      return "redirect:/web/users";
    } catch (ApplicationException ex) {
      model.addAttribute("error", ex.getMessage());
      return "users/form";
    }
  }

  /** GET /web/users/delete/{id} → elimina usuario */
  @GetMapping("/delete/{id}")
  public String deleteUser(@PathVariable Long id, Model model) {
    boolean eliminado = userService.deleteUser(id);
    if (!eliminado) {
      model.addAttribute("error", "No se encontró usuario con ID=" + id);
    }
    return "redirect:/web/users";
  }

    /** Helper para convertir entidad → DTO */
    private UserDTO toDTO(User u) {
        // Si es Player:
        if (u instanceof pt.ul.fc.css.soccernow.domain.Player p) {
          return new UserDTO(p.getId(), p.getName(), p.getEmail(), p.getPreferredPosition());

        } else {
            pt.ul.fc.css.soccernow.domain.Referee r = (pt.ul.fc.css.soccernow.domain.Referee) u;
            return new UserDTO(r.getId(), r.getName(), r.getEmail(), r.isCertified());
        }
    }
}
