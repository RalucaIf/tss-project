package echipa13.calatorii.repository;

import echipa13.calatorii.Dto.TourDto;
import echipa13.calatorii.models.Continent;
import echipa13.calatorii.models.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {

    // Găsește tur după id
    @Override
    Optional<Tour> findById(Long id);

    // Căutare tururi după titlu, ignorând majuscule/minuscule
    @Query("SELECT t FROM Tour t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Tour> searchByTitle(@Param("query") String query);

    // Găsește tururi după ghid
    List<Tour> findByGuideId(Long guideId);

    // Găsește tururi după status
    List<Tour> findByStatus(String status);

    // Găsește tururi create după o anumită dată
    List<Tour> findByCreatedAtAfter(java.time.LocalDateTime date);
    List<Tour> findByContinent(Continent continent);
    Page<Tour> findAll(Pageable pageable);


}
