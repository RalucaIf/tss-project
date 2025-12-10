package echipa13.calatorii.controller;

import echipa13.calatorii.Dto.LoginDto;
import echipa13.calatorii.Dto.RegistrationDto;
import echipa13.calatorii.Dto.registerDto;
import echipa13.calatorii.models.Role;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.repository.RoleRepository;
import echipa13.calatorii.repository.UserRepository;
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

import java.util.Collections;

@Controller
public class UserProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/user_profile")
    public String userProfile(Model model, Authentication authentication) {

        if (authentication == null) {
            System.out.println("Authentication is null!");
            return "redirect:/login";
        }

        String username = authentication.getName();
        System.out.println("Logged in username: " + username);

        UserEntity user = userService.findByEmail(username);
        if (user == null) {
            System.out.println("User not found in DB!");
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "user_profile";
    }

}
