package yourdiet.controller;
import org.springframework.web.bind.annotation.*;
import yourdiet.model.FoodEntry;
import yourdiet.model.Objective;
import yourdiet.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import yourdiet.security.UserDetailsImpl;
import yourdiet.service.DietService;
import java.time.LocalDate;
import yourdiet.service.ObjectiveService;

@Controller
@RequestMapping("/diet")
public class DietController {

    @Autowired
    private DietService dietService;
    @Autowired
    private ObjectiveService objectiveService;

    @GetMapping
    public String showDashboard(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        LocalDate today = LocalDate.now();
        Objective objective = objectiveService.getObjectiveByUser(user);
        if (objective == null) {
            objective = new Objective();
            objective.setUser(user);
        }
        objective = objectiveService.calculateObjective(objective);
        model.addAttribute("calories", objective.getCalories());
        Integer dailyCalories = dietService.getDailyCalories(user,today);
        if(dailyCalories==-1){
            model.addAttribute("dailyCalories", "Vous n'avez pas mangé aujourd'hui");
        }
        else{
            model.addAttribute("dailyCalories", dailyCalories);
        }
        return "dashboard";
    }

    @PostMapping("/add")
    public String addEntry(@RequestParam String foodName,
                           @RequestParam Integer calories,
                           @RequestParam Double proteins,
                           @RequestParam Double carbs,
                           @RequestParam Double fats,
                           @AuthenticationPrincipal User user) {
        FoodEntry entry = new FoodEntry();
        entry.setFoodName(foodName);
        entry.setCalories(calories);
        entry.setProteins(proteins);
        entry.setCarbs(carbs);
        entry.setFats(fats);
        entry.setUser(user);
        //entry.setDateTime(LocalDateTime.now());
        dietService.addFoodEntry(entry);
        return "dashboard";
    }
}
