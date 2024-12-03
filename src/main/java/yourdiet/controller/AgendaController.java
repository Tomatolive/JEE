package yourdiet.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import yourdiet.model.FoodAgenda;
import yourdiet.model.FoodEntry;
import yourdiet.model.User;
import yourdiet.repository.FoodAgendaRepository;
import yourdiet.repository.FoodEntryRepository;
import yourdiet.service.DietService;
import yourdiet.service.UserService;
import yourdiet.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/agenda")
public class AgendaController {

    private final UserService userService;
    private final DietService dietService;
    private final FoodEntryRepository foodEntryRepository;
    private final FoodAgendaRepository foodAgendaRepository;

    @Autowired
    public AgendaController(UserService userService,
                            DietService dietService,
                            FoodEntryRepository foodEntryRepository,
                            FoodAgendaRepository foodAgendaRepository) {
        this.userService = userService;
        this.dietService = dietService;
        this.foodEntryRepository = foodEntryRepository;
        this.foodAgendaRepository = foodAgendaRepository;
    }

    /**
     * Affiche la page Agenda.
     */
    @GetMapping
    public String showAgendaPage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                 @RequestParam(value = "weekOffset", defaultValue = "0") int weekOffset,
                                 Model model) {
        if (userDetails == null || userDetails.getUserId() == null) {
            return "redirect:/login";
        }

        User user = userService.getUserById(userDetails.getUserId());
        if (user == null) {
            return "redirect:/login";
        }

        // Calcul de la semaine à afficher
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.plusWeeks(weekOffset).with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // Récupération des données alimentaires de l'utilisateur pour cette semaine
        Map<LocalDate, List<FoodEntry>> agenda = dietService.getFoodAgenda(user, startOfWeek, endOfWeek);

        // Normalisation de l'agenda : Initialisation des jours sans entrées avec une liste vide
        Map<LocalDate, List<FoodEntry>> normalizedAgenda = new HashMap<>();
        for (int i = 0; i <= 6; i++) {
            LocalDate date = startOfWeek.plusDays(i);
            normalizedAgenda.put(date, agenda.getOrDefault(date, new ArrayList<>()));
        }

        // Ajout des données au modèle
        model.addAttribute("weekOffset", weekOffset);
        model.addAttribute("startOfWeek", startOfWeek);
        model.addAttribute("endOfWeek", endOfWeek);
        model.addAttribute("agenda", normalizedAgenda);
        model.addAttribute("days", List.of("LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI", "DIMANCHE"));

        return "agenda";
    }

    @GetMapping("/ajouter-repas")
    public String afficherFormulaireAjoutAgenda(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        if (userDetails == null || userDetails.getUserId() == null) {
            return "redirect:/login";
        }

        User user = userService.getUserById(userDetails.getUserId());
        if (user == null) {
            return "redirect:/login";
        }

        // Récupérer les entrées alimentaires de l'utilisateur
        List<FoodEntry> foodEntries = dietService.getFoodEntriesForUser(user);
        model.addAttribute("foodEntries", foodEntries);

        return "ajoutAgenda";
    }

    @PostMapping("/save-repas")
    public String ajouterRepas(@AuthenticationPrincipal UserDetailsImpl userDetails,
                               @RequestParam("date") String date,
                               @RequestParam("mealType") String mealType,
                               @RequestParam("foodEntryId") Long foodEntryId) {
        if (userDetails == null || userDetails.getUserId() == null) {
            return "redirect:/login";
        }

        User user = userService.getUserById(userDetails.getUserId());
        if (user == null) {
            return "redirect:/login";
        }

        FoodEntry foodEntry = foodEntryRepository.findById(foodEntryId)
                .orElseThrow(() -> new IllegalArgumentException("Plat non trouvé"));

        FoodAgenda foodAgenda = new FoodAgenda();
        foodAgenda.setUser(user);
        foodAgenda.setFoodEntry(foodEntry);
        foodAgenda.setDateAgenda(LocalDate.parse(date));
        foodAgenda.setMealType(FoodAgenda.MealType.valueOf(mealType));

        foodAgendaRepository.save(foodAgenda);

        return "redirect:/agenda";
    }

}
