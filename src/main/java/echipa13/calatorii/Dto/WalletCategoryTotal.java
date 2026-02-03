package echipa13.calatorii.Dto;

import echipa13.calatorii.models.WalletCategory;

import java.math.BigDecimal;

public class WalletCategoryTotal {

    private final WalletCategory category;
    private final BigDecimal amount;
    private final Integer percentOfSpent; // 0..100

    public WalletCategoryTotal(WalletCategory category, BigDecimal amount, Integer percentOfSpent) {
        this.category = category;
        this.amount = amount;
        this.percentOfSpent = percentOfSpent;
    }

    public WalletCategory getCategory() { return category; }
    public BigDecimal getAmount() { return amount; }
    public Integer getPercentOfSpent() { return percentOfSpent; }
}