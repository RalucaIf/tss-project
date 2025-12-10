package echipa13.calatorii.service.impl;

import echipa13.calatorii.Dto.RegistrationGuideDto;
import echipa13.calatorii.models.Guide;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.repository.GuideRepository;
import echipa13.calatorii.repository.UserRepository;
import echipa13.calatorii.service.GuideService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class GuideServiceImpl implements GuideService {

    private final GuideRepository guideRepository;
    private final UserRepository userRepository;

    public GuideServiceImpl(GuideRepository guideRepository, UserRepository userRepository) {
        this.guideRepository = guideRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void saveGuide(Long userId, RegistrationGuideDto dto) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Guide not found"));

        Guide guide = Guide.builder()
                .user(user)
                .displayName(dto.getDisplayName())
                .bio(dto.getBio())
                .verified(false)
                .ratingAvg(0.0)
                .ratingCnt(0)
                .createdAt(LocalDateTime.now())
                .imageUrl(dto.getImageUrl())
                .build();

        guideRepository.save(guide);
    }


    public boolean isUserGuide(Long userId) {
        return guideRepository.existsByUser_Id(userId);
    }

    public Optional<Guide> getGuideByUserId(Long userId) {
        return guideRepository.findByUser_Id(userId);
    }
}
