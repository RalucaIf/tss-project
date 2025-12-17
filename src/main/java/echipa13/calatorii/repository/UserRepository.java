package echipa13.calatorii.repository;

import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.models.UserPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    UserEntity findByUsername(String username);

    @Query ("SELECT up FROM UserPoints up JOIN FETCH up.user ORDER BY up.points DESC")
    List<UserPoints> findAllUserPoints();

}
