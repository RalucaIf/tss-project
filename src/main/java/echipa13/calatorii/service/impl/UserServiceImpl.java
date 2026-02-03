package echipa13.calatorii.service.impl;

import echipa13.calatorii.Dto.RegistrationDto;
import echipa13.calatorii.models.Role;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.models.UserPoints;
import echipa13.calatorii.repository.RoleRepository;
import echipa13.calatorii.repository.UserPointsRepository;
import echipa13.calatorii.repository.UserRepository;
import echipa13.calatorii.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserPointsRepository userPointsRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           UserPointsRepository userPointsRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userPointsRepository = userPointsRepository;
    }

    @Override
    public void saveUser(RegistrationDto registrationDto) {

        UserEntity user = new UserEntity();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword_hash(registrationDto.getPassword_hash());
        user.setAvatar(registrationDto.getAvatar());

        // Rol default: User
        Role role = roleRepository.findByName("User");
        user.setRoles(Arrays.asList(role));

        // Salvăm userul
        userRepository.save(user);

        // 2. Creăm entry default în user_points
        UserPoints points = new UserPoints();
        points.setUser(user);
        points.setPoints(0);   // puncte initiale
        points.setLevel(1);    // nivel initial
        points.setCreatedAt(LocalDateTime.now());

        userPointsRepository.save(points); // salvăm în baza de date
    }

    @Override
    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserPoints> findAllUserPoints(){
        return userRepository.findAllUserPoints();
    }

    @Override
    public Long getCurrentUserId(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }

        String principalName;
        Object principal = auth.getPrincipal();

        if (principal instanceof UserDetails ud) {
            principalName = ud.getUsername(); // de obicei username
        } else {
            principalName = auth.getName();   // fallback
        }

        // 🔒 În unele proiecte login-ul e cu username, în altele cu email.
        // Încercăm întâi username, apoi email.
        UserEntity user = userRepository.findByUsername(principalName);
        if (user == null) {
            user = userRepository.findByEmail(principalName);
        }

        if (user == null) {
            throw new IllegalStateException("User not found for: " + principalName);
        }

        return user.getId();
    }
}
