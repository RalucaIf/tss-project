package echipa13.calatorii.repository;

import echipa13.calatorii.models.TripDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface TripDayRepository extends JpaRepository<TripDay, Long> {
    List<TripDay> findByTripIdOrderByDayIndex(Long tripId);
    Optional<TripDay> findByTripIdAndDayIndex(Long tripId, Integer dayIndex);
    boolean existsByTripIdAndDayIndex(Long tripId, Integer dayIndex);
}