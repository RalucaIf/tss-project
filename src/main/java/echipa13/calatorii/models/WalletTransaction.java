package echipa13.calatorii.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "wallet_transaction",
        indexes = {
                @Index(name = "idx_wallet_tx_wallet", columnList = "wallet_id"),
                @Index(name = "idx_wallet_tx_spent_at", columnList = "spent_at")
        }
)
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // multe tranzacții pentru un portofel
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    private TripWallet wallet;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 32)
    private WalletCategory category = WalletCategory.OTHER;

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @Column(name = "note", length = 1000)
    private String note;

    @Column(name = "spent_at", nullable = false)
    private LocalDate spentAt;

    // legătură cu ziua de jurnal (opțional, 1..N)
    @Column(name = "day_index")
    private Integer dayIndex;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.spentAt == null) {
            this.spentAt = LocalDate.now();
        }
        if (this.category == null) {
            this.category = WalletCategory.OTHER;
        }
    }

    public Long getId() { return id; }

    public TripWallet getWallet() { return wallet; }
    public void setWallet(TripWallet wallet) { this.wallet = wallet; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public WalletCategory getCategory() { return category; }
    public void setCategory(WalletCategory category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDate getSpentAt() { return spentAt; }
    public void setSpentAt(LocalDate spentAt) { this.spentAt = spentAt; }

    public Integer getDayIndex() { return dayIndex; }
    public void setDayIndex(Integer dayIndex) { this.dayIndex = dayIndex; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
