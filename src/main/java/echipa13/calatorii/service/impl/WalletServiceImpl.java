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

    // Extensia A: praguri dinamice
    private static final int WARNING_THRESHOLD_PERCENT = 75;
    private static final int CRITICAL_THRESHOLD_PERCENT = 90;

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
        Trip trip = getTripOwnedByUser(tripId, usernameOrEmail);

        return tripWalletRepository.findByTrip_Id(tripId)
                .orElseGet(() -> createWallet(trip));
    }

    private Trip getTripOwnedByUser(Long tripId, String usernameOrEmail) {
        UserEntity user = findUserByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilizator inexistent."));

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Itinerariu inexistent."));

        if (trip.getUser() == null || !Objects.equals(trip.getUser().getId(), user.getId())) {
            throw new IllegalStateException("Nu ai acces la acest itinerariu.");
        }
        return trip;
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

        // Praguri dinamice (A)
        // NOTĂ: nu forțăm enum nou; păstrăm Status existent: OK / ATENTIE / DEPASIT / NESETAT
        WalletSummary.Status status;
        if (percent >= 100) status = WalletSummary.Status.DEPASIT;
        else if (percent >= WARNING_THRESHOLD_PERCENT) status = WalletSummary.Status.ATENTIE;
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

    // ------------------------------------------------------------------
    // Extensia A: Buget inteligent (forecast + allowance + message)
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public SmartBudgetAdvice computeSmartBudgetAdviceOwnedByUser(Long tripId, String usernameOrEmail) {
        Trip trip = getTripOwnedByUser(tripId, usernameOrEmail);
        TripWallet wallet = getOrCreateWalletOwnedByUser(tripId, usernameOrEmail);

        WalletSummary summary = computeSummaryOwnedByUser(tripId, usernameOrEmail);
        WalletInsights insights = computeInsightsOwnedByUser(tripId, usernameOrEmail);

        BigDecimal budget = wallet.getBudgetTotal();
        String currency = wallet.getCurrency() == null ? "RON" : wallet.getCurrency();

        // burn-rate = avg/zi activă (deja core în proiect)
        BigDecimal burnRatePerDay = insights != null && insights.getAvgPerActiveDay() != null
                ? insights.getAvgPerActiveDay()
                : BigDecimal.ZERO;

        Integer daysRemaining = computeDaysRemainingSafe(trip);
        BigDecimal dailyAllowance = null;

        BigDecimal remaining = null;
        Integer percentUsed = null;

        if (summary != null) {
            remaining = summary.getRemaining();
            percentUsed = summary.getPercent();
        }

        if (budget != null && budget.compareTo(BigDecimal.ZERO) > 0 && daysRemaining != null && daysRemaining > 0) {
            BigDecimal safeRemaining = remaining == null ? budget : remaining;
            dailyAllowance = safeRemaining.divide(BigDecimal.valueOf(daysRemaining), 2, RoundingMode.HALF_UP);
        }

        Integer daysToExceed = null;
        if (remaining != null && burnRatePerDay != null
                && burnRatePerDay.compareTo(BigDecimal.ZERO) > 0) {

            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                daysToExceed = 0;
            } else {
                daysToExceed = remaining
                        .divide(burnRatePerDay, 0, RoundingMode.CEILING)
                        .intValue();
            }
        }

        RiskUi riskUi = buildRiskUi(budget, percentUsed, summary);

        String recommendation = buildRecommendation(
                budget,
                currency,
                percentUsed,
                remaining,
                burnRatePerDay,
                daysRemaining,
                dailyAllowance,
                daysToExceed,
                riskUi.riskLabelRo
        );

        return new SmartBudgetAdvice(
                WARNING_THRESHOLD_PERCENT,
                CRITICAL_THRESHOLD_PERCENT,
                percentUsed,
                riskUi.riskLabelRo,
                riskUi.uiClass,
                scale2(burnRatePerDay),
                dailyAllowance == null ? null : scale2(dailyAllowance),
                daysRemaining,
                daysToExceed,
                recommendation
        );
    }

    private static final class RiskUi {
        private final String riskLabelRo;
        private final String uiClass;

        private RiskUi(String riskLabelRo, String uiClass) {
            this.riskLabelRo = riskLabelRo;
            this.uiClass = uiClass;
        }
    }

    private RiskUi buildRiskUi(BigDecimal budget, Integer percentUsed, WalletSummary summary) {
        if (budget == null || budget.compareTo(BigDecimal.ZERO) <= 0 || percentUsed == null) {
            return new RiskUi("Buget nesetat", "bg-secondary");
        }

        if (percentUsed >= 100) return new RiskUi("Depășit", "bg-danger");
        if (percentUsed >= CRITICAL_THRESHOLD_PERCENT) return new RiskUi("Critic", "bg-danger");
        if (percentUsed >= WARNING_THRESHOLD_PERCENT) return new RiskUi("Atenție", "bg-warning");
        return new RiskUi("OK", "bg-success");
    }

    private String buildRecommendation(BigDecimal budget,
                                       String currency,
                                       Integer percentUsed,
                                       BigDecimal remaining,
                                       BigDecimal burnRatePerDay,
                                       Integer daysRemaining,
                                       BigDecimal dailyAllowance,
                                       Integer daysToExceed,
                                       String riskLabelRo) {

        if (budget == null || budget.compareTo(BigDecimal.ZERO) <= 0) {
            return "Setează un buget total ca să primești estimări inteligente (ritm/zi, recomandări și avertizări).";
        }

        StringBuilder sb = new StringBuilder();

        if (percentUsed != null) {
            sb.append("Status: ").append(riskLabelRo).append(" (").append(percentUsed).append("% din buget). ");
        }

        if (burnRatePerDay != null && burnRatePerDay.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("Ritmul curent: ~")
                    .append(scale2(burnRatePerDay)).append(" ").append(currency).append("/zi. ");
        } else {
            sb.append("Nu există suficiente date pentru a estima ritmul curent. ");
        }

        if (daysToExceed != null) {
            if (daysToExceed == 0 && remaining != null && remaining.compareTo(BigDecimal.ZERO) <= 0) {
                sb.append("Bugetul este deja depășit. ");
            } else if (daysToExceed > 0) {
                sb.append("La acest ritm, vei depăși bugetul în ~").append(daysToExceed).append(" zile. ");
            }
        }

        if (daysRemaining != null && daysRemaining > 0 && dailyAllowance != null) {
            sb.append("Ca să rămâi în buget, încearcă să cheltuiești cel mult ~")
                    .append(scale2(dailyAllowance)).append(" ").append(currency).append("/zi")
                    .append(" (").append(daysRemaining).append(" zile rămase). ");
        }

        if ("Critic".equals(riskLabelRo)) {
            sb.append("Ești foarte aproape de limită. Prioritizează cheltuielile esențiale.");
        } else if ("Atenție".equals(riskLabelRo)) {
            sb.append("E bine să fii atent la cheltuieli și să eviți achizițiile impulsive.");
        } else if ("OK".equals(riskLabelRo)) {
            sb.append("Te încadrezi bine în buget. Continuă în același ritm.");
        } else if ("Depășit".equals(riskLabelRo)) {
            sb.append("Ai depășit bugetul. Redu cheltuielile sau ajustează bugetul dacă este cazul.");
        }

        return sb.toString().trim();
    }

    private Integer computeDaysRemainingSafe(Trip trip) {
        // Dacă Trip nu are start/end sau metoda nu există în proiect, te vei lovi la compilare.
        // Presupunem că ai LocalDate getStartDate() și getEndDate().
        try {
            LocalDate start = trip.getStartDate();
            LocalDate end = trip.getEndDate();
            if (start == null || end == null || end.isBefore(start)) return null;

            LocalDate today = LocalDate.now();
            if (today.isAfter(end)) return 0;
            if (today.isBefore(start)) {
                return (int) (end.toEpochDay() - start.toEpochDay() + 1);
            }
            return (int) (end.toEpochDay() - today.toEpochDay() + 1);
        } catch (Exception ignored) {
            return null;
        }
    }

    private BigDecimal scale2(BigDecimal v) {
        if (v == null) return null;
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}
