package echipa13.calatorii.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "trip_wallet",
        uniqueConstraints = @UniqueConstraint(name = "uk_trip_wallet_trip", columnNames = "trip_id")
)
public class TripWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1:1 cu Trip (un trip are un singur portofel)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "budget_total", precision = 12, scale = 2)
    private BigDecimal budgetTotal;

    // la început poți folosi doar "RON"
    @Column(name = "currency", length = 3, nullable = false)
    private String currency = "RON";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.currency == null || this.currency.isBlank()) {
            this.currency = "RON";
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

    public BigDecimal getBudgetTotal() { return budgetTotal; }
    public void setBudgetTotal(BigDecimal budgetTotal) { this.budgetTotal = budgetTotal; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

