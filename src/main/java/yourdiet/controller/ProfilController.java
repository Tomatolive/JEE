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
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        return "profil";
    }

    // Afficher le formulaire pour modifier le profil
    @GetMapping("/edit")
    public String editUserProfil(@AuthenticationPrincipal UserDetailsImpl userDetails,Model model) {
        User user = userDetails.getUser(); // Récupérer l'utilisateur actuel
        model.addAttribute("user", user);  // Ajouter l'utilisateur au modèle
        return "editProfil";  // Nom de la vue pour afficher le formulaire d'édition
    }

    // Traitement de la soumission du formulaire de modification
    @PostMapping("/update")
    public String updateProfil(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                 @RequestParam Integer newAge,
                                 @RequestParam String newGender,
                                 @RequestParam Double newWeight,
                                 @RequestParam Double newHeight,
                                 Model model) {
        User user = userDetails.getUser();
        userService.updateAge(user,newAge);
        userService.updateGender(user,newGender);
        userService.updateWeight(user,newWeight);
        userService.updateHeight(user,newHeight);
        return "profil";
    }


}
