package yourdiet.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "food_agenda")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodAgenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    @JsonIgnore
    private FoodEntry foodEntry;

    @Column(name = "date_agenda", nullable = false)
    private LocalDate dateAgenda;

    public enum MealType {
        BREAKFAST,
        LUNCH,
        DINNER
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;
}
