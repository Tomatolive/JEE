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

    /**
     * Ajoute une entrée alimentaire (repas) à la base de données.
     *
     * @param entry L'entrée alimentaire à ajouter.
     * @return L'entrée alimentaire ajoutée après l'enregistrement dans la base de données.
     */
    public FoodEntry addFoodEntry(FoodEntry entry) {
        return foodEntryRepository.save(entry);
    }

    /**
     * Récupère les repas du jour pour un utilisateur donné.
     *
     * @param user L'utilisateur pour lequel récupérer les repas.
     * @param date La date pour laquelle récupérer les repas.
     * @return Une liste de repas consommés par l'utilisateur à la date spécifiée.
     */
    public List<FoodEntry> getDailyFood(User user, LocalDate date) {
        Map<LocalDate, List<FoodEntry>> foodWeek = getFoodAgenda(user, date, date);
        return foodWeek.get(date);
    }

    /**
     * Calcule le total des calories consommées par un utilisateur à une date donnée.
     *
     * @param user L'utilisateur pour lequel calculer les calories.
     * @param date La date pour laquelle calculer les calories.
     * @return Le total des calories ou -1 si aucun repas n'est trouvé pour la date.
     */
    public Integer getDailyCalories(User user, LocalDate date) {
        Integer totalCalories = 0;
        List<FoodEntry> dailyFood = getDailyFood(user, date);
        if (dailyFood != null) {
            for (FoodEntry entry : dailyFood) {
                totalCalories += entry.getCalories();
            }
            return totalCalories;
        } else {
            return -1;
        }
    }

    /**
     * Calcule le total des protéines consommées par un utilisateur à une date donnée.
     *
     * @param user L'utilisateur pour lequel calculer les protéines.
     * @param date La date pour laquelle calculer les protéines.
     * @return Le total des protéines consommées à la date spécifiée.
     */
    public Double getDailyProteins(User user, LocalDate date) {
        Double total = 0.0;
        for (FoodEntry entry : getDailyFood(user, date)) {
            total += entry.getProteins();
        }
        return total;
    }

    /**
     * Calcule le total des glucides consommés par un utilisateur à une date donnée.
     *
     * @param user L'utilisateur pour lequel calculer les glucides.
     * @param date La date pour laquelle calculer les glucides.
     * @return Le total des glucides consommés à la date spécifiée.
     */
    public Double getDailyCarbs(User user, LocalDate date) {
        Double total = 0.0;
        for (FoodEntry entry : getDailyFood(user, date)) {
            total += entry.getCarbs();
        }
        return total;
    }

    /**
     * Calcule le total des lipides consommés par un utilisateur à une date donnée.
     *
     * @param user L'utilisateur pour lequel calculer les lipides.
     * @param date La date pour laquelle calculer les lipides.
     * @return Le total des lipides consommés à la date spécifiée.
     */
    public Double getDailyFats(User user, LocalDate date) {
        Double total = 0.0;
        for (FoodEntry entry : getDailyFood(user, date)) {
            total += entry.getFats();
        }
        return total;
    }

    /**
     * Calcule la nutrition quotidienne totale à partir d'une liste d'entrées alimentaires.
     *
     * @param entries La liste des repas pour lesquels calculer les valeurs nutritionnelles.
     * @return Un objet contenant les totaux des calories, protéines, glucides et lipides.
     */
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

    /**
     * Récupère l'agenda des repas pour une plage de dates donnée.
     *
     * @param user L'utilisateur pour lequel récupérer l'agenda.
     * @param startOfWeek La date de début de la semaine.
     * @param endOfWeek La date de fin de la semaine.
     * @return Une carte des dates avec les repas associés pour cette plage de dates.
     */
    public Map<LocalDate, List<FoodEntry>> getFoodAgenda(User user, LocalDate startOfWeek, LocalDate endOfWeek) {
        List<FoodAgenda> foodAgendas = foodAgendaRepository.findByUserAndDateAgendaBetween(user, startOfWeek, endOfWeek);

        Map<LocalDate, List<FoodEntry>> agenda = new HashMap<>();
        for (FoodAgenda foodAgenda : foodAgendas) {
            if (foodAgenda.getFoodEntry() == null) {
                System.out.println("Erreur: FoodEntry manquant pour FoodAgenda ID " + foodAgenda.getId());
                continue;
            }
            LocalDate date = foodAgenda.getDateAgenda();
            agenda.computeIfAbsent(date, k -> new ArrayList<>()).add(foodAgenda.getFoodEntry());
        }

        return agenda;
    }

    /**
     * Récupère toutes les entrées alimentaires pour un utilisateur donné.
     *
     * @param user L'utilisateur pour lequel récupérer les repas.
     * @return Une liste de toutes les entrées alimentaires associées à l'utilisateur.
     */
    public List<FoodEntry> getFoodEntriesForUser(User user) {
        return foodEntryRepository.findByUser(user);
    }

    /**
     * Supprime un repas en fonction de son identifiant.
     *
     * @param id L'identifiant du repas à supprimer.
     */
    public void deleteMeal(Long id) {
        foodEntryRepository.deleteById(id);
    }

    /**
     * Met à jour un repas existant.
     *
     * @param id L'identifiant du repas à mettre à jour.
     * @param updatedEntry Les nouvelles informations du repas à mettre à jour.
     * @return L'entrée alimentaire mise à jour.
     * @throws RuntimeException Si le repas n'est pas trouvé.
     */
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

    /**
     * Récupère les entrées de l'agenda alimentaire pour un utilisateur donné, sur une plage de dates.
     *
     * @param user L'utilisateur pour lequel récupérer les entrées de l'agenda.
     * @param startOfWeek La date de début de la semaine.
     * @param endOfWeek La date de fin de la semaine.
     * @return Une liste des entrées de l'agenda alimentaire pour l'utilisateur sur cette plage de dates.
     */
    public List<FoodAgenda> getFoodAgendaEntries(User user, LocalDate startOfWeek, LocalDate endOfWeek) {
        return foodAgendaRepository.findByUserAndDateAgendaBetween(user, startOfWeek, endOfWeek);
    }
}
