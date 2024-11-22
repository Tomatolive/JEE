package yourdiet.service;
import yourdiet.model.FoodAgenda;
import yourdiet.model.FoodEntry;
import yourdiet.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yourdiet.repository.FoodAgendaRepository;
import yourdiet.repository.FoodEntryRepository;
import yourdiet.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yourdiet.DailyNutrition;

@Service
@Transactional
public class DietService {

    @Autowired
    private FoodEntryRepository foodEntryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FoodAgendaRepository foodAgendaRepository;

    public FoodEntry addFoodEntry(FoodEntry entry) {
        return foodEntryRepository.save(entry);
    }

    public List<FoodEntry> getDailyEntries(User user, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return foodEntryRepository.findByUserAndDateTimeBetween(user, start, end);
    }

    public DailyNutrition calculateDailyNutrition(List<FoodEntry> entries) {
        return entries.stream()
                .reduce(new DailyNutrition(),
                        (nutrition, entry) -> {
                            nutrition.addCalories(entry.getCalories());
                            nutrition.addProteins(entry.getProteins());
                            nutrition.addCarbs(entry.getCarbs());
                            nutrition.addFats(entry.getFats());
                            return nutrition;
                        },
                        (n1, n2) -> n1);
    }

    public Map<LocalDate, List<FoodEntry>> getFoodAgenda(User user, LocalDate startOfWeek, LocalDate endOfWeek) {
        List<FoodAgenda> foodAgendas = foodAgendaRepository.findByUserAndDateAgendaBetween(user, startOfWeek, endOfWeek);

        Map<LocalDate, List<FoodEntry>> agenda = new HashMap<>();
        for (FoodAgenda foodAgenda : foodAgendas) {
            LocalDate date = foodAgenda.getDateAgenda();
            agenda.computeIfAbsent(date, k -> new ArrayList<>()).add(foodAgenda.getFoodEntry());
        }

        // Retourner un agenda vide si aucune donnée n'est trouvée
        return agenda != null ? agenda : new HashMap<>();
    }
}
