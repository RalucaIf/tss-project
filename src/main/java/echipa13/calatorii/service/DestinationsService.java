package echipa13.calatorii.service;

import echipa13.calatorii.Dto.DestinationsDto;
import echipa13.calatorii.Dto.TourDto;
import echipa13.calatorii.models.Destinations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DestinationsService {

    // 🔹 Paginare
    Page<Destinations> findAll(Pageable pageable);

    // 🔹 ENTITATE (pentru legături, ex: Tour → Destination)
    Destinations findEntityById(Long id);

    // 🔹 DTO (pentru pagini)
    DestinationsDto findDtoById(Long id);

    // 🔹 Save / delete
    Destinations save(Destinations destination);
    void delete(Long id);

    // 🔹 Conversii
    DestinationsDto toDto(Destinations destination);
    List<DestinationsDto> findAllDTOs();

    DestinationsDto findById(Long id);

}
