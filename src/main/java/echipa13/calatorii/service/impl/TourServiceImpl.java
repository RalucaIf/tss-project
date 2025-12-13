package echipa13.calatorii.service.impl;

import echipa13.calatorii.Dto.TourDto;
import echipa13.calatorii.models.Tour;
import echipa13.calatorii.repository.TourRepository;
import echipa13.calatorii.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;

    @Autowired
    public TourServiceImpl(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    @Override
    public List<TourDto> findByGuideId(Long guideId) {
        return tourRepository.findByGuideId(guideId).stream()
                .map(this::mapToTourDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TourDto> findByTitle(String title) {
        return tourRepository.findAll().stream()
                .filter(t -> t.getTitle() != null && t.getTitle().equalsIgnoreCase(title))
                .map(this::mapToTourDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TourDto> findAll() {
        return tourRepository.findAll().stream()
                .map(this::mapToTourDto)
                .collect(Collectors.toList());
    }

    @Override
    public Tour saveTour(Tour tour) {
        return tourRepository.save(tour);
    }

    @Override
    public TourDto findTourById(long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found with id: " + id));
        return mapToTourDto(tour);
    }

    @Override
    public void delete(long id) {
        tourRepository.deleteById(id);
    }

    @Override
    public List<TourDto> searchByTitle(String query) {
        List<Tour> tourList = tourRepository.searchByTitle(query);
        return tourList.stream()
                .map(this::mapToTourDto)
                .collect(Collectors.toList());
    }

    private TourDto mapToTourDto(Tour t) {
        TourDto dto = new TourDto();
        dto.setId(t.getId());
        dto.setGuideId(t.getGuideId());
        dto.setTitle(t.getTitle());
        dto.setSummary(t.getSummary());
        dto.setPricePoints(t.getPricePoints());
        dto.setStatus(t.getStatus());
        dto.setCreatedAt(t.getCreatedAt());
        dto.setImage(t.getImage());
        return dto;
    }

    @Override
    public Tour findEntityById(Long id) {
        return tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
    }
}
