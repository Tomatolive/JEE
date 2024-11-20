package yourdiet.controller;
import yourdiet.model.User;
import yourdiet.security.UserService;
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
@RequestMapping("/settings")
public class SettingsController {

    private final UserService userService;

    @Autowired
    public SettingsController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Affiche la page des paramètres utilisateur.
     */
    @GetMapping
    public String showSettingsPage(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "settings";
    }

    /**
     * Permet à l'utilisateur de mettre à jour son nom d'utilisateur.
     */
    @PostMapping("/update-username")
    public String updateUsername(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                 @RequestParam String newUsername, Model model) {
        try {
            User user = userDetails.getUser(); // Obtenir l'utilisateur authentifié
            userService.updateUsername(user, newUsername);
            model.addAttribute("successMessage", "Nom d'utilisateur mis à jour avec succès.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur : " + e.getMessage());
        }
        return "settings";
    }

    /**
     * Permet à l'utilisateur de mettre à jour son mot de passe.
     */
    @PostMapping("/update-password")
    public String updatePassword(@AuthenticationPrincipal UserDetailsImpl userDetails,
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
        return "settings";
    }
}
