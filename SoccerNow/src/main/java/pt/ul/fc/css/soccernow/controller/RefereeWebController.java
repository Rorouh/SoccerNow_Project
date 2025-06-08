package pt.ul.fc.css.soccernow.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pt.ul.fc.css.soccernow.domain.Referee;
import pt.ul.fc.css.soccernow.dto.RefereeDTO;
import pt.ul.fc.css.soccernow.service.RefereeService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web/referees")
public class RefereeWebController {
    private final RefereeService refereeService;

    public RefereeWebController(RefereeService refereeService) {
        this.refereeService = refereeService;
    }

    @GetMapping
    public String listReferees(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "minGames", required = false) Integer minGames,
            @RequestParam(value = "minCards", required = false) Integer minCards,
            Model model) {
        List<Referee> referees = refereeService.filterReferees(name, minGames, minCards);
        List<RefereeDTO> dtos = referees.stream()
                .map(r -> new RefereeDTO(r.getId(), r.getName(), r.getEmail(), r.isCertified()))
                .collect(Collectors.toList());
        model.addAttribute("referees", dtos);
        return "referees/list";
    }
}
