package echipa13.calatorii.controller;

import echipa13.calatorii.models.Destinations;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.models.Destinations;
import echipa13.calatorii.repository.DestinationsRepository;
import echipa13.calatorii.repository.UserRepository;
import echipa13.calatorii.repository.DestinationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired private UserRepository userRepository;
    @Autowired private DestinationsRepository destinationRepository;

    @GetMapping
    public String profile(Model model, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        UserEntity user = userRepository.findByEmail(auth.getName());
        if (user == null) {
            user = userRepository.findByUsername(auth.getName());
        }
        if (user == null) {
            return "redirect:/Itravel?error=userNotFound";
        }

        model.addAttribute("user", user);

        // ✅ FAVORITE DESTINATIONS (direct din tabela favorite_destinations)
        List<Destinations> favoriteDestinations =
                destinationRepository.findFavoriteDestinations(user.getId());

        model.addAttribute("favoriteDestinations", favoriteDestinations);
        model.addAttribute("favoriteDestinationsCount", favoriteDestinations.size());

        return "user_profile";
    }

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

        user.setAvatar(avatar);
        userRepository.saveAndFlush(user);

        return "redirect:/profile?success=avatarUpdated";
    }
}
