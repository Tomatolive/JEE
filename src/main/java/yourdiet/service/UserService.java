package yourdiet.service;

import yourdiet.model.Tags;
import yourdiet.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yourdiet.repository.TagsRepository;
import yourdiet.repository.UserRepository;
import yourdiet.security.UserDetailsImpl;
import java.util.List;

@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TagsRepository tagsRepository;

    /**
     * Récupère un utilisateur par son nom d'utilisateur.
     *
     * @param username Le nom d'utilisateur de l'utilisateur recherché.
     * @return L'utilisateur correspondant au nom d'utilisateur.
     * @throws RuntimeException Si l'utilisateur n'est pas trouvé.
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + username));
    }

    /**
     * Récupère un utilisateur par son identifiant unique.
     *
     * @param id L'identifiant unique de l'utilisateur.
     * @return L'utilisateur correspondant à l'identifiant.
     * @throws RuntimeException Si l'utilisateur n'est pas trouvé.
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + id));
    }

    /**
     * Charge un utilisateur pour l'authentification en utilisant son nom d'utilisateur.
     *
     * @param username Le nom d'utilisateur de l'utilisateur à charger.
     * @return L'objet UserDetails contenant les informations de l'utilisateur.
     * @throws UsernameNotFoundException Si l'utilisateur n'est pas trouvé.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new UserDetailsImpl(user);
    }

    /**
     * Enregistre un nouvel utilisateur avec un nom d'utilisateur et un mot de passe.
     *
     * @param username Le nom d'utilisateur de l'utilisateur.
     * @param password Le mot de passe de l'utilisateur.
     * @return L'utilisateur nouvellement enregistré.
     * @throws RuntimeException Si le nom d'utilisateur existe déjà.
     */
    public User registerNewUser(String username, String password) throws RuntimeException {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    /**
     * Met à jour le nom d'utilisateur d'un utilisateur.
     *
     * @param user L'utilisateur dont le nom d'utilisateur doit être mis à jour.
     * @param newUsername Le nouveau nom d'utilisateur.
     * @throws RuntimeException Si le nouveau nom d'utilisateur existe déjà.
     */
    public void updateUsername(User user, String newUsername) {
        if (userRepository.findByUsername(newUsername).isPresent()) {
            throw new RuntimeException("Username already exists.");
        }
        user.setUsername(newUsername);
        userRepository.save(user);
    }

    /**
     * Met à jour le mot de passe d'un utilisateur.
     *
     * @param user L'utilisateur dont le mot de passe doit être mis à jour.
     * @param oldPassword L'ancien mot de passe de l'utilisateur.
     * @param newPassword Le nouveau mot de passe de l'utilisateur.
     * @param confirmPassword La confirmation du nouveau mot de passe.
     * @throws RuntimeException Si l'ancien mot de passe est incorrect ou si les nouveaux mots de passe ne correspondent pas.
     */
    public void updatePassword(User user, String oldPassword, String newPassword, String confirmPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password incorrect.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("New password fields don't match.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Met à jour le genre de l'utilisateur.
     *
     * @param user L'utilisateur dont le genre doit être mis à jour.
     * @param gender Le nouveau genre de l'utilisateur.
     * @throws RuntimeException Si l'utilisateur n'est pas trouvé.
     */
    public void updateGender(User user, String gender) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        existingUser.setGender(gender);
        userRepository.save(existingUser);
    }

    /**
     * Met à jour l'âge de l'utilisateur.
     *
     * @param user L'utilisateur dont l'âge doit être mis à jour.
     * @param age Le nouvel âge de l'utilisateur.
     * @return L'utilisateur mis à jour.
     * @throws RuntimeException Si l'utilisateur n'est pas trouvé.
     */
    public User updateAge(User user, Integer age) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        existingUser.setAge(age);
        userRepository.save(existingUser);
        return existingUser;
    }

    /**
     * Met à jour la taille de l'utilisateur.
     *
     * @param user L'utilisateur dont la taille doit être mise à jour.
     * @param height La nouvelle taille de l'utilisateur.
     * @throws RuntimeException Si l'utilisateur n'est pas trouvé.
     */
    public void updateHeight(User user, Double height) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        existingUser.setHeight(height);
        userRepository.save(existingUser);
    }

    /**
     * Met à jour le poids de l'utilisateur.
     *
     * @param user L'utilisateur dont le poids doit être mis à jour.
     * @param weight Le nouveau poids de l'utilisateur.
     * @throws RuntimeException Si l'utilisateur n'est pas trouvé.
     */
    public void updateWeight(User user, Double weight) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        existingUser.setWeight(weight);
        userRepository.save(existingUser);
    }

    /**
     * Met à jour le poids cible de l'utilisateur.
     *
     * @param user L'utilisateur dont le poids cible doit être mis à jour.
     * @param targetWeight Le nouveau poids cible de l'utilisateur.
     * @throws RuntimeException Si l'utilisateur n'est pas trouvé.
     */
    public void updateTargetWeight(User user, Double targetWeight) {
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        managedUser.setTargetWeight(targetWeight);
        userRepository.save(managedUser);
    }

    /**
     * Met à jour le niveau d'activité de l'utilisateur.
     *
     * @param user L'utilisateur dont le niveau d'activité doit être mis à jour.
     * @param activityLevel Le nouveau niveau d'activité de l'utilisateur.
     * @throws RuntimeException Si l'utilisateur n'est pas trouvé.
     */
    public void updateActivityLevel(User user, Double activityLevel) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        existingUser.setActivityLevel(activityLevel);
        userRepository.save(existingUser);
    }

    /**
     * Récupère les tags associés à un utilisateur.
     *
     * @param user L'utilisateur pour lequel récupérer les tags.
     * @return La liste des tags associés à l'utilisateur.
     */
    public List<Tags> getUserTags(User user) {
        return tagsRepository.findByUser(user);
    }

    /**
     * Récupère les tags en fonction de leurs identifiants.
     *
     * @param tagIds La liste des identifiants des tags à récupérer.
     * @return La liste des tags correspondant aux identifiants fournis.
     */
    public List<Tags> getTagsByIds(List<Long> tagIds) {
        return tagsRepository.findAllById(tagIds);
    }

    /**
     * Enregistre un nouveau tag dans la base de données.
     *
     * @param tag Le tag à enregistrer.
     */
    public void saveTag(Tags tag) {
        tagsRepository.save(tag);
    }

    /**
     * Supprime un tag de la base de données.
     *
     * @param id L'identifiant du tag à supprimer.
     */
    public void deleteTag(Long id) {
        tagsRepository.deleteById(id);
    }
}