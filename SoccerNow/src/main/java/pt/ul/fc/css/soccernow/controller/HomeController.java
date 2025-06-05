package pt.ul.fc.css.soccernow.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * Controlador raíz: redirige a /web/users
     */
    @GetMapping("/")
    public String home() {
        // Puedes devolver "index" si tienes un índice estático en templates/index.html
        // o hacer una redirección a la lista de usuarios:
        return "redirect:/web/users";
    }
}
