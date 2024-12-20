package yourdiet.controller;

import yourdiet.model.FoodEntry;
import yourdiet.model.Tags;
import yourdiet.model.User;
import yourdiet.service.DietService;
import yourdiet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/listeRepas")
public class ListeRepasController {

    @Autowired
    private DietService dietService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String showListeRepasPage(Model model, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        List<FoodEntry> meals = dietService.getFoodEntriesForUser(user);
        List<Tags> userTags = userService.getUserTags(user);
        model.addAttribute("meals", meals);
        model.addAttribute("tags", userTags);
        return "listeRepas";
    }

    @PostMapping("/ajouter")
    public String addMeal(@ModelAttribute FoodEntry foodEntry, @RequestParam(required = false) List<Long> tags, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        foodEntry.setUser(user);

        if (tags != null && !tags.isEmpty()) {
            List<Tags> selectedTags = userService.getTagsByIds(tags);
            foodEntry.setTags(selectedTags);
        }

        dietService.addFoodEntry(foodEntry);
        return "redirect:/listeRepas";
    }

    @PostMapping("/supprimer/{id}")
    public String deleteRepas(@PathVariable Long id) {
        System.out.println("Suppression du repas avec l'id: " + id);
        dietService.deleteMeal(id);
        return "redirect:/listeRepas";
    }

    @PostMapping("/modifier/{id}")
    public String editMeal(@PathVariable Long id, @ModelAttribute FoodEntry updatedEntry) {
        System.out.println("Updated Entry: " + updatedEntry);
        try {
            if (updatedEntry.getFoodName() == null || updatedEntry.getFoodName().equals("")) {
                updatedEntry.setFoodName("Plat inconnu");
            }
            if (updatedEntry.getCalories() == null) {
              updatedEntry.setCalories(0);
            }
            if (updatedEntry.getProteins() == null) {
              updatedEntry.setProteins(0.0);
            }
            if (updatedEntry.getCarbs() == null) {
              updatedEntry.setCarbs(0.0);
            }
            if (updatedEntry.getFats() == null) {
                updatedEntry.setFats(0.0);
            }
            dietService.updateMeal(id, updatedEntry);
        } catch (RuntimeException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
        return "redirect:/listeRepas";
    }
}
