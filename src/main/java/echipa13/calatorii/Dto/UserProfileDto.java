package echipa13.calatorii.Dto;

import lombok.Data;

@Data
public class UserProfileDto {
    private String username;
    private String avatarUrl;
    private String role;
    private Integer points;
    private Integer level;
}