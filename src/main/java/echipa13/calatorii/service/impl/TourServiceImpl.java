package echipa13.calatorii.service.impl;

import echipa13.calatorii.Dto.TourDto;
import echipa13.calatorii.models.Destinations;
import echipa13.calatorii.models.Tour;
import echipa13.calatorii.repository.TourRepository;
import echipa13.calatorii.service.DestinationsService;
import echipa13.calatorii.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;
    private final DestinationsService destinationsService;

    @Autowired
    public TourServiceImpl(TourRepository tourRepository, DestinationsService destinationsService) {
        this.tourRepository = tourRepository;
        this.destinationsService = destinationsService;
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
    public Page<TourDto> findAll(Pageable pageable) {
        return tourRepository.findAll(pageable).map(this::mapToTourDto);
    }

    @Override
    public Tour saveTour(Tour tour) {
        return tourRepository.save(tour);
    }

    // ✅ Salvare tur cu destinație
    @Override
    public Tour saveTour(Tour tour, Long destinationId) {
        if (destinationId != null) {
            Destinations destination =
                    destinationsService.findEntityById(destinationId);

            tour.setDestination(destination);
        }
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
        dto.setDestinationId(t.getDestination() != null ? t.getDestination().getId() : null);
        return dto;
    }

    @Override
    public Tour findEntityById(Long id) {
        return tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
    }
}
