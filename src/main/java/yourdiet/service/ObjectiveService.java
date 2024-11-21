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
}