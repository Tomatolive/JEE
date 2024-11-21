package yourdiet.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import yourdiet.model.User;
import yourdiet.security.UserDetailsImpl;
import yourdiet.service.UserService;
@Controller
@RequestMapping("/profil")
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
    public String showProfilPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        return "profil";
    }

    // Afficher le formulaire pour modifier le profil
    @GetMapping("/edit")
    public String editUserProfile(@AuthenticationPrincipal UserDetailsImpl userDetails,Model model) {
        User user = userDetails.getUser(); // Récupérer l'utilisateur actuel
        model.addAttribute("user", user);  // Ajouter l'utilisateur au modèle
        return "editProfil";  // Nom de la vue pour afficher le formulaire d'édition
    }
/*
    // Traitement de la soumission du formulaire de modification
    @PostMapping("/edit")
    public String updateProfil(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                 @RequestParam String newUsername,
                                 @RequestParam String newAge,
                                 Model model) {
        try {
            User user = userDetails.getUser();
            userService.updatePassword(user, oldPassword, newPassword, confirmPassword);
            model.addAttribute("successMessage", "Mot de passe mis à jour avec succès.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur : " + e.getMessage());
        }
        return "settings";
    }

*/
}
