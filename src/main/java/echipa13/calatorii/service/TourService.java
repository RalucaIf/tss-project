package echipa13.calatorii.service;

import echipa13.calatorii.Dto.TourDto;
import echipa13.calatorii.models.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TourService {

 List<TourDto> findByGuideId(Long guideId);

 Page<TourDto> findAll(Pageable pageable);

 List<TourDto> findByTitle(String title);

 List<TourDto> findAll();

 Tour saveTour(Tour tour);

 // ✅ Salvare tur cu legătură la destinație
 Tour saveTour(Tour tour, Long destinationId);

 TourDto findTourById(long id);

 void delete(long id);

 List<TourDto> searchByTitle(String query);

}
