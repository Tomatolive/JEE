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
    private static final double CALORIES_PER_GRAM_PROTEIN = 4.0;
    private static final double CALORIES_PER_GRAM_CARBS = 4.0;
    private static final double CALORIES_PER_GRAM_FATS = 9.0;

    /**
     * Récupère l'objectif nutritionnel d'un utilisateur donné.
     *
     * @param user L'utilisateur pour lequel récupérer l'objectif.
     * @return L'objectif nutritionnel associé à l'utilisateur.
     * @throws RuntimeException Si l'utilisateur n'est pas trouvé.
     */
    public Objective getObjectiveByUser(User user) {
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return objectiveRepository.findByUser(managedUser);
    }

    /**
     * Enregistre un objectif nutritionnel pour un utilisateur donné.
     *
     * @param objective L'objectif nutritionnel à enregistrer.
     * @param user L'utilisateur auquel l'objectif appartient.
     * @return L'objectif nutritionnel enregistré.
     * @throws RuntimeException Si l'utilisateur n'est pas trouvé.
     */
    public Objective saveObjectiveForUser(Objective objective, User user) {
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        objective.setUser(managedUser);
        return objectiveRepository.save(objective);
    }

    /**
     * Calcule l'objectif nutritionnel d'un utilisateur, en fonction de son métabolisme de base,
     * de son poids cible, de son poids actuel et de son niveau d'activité.
     *
     * @param objective L'objectif nutritionnel à calculer.
     * @return L'objectif nutritionnel mis à jour avec les valeurs de calories et macronutriments.
     */
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

            // Calculer les calories supplémentaires/déficitaires par jour pour atteindre l'objectif de poids
            double additionalCaloriesPerDay = (CALORIES_PER_KG * weightDifference) / DAYS_IN_MONTH;

            // Ajouter ou soustraire ces calories au métabolisme de base
            dailyCalories += additionalCaloriesPerDay;
        }

        // Ajouter les calories de thermogenèse (10% des calories totales)
        double thermogenesisCalories = dailyCalories * 0.10;
        dailyCalories += thermogenesisCalories;

        // S'assurer que les calories quotidiennes restent dans des limites saines
        double minimumCalories = user.getGender().equals("Male") ? 1500.0 : 1200.0;
        double maximumCalories = user.getGender().equals("Male") ? 4000.0 : 3500.0;
        dailyCalories = Math.min(Math.max(dailyCalories, minimumCalories), maximumCalories);

        // Définir les calories et calculer les macronutriments
        objective.setCalories((int) Math.round(dailyCalories));
        calculateMacronutrients(objective, dailyCalories);

        return objective;
    }

    /**
     * Calcule le métabolisme de base (BMR) d'un utilisateur à partir de sa taille, son poids,
     * son âge et son sexe en utilisant la formule de Mifflin-St Jeor.
     *
     * @param user L'utilisateur pour lequel calculer le métabolisme de base.
     * @return La valeur du métabolisme de base ajustée selon le niveau d'activité.
     * @throws IllegalArgumentException Si des données utilisateur nécessaires sont manquantes.
     */
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

    /**
     * Calcule les macronutriments (protéines, glucides, lipides) en fonction des calories quotidiennes
     * et du niveau d'activité de l'utilisateur. Les pourcentages des macronutriments varient selon
     * l'activité physique.
     *
     * @param objective L'objectif nutritionnel de l'utilisateur à mettre à jour.
     * @param dailyCalories Le nombre total de calories quotidiennes.
     */
    private void calculateMacronutrients(Objective objective, double dailyCalories) {
        double proteinPercentage;
        double carbsPercentage;
        double fatsPercentage;

        double activityLevel = objective.getUser().getActivityLevel();
        // Déterminer les pourcentages des macronutriments en fonction du niveau d'activité
        if (activityLevel <= 1.2) {
            proteinPercentage = 0.30;
            carbsPercentage = 0.25;
            fatsPercentage = 0.45;
        } else if (activityLevel <= 1.375) {
            proteinPercentage = 0.25;
            carbsPercentage = 0.30;
            fatsPercentage = 0.45;
        } else if (activityLevel <= 1.55) {
            proteinPercentage = 0.20;
            carbsPercentage = 0.40;
            fatsPercentage = 0.40;
        } else if (activityLevel <= 1.725) {
            proteinPercentage = 0.20;
            carbsPercentage = 0.50;
            fatsPercentage = 0.30;
        } else {
            proteinPercentage = 0.15;
            carbsPercentage = 0.55;
            fatsPercentage = 0.30;
        }

        // Calculer les calories associées à chaque macronutriment
        double proteinCalories = dailyCalories * proteinPercentage;
        double carbsCalories = dailyCalories * carbsPercentage;
        double fatsCalories = dailyCalories * fatsPercentage;

        // Convertir les calories en grammes
        double proteinGrams = proteinCalories / CALORIES_PER_GRAM_PROTEIN;
        double carbsGrams = carbsCalories / CALORIES_PER_GRAM_CARBS;
        double fatsGrams = fatsCalories / CALORIES_PER_GRAM_FATS;

        // Arrondir les valeurs à une décimale
        objective.setProteins(Math.round(proteinGrams * 10.0) / 10.0);
        objective.setCarbs(Math.round(carbsGrams * 10.0) / 10.0);
        objective.setFats(Math.round(fatsGrams * 10.0) / 10.0);
    }
}
