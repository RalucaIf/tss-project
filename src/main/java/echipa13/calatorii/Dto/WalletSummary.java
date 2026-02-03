package echipa13.calatorii.Dto;

import java.math.BigDecimal;

public class WalletSummary {

    public enum Status {
        NESETAT,
        OK,
        ATENTIE,
        DEPASIT
    }

    private final BigDecimal budgetTotal;
    private final BigDecimal totalSpent;
    private final BigDecimal remaining;
    private final Integer percent; // 0..100+
    private final Status status;

    public WalletSummary(BigDecimal budgetTotal,
                         BigDecimal totalSpent,
                         BigDecimal remaining,
                         Integer percent,
                         Status status) {
        this.budgetTotal = budgetTotal;
        this.totalSpent = totalSpent;
        this.remaining = remaining;
        this.percent = percent;
        this.status = status;
    }

    public BigDecimal getBudgetTotal() {
        return budgetTotal;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public BigDecimal getRemaining() {
        return remaining;
    }

    public Integer getPercent() {
        return percent;
    }

    public Status getStatus() {
        return status;
    }
}
