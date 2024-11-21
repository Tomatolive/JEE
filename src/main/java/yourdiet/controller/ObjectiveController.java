package yourdiet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import yourdiet.model.Objective;
import yourdiet.security.UserDetailsImpl;
import yourdiet.service.ObjectiveService;
import yourdiet.service.UserService;

@Controller
@RequestMapping("/objective")
public class ObjectiveController {
    @Autowired
    private ObjectiveService objectiveService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String showObjectives(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        var user = userService.getUserByUsername(userDetails.getUsername());
        var objective = objectiveService.getObjectiveByUser(user);
        if (objective == null) {
            objective = new Objective();
        }
        model.addAttribute("objective", objective);
        return "objectives";
    }

    @PostMapping("/save")
    public String saveObjectives(@ModelAttribute Objective objective,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        var user = userService.getUserByUsername(userDetails.getUsername());
        objectiveService.saveObjectiveForUser(objective, user);
        return "redirect:/objective";
    }
}