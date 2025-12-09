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
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping(
            value = "/login",
            consumes = "application/x-www-form-urlencoded"
    )
    public ResponseEntity<String> login(LoginDto dto) {
        try {
            // autentificare cu email
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return new ResponseEntity<>("User signed in successfully", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
    }



    @PostMapping(
            value = "/register",
            consumes = "application/x-www-form-urlencoded"
    )
    public ResponseEntity<?> register(registerDto dto) {

        if (userRepository.findByUsername(dto.getUsername()) != null) {
            return new ResponseEntity<>("Username is already taken", HttpStatus.BAD_REQUEST);
        }

        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword_hash(passwordEncoder.encode(dto.getPassword_hash()));
        user.setEnabled(true);

        Role defaultRole = roleRepository.findByName("User");
        user.setRoles(Collections.singletonList(defaultRole));

        userRepository.save(user);

        return new ResponseEntity<>("User created", HttpStatus.CREATED);
    }
}

