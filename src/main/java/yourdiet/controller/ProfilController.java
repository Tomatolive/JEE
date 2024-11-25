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
        String activityLevelLettre;
        User user = userDetails.getUser();
        Double activityLevel= user.getActivityLevel();
        float activityLevelFloat = activityLevel.floatValue();
        int activityLevelEntier = (int) (1000* activityLevelFloat);
        switch (activityLevelEntier){
            case 1200:
                activityLevelLettre="Sédentaire - Pratiquemment aucun exercice quotidien";
                break;
            case 1375:
                activityLevelLettre="Peu actif - Peu d'activité physique (1 à 3 par semaine, intensité modérée)";
                break;
            case 1550:
                activityLevelLettre="Actif - Activité physique régulière (3 à 5 par semaine, intensité modérée)";
                break;
            case 1725:
                activityLevelLettre="Très actif - Activité quotidienne (intensité soutenue)";
                break;
            case 1900:
                activityLevelLettre="Extrêmement actif - Travail extrêmement physique ou grand sportif";
                break;
            default:
                activityLevelLettre="Non déterminé";
        }
        model.addAttribute("user", userDetails.getUser());
        model.addAttribute("activityLevel",activityLevelLettre);
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
    public String updateProfil(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam Integer newAge, @RequestParam String newGender, @RequestParam Double newWeight, @RequestParam Double newHeight,@RequestParam Double newActivityLevel, Model model) {

        User user = userDetails.getUser();

        // Mettre à jour l'utilisateur dans la base de données
        userService.updateAge(user, newAge);
        userService.updateGender(user, newGender);
        userService.updateWeight(user, newWeight);
        userService.updateHeight(user, newHeight);
        userService.updateActivityLevel(user,newActivityLevel);

        // Mettre à jour les données dans UserDetails
        userDetails.updateUserData(user);

        // Ajouter l'utilisateur mis à jour au modèle
        model.addAttribute("user", user);

        return "redirect:/profil";
    }

}
