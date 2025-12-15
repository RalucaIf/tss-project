package echipa13.calatorii.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItinerariuZiDto {
    private Long id;   // 🔥 CHEIA EDIT-ULUI
    private int zi;
    private String titlu;
    private String locatie;
    private String descriere;
    private List<String> features;
}
