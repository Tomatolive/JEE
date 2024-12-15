package yourdiet.controller;

import yourdiet.model.Tags;
import yourdiet.model.User;
import yourdiet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/tags")
public class TagsController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showGererTags(Model model, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        List<Tags> userTags = userService.getUserTags(user);
        model.addAttribute("tags", userTags);
        return "gererTags";
    }

    @PostMapping("/ajouter")
    public String addTag(@RequestParam String tagName, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        Tags newTag = new Tags();
        newTag.setName(tagName);
        newTag.setUser(user);
        userService.saveTag(newTag);
        return "redirect:/tags";
    }

    @PostMapping("/supprimer/{id}")
    public String deleteTag(@PathVariable Long id) {
        userService.deleteTag(id); 
        return "redirect:/tags";
    }
}
