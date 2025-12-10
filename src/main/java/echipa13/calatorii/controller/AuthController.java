package echipa13.calatorii.controller;

import echipa13.calatorii.Dto.LoginDto;
import echipa13.calatorii.Dto.registerDto;
import echipa13.calatorii.models.Role;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.models.Guide;
import echipa13.calatorii.repository.RoleRepository;
import echipa13.calatorii.repository.UserRepository;
import echipa13.calatorii.repository.GuideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    // --------------------------------------------------
    //                     LOGIN
    // --------------------------------------------------
    @PostMapping(
            value = "/login",
            consumes = "application/x-www-form-urlencoded"
    )
    public ResponseEntity<?> login(LoginDto dto) {
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserEntity user = userRepository.findByEmail(dto.getEmail());
            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            List<String> roles = user.getRoles()
                    .stream()
                    .map(Role::getName)
                    .toList();

            return ResponseEntity.ok(
                    Map.of(
                            "message", "User signed in successfully",
                            "roles", roles
                    )
            );

        } catch (Exception e) {
            return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
    }


    // --------------------------------------------------
    //              REGISTER USER + GUIDE
    // --------------------------------------------------
    @PostMapping(
            value = "/register",
            consumes = "application/x-www-form-urlencoded"
    )
    public ResponseEntity<?> register(
            registerDto dto,
            @RequestParam(value = "isGuide", required = false) Boolean isGuide
    ) {

        // 1. Validare
        if (userRepository.findByUsername(dto.getUsername()) != null) {
            return new ResponseEntity<>("Username is already taken", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByEmail(dto.getEmail()) != null) {
            return new ResponseEntity<>("Email is already in use", HttpStatus.BAD_REQUEST);
        }

        // 2. Creăm userul
        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword_hash(passwordEncoder.encode(dto.getPassword_hash()));
        user.setEnabled(true);

        // rol implicit
        Role userRole = roleRepository.findByName("User");
        user.setRoles(new ArrayList<>());
        user.getRoles().add(userRole);

        // Salvăm userul
        userRepository.save(user);

        // 3. Dacă e ghid, adăugăm rol și creăm profilul Guide
        if (Boolean.TRUE.equals(isGuide)) {

            // Adăugăm rolul GUIDE
            Role guideRole = roleRepository.findByName("Guide");
            user.getRoles().add(guideRole);
            userRepository.save(user);

            // Creăm înregistrarea în tabela "guides"
            Guide guide = new Guide();
            guide.setDisplayName(dto.getUsername());
            guide.setVerified(false);
            guide.setRatingAvg(0.0);
            guide.setRatingCnt(0);
            guide.setCreatedAt(LocalDateTime.now());
            guide.setUser(user);

            guideRepository.save(guide);
        }

        return new ResponseEntity<>("User created", HttpStatus.CREATED);
    }
}
