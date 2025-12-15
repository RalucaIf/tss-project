package echipa13.calatorii.Dto;

import echipa13.calatorii.models.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class TourDto {

    private Long id;

    @NotNull(message = "Guide ID should not be null")
    private Long guideId;

    @NotEmpty(message = "Title should not be empty")
    private String title;

    private String summary;

    private Integer pricePoints;

    private String status;

    private LocalDateTime createdAt;

    private String description;

    private Integer maxGuests;
    private Integer duration;
    private String subtitle;

    private String category;
    private String locations;
    private String image;

    private Set<Highlight> highlights = new HashSet<>();

    // 🔹 Lista de zile pentru itinerariu
    private List<ItinerariuZiDto> itinerariu = new ArrayList<>();

    private Long destinationId;

    private Long tripId;

    public TourDto() {
        this.createdAt = LocalDateTime.now();
    }

    public TourDto(Long id, String title, Integer pricePoints, String summary, String status,
                   String image, String description, Integer duration, Integer maxGuests,
                   String subtitle, String category, String locations, Set<Highlight> highlights,
                   List<ItinerariuZiDto> itinerariu, Long tripId) {

        this.id = id;
        this.title = title;
        this.pricePoints = pricePoints;
        this.createdAt = LocalDateTime.now();
        this.summary = summary;
        this.status = status;
        this.image = image;
        this.description = description;
        this.duration = duration;
        this.maxGuests = maxGuests;
        this.subtitle = subtitle;
        this.category = category;
        this.locations = locations;
        this.highlights = highlights != null ? highlights : new HashSet<>();
        this.itinerariu = itinerariu != null ? itinerariu : new ArrayList<>();
        this.tripId = tripId;
    }

    // 🔹 Transformare DTO -> Entity
    public Tour toEntity(Destinations destination) {
        Tour t = new Tour();
        t.setId(this.id);
        t.setGuideId(this.guideId);
        t.setTitle(this.title);
        t.setSummary(this.summary);
        t.setPricePoints(this.pricePoints);
        t.setStatus(this.status);
        t.setCreatedAt(this.createdAt);
        t.setImage(this.image);
        t.setDescription(this.description);
        t.setDuration(this.duration);
        t.setMaxGuests(this.maxGuests);
        t.setSubtitle(this.subtitle);
        t.setCategory(this.category);
        t.setLocations(this.locations);
        t.setHighlights(this.highlights);
        t.setDestination(destination);

        // 🔹 Mapare itinerariu DTO -> Entity
        if (this.itinerariu != null && !this.itinerariu.isEmpty()) {
            List<ItinerariuZi> itinerariuList = new ArrayList<>();
            for (ItinerariuZiDto dayDto : this.itinerariu) {
                ItinerariuZi day = new ItinerariuZi();
                day.setId(dayDto.getId()); // safe pt create, util pt debug
                day.setZi(dayDto.getZi());
                day.setTitlu(dayDto.getTitlu());
                day.setLocatie(dayDto.getLocatie());
                day.setDescriere(dayDto.getDescriere());

                // Dacă features vin ca String comma-separated, poți converti aici:
                if (dayDto.getFeatures() != null) {
                    day.setFeatures(dayDto.getFeatures());
                } else {
                    day.setFeatures(new ArrayList<>());
                }

                day.setTour(t); // legătura inversă
                itinerariuList.add(day);
            }
            t.setItinerariu(itinerariuList);
        }

        return t;
    }
}
