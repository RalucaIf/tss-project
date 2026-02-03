package echipa13.calatorii.controller;

import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.models.UserPoints;
import echipa13.calatorii.repository.UserPointsRepository;
import echipa13.calatorii.repository.UserRepository;
import echipa13.calatorii.service.impl.UserLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPointsRepository userPointsRepository;

    @Autowired
    private UserLevelService userLevelService;

    // returneaza userul curent
    @ModelAttribute("user")
    public UserEntity currentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(String.valueOf(auth.getPrincipal()))) {
            return null;
        }
        String name = auth.getName();
        UserEntity user = userRepository.findByEmail(name);
        if (user == null) user = userRepository.findByUsername(name);
        return user;
    }

    // avatar navbar
    @ModelAttribute("navbarAvatarUrl")
    public String navbarAvatarUrl(Authentication auth) {
        final String fallback = "https://api.dicebear.com/9.x/personas/svg?seed=avatar1";
        UserEntity user = currentUser(auth);
        if (user == null) return fallback;

        String seed = user.getAvatar();
        if (seed == null || seed.isBlank()) return fallback;
        if (seed.startsWith("http") || seed.startsWith("/")) return seed;
        return "https://api.dicebear.com/9.x/personas/svg?seed=" + seed;
    }

    @ModelAttribute("navbarPoints")
    public Integer navbarPoints(Authentication auth) {
        UserEntity user = currentUser(auth);
        if (user == null) return 0;
        return userPointsRepository.findByUser_Id(user.getId()).map(UserPoints::getPoints).orElse(0);
    }

    @ModelAttribute("navbarLevel")
    public Integer navbarLevel(Authentication auth) {
        UserEntity user = currentUser(auth);
        if (user == null) return 1;
        return userLevelService.getUserPoints(user).getLevel();
    }

    @ModelAttribute("navbarXp")
    public Integer navbarXp(Authentication auth) {
        UserEntity user = currentUser(auth);
        if (user == null) return 0;
        return userLevelService.getUserPoints(user).getXpoints();
    }

    @ModelAttribute("navbarXpNeeded")
    public Integer navbarXpNeeded(Authentication auth) {
        UserEntity user = currentUser(auth);
        if (user == null) return 100;
        int level = userLevelService.getUserPoints(user).getLevel();
        return 100 + (level - 1) * 20;
    }

    @ModelAttribute("navbarXpPercent")
    public Integer navbarXpPercent(Authentication auth) {
        UserEntity user = currentUser(auth);
        if (user == null) return 0;
        UserPoints up = userLevelService.getUserPoints(user);
        int xp = up.getXpoints() == null ? 0 : up.getXpoints();
        int level = up.getLevel();
        int xpNeeded = 100 + (level - 1) * 20;
        return Math.min(100, (xp * 100) / xpNeeded);
    }

    @ModelAttribute("navbarRole")
    public String navbarRole(Authentication auth) {
        UserEntity user = currentUser(auth);
        if (user == null) return "User";
        if (user.getRoles() == null || user.getRoles().isEmpty()) return "User";
        return user.getRoles().get(0).getName();
    }

    @ModelAttribute("navbarLevelClass")
    public String navbarLevelClass(Authentication auth) {
        UserEntity user = currentUser(auth);
        if (user == null) return "level-1";
        int level = userLevelService.getUserPoints(user).getLevel();
        if (level >= 10) return "level-10";
        if (level >= 5) return "level-5";
        return "level-1";
    }

    @ModelAttribute("navbarFrameGif")
    public String navbarFrameGif(Authentication auth) {
        UserEntity user = currentUser(auth);
        if (user == null) return null;

        int level = userLevelService.getUserPoints(user).getLevel();
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals("Admin"));
        if (isAdmin) return "/imagini/rame/frame-admin.gif";
        if (level < 5) return null;

        int frameIndex = Math.min(8, (level - 5) / 2 + 1);
        return "/imagini/rame/frame" + frameIndex + ".gif";
    }
}
