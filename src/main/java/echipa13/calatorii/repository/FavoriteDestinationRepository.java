package echipa13.calatorii.repository;

import echipa13.calatorii.models.FavoriteDestination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteDestinationRepository extends JpaRepository<FavoriteDestination, Long> {

    boolean existsByUser_IdAndDestination_Id(Long userId, Long destinationId);

    Optional<FavoriteDestination> findByUser_IdAndDestination_Id(Long userId, Long destinationId);

    List<FavoriteDestination> findAllByUser_IdOrderByCreatedAtDesc(Long userId);

    void deleteByUser_IdAndDestination_Id(Long userId, Long destinationId);

    @Query("select fd.destination.id from FavoriteDestination fd where fd.user.id = :userId")
    List<Long> findDestinationIdsByUserId(@Param("userId") Long userId);
}

