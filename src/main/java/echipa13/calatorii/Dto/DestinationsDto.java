package echipa13.calatorii.Dto;

import echipa13.calatorii.models.Continent;
import echipa13.calatorii.models.Destinations;
import echipa13.calatorii.models.Tour;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class DestinationsDto {

    private Long id;
    private String name;
    private Continent continent;
    private String description;
    private String image;
    private Integer toursCount;
    private Integer minPrice;
    private List<TourDto> tours;
    private LocalDateTime createdAt;

    public DestinationsDto(String name, Continent continent, String description, String image, Integer minPrice, Integer toursCount, List<TourDto> tours, LocalDateTime createdAt) {
        this.name = name;
        this.continent = continent;
        this.description = description;
        this.image = image;
        this.minPrice = minPrice;
        this.toursCount = toursCount;
        this.tours = tours;
        this.createdAt = createdAt;
    }

    public DestinationsDto() {
        this.name = name;
        this.continent = continent;
        this.description = description;
        this.image = image;
        this.minPrice = minPrice;
        this.toursCount = toursCount;
        this.tours = tours;
        this.createdAt = createdAt;
    }

    // 🔹 Transformare DTO -> Entity
    public Destinations toEntity() {
        Destinations d = new Destinations();
        d.setId(this.id);
        d.setName(this.name);
        d.setContinent(this.continent);
        d.setDescription(this.description);
        d.setImage(this.image);

        if (this.tours != null) {
            List<Tour> tourEntities = this.tours.stream()
                    .map(t -> t.toEntity(d)) // trece destinația aici
                    .collect(Collectors.toList());
            d.setTours(tourEntities);
        }

        return d;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
