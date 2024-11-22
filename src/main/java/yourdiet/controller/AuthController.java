package yourdiet.controller;

import jakarta.servlet.http.HttpSession;
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
    public String registerUser(@ModelAttribute("user") User user, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            User savedUser = userService.registerNewUser(user.getUsername(), user.getPassword());
            session.setAttribute("registeredUserId", savedUser.getId());
            return "redirect:/register/attributes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'inscription : " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/register/attributes")
    public String showRegistrationAttributes(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("registeredUserId");
        if (userId == null) {
            return "redirect:/register";
        }
        try {
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);
            return "register-attributes";
        } catch (Exception e) {
            return "redirect:/register";
        }
    }

    @PostMapping("/register/attributes")
    public String registerUserAttributes(@ModelAttribute("user") User userForm, HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("registeredUserId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Session expirée");
            return "redirect:/register";
        }

        try {
            User user = userService.getUserById(userId);
            userService.updateGender(user, userForm.getGender());
            userService.updateAge(user, userForm.getAge());
            userService.updateHeight(user, userForm.getHeight());
            userService.updateWeight(user, userForm.getWeight());

            session.removeAttribute("registeredUserId");
            redirectAttributes.addFlashAttribute("successMessage", "Inscription réussie ! Vous pouvez maintenant vous connecter.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur : " + e.getMessage());
            return "redirect:/register";
        }
    }
}
