package echipa13.calatorii.repository;
import echipa13.calatorii.models.Destinations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DestinationsRepository extends JpaRepository<Destinations, Long> {
    @Query(value = """
        SELECT d.*
        FROM destinations d
        JOIN favorite_destinations fd ON fd.destination_id = d.id
        WHERE fd.user_id = :userId
        ORDER BY d.id DESC
    """, nativeQuery = true)
    List<Destinations> findFavoriteDestinations(@Param("userId") Long userId);
}
