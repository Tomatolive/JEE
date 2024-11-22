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
public class ProfilController {
    private final UserService userService;
    @Autowired
    public ProfilController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Affiche la page du profil.
     */
    @GetMapping
    public String showProfilPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        model.addAttribute("user", userDetails.getUser());
        return "profil";
    }

    // Afficher le formulaire pour modifier le profil
    @GetMapping("/edit")
    public String editUserProfil(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        User user = userService.getUserById(userDetails.getUserId());
        model.addAttribute("user", user);
        return "editProfil";
    }

    // Traitement de la soumission du formulaire de modification
    @PostMapping("/update")
    public String updateProfil(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam Integer newAge, @RequestParam String newGender, @RequestParam Double newWeight, @RequestParam Double newHeight, Model model) {

        User user = userDetails.getUser();

        // Mettre à jour l'utilisateur dans la base de données
        User updatedUser = userService.updateAge(user, newAge);
        userService.updateGender(updatedUser, newGender);
        userService.updateWeight(updatedUser, newWeight);
        userService.updateHeight(updatedUser, newHeight);

        // Mettre à jour les données dans UserDetails
        userDetails.updateUserData(updatedUser);

        // Ajouter l'utilisateur mis à jour au modèle
        model.addAttribute("user", updatedUser);

        return "redirect:/profil";
    }

}
