package echipa13.calatorii.service.impl;

import echipa13.calatorii.Dto.WalletInsights;
import echipa13.calatorii.Dto.WalletSummary;
import echipa13.calatorii.models.*;
import echipa13.calatorii.repository.*;
import echipa13.calatorii.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class WalletServiceImplMutationTest {

    @Mock private TripRepository tripRepo;
    @Mock private TripWalletRepository walletRepo;
    @Mock private UserRepository userRepo;
    @Mock private WalletTransactionRepository txRepo;

    @InjectMocks private WalletServiceImpl service;

    private TripWallet wallet;
    private final Long tripId = 10L;
    private final String login = "raluca@test.com";

    @BeforeEach
    public void setUp() throws Exception {
        UserEntity user = new UserEntity();
        user.setId(1L);

        Trip trip = new Trip();
        trip.setId(tripId);
        trip.setUser(user);

        wallet = new TripWallet();
        wallet.setTrip(trip);

        // Setam ID-ul prin reflection (nu are setter public)
        java.lang.reflect.Field idField = TripWallet.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(wallet, 1L);

        when(userRepo.findByEmail(login)).thenReturn(user);
        when(tripRepo.findById(tripId)).thenReturn(Optional.of(trip));
        when(walletRepo.findByTrip_Id(tripId)).thenReturn(Optional.of(wallet));
        when(txRepo.sumAmountByWalletId(1L)).thenReturn(BigDecimal.ZERO);
    }

    // Mutant 1: schimba > cu >= la validarea sumei
    // Daca PITest schimba "amount > 0" in "amount >= 0",
    // acest test cu suma = 0.01 trebuie sa treaca (nu exceptie)
    // iar testul cu suma = 0 trebuie sa arunce exceptie
    @Test
    public void testSumaZeroAruncaExceptie() {
        assertThrows(IllegalArgumentException.class, () ->
                service.addExpenseOwnedByUser(tripId, login,
                        BigDecimal.ZERO, WalletCategory.FOOD,
                        "Pizza", null, LocalDate.now(), 1));
    }

    @Test
    public void testSumaMinimaPozitivaEsteValida() {
        // 0.01 trebuie sa mearga - distinge > 0 de >= 0
        assertDoesNotThrow(() ->
                service.addExpenseOwnedByUser(tripId, login,
                        new BigDecimal("0.01"), WalletCategory.FOOD,
                        "Pizza", null, LocalDate.now(), 1));
    }

    // Mutant 2: schimba >= cu > la validarea zilei
    // dayIndex = 1 trebuie sa fie valid (>= 1)
    @Test
    public void testZiuaUnaEsteValida() {
        assertDoesNotThrow(() ->
                service.addExpenseOwnedByUser(tripId, login,
                        new BigDecimal("10"), WalletCategory.FOOD,
                        "Pizza", null, LocalDate.now(), 1));
    }

    @Test
    public void testZiuaZeroAruncaExceptie() {
        assertThrows(IllegalArgumentException.class, () ->
                service.addExpenseOwnedByUser(tripId, login,
                        new BigDecimal("10"), WalletCategory.FOOD,
                        "Pizza", null, LocalDate.now(), 0));
    }

    // Mutant 3: neaga conditia pentru titlu blank
    @Test
    public void testTitluUnSingurCaracterEsteValid() {
        assertDoesNotThrow(() ->
                service.addExpenseOwnedByUser(tripId, login,
                        new BigDecimal("10"), WalletCategory.FOOD,
                        "X", null, LocalDate.now(), 1));
    }

    // Omoram mutantii
    @Test
    @DisplayName("Mutant 1: totalSpent null cu activeDays > 0 trebuie sa nu dea NPE")
    public void testInsightsTotalSpentNullEsteTratatCaZero() throws Exception {
        // activeDays > 0 forteaza folosirea lui totalSpent in divide
        // daca totalSpent ramane null -> NPE -> mutantul e detectat
        when(txRepo.sumAmountByWalletId(1L)).thenReturn(null);
        when(txRepo.countDistinctSpentDays(1L)).thenReturn(3L); // > 0 important!
        when(txRepo.sumByCategory(1L)).thenReturn(new ArrayList<>());

        // cu mutantul activ: totalSpent = null, activeDays = 3
        // -> null.divide(...) -> NPE -> testul detecteaza
        assertDoesNotThrow(() -> {
            WalletInsights insights = service.computeInsightsOwnedByUser(tripId, login);
            assertNotNull(insights);
            // avgPerDay trebuie sa fie 0/3 = 0, nu NPE
            assertEquals(0, BigDecimal.ZERO.compareTo(insights.getAvgPerActiveDay()));
        });
    }

    @Test
    @DisplayName("Mutant 2: percentUsed exact 100 trebuie sa returneze Depasit in SmartBudgetAdvice")
    public void testSmartBudgetAdvicePercentUsed100ReturnezaDepasit() throws Exception {
        // Setam buget 100, cheltuit 100 -> percent = 100 -> "Depășit"
        wallet.setBudgetTotal(new BigDecimal("100.00"));

        // Trip cu date valide
        Trip trip = new Trip();
        trip.setId(tripId);

        UserEntity user = new UserEntity();
        user.setId(1L);
        trip.setUser(user);

        // Setam startDate si endDate prin reflection
        java.lang.reflect.Field startField = Trip.class.getDeclaredField("startDate");
        startField.setAccessible(true);
        startField.set(trip, LocalDate.now().minusDays(5));

        java.lang.reflect.Field endField = Trip.class.getDeclaredField("endDate");
        endField.setAccessible(true);
        endField.set(trip, LocalDate.now().plusDays(5));

        when(tripRepo.findById(tripId)).thenReturn(Optional.of(trip));
        when(txRepo.sumAmountByWalletId(1L)).thenReturn(new BigDecimal("100.00"));
        when(txRepo.countDistinctSpentDays(1L)).thenReturn(5L);
        when(txRepo.sumByCategory(1L)).thenReturn(new ArrayList<>());

        WalletService.SmartBudgetAdvice advice = service.computeSmartBudgetAdviceOwnedByUser(tripId, login);

        assertNotNull(advice);
        // "Depășit" distinge >= 100 de > 100
        assertEquals("Depășit", advice.riskLabelRo());
        assertEquals(100, advice.percentUsed());
    }
}