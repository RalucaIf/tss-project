package echipa13.calatorii.controller;

import echipa13.calatorii.Dto.LoginDto;
import echipa13.calatorii.Dto.RegistrationDto;
import echipa13.calatorii.Dto.TourDto;
import echipa13.calatorii.Dto.registerDto;
import echipa13.calatorii.models.*;
import echipa13.calatorii.repository.*;
import echipa13.calatorii.service.GuideService;
import echipa13.calatorii.service.TourService;
import echipa13.calatorii.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private GuideService guideService;
    @Autowired
    private TourService tourService;

    @Autowired
    private TourPurchaseRepository tourPurchaseRepository;

    @GetMapping("/user_profile")
    public String userProfile(Model model, Authentication authentication) {

        if (authentication == null) {
            System.out.println("Authentication is null!");
            return "redirect:/login";
        }

        String username = authentication.getName();
        System.out.println("Logged in username: " + username);

        UserEntity user = userService.findByUsername(username);
        if (user == null) {
            System.out.println("User not found in DB!");
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        Guide guide = guideRepository.findByUser_Id(user.getId()).orElse(null);

        List<TourDto> calatorii;
        if (guide != null) {
            calatorii = tourService.findByGuideId(guide.getId());
        } else {
            calatorii = Collections.emptyList();
        }

        model.addAttribute("calatorii", calatorii);
        model.addAttribute("isGuide", guide != null);

        List<UserPoints> userPoints = userRepository.findAllUserPoints();
        model.addAttribute("userPoints", userPoints);

        Map<Long, Integer> frameNumbers = new HashMap<>();
        for(UserPoints userPoint : userPoints){
            int level = userPoint.getLevel();
            int frameNumber = Math.min(8, (level - 5)/2 + 1);
            frameNumbers.put(userPoint.getUser().getId(), frameNumber); // use user's ID as key
        }
        model.addAttribute("frameNumbers", frameNumbers);

        List<TourPurchase> purchases = tourPurchaseRepository.findByBuyer(user);
        model.addAttribute("purchases", purchases);

        return "user_profile";
    }

}
