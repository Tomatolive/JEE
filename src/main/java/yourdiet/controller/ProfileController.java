package yourdiet.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import yourdiet.model.User;
import yourdiet.security.UserDetailsImpl;
import yourdiet.security.UserService;
@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserService userService;
    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Affiche la page du profile.
     */
    @GetMapping
    public String showSettingsPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        return "profile";
    }
    @PostMapping("/update")
    public String updateDonne(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model) {
        try {
            User user = userDetails.getUser(); // Obtenir l'utilisateur authentifié
            userService.updatePassword(user, oldPassword, newPassword, confirmPassword);
            model.addAttribute("successMessage", "Mot de passe mis à jour avec succès.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur : " + e.getMessage());
        }
        return "profil";
    }
}
