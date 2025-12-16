package echipa13.calatorii.service.impl;

import echipa13.calatorii.Dto.PointsDto;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.models.UserPoints;
import echipa13.calatorii.repository.UserPointsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PointsServiceImpl implements echipa13.calatorii.service.PointsService {

    private final UserPointsRepository userPointsRepository;

    @Autowired
    public PointsServiceImpl(UserPointsRepository userPointsRepository) {
        this.userPointsRepository = userPointsRepository;
    }

    @Override
    public void savePoints(PointsDto pointsDto) {
        if (pointsDto.getUser() == null) return;

        Optional<UserPoints> opt = userPointsRepository.findByUser_Id(pointsDto.getUser().getId());

        UserPoints userPoints;
        if (opt.isPresent()) {
            userPoints = opt.get();
            // actualizează punctele existente și nivelul
            userPoints.setPoints(pointsDto.getPoints());
            userPoints.setLevel(pointsDto.getLevel());
        } else {
            // dacă nu există, creăm un entry nou
            userPoints = new UserPoints();
            userPoints.setUser(pointsDto.getUser());
            userPoints.setPoints(pointsDto.getPoints());
            userPoints.setLevel(pointsDto.getLevel());
        }

        userPointsRepository.save(userPoints);
    }
}
