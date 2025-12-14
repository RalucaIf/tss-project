package echipa13.calatorii.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    private String image;

    // Corect: numele câmpului trebuie să fie exact "destination"
    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Destinations destination;
}
