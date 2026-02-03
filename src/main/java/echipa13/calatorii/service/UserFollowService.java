package echipa13.calatorii.service;

import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.models.UserFollow;
import echipa13.calatorii.repository.UserFollowRepository;
import echipa13.calatorii.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserFollowService {

    private final UserFollowRepository followRepo;
    private final UserRepository userRepo;

    public UserFollowService(UserFollowRepository followRepo, UserRepository userRepo) {
        this.followRepo = followRepo;
        this.userRepo = userRepo;
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        if (followerId == null || followingId == null) return false;
        return followRepo.existsByFollower_IdAndFollowing_Id(followerId, followingId);
    }

    public long followersCount(Long userId) {
        return followRepo.countByFollowing_Id(userId);
    }

    public long followingCount(Long userId) {
        return followRepo.countByFollower_Id(userId);
    }

    public boolean isMutual(Long userAId, Long userBId) {
        return isFollowing(userAId, userBId) && isFollowing(userBId, userAId);
    }

    /**
     * @return true dacă după apel userul follower îl urmărește pe target (FOLLOW),
     *         false dacă s-a dat UNFOLLOW
     */
    @Transactional
    public boolean toggleFollow(String currentUserEmail, Long targetUserId) {
        if (currentUserEmail == null) {
            throw new IllegalArgumentException("currentUserEmail is null");
        }
        if (targetUserId == null) {
            throw new IllegalArgumentException("targetUserId is null");
        }

        UserEntity me = userRepo.findByEmail(currentUserEmail);
        if (me == null) {
            throw new IllegalStateException("Current user not found by email: " + currentUserEmail);
        }

        if (me.getId().equals(targetUserId)) {
            // nu te poți urmări pe tine
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        UserEntity target = userRepo.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Target user not found: " + targetUserId));

        return followRepo.findByFollower_IdAndFollowing_Id(me.getId(), target.getId())
                .map(existing -> {
                    followRepo.delete(existing);
                    return false; // UNFOLLOW
                })
                .orElseGet(() -> {
                    followRepo.save(new UserFollow(me, target));
                    return true; // FOLLOW
                });
    }
}

