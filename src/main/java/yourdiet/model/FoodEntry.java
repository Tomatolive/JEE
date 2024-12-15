package yourdiet.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "food_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "food_name", nullable = false)
    private String foodName;
    private Integer calories;
    private Double proteins;
    private Double carbs;
    private Double fats;
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToMany
    @JoinTable(
            name = "food_entries_tags",
            joinColumns = @JoinColumn(name = "food_entry_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tags> tags;


    @Override
    public String toString() {
        return "FoodEntry{" +
                "id=" + id +
                ", foodName='" + foodName + '\'' +
                '}';
    }

}
