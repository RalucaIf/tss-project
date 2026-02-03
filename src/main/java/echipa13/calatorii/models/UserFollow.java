package echipa13.calatorii.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_follows",
        uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "following_id"})
)
public class UserFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_id", nullable = false)
    private UserEntity follower;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "following_id", nullable = false)
    private UserEntity following;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public UserFollow() {}

    public UserFollow(UserEntity follower, UserEntity following) {
        this.follower = follower;
        this.following = following;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public UserEntity getFollower() { return follower; }
    public UserEntity getFollowing() { return following; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setFollower(UserEntity follower) { this.follower = follower; }
    public void setFollowing(UserEntity following) { this.following = following; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

