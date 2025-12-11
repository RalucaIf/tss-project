package echipa13.calatorii.controller;

import echipa13.calatorii.Dto.LoginDto;
import echipa13.calatorii.Dto.RegistrationDto;
import echipa13.calatorii.Dto.TourDto;
import echipa13.calatorii.Dto.registerDto;
import echipa13.calatorii.models.Guide;
import echipa13.calatorii.models.Role;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.repository.GuideRepository;
import echipa13.calatorii.repository.RoleRepository;
import echipa13.calatorii.repository.UserRepository;
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
import java.util.List;

import java.util.Collections;

@Controller
public class UserProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private GuideService guideService;
    @Autowired
    private TourService tourService;

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


        return "user_profile";
    }

}
