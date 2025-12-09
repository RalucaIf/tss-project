package echipa13.calatorii.Dto;

import lombok.Data;

@Data
public class registerDto {
    private String username;
    private String email;
    private String password_hash;

    // getters & setters
}