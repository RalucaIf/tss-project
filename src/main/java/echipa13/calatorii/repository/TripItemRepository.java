package echipa13.calatorii.repository;

import echipa13.calatorii.models.TripItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripItemRepository extends JpaRepository<TripItem, Long> {
    List<TripItem> findByTripIdOrderByDayIndexAscCreatedAtAsc(Long tripId);
}

