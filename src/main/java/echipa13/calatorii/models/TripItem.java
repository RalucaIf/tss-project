package echipa13.calatorii.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "trip_items")
@Getter @Setter
public class TripItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // legat de trip
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Column(nullable = false, length = 64)
    private String category; // TRANSPORT/FOOD/LODGING/ATTRACTION/OTHER etc.

    @Column(nullable = false)
    private String title;

    // evităm OID -> text
    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "day_index")
    private Integer dayIndex;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}

