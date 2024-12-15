package yourdiet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yourdiet.model.FoodAgenda;
import yourdiet.model.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FoodAgendaRepository extends JpaRepository<FoodAgenda, Long> {
    List<FoodAgenda> findByUserAndDateAgendaBetween(User user, LocalDate start, LocalDate end);
    List<FoodAgenda> findByUserAndDateAgenda(User user, LocalDate date);
}
