package yourdiet.controller;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import yourdiet.model.FoodAgenda;
import yourdiet.model.FoodEntry;
import yourdiet.model.User;
import yourdiet.repository.FoodAgendaRepository;
import yourdiet.repository.FoodEntryRepository;
import yourdiet.repository.UserRepository;
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
    private UserRepository userRepository;

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
    public String showAgendaPage(Model model,
                                 @RequestParam(defaultValue = "0") int week,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        // Récupération de l'utilisateur courant
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Calcul des dates de la semaine en fonction du paramètre 'week'
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.plusWeeks(week).with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // Récupération de l'agenda
        List<FoodAgenda> weeklyAgenda = dietService.getFoodAgendaEntries(currentUser, startOfWeek, endOfWeek);

        // Organisation des repas
        Map<LocalDate, Map<String, List<FoodEntry>>> agenda = new HashMap<>();
        for (LocalDate date = startOfWeek; !date.isAfter(endOfWeek); date = date.plusDays(1)) {
            agenda.putIfAbsent(date, new HashMap<>());

            // Remplissage des repas pour chaque jour, même s'il n'y en a pas
            agenda.get(date).putIfAbsent("BREAKFAST", new ArrayList<>());
            agenda.get(date).putIfAbsent("LUNCH", new ArrayList<>());
            agenda.get(date).putIfAbsent("DINNER", new ArrayList<>());
        }

        for (FoodAgenda entry : weeklyAgenda) {
            LocalDate date = entry.getDateAgenda();
            String mealType = String.valueOf(entry.getMealType());
            FoodEntry foodEntry = entry.getFoodEntry();

            agenda.computeIfAbsent(date, k -> new HashMap<>());
            agenda.get(date).computeIfAbsent(mealType, k -> new ArrayList<>()).add(foodEntry);
        }

        // Calcul des semaines précédente et suivante
        int prevWeek = week - 1;
        int nextWeek = week + 1;

        // Ajout des données au modèle
        model.addAttribute("agenda", agenda);
        model.addAttribute("weekOffset", week);
        model.addAttribute("prevWeek", prevWeek);
        model.addAttribute("nextWeek", nextWeek);

        return "agenda";
    }



    @GetMapping("/ajouter-repas")
    public String showAjoutAgendaPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
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
