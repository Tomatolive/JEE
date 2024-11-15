package yourdiet.service;
import yourdiet.model.FoodEntry;
import yourdiet.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yourdiet.repository.FoodEntryRepository;
import yourdiet.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import yourdiet.DailyNutrition;

@Service
@Transactional
public class DietService {
    @Autowired
    private FoodEntryRepository foodEntryRepository;

    @Autowired
    private UserRepository userRepository;

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
}
