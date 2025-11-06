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
    private String description;
    private String image;
    private String title;

    public calatoriiDto( String name, String lastname, String email, String phone, String description, String image, String title) {

        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.description = description;
        this.image = image;
        this.title= title;
          }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getLastname() { return lastname; }
    public String getPhone() { return phone; }
    public String getDescription() { return description; }
    public String getImage() { return image; }
    public String getTitle() { return title; }

}


