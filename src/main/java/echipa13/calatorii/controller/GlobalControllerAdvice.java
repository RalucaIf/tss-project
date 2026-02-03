package echipa13.calatorii.controller;

import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.repository.UserPointsRepository;
import echipa13.calatorii.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;
@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPointsRepository userPointsRepository;


    // 🔹 1. Returnează userul curent
    @ModelAttribute("user")
    public UserEntity currentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(String.valueOf(auth.getPrincipal()))) {
            return null;
        }

        String name = auth.getName();
        UserEntity user = userRepository.findByEmail(name);

        if (user == null) {
            user = userRepository.findByUsername(name);
        }

        return user;
    }



    // 🔹 2. Avatar global
    @ModelAttribute("navbarAvatarUrl")
    public String navbarAvatarUrl(Authentication auth) {
        final String fallback = "https://api.dicebear.com/9.x/personas/svg?seed=avatar1";

        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(String.valueOf(auth.getPrincipal()))) {
            return fallback;
        }

        String name = auth.getName();
        UserEntity user = userRepository.findByEmail(name);

        if (user == null) user = userRepository.findByUsername(name);
        if (user == null) return fallback;

        String seed = user.getAvatar();
        if (seed == null || seed.isBlank()) return fallback;
        if (seed.startsWith("http") || seed.startsWith("/")) return seed;

        return "https://api.dicebear.com/9.x/personas/svg?seed=" + seed;
    }



    // 🔹 3. Puncte globale pentru navbar
    @ModelAttribute("navbarPoints")
    public Integer navbarPoints(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return 0;

        String name = auth.getName();
        UserEntity user = userRepository.findByEmail(name);

        if (user == null) user = userRepository.findByUsername(name);
        if (user == null) return 0;

        return userPointsRepository.findByUser_Id(user.getId())
                .map(p -> p.getPoints())
                .orElse(0);
    }



    // 🔹 4. Rol global pentru navbar
    @ModelAttribute("navbarRole")
    public String navbarRole(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return "User";

        String name = auth.getName();
        UserEntity user = userRepository.findByEmail(name);

        if (user == null) user = userRepository.findByUsername(name);
        if (user == null) return "User";

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return "User";
        }

        return user.getRoles().get(0).getName(); // primul rol
    }

    // 🔹 5. Level global pentru navbar
    @ModelAttribute("navbarLevel")
    public Integer navbarLevel(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return 1; // nivel default

        String name = auth.getName();
        UserEntity user = userRepository.findByEmail(name);

        if (user == null) user = userRepository.findByUsername(name);
        if (user == null) return 1;

        return userPointsRepository.findByUser_Id(user.getId())
                .map(p -> p.getLevel())
                .orElse(1); // nivel default dacă nu există intrare
    }


    @ModelAttribute("navbarLevelClass")
    public String navbarLevelClass(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return "level-1";

        String name = auth.getName();
        UserEntity user = userRepository.findByEmail(name);

        if (user == null) user = userRepository.findByUsername(name);
        if (user == null) return "level-1";

        int level = userPointsRepository.findByUser_Id(user.getId())
                .map(p -> p.getLevel())
                .orElse(1);

        // returnează o clasă în funcție de level
        if (level >= 10) return "level-10";
        if (level >= 5) return "level-5";
        return "level-1";
    }



    @ModelAttribute("navbarFrameGif")
    public String navbarFrameGif(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;

        String name = auth.getName();
        UserEntity user = userRepository.findByEmail(name);
        if (user == null) user = userRepository.findByUsername(name);
        if (user == null) return null;

        // verificăm nivel și rol
        int level = userPointsRepository.findByUser_Id(user.getId())
                .map(p -> p.getLevel())
                .orElse(0);

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("Admin"));

        if (isAdmin) {
            return "/imagini/rame/frame-admin.gif"; // rama specială Admin
        }

        if (level < 5) return null; // sub 5 nu ai rama

        // index rama standard pentru nivel > 5
        int frameIndex = Math.min(8, (level - 5) / 2 + 1);
        return "/imagini/rame/frame" + frameIndex + ".gif";
    }



}

