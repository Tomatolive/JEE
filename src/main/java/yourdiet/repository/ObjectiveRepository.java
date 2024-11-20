package yourdiet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yourdiet.model.Objective;
import yourdiet.model.User;

public interface ObjectiveRepository extends JpaRepository<Objective, Long> {
    Objective findByUser(User user);
}
