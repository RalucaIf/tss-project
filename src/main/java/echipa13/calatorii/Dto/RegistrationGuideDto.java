package echipa13.calatorii.Dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class RegistrationGuideDto {

    @NotEmpty
    private String displayName;

    private String bio;

    private String imageUrl; // optional, dacă îl vrei acum
}
