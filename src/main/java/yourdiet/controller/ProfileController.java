package yourdiet.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
/*
    // Afficher le formulaire pour modifier le profil
    @GetMapping("/profile/edit")
    public String editUserProfile(@AuthenticationPrincipal UserDetailsImpl userDetails,Model model) {
        User user = userService.getCurrentUser();  // Récupérer l'utilisateur actuel
        model.addAttribute("user", user);  // Ajouter l'utilisateur au modèle
        return "editProfile";  // Nom de la vue pour afficher le formulaire d'édition
    }

    // Traitement de la soumission du formulaire de modification
    @PostMapping("/profile/edit")
    public String updateUserProfile(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        userService.updateUser(user);  // Mettre à jour l'utilisateur avec les nouvelles informations
        redirectAttributes.addFlashAttribute("successMessage", "Profil mis à jour avec succès!");
        return "redirect:/profile";  // Rediriger vers la page de profil après la mise à jour
    }
*/

}
