package echipa13.calatorii.service;

import echipa13.calatorii.models.TripWallet;
import echipa13.calatorii.models.WalletTransaction;
import echipa13.calatorii.models.WalletCategory;
import echipa13.calatorii.Dto.WalletInsights;
import echipa13.calatorii.Dto.WalletSummary;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface WalletService {

    TripWallet getOrCreateWalletOwnedByUser(Long tripId, String usernameOrEmail);

    TripWallet updateBudgetOwnedByUser(Long tripId,
                                       String usernameOrEmail,
                                       BigDecimal budgetTotal,
                                       String currency);

    List<WalletTransaction> listTransactionsOwnedByUser(Long tripId, String usernameOrEmail);

    void addExpenseOwnedByUser(Long tripId,
                               String usernameOrEmail,
                               BigDecimal amount,
                               WalletCategory category,
                               String title,
                               String note,
                               LocalDate spentAt,
                               Integer dayIndex);

    void deleteTransactionOwnedByUser(Long tripId, String usernameOrEmail, Long txId);

    WalletSummary computeSummaryOwnedByUser(Long tripId, String usernameOrEmail);

    WalletInsights computeInsightsOwnedByUser(Long tripId, String usernameOrEmail);

    /**
     * Extensia A: “Buget inteligent”.
     * Se poate afișa în wallet.html fără logică SpEL/enum T(...).
     */
    SmartBudgetAdvice computeSmartBudgetAdviceOwnedByUser(Long tripId, String usernameOrEmail);

    /**
     * DTO “light” pentru UI: praguri, burn-rate, daily allowance, estimare depășire, mesaj recomandare.
     * NOTĂ: e record în interfață ca să nu mai creezi fișiere în plus acum.
     */
    record SmartBudgetAdvice(
            int warningThresholdPercent,        // 75
            int criticalThresholdPercent,       // 90
            Integer percentUsed,                // poate fi null dacă buget nesetat
            String riskLabelRo,                 // OK / Atenție / Critic / Depășit / Buget nesetat
            String uiClass,                     // bg-success / bg-warning / bg-danger / bg-secondary
            BigDecimal burnRatePerDay,          // cheltuială medie pe zi activă
            BigDecimal dailyAllowance,          // cât îți permiți pe zi (dacă știm zile rămase)
            Integer daysRemaining,              // zile rămase (dacă avem start/end)
            Integer daysToExceed,               // estimare depășire (dacă burn-rate > 0)
            String recommendation               // text “wow”
    ) {}
}
