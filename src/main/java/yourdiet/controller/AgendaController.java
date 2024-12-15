package yourdiet.controller;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/detail/{date}")
    public String showAgendaDetailPage(@PathVariable("date") String date,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       Model model) {
        // Récupérer l'utilisateur courant
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Convertir la date
        LocalDate day = LocalDate.parse(date);

        // Récupérer les FoodAgenda pour ce jour
        List<FoodAgenda> dailyAgenda = dietService.getFoodAgendaEntriesForDay(currentUser, day);

        // Organiser les repas par type
        Map<String, List<FoodAgenda>> meals = new HashMap<>();
        meals.put("BREAKFAST", new ArrayList<>());
        meals.put("LUNCH", new ArrayList<>());
        meals.put("DINNER", new ArrayList<>());

        // Variables pour les totaux
        int totalCalories = 0;
        double totalProteins = 0;
        double totalCarbs = 0;
        double totalFats = 0;

        for (FoodAgenda entry : dailyAgenda) {
            FoodEntry foodEntry = entry.getFoodEntry();
            meals.get(entry.getMealType().name()).add(entry);

            // Additionner les valeurs nutritionnelles
            totalCalories += foodEntry.getCalories() != null ? foodEntry.getCalories() : 0;
            totalProteins += foodEntry.getProteins() != null ? foodEntry.getProteins() : 0;
            totalCarbs += foodEntry.getCarbs() != null ? foodEntry.getCarbs() : 0;
            totalFats += foodEntry.getFats() != null ? foodEntry.getFats() : 0;
        }

        // Ajouter les données au modèle
        model.addAttribute("date", day);
        model.addAttribute("meals", meals);
        model.addAttribute("totalCalories", totalCalories);
        model.addAttribute("totalProteins", totalProteins);
        model.addAttribute("totalCarbs", totalCarbs);
        model.addAttribute("totalFats", totalFats);

        return "agendaRepasDetail";
    }


    @PostMapping("/supprimer-repas/{id}")
    public String supprimerRepas(@PathVariable("id") Long id,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        FoodAgenda foodAgenda = foodAgendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repas non trouvé"));

        // Vérifier que le repas appartient bien à l'utilisateur courant
        if (!foodAgenda.getUser().equals(currentUser)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer ce repas");
        }

        // Supprimer l'entrée
        foodAgendaRepository.delete(foodAgenda);

        // Redirection vers la page du jour
        return "redirect:/agenda/detail/" + foodAgenda.getDateAgenda();
    }


}
