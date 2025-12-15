package echipa13.calatorii.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
// ItinerariuZi.java
@Data
@Entity
@Table(name = "itinerariu_zi")
@NoArgsConstructor
@AllArgsConstructor
public class ItinerariuZi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int zi;
    private String titlu;
    private String locatie;

    @Column(length = 2000)
    private String descriere;

    @ElementCollection
    private List<String> features = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "tour_id")
    private Tour tour;
}

