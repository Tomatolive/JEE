package yourdiet.controller;
import yourdiet.model.FoodEntry;
import yourdiet.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import yourdiet.security.UserDetailsImpl;
import yourdiet.service.DietService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import yourdiet.DailyNutrition;

@Controller
@RequestMapping("/diet")
public class DietController {

    @Autowired
    private DietService dietService;

    @GetMapping
    public String showDashboard(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        LocalDate today = LocalDate.now();
        List<FoodEntry> todayEntries = dietService.getDailyEntries(user, today);
        DailyNutrition nutrition = dietService.calculateDailyNutrition(todayEntries);

        model.addAttribute("entries", todayEntries);
        model.addAttribute("nutrition", nutrition);
        model.addAttribute("entry", new FoodEntry());
        return "dashboard";
    }

    @PostMapping("/add")
    public String addEntry(@ModelAttribute FoodEntry entry, @AuthenticationPrincipal User user) {
        entry.setUser(user);
        entry.setDateTime(LocalDateTime.now());
        dietService.addFoodEntry(entry);
        return "redirect:/diet";
    }
}
