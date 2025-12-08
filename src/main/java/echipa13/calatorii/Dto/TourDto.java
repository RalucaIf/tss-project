package echipa13.calatorii.Dto;

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

    public TourDto() {
        this.createdAt = LocalDateTime.now();

    }

    // Getters și setters (Lombok @Data generează automat majoritatea)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getGuideId() { return guideId; }
    public void setGuideId(Long guideId) { this.guideId = guideId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public Integer getPricePoints() { return pricePoints; }
    public void setPricePoints(Integer pricePoints) { this.pricePoints = pricePoints; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}
