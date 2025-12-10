package echipa13.calatorii.controller;

import echipa13.calatorii.models.UserEntity;
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

    @ModelAttribute("user")
    public UserEntity currentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(String.valueOf(auth.getPrincipal()))) {
            return null; // anonim -> în layout ai sec:authorize="isAuthenticated()" care ascunde zona
        }
        String name = auth.getName(); // la tine e de regulă email sau username
        UserEntity u = userRepository.findByEmail(name);
        if (u == null) {
            u = userRepository.findByUsername(name);
        }
        return u; // poate fi null dacă nu găsește – dar sec:authorize protejează zona
    }

    @ModelAttribute("navbarAvatarUrl")
    public String navbarAvatarUrl(Authentication auth) {
        final String fallback = "https://api.dicebear.com/9.x/dylan/svg?seed=avatar1";
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(String.valueOf(auth.getPrincipal()))) {
            return fallback;
        }

        String name = auth.getName(); // de obicei email sau username
        UserEntity u = userRepository.findByEmail(name);
        if (u == null) {
            u = userRepository.findByUsername(name);
        }
        if (u == null) return fallback;

        String seed = u.getAvatar();
        if (seed == null || seed.isBlank()) return fallback;
        if (seed.startsWith("http") || seed.startsWith("/")) return seed;
        return "https://api.dicebear.com/9.x/dylan/svg?seed=" + seed;
    }


}
