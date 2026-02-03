package echipa13.calatorii.repository;

import echipa13.calatorii.models.TripWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TripWalletRepository extends JpaRepository<TripWallet, Long> {
    Optional<TripWallet> findByTrip_Id(Long tripId);
    boolean existsByTrip_Id(Long tripId);
}