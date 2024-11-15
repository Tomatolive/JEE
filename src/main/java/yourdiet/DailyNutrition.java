package yourdiet;

import lombok.Data;

@Data
public class DailyNutrition {
    private int totalCalories = 0;
    private double totalProteins = 0;
    private double totalCarbs = 0;
    private double totalFats = 0;

    public void addCalories(int calories) {
        this.totalCalories += calories;
    }

    public void addProteins(double proteins) {
        this.totalProteins += proteins;
    }

    public void addCarbs(double carbs) {
        this.totalCarbs += carbs;
    }

    public void addFats(double fats) {
        this.totalFats += fats;
    }
}
