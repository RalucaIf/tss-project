package echipa13.calatorii.Dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data


//aici facem dto, practic unele informatii vrem sa fie ascunse cum ar fi parola, etc, deci oferim o oarecare securitate
public class calatoriiDto {
    private Long id;
    @NotEmpty(message = "Name should not be empty")
    private String name;
    @NotEmpty(message = "LastName should not be empty")
    private String lastname;
    @NotEmpty(message = "Email should not be empty")
    private String email;
    @NotEmpty(message = "Phone should not be empty")
    private String phone;

    private String description;
    @NotEmpty(message = "Image should not be empty")
    private String image;
    @NotEmpty(message = "Title should not be empty")
    private String title;

    public calatoriiDto() {

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

    public void setName(String name) {
        this.name = name;
    }

    public void setLastname(String lastname) {}
}


