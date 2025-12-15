package echipa13.calatorii.repository;

import echipa13.calatorii.models.TripItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripItemRepository extends JpaRepository<TripItem, Long> {
    List<TripItem> findByTrip_Id(Long tripId);
    List<TripItem> findByTrip_IdOrderByDayIndexAscIdAsc(Long tripId);

    @Query("select coalesce(max(t.dayIndex), 0) from TripItem t where t.trip.id = :tripId")
    int findMaxDayIndex(Long tripId);
}

