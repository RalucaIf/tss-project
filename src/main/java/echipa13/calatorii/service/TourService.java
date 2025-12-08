package echipa13.calatorii.service;

import echipa13.calatorii.models.Tour;
import echipa13.calatorii.Dto.TourDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TourService {

 // Găsește tururi după guideId
 List<TourDto> findByGuideId(Long guideId);

 // Găsește tururi după titlu
 List<TourDto> findByTitle(String title);

 // Returnează toate tururile
 List<TourDto> findAll();

 // Salvează un tur
 Tour saveTour(Tour tour);

 // Găsește tur după id
 TourDto findTourById(long id);

 // Șterge tur după id
 void delete(long id);

 // Căutare tururi după titlu
 List<TourDto> searchByTitle(String query);
}
