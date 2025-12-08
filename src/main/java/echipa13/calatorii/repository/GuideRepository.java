package echipa13.calatorii.repository;

import echipa13.calatorii.models.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Long> {

    // Găsește un ghid după id
    @Override
    Optional<Guide> findById(Long id);

    // Căutare ghid după display_name, ignorând majuscule/minuscule
    @Query("SELECT g FROM Guide g WHERE LOWER(g.displayName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Guide> searchByDisplayName(@Param("query") String query);

    // Găsește ghid după user_id
    Optional<Guide> findByUserId(Long userId);

    // Găsește ghizi verificați cu rating minim
    @Query("SELECT g FROM Guide g WHERE g.verified = true AND g.ratingAvg >= :minRating")
    List<Guide> findVerifiedWithMinRating(@Param("minRating") Double minRating);

//    @Query("SELECT g FROM Guide c WHERE LOWER(g.displayName) LIKE LOWER(CONCAT('%', :query, '%'))")
//    List<guide> searchCalatorii(@Param("query") String query);
}
