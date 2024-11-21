package yourdiet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "objectives")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Objective {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Double calories;
    private Double proteins;
    private Double carbs;
    private Double fats;
    private Double weight;
}