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
}