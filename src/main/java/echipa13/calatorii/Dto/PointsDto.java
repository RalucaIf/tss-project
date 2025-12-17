package echipa13.calatorii.Dto;

import echipa13.calatorii.models.UserEntity;
import lombok.Data;

@Data
public class PointsDto {
    private UserEntity user;  // cine primește punctele
    private Integer points;   // punctele actuale
    private Integer level;    // nivelul actual
}
