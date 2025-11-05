package echipa13.calatorii.Dto;

import lombok.Builder;
import lombok.Data;

@Data


//aici facem dto, practic unele informatii vrem sa fie ascunse cum ar fi parola, etc, deci oferim o oarecare securitate
public class calatoriiDto {
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String phone;

    public calatoriiDto( String name, String lastname, String email, String phone) {

        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
          }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getLastname() { return lastname; }
    public String getPhone() { return phone; }
}


