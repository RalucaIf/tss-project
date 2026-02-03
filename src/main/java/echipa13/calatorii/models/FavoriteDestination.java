package echipa13.calatorii.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "favorite_destinations",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_fav_user_destination",
                columnNames = {"user_id", "destination_id"}
        ),
        indexes = {
                @Index(name = "idx_fav_user", columnList = "user_id"),
                @Index(name = "idx_fav_destination", columnList = "destination_id")
        }
)
public class FavoriteDestination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user; // <-- schimbă dacă entitatea ta de user are alt nume

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destinations destination;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public FavoriteDestination() {}

    public FavoriteDestination(UserEntity user, Destinations destination) {
        this.user = user;
        this.destination = destination;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public UserEntity getUser() { return user; }
    public Destinations getDestination() { return destination; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
