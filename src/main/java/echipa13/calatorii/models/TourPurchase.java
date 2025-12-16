package echipa13.calatorii.models;

import echipa13.calatorii.Dto.TourDto;
import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tour_purchases")
@Data
public class TourPurchase {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @ManyToOne
    @JoinColumn(name = "buyer_user_id", nullable = false)
    private UserEntity buyer;

    private Integer pointsPaid;

    private LocalDateTime purchasedAt = LocalDateTime.now();


}