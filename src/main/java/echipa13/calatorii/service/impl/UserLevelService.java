package echipa13.calatorii.service.impl;

import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.models.UserPoints;
import echipa13.calatorii.repository.UserPointsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLevelService {

    @Autowired
    private UserPointsRepository userPointsRepository;

    /**
     * Calculeaza level-ul progresiv si XP-ul ramas din punctele cheltuite si salveaza in DB
     */
    public UserPoints calculateLevel(UserEntity user, int totalSpent) {
        int xp = totalSpent / 2; // XP total din puncte
        int level = 1;
        int xpNeeded = 100;
        int increment = 20;

        while (xp >= xpNeeded) {
            xp -= xpNeeded;
            level++;
            xpNeeded += increment;
        }

        // preia sau creeaza UserPoints
        UserPoints up = userPointsRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    UserPoints newUp = new UserPoints();
                    newUp.setUser(user);
                    return newUp;
                });

        up.setLevel(level);
        up.setXpoints(xp);

        return userPointsRepository.save(up);
    }

    /**
     * Doar returneaza UserPoints deja calculat din DB
     */
    public UserPoints getUserPoints(UserEntity user) {
        return userPointsRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    UserPoints newUp = new UserPoints();
                    newUp.setUser(user);
                    newUp.setLevel(1);
                    newUp.setXpoints(0);
                    return userPointsRepository.save(newUp);
                });
    }
}
