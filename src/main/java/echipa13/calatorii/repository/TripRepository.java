package echipa13.calatorii.repository;

import echipa13.calatorii.models.Trip;
import echipa13.calatorii.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {

    // <<IMPORTANT>> Trip are acum câmpul createdAt -> putem ordona după el
    List<Trip> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Trip> findByIdAndUserId(Long id, Long userId);

    long deleteByIdAndUserId(Long id, Long userId);

    List<Trip> findByUserIdAndCategoryIgnoreCaseOrderByCreatedAtDesc(Long userId, String category);

    @Query("""
           select distinct t.category
           from Trip t
           where t.user.id = :uid and t.category is not null and trim(t.category) <> ''
           order by t.category
           """)
    List<String> findDistinctCategoriesForUser(@Param("uid") Long uid);

    boolean existsByUserAndExcursii_Id(UserEntity user, Long tourId);
}

