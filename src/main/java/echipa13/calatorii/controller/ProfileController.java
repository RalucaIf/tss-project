package echipa13.calatorii.controller;

import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    // --- Pagina profilului ---
    @GetMapping
    public String profile(Model model, Authentication auth) {
        String email = auth.getName(); // preluăm email-ul utilizatorului logat
        UserEntity user = userRepository.findByEmail(email);

        if (user == null) {
            return "redirect:/Itravel?error=userNotFound";
        }

        model.addAttribute("user", user); // UserEntity în model
        return "layout"; // numele template-ului Thymeleaf
    }

    // --- Salvare avatar ---
    @PostMapping("/avatar")
    public String saveAvatar(@RequestParam("avatar") String avatar, Authentication auth) {
        String email = auth.getName();
        UserEntity user = userRepository.findByEmail(email);

        if (user == null) {
            return "redirect:/Itravel?error=userNotFound";
        }

        if (avatar != null && !avatar.isEmpty()) {
            user.setAvatar(avatar);
            userRepository.save(user);
        } else {
            return "redirect:/profile?error=noAvatarSelected";
        }

        return "redirect:/Itravel?success=avatarUpdated";
    }
}
