package echipa13.calatorii.service.impl;

import echipa13.calatorii.models.Trip;
import echipa13.calatorii.models.TripWallet;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.models.WalletTransaction;
import echipa13.calatorii.models.WalletCategory;
import echipa13.calatorii.repository.TripRepository;
import echipa13.calatorii.repository.TripWalletRepository;
import echipa13.calatorii.repository.UserRepository;
import echipa13.calatorii.repository.WalletTransactionRepository;
import echipa13.calatorii.service.WalletService;
import echipa13.calatorii.Dto.WalletCategoryTotal;
import echipa13.calatorii.Dto.WalletInsights;
import echipa13.calatorii.Dto.WalletSummary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
public class WalletServiceImpl implements WalletService {

    private static final Set<String> ALLOWED_CURRENCIES = Set.of("RON", "EUR");

    private final TripRepository tripRepository;
    private final TripWalletRepository tripWalletRepository;
    private final UserRepository userRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public WalletServiceImpl(TripRepository tripRepository,
                             TripWalletRepository tripWalletRepository,
                             UserRepository userRepository,
                             WalletTransactionRepository walletTransactionRepository) {
        this.tripRepository = tripRepository;
        this.tripWalletRepository = tripWalletRepository;
        this.userRepository = userRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    // ------------------------------------------------------------------
    // Wallet: create / ownership
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public TripWallet getOrCreateWalletOwnedByUser(Long tripId, String usernameOrEmail) {
        UserEntity user = findUserByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilizator inexistent."));

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Itinerariu inexistent."));

        if (trip.getUser() == null || !Objects.equals(trip.getUser().getId(), user.getId())) {
            throw new IllegalStateException("Nu ai acces la acest itinerariu.");
        }

        return tripWalletRepository.findByTrip_Id(tripId)
                .orElseGet(() -> createWallet(trip));
    }

    private TripWallet createWallet(Trip trip) {
        TripWallet wallet = new TripWallet();
        wallet.setTrip(trip);
        return tripWalletRepository.save(wallet);
    }

    private Optional<UserEntity> findUserByUsernameOrEmail(String login) {
        if (login == null || login.isBlank()) return Optional.empty();

        UserEntity byEmail = userRepository.findByEmail(login);
        if (byEmail != null) return Optional.of(byEmail);

        UserEntity byUsername = userRepository.findByUsername(login);
        if (byUsername != null) return Optional.of(byUsername);

        return Optional.empty();
    }

    // ------------------------------------------------------------------
    // Budget
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public TripWallet updateBudgetOwnedByUser(Long tripId,
                                              String usernameOrEmail,
                                              BigDecimal budgetTotal,
                                              String currency) {
        TripWallet wallet = getOrCreateWalletOwnedByUser(tripId, usernameOrEmail);

        if (budgetTotal == null) {
            throw new IllegalArgumentException("Bugetul este obligatoriu.");
        }
        if (budgetTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Bugetul nu poate fi negativ.");
        }

        String normalizedCurrency = (currency == null) ? "RON" : currency.trim().toUpperCase(Locale.ROOT);
        if (!ALLOWED_CURRENCIES.contains(normalizedCurrency)) {
            normalizedCurrency = "RON";
        }

        wallet.setBudgetTotal(budgetTotal);
        wallet.setCurrency(normalizedCurrency);
        return tripWalletRepository.save(wallet);
    }

    // ------------------------------------------------------------------
    // Transactions: list / add / delete
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<WalletTransaction> listTransactionsOwnedByUser(Long tripId, String usernameOrEmail) {
        TripWallet wallet = getOrCreateWalletOwnedByUser(tripId, usernameOrEmail);
        return walletTransactionRepository.findByWallet_IdOrderBySpentAtDescIdDesc(wallet.getId());
    }

    @Override
    @Transactional
    public void addExpenseOwnedByUser(Long tripId,
                                      String usernameOrEmail,
                                      BigDecimal amount,
                                      WalletCategory category,
                                      String title,
                                      String note,
                                      LocalDate spentAt,
                                      Integer dayIndex) {

        TripWallet wallet = getOrCreateWalletOwnedByUser(tripId, usernameOrEmail);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Suma trebuie să fie > 0.");
        }

        String safeTitle = (title == null) ? "" : title.trim();
        if (safeTitle.isBlank()) {
            throw new IllegalArgumentException("Titlul este obligatoriu.");
        }

        if (dayIndex != null && dayIndex <= 0) {
            throw new IllegalArgumentException("Ziua trebuie să fie >= 1.");
        }

        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setAmount(amount);
        tx.setCategory(category == null ? WalletCategory.OTHER : category);
        tx.setTitle(safeTitle);
        tx.setNote(note == null ? null : note.trim());
        tx.setSpentAt(spentAt == null ? LocalDate.now() : spentAt);
        tx.setDayIndex(dayIndex);

        walletTransactionRepository.save(tx);
    }

    @Override
    @Transactional
    public void deleteTransactionOwnedByUser(Long tripId, String usernameOrEmail, Long txId) {
        TripWallet wallet = getOrCreateWalletOwnedByUser(tripId, usernameOrEmail);

        WalletTransaction tx = walletTransactionRepository.findById(txId)
                .orElseThrow(() -> new IllegalArgumentException("Tranzacția nu există."));

        if (tx.getWallet() == null || tx.getWallet().getId() == null ||
                !Objects.equals(tx.getWallet().getId(), wallet.getId())) {
            throw new IllegalStateException("Nu ai acces la această tranzacție.");
        }

        walletTransactionRepository.delete(tx);
    }

    // ------------------------------------------------------------------
    // KPI Summary (Buget/Cheltuit/Rămas/Progres/Status)
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public WalletSummary computeSummaryOwnedByUser(Long tripId, String usernameOrEmail) {
        TripWallet wallet = getOrCreateWalletOwnedByUser(tripId, usernameOrEmail);

        BigDecimal totalSpent = walletTransactionRepository.sumAmountByWalletId(wallet.getId());
        if (totalSpent == null) totalSpent = BigDecimal.ZERO;

        BigDecimal budget = wallet.getBudgetTotal();
        if (budget == null || budget.compareTo(BigDecimal.ZERO) <= 0) {
            return new WalletSummary(null, totalSpent, null, null, WalletSummary.Status.NESETAT);
        }

        BigDecimal remaining = budget.subtract(totalSpent);

        int percent = totalSpent
                .multiply(BigDecimal.valueOf(100))
                .divide(budget, 0, RoundingMode.HALF_UP)
                .intValue();

        WalletSummary.Status status;
        if (percent >= 100) status = WalletSummary.Status.DEPASIT;
        else if (percent >= 80) status = WalletSummary.Status.ATENTIE;
        else status = WalletSummary.Status.OK;

        return new WalletSummary(budget, totalSpent, remaining, percent, status);
    }

    // ------------------------------------------------------------------
    // Insights (Top categorii + zile active + medie/zi)
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public WalletInsights computeInsightsOwnedByUser(Long tripId, String usernameOrEmail) {
        TripWallet wallet = getOrCreateWalletOwnedByUser(tripId, usernameOrEmail);

        BigDecimal totalSpent = walletTransactionRepository.sumAmountByWalletId(wallet.getId());
        if (totalSpent == null) totalSpent = BigDecimal.ZERO;

        long activeDays = walletTransactionRepository.countDistinctSpentDays(wallet.getId());
        BigDecimal avgPerDay = (activeDays <= 0)
                ? BigDecimal.ZERO
                : totalSpent.divide(BigDecimal.valueOf(activeDays), 2, RoundingMode.HALF_UP);

        List<Object[]> rows = walletTransactionRepository.sumByCategory(wallet.getId());
        List<WalletCategoryTotal> totals = new ArrayList<>();

        for (Object[] row : rows) {
            WalletCategory category = (WalletCategory) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            if (amount == null) amount = BigDecimal.ZERO;

            int percent = 0;
            if (totalSpent.compareTo(BigDecimal.ZERO) > 0) {
                percent = amount
                        .multiply(BigDecimal.valueOf(100))
                        .divide(totalSpent, 0, RoundingMode.HALF_UP)
                        .intValue();
            }

            totals.add(new WalletCategoryTotal(category, amount, percent));
        }

        return new WalletInsights(activeDays, avgPerDay, totals);
    }
}
