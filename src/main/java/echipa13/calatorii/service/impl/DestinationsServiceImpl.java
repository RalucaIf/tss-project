package echipa13.calatorii.service.impl;

import echipa13.calatorii.Dto.DestinationsDto;
import echipa13.calatorii.Dto.TourDto;
import echipa13.calatorii.models.Destinations;
import echipa13.calatorii.models.Tour;
import echipa13.calatorii.repository.DestinationsRepository;
import echipa13.calatorii.service.DestinationsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class DestinationsServiceImpl implements DestinationsService {

    private final DestinationsRepository destinationsRepository;

    public DestinationsServiceImpl(DestinationsRepository destinationsRepository) {
        this.destinationsRepository = destinationsRepository;
    }

    @Override
    public Page<Destinations> findAll(Pageable pageable) {
        return destinationsRepository.findAll(pageable);
    }

    @Override
    public Destinations findEntityById(Long id) {
        return destinationsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destination not found"));
    }


    @Override
    public DestinationsDto findDtoById(Long id) {
        return toDto(findEntityById(id));
    }

    @Override
    public DestinationsDto findById(Long id) {
        Destinations destination = destinationsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destination not found with id: " + id));
        return toDto(destination);
    }



    @Override
    public List<DestinationsDto> findAllDTOs() {
        return destinationsRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }


    @Override
    public Destinations save(Destinations destination) {
        return destinationsRepository.save(destination);
    }

    @Override
    public void delete(Long id) {
        destinationsRepository.deleteById(id);
    }

    @Override
    public DestinationsDto toDto(Destinations d) {
        DestinationsDto dto = new DestinationsDto();
        dto.setId(d.getId());
        dto.setName(d.getName());
        dto.setContinent(d.getContinent());
        dto.setDescription(d.getDescription());
        dto.setImage(d.getImage());

        // Preț minim al tururilor
        dto.setMinPrice(
                (d.getTours() != null ? d.getTours().stream() : Stream.<Tour>empty())
                        .map(Tour::getPricePoints)
                        .min(Integer::compareTo)
                        .orElse(0)
        );

        // Map tururi complet cu toate câmpurile necesare
        dto.setTours(
                (d.getTours() != null ? d.getTours().stream() : Stream.<Tour>empty())
                        .map(t -> {
                            TourDto tourDto = new TourDto();
                            tourDto.setId(t.getId());
                            tourDto.setTitle(t.getTitle());
                            tourDto.setPricePoints(t.getPricePoints());
                            tourDto.setImage(t.getImage());
                            tourDto.setSummary(t.getSummary());
                            tourDto.setDestinationId(t.getDestination() != null ? t.getDestination().getId() : null);
                            tourDto.setGuideId(t.getGuideId());
                            tourDto.setStatus(t.getStatus());
                            tourDto.setCreatedAt(t.getCreatedAt());
                            return tourDto;
                        })
                        .toList()
        );

        return dto;
    }

}
