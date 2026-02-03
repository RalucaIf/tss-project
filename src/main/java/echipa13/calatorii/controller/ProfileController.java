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

    // --- Salvare avatar ---  !!!
    @PostMapping("/avatar")
    public String saveAvatar(@RequestParam("avatar") String avatar, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/Itravel?error=unauthorized";
        }
        if (avatar == null || avatar.isEmpty()) {
            return "redirect:/profile?error=noAvatarSelected";
        }

        UserEntity user = userRepository.findByEmail(auth.getName());
        if (user == null) {
            user = userRepository.findByUsername(auth.getName());
        }
        if (user == null) {
            return "redirect:/Itravel?error=userNotFound";
        }

        // ✅ Salvează doar seed-ul
        user.setAvatar(avatar);
        userRepository.saveAndFlush(user);

        return "redirect:/profile?success=avatarUpdated"; // redirect către profil
    }

}
