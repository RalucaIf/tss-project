package echipa13.calatorii.models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
//face automat setteri si getteri pentru toate valorile de mai jos
@NoArgsConstructor
@Builder

public class calatorii {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)

//    setam id ca cheie primara si autoincremenet
    private Long id;

    private String name;

    private String lastname;

    private String email;

    private String phone;
}
