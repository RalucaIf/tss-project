package echipa13.calatorii.repository;

import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.models.VisitedCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisitedCountryRepository  extends JpaRepository<VisitedCountry,Long> {

    List<VisitedCountry> findByUser(UserEntity user);
    Optional<VisitedCountry> findByUserAndCountry(UserEntity user, String country);
}
