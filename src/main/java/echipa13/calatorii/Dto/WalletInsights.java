package echipa13.calatorii.Dto;

import java.math.BigDecimal;
import java.util.List;

public class WalletInsights {

    private final long activeDays;
    private final BigDecimal avgPerActiveDay; // cheltuit/zi activă
    private final List<WalletCategoryTotal> topCategories;

    public WalletInsights(long activeDays, BigDecimal avgPerActiveDay, List<WalletCategoryTotal> topCategories) {
        this.activeDays = activeDays;
        this.avgPerActiveDay = avgPerActiveDay;
        this.topCategories = topCategories;
    }

    public long getActiveDays() { return activeDays; }
    public BigDecimal getAvgPerActiveDay() { return avgPerActiveDay; }
    public List<WalletCategoryTotal> getTopCategories() { return topCategories; }
}

