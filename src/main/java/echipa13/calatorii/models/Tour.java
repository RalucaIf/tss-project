package echipa13.calatorii.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import echipa13.calatorii.models.ItinerariuZi;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tours")
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guide_id", nullable = false)
    private Long guideId;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String summary;

    @Column(length = 2000)
    private String description;

    @Column(name = "price_points")
    private Integer pricePoints;

    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "max_guests")
    private Integer maxGuests;

    private Integer duration;

    private String image;

    private String subtitle;

    @Column(length = 100)
    private String category;

    @Column(length = 500)
    private String locations; // ex: "Rome • Florence • Amalfi"

    @ElementCollection(targetClass = Highlight.class)
    @Enumerated(EnumType.STRING)
    private Set<Highlight> highlights = new HashSet<>();


    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItinerariuZi> itinerariu = new ArrayList<>();


// getter/setter

    // Corect: numele câmpului trebuie să fie exact "destination"
    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Destinations destination;

    @ManyToOne
    @JoinColumn(name ="trip_id")
    private Trip trip;
}
