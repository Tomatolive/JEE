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

import java.util.ArrayList;
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

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + username));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + id));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new UserDetailsImpl(user);
    }

    public User registerNewUser(String username, String password) throws RuntimeException {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public void updateUsername(User user, String newUsername) {
        if (userRepository.findByUsername(newUsername).isPresent()) {
            throw new RuntimeException("Username already exists.");
        }
        user.setUsername(newUsername);
        userRepository.save(user);
    }

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

    public void updateGender(User user, String gender) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        existingUser.setGender(gender);
        userRepository.save(existingUser);
    }

    public User updateAge(User user, Integer age) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        existingUser.setAge(age);
        userRepository.save(existingUser);
        return existingUser;
    }

    public void updateHeight(User user, Double height) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        existingUser.setHeight(height);
        userRepository.save(existingUser);
    }

    public void updateWeight(User user, Double weight) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        existingUser.setWeight(weight);
        userRepository.save(existingUser);
    }

    public void updateTargetWeight(User user, Double targetWeight) {
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        managedUser.setTargetWeight(targetWeight);
        userRepository.save(managedUser);
    }

    public void updateActivityLevel(User user, Double activityLevel) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        existingUser.setActivityLevel(activityLevel);
        userRepository.save(existingUser);
    }

    public List<Tags> getUserTags(User user) {
        return tagsRepository.findByUser(user);
    }

    public List<Tags> getTagsByIds(List<Long> tagIds) {
        return tagsRepository.findAllById(tagIds);
    }

    public void saveTag(Tags tag) {
        tagsRepository.save(tag);
    }

    public void deleteTag(Long id) {
        tagsRepository.deleteById(id);
    }
}