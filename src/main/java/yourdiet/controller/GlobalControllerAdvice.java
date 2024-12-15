package yourdiet.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import yourdiet.security.UserDetailsImpl;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addUserToModel(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails != null && userDetails.getUser() != null) {
            model.addAttribute("user", userDetails.getUser());
        }
    }
}
