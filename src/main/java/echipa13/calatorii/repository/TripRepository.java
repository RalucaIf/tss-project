package echipa13.calatorii.repository;

import echipa13.calatorii.models.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {

    // <<IMPORTANT>> Trip are acum câmpul createdAt -> putem ordona după el
    List<Trip> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Trip> findByIdAndUserId(Long id, Long userId);

    long deleteByIdAndUserId(Long id, Long userId);
}

