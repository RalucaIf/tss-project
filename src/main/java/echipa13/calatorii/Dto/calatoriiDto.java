package echipa13.calatorii.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

//aici facem dto, practic unele informatii vrem sa fie ascunse cum ar fi parola, etc, deci oferim o oarecare securitate
public class calatoriiDto {
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String phone;
}

