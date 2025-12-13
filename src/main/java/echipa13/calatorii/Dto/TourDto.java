package echipa13.calatorii.Dto;

import echipa13.calatorii.models.Destinations;
import echipa13.calatorii.models.Tour;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

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

    private String image;

    private Long destinationId;
    public TourDto() {
        this.createdAt = LocalDateTime.now();
    }

    public TourDto(Long id, String title, Integer pricePoints) {
        this.id = id;
        this.title = title;
        this.pricePoints = pricePoints;
        this.createdAt = LocalDateTime.now();
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
        t.setDestination(destination); // legătura către destinație
        return t;
    }
}
