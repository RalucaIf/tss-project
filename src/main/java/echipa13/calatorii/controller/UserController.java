package echipa13.calatorii.controller;


import echipa13.calatorii.Dto.TourDto;
import echipa13.calatorii.Dto.UserProfileDto;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.models.UserPoints;
import echipa13.calatorii.repository.UserRepository;
import echipa13.calatorii.repository.UserPointsRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserPointsRepository pointsRepository;

    public UserController(UserRepository userRepository, UserPointsRepository pointsRepository) {
        this.userRepository = userRepository;
        this.pointsRepository = pointsRepository;
    }

    @GetMapping("/profile/{id}")
    public UserProfileDto getProfile(@PathVariable Long id) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserPoints userPoints = pointsRepository.findByUserId(id)
                .orElse(new UserPoints()); // fallback 0

        UserProfileDto dto = new UserProfileDto();
        dto.setUsername(user.getUsername());
        dto.setAvatarUrl(user.getAvatar());
        dto.setRole(user.getRoles().get(0).getName());
        dto.setPoints(userPoints.getPoints());

        return dto;
    }
}
