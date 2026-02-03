package echipa13.calatorii.repository;

import echipa13.calatorii.models.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

    Optional<UserFollow> findByFollower_IdAndFollowing_Id(Long followerId, Long followingId);

    boolean existsByFollower_IdAndFollowing_Id(Long followerId, Long followingId);

    long countByFollower_Id(Long followerId);   // câți urmăresc eu
    long countByFollowing_Id(Long followingId); // câți urmăritori am

    @Query("select uf.following.id from UserFollow uf where uf.follower.id = :followerId")
    Set<Long> findFollowingIds(@Param("followerId") Long followerId);
}

