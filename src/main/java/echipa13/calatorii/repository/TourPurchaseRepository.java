package echipa13.calatorii.repository;

import echipa13.calatorii.models.Tour;
import echipa13.calatorii.models.TourPurchase;
import echipa13.calatorii.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourPurchaseRepository extends JpaRepository<TourPurchase, Long> {

    List<TourPurchase> findByBuyer(UserEntity buyer);

    boolean existsByBuyerAndTour(UserEntity buyer, Tour tour);
}
