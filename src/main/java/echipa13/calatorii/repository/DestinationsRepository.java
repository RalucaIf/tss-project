package echipa13.calatorii.repository;
import echipa13.calatorii.models.Destinations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DestinationsRepository extends JpaRepository<Destinations, Long> {

    List<Destinations> findByName(String name);
}
