package yourdiet.controller;

import yourdiet.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import yourdiet.service.UserService;

@Controller
@RequestMapping
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "redirect:/diet";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        try {
            userService.registerNewUser(user.getUsername(), user.getPassword());
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/register/attributes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'inscription : " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/register/attributes")
    public String showRegistrationAttributes(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "register-attributes";
    }

    @PostMapping("/register/attributes")
    public String registerUserAttributes(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        userService.updateGender(user, user.getGender());
        userService.updateAge(user, user.getAge());
        userService.updateHeight(user, user.getHeight());
        userService.updateWeight(user, user.getWeight());
        redirectAttributes.addFlashAttribute("successMessage", "Inscription réussie ! Vous pouvez maintenant vous connecter.");
        return "redirect:/login";
    }
}
