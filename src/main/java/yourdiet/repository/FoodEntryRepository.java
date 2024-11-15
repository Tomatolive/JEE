package yourdiet.repository;
import yourdiet.model.FoodEntry;
import yourdiet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FoodEntryRepository extends JpaRepository<FoodEntry, Long> {
    List<FoodEntry> findByUserAndDateTimeBetween(User user, LocalDateTime start, LocalDateTime end);
}
