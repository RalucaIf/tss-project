package echipa13.calatorii.repository;
import echipa13.calatorii.models.Destinations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DestinationsRepository extends JpaRepository<Destinations, Long> {
}
