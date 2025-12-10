package echipa13.calatorii.Dto;

import jakarta.persistence.Column;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.time.LocalDateTime;
@Data
public class RegistrationDto {
    private Long id;
    @NotEmpty
    private String username;
    @NotEmpty
    private String email;
    @NotEmpty
    private String password_hash;

    private boolean enabled;

    private String avatar;  // !

    private LocalDateTime createdAt;
}
