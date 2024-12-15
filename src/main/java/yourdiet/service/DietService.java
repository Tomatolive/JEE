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

    public List<FoodEntry> getDailyFood(User user, LocalDate date) {
        Map<LocalDate, List<FoodEntry>> foodWeek = getFoodAgenda(user,date,date);
        return (foodWeek.get(date));
    }
    public Integer getDailyCalories(User user, LocalDate date){
        Integer totalCalories = 0;
        if (getDailyFood(user,date)!=null){
            for (FoodEntry entry : getDailyFood(user,date)) {
                totalCalories += entry.getCalories();
            }
            return (totalCalories);
        }
        else{
            return(-1);
        }
    }
    public Double getDailyProteins(User user, LocalDate date){
        Double total = Double.valueOf(0);
        for (FoodEntry entry : getDailyFood(user,date)) {
            total += entry.getProteins();
        }
        return total;
    }
    public Double getDailyCarbs(User user, LocalDate date){
        Double total = Double.valueOf(0);
        for (FoodEntry entry : getDailyFood(user,date)) {
            total += entry.getCarbs();
        }
        return total;
    }
    public Double getDailyFats(User user, LocalDate date){
        Double total = Double.valueOf(0);
        for (FoodEntry entry : getDailyFood(user,date)) {
            total += entry.getFats();
        }
        return total;
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

        return agenda;
    }

    public List<FoodEntry> getFoodEntriesForUser(User user) {
        return foodEntryRepository.findByUser(user);
    }

    public void deleteMeal(Long id) {
        foodEntryRepository.deleteById(id);
    }

    public FoodEntry updateMeal(Long id, FoodEntry updatedEntry) {
        FoodEntry existingEntry = foodEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repas non trouvé"));
        existingEntry.setFoodName(updatedEntry.getFoodName());
        existingEntry.setCalories(updatedEntry.getCalories());
        existingEntry.setProteins(updatedEntry.getProteins());
        existingEntry.setCarbs(updatedEntry.getCarbs());
        existingEntry.setFats(updatedEntry.getFats());
        return foodEntryRepository.save(existingEntry);
    }
}
