package yourdiet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yourdiet.model.Objective;
import yourdiet.model.User;
import yourdiet.repository.ObjectiveRepository;
import yourdiet.repository.UserRepository;

@Service
@Transactional
public class ObjectiveService {
    @Autowired
    private ObjectiveRepository objectiveRepository;

    @Autowired
    private UserRepository userRepository;

    private static final double CALORIES_PER_KG = 7700.0; // Calories par kg de poids
    private static final int DAYS_IN_MONTH = 30;
    private static final double PROTEIN_PERCENTAGE = 0.25; // 25% des calories
    private static final double CARBS_PERCENTAGE = 0.30;   // 25% des calories
    private static final double FATS_PERCENTAGE = 0.45;    // 45// % des calories
    private static final double CALORIES_PER_GRAM_PROTEIN = 4.0;
    private static final double CALORIES_PER_GRAM_CARBS = 4.0;
    private static final double CALORIES_PER_GRAM_FATS = 9.0;

    public Objective getObjectiveByUser(User user) {
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return objectiveRepository.findByUser(managedUser);
    }

    public Objective saveObjectiveForUser(Objective objective, User user) {
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        objective.setUser(managedUser);
        return objectiveRepository.save(objective);
    }

    public Objective calculateObjective(Objective objective) {
        User user = objective.getUser();
        double basalMetabolism = calculateBasalMetabolism(user);

        Double targetWeight = user.getTargetWeight();
        Double currentWeight = user.getWeight();

        // Calculer les calories quotidiennes de base
        double dailyCalories = basalMetabolism;

        if (targetWeight != null && currentWeight != null && !targetWeight.equals(currentWeight)) {
            double weightDifference = targetWeight - currentWeight;
            // Limiter la variation de poids à un maximum sain (0.5kg par semaine)
            double maxWeightChangePerMonth = weightDifference > 0 ? 2.0 : -2.0;
            weightDifference = Math.max(Math.min(weightDifference, maxWeightChangePerMonth), -maxWeightChangePerMonth);

            // Calculer les calories supplémentaires/déficitaires par jour
            // Pour gagner/perdre weightDifference kg en un mois
            double additionalCaloriesPerDay = (CALORIES_PER_KG * weightDifference) / DAYS_IN_MONTH;

            // Ajouter/soustraire ces calories au métabolisme de base
            dailyCalories += additionalCaloriesPerDay;
        }

        // S'assurer que les calories quotidiennes restent dans des limites saines
        double minimumCalories = user.getGender().equals("Male") ? 1500.0 : 1200.0;
        double maximumCalories = user.getGender().equals("Male") ? 4000.0 : 3500.0;
        dailyCalories = Math.min(Math.max(dailyCalories, minimumCalories), maximumCalories);

        // Définir les calories et calculer les macronutriments
        objective.setCalories((int) Math.round(dailyCalories));
        calculateMacronutrients(objective, dailyCalories);

        return objective;
    }

    private double calculateBasalMetabolism(User user) {
        if (user.getWeight() == null || user.getHeight() == null || user.getAge() == null) {
            throw new IllegalArgumentException("Données utilisateur incomplètes");
        }

        // Formule de Mifflin-St Jeor pour le BMR
        double bmr;
        if (user.getGender().equals("Male")) {
            bmr = 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() + 5;
        } else {
            bmr = 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() - 161;
        }

        // Facteur d'activité
        return bmr * user.getActivityLevel();
    }

    private void calculateMacronutrients(Objective objective, double dailyCalories) {
        double caloriesLeft = dailyCalories;
        double proteinGrams = (caloriesLeft * PROTEIN_PERCENTAGE) / CALORIES_PER_GRAM_PROTEIN;
        caloriesLeft -= proteinGrams * CALORIES_PER_GRAM_PROTEIN;
        double carbsGrams = (caloriesLeft * CARBS_PERCENTAGE) / CALORIES_PER_GRAM_CARBS;
        caloriesLeft -= carbsGrams * CALORIES_PER_GRAM_CARBS;
        double fatsGrams = (caloriesLeft * FATS_PERCENTAGE) / CALORIES_PER_GRAM_FATS;

        objective.setProteins(Math.round(proteinGrams * 10.0) / 10.0);
        objective.setCarbs(Math.round(carbsGrams * 10.0) / 10.0);
        objective.setFats(Math.round(fatsGrams * 10.0) / 10.0);
    }
}