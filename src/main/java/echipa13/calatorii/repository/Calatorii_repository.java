package echipa13.calatorii.repository;
import echipa13.calatorii.models.calatorii;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//prin codul de mai jos spring boot ne ofera metode diverse in sql fara ca noi sa creem functiile

public interface Calatorii_repository extends JpaRepository<calatorii, Long> {
    @Override
    Optional<calatorii> findById(Long aLong);
}
