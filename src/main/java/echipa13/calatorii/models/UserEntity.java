package echipa13.calatorii.models;
 import jakarta.persistence.*;
 import lombok.AllArgsConstructor;
 import lombok.Getter;
 import lombok.NoArgsConstructor;
 import lombok.Setter;

 import java.time.LocalDateTime;
 import java.util.ArrayList;
 import java.util.List;

@Getter
 @Setter
 @NoArgsConstructor
 @AllArgsConstructor
 @Entity(name="users")
public class UserEntity {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     private String email;

     private String password_hash;

     private String username;

     private boolean enabled;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private List<Role> roles = new ArrayList<>();
 }
