package echipa13.calatorii.service;

import echipa13.calatorii.models.Continent;
import echipa13.calatorii.models.Tour;
import echipa13.calatorii.Dto.TourDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;

@Service
public interface TourService {

 // Găsește tururi după guideId
 List<TourDto> findByGuideId(Long guideId);
 Page<TourDto> findAll(Pageable pageable);
 // Găsește tururi după titlu
 List<TourDto> findByTitle(String title);

 // Returnează toate tururile
 List<TourDto> findAll();
 // Găsește tururi după continent
 List<TourDto> findByContinent(Continent continent);

 // Salvează un tur
 Tour saveTour(Tour tour);

 // Găsește tur după id
 TourDto findTourById(long id);

 // Șterge tur după id
 void delete(long id);

 // Căutare tururi după titlu
 List<TourDto> searchByTitle(String query);

 Tour findEntityById(Long id);


}
