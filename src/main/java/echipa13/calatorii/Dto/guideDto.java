package echipa13.calatorii.Dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.time.LocalDateTime;
@Data


//aici facem dto, practic unele informatii vrem sa fie ascunse cum ar fi parola, etc, deci oferim o oarecare securitate
public class guideDto {
    private Long id;

    private Long userId;

    @NotEmpty(message = "Display name should not be empty")
    private String displayName;

    private String bio;

    private Boolean verified;

    private Double ratingAvg;

    private Integer ratingCnt;

    private LocalDateTime createdAt;

    private String imageUrl;

    public guideDto() {

        this.displayName = displayName;
        this.bio = bio;
        this.verified = verified;
        this.ratingAvg = ratingAvg;
        this.ratingCnt = ratingCnt;
        this.createdAt = createdAt;
        this.imageUrl= imageUrl;
          }


    public String getDisplayName() {return displayName;}
    public String getBio() {return bio;}
    public Boolean getVerified() {return verified;}
    public Double getRatingAvg() {return ratingAvg;}
    public Integer getRatingCnt() {return ratingCnt;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public String getImageUrl() {return imageUrl;}

    public void setDisplayName(String displayName) {this.displayName = displayName;}

}


