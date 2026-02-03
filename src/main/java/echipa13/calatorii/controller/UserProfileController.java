package echipa13.calatorii.controller;

import echipa13.calatorii.Dto.TourDto;
import echipa13.calatorii.models.*;
import echipa13.calatorii.repository.*;
import echipa13.calatorii.service.TourService;
import echipa13.calatorii.service.impl.UserLevelService;
import echipa13.calatorii.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class UserProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private UserPointsRepository userPointsRepository;

    @Autowired
    private TourService tourService;

    @Autowired
    private TourPurchaseRepository tourPurchaseRepository;

    @Autowired
    private UserLevelService userLevelService;

    @GetMapping("/user_profile")
    public String userProfile(Model model, Authentication authentication) {

        if (authentication == null) return "redirect:/login";

        String username = authentication.getName();
        UserEntity user = userService.findByUsername(username);
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);

        // Tururile ghidului
        Guide guide = guideRepository.findByUser_Id(user.getId()).orElse(null);
        List<TourDto> calatorii = guide != null ? tourService.findByGuideId(guide.getId()) : Collections.emptyList();
        model.addAttribute("calatorii", calatorii);
        model.addAttribute("isGuide", guide != null);

        // Leaderboard + frame-uri avatar
        List<UserPoints> allUserPoints = userRepository.findAllUserPoints();
        Map<Long, Integer> frameNumbers = new HashMap<>();
        Map<Long, Integer> pointsSpent = new HashMap<>();
        Map<Long, Integer> userLevels = new HashMap<>();
        Map<Long, Integer> userXp = new HashMap<>();

        for (UserPoints up : allUserPoints) {
            // puncte cheltuite
            List<TourPurchase> purchases = tourPurchaseRepository.findByBuyer(up.getUser());
            int totalSpent = purchases.stream().mapToInt(TourPurchase::getPointsPaid).sum();
            pointsSpent.put(up.getUser().getId(), totalSpent);

            // calculeaza level + XP folosind serviciul
            UserPoints calculated = userLevelService.calculateLevel(up.getUser(), totalSpent);
            userLevels.put(up.getUser().getId(), calculated.getLevel());
            userXp.put(up.getUser().getId(), calculated.getXpoints());

            // frame avatar
            int level = calculated.getLevel() != null ? calculated.getLevel() : 1;
            int frameNumber = Math.min(8, (level - 5) / 2 + 1);
            frameNumbers.put(up.getUser().getId(), frameNumber);
        }

        // sortam leaderboard descrescator dupa puncte cheltuite
        allUserPoints.sort((a, b) -> Integer.compare(
                pointsSpent.getOrDefault(b.getUser().getId(), 0),
                pointsSpent.getOrDefault(a.getUser().getId(), 0))
        );

        model.addAttribute("userPoints", allUserPoints);
        model.addAttribute("frameNumbers", frameNumbers);
        model.addAttribute("pointsSpent", pointsSpent);
        model.addAttribute("userLevels", userLevels);
        model.addAttribute("userXp", userXp);

        // excursiile curentului user
        List<TourPurchase> purchases = tourPurchaseRepository.findByBuyer(user);
        model.addAttribute("purchases", purchases);

        return "user_profile";
    }
}
