package yourdiet.controller;

import yourdiet.model.User;
import yourdiet.service.UserService;
import yourdiet.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/agenda")
public class AgendaController {

    private final UserService userService;

    @Autowired
    public AgendaController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Affiche la page Agenda.
     */
    @GetMapping
    public String showAgendaPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
            return "agenda";
    }

}
