package echipa13.calatorii.models;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_points")
@Data
@Order

public class UserPoints {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private Integer points = 0;

    private Integer level = 1;

    private LocalDateTime createdAt = LocalDateTime.now();;
}