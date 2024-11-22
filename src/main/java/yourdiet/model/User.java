package yourdiet.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @JsonIgnore
    private String password;

    private String gender;
    private Double weight;
    private Double height;
    private Integer age;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FoodEntry> foodEntries = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FoodAgenda> foodAgendas = new ArrayList<>();
    public String getUsername() {
        return username;
    }
}