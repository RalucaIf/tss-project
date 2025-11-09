package echipa13.calatorii.repository;
import echipa13.calatorii.models.calatorii;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//prin codul de mai jos spring boot ne ofera metode diverse in sql fara ca noi sa creem functiile
@Repository
public interface Calatorii_repository extends JpaRepository<calatorii, Long> {
    @Override
    Optional<calatorii> findById(Long aLong);
    @Query("SELECT c FROM calatorii c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<calatorii> searchCalatorii(@Param("query") String query);
}
