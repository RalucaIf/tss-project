package echipa13.calatorii.service.impl;

import echipa13.calatorii.Dto.WalletSummary;
import echipa13.calatorii.models.Trip;
import echipa13.calatorii.models.TripWallet;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.models.WalletCategory;
import echipa13.calatorii.models.WalletTransaction;
import echipa13.calatorii.repository.TripRepository;
import echipa13.calatorii.repository.TripWalletRepository;
import echipa13.calatorii.repository.UserRepository;
import echipa13.calatorii.repository.WalletTransactionRepository;
import echipa13.calatorii.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class WalletServiceImplConditionTest {

    // Condition coverage
    /*
     * pt fiecare conditie compusa (cu && sau ||), fiecare operand individual
     * trebuie evaluat si pe TRUE si pe FALSE cel putin o data
     *
     * Conditii tintite:
     *  C1: addExpenseOwnedByUser - "amount == null || amount <= 0"              (|| 2 operanzi)
     *  C2: addExpenseOwnedByUser - "dayIndex != null && dayIndex <= 0"          (&& 2 operanzi)
     *  C3: deleteTransactionOwnedByUser - "wallet==null || id==null || !equals" (|| 3 operanzi)
     *  C4: buildRiskUi - "budget==null || budget<=0 || percentUsed==null"       (|| 3 operanzi)
     *  C5: getTripOwnedByUser - "user==null || !equals(ids)"                    (|| 2 operanzi)
     *  C6: computeSummary - "if (totalSpent == null)"                           (conditie simpla)
     */

    @Mock private TripRepository tripRepo;
    @Mock private TripWalletRepository walletRepo;
    @Mock private UserRepository userRepo;
    @Mock private WalletTransactionRepository txRepo;

    @InjectMocks private WalletServiceImpl service;

    private TripWallet wallet;
    private Trip trip;
    private UserEntity user;
    private final Long tripId = 10L;
    private final String login = "elena@test.com";

    @BeforeEach
    public void setUp() throws Exception {
        user = new UserEntity();
        user.setId(1L);

        trip = new Trip();
        trip.setId(tripId);
        trip.setUser(user);

        wallet = new TripWallet();
        wallet.setTrip(trip);
        wallet.setCurrency("RON");
        setIdPrinReflection(wallet, 500L);

        when(userRepo.findByEmail(login)).thenReturn(user);
        when(tripRepo.findById(tripId)).thenReturn(Optional.of(trip));
    }

    @Test
    public void testConditie_ValidareSumeiLaAdaugare() {
        // C1: amount == null || amount <= 0 (|| cu 2 operanzi)
        when(walletRepo.findByTrip_Id(tripId)).thenReturn(Optional.of(wallet));

        // C1 = TRUE (short-circuit) -> throw
        assertThrows(IllegalArgumentException.class, () ->
                service.addExpenseOwnedByUser(tripId, login,
                        null, WalletCategory.FOOD,
                        "Pizza", null, LocalDate.now(), 1));

        // C1 = FALSE, C2 = TRUE -> throw
        assertThrows(IllegalArgumentException.class, () ->
                service.addExpenseOwnedByUser(tripId, login,
                        new BigDecimal("-1"), WalletCategory.FOOD,
                        "Pizza", null, LocalDate.now(), 1));

        // C1 = FALSE, C2 = FALSE -> se salveaza
        service.addExpenseOwnedByUser(tripId, login,
                new BigDecimal("10"), WalletCategory.FOOD,
                "Pizza", null, LocalDate.now(), 1);
        verify(txRepo).save(any(WalletTransaction.class));
    }

    @Test
    public void testConditie_ValidareZileiLaAdaugare() {
        // C2: dayIndex != null && dayIndex <= 0 (&& cu 2 operanzi)
        when(walletRepo.findByTrip_Id(tripId)).thenReturn(Optional.of(wallet));

        // C1 = FALSE (short-circuit) -> acceptat
        service.addExpenseOwnedByUser(tripId, login,
                new BigDecimal("20"), WalletCategory.FOOD,
                "Suvenir", null, LocalDate.now(), null);

        // C1 = TRUE, C2 = TRUE -> throw
        assertThrows(IllegalArgumentException.class, () ->
                service.addExpenseOwnedByUser(tripId, login,
                        new BigDecimal("20"), WalletCategory.FOOD,
                        "Suvenir", null, LocalDate.now(), 0));

        // C1 = TRUE, C2 = FALSE -> acceptat
        service.addExpenseOwnedByUser(tripId, login,
                new BigDecimal("20"), WalletCategory.FOOD,
                "Suvenir", null, LocalDate.now(), 1);

        verify(txRepo, times(2)).save(any(WalletTransaction.class));
    }

    @Test
    public void testConditie_AccesTranzactieLaStergere() throws Exception {
        // C3: wallet == null || id == null || !equals(ids) (|| cu 3 operanzi)
        when(walletRepo.findByTrip_Id(tripId)).thenReturn(Optional.of(wallet));

        // C1 = TRUE (short-circuit) -> throw
        WalletTransaction txFaraWallet = new WalletTransaction();
        txFaraWallet.setWallet(null);
        when(txRepo.findById(1L)).thenReturn(Optional.of(txFaraWallet));

        assertThrows(IllegalStateException.class, () ->
                service.deleteTransactionOwnedByUser(tripId, login, 1L));

        // C1 = FALSE, C2 = TRUE (wallet fara id) -> throw
        TripWallet walletFaraId = new TripWallet();
        walletFaraId.setTrip(trip);

        WalletTransaction txWalletFaraId = new WalletTransaction();
        txWalletFaraId.setWallet(walletFaraId);
        when(txRepo.findById(2L)).thenReturn(Optional.of(txWalletFaraId));

        assertThrows(IllegalStateException.class, () ->
                service.deleteTransactionOwnedByUser(tripId, login, 2L));

        // C1 = FALSE, C2 = FALSE, C3 = TRUE (id-uri diferite) -> throw
        TripWallet altWallet = new TripWallet();
        altWallet.setTrip(trip);
        setIdPrinReflection(altWallet, 999L);

        WalletTransaction txAltuia = new WalletTransaction();
        txAltuia.setWallet(altWallet);
        when(txRepo.findById(3L)).thenReturn(Optional.of(txAltuia));

        assertThrows(IllegalStateException.class, () ->
                service.deleteTransactionOwnedByUser(tripId, login, 3L));

        // C1 = FALSE, C2 = FALSE, C3 = FALSE -> delete
        WalletTransaction txValid = new WalletTransaction();
        txValid.setWallet(wallet);
        when(txRepo.findById(4L)).thenReturn(Optional.of(txValid));

        service.deleteTransactionOwnedByUser(tripId, login, 4L);
        verify(txRepo).delete(txValid);
    }

    @Test
    public void testConditie_ValidareBugetPentruRisc() {
        // C4: budget == null || budget <= 0 || percentUsed == null (|| cu 3 operanzi)
        when(walletRepo.findByTrip_Id(tripId)).thenReturn(Optional.of(wallet));
        when(txRepo.sumAmountByWalletId(any())).thenReturn(BigDecimal.ZERO);
        when(txRepo.countDistinctSpentDays(any())).thenReturn(0L);
        when(txRepo.sumByCategory(any())).thenReturn(new ArrayList<>());
        trip.setStartDate(null);
        trip.setEndDate(null);

        // C1 = TRUE (short-circuit) -> "Buget nesetat"
        wallet.setBudgetTotal(null);
        WalletService.SmartBudgetAdvice r1 = service
                .computeSmartBudgetAdviceOwnedByUser(tripId, login);
        assertEquals("Buget nesetat", r1.riskLabelRo());

        // C1 = FALSE, C2 = TRUE -> "Buget nesetat"
        wallet.setBudgetTotal(BigDecimal.ZERO);
        WalletService.SmartBudgetAdvice r2 = service
                .computeSmartBudgetAdviceOwnedByUser(tripId, login);
        assertEquals("Buget nesetat", r2.riskLabelRo());

        // C1 = FALSE, C2 = FALSE, C3 = FALSE -> risc calculat
        wallet.setBudgetTotal(new BigDecimal("100"));
        when(txRepo.sumAmountByWalletId(any())).thenReturn(new BigDecimal("30"));
        WalletService.SmartBudgetAdvice r3 = service
                .computeSmartBudgetAdviceOwnedByUser(tripId, login);
        assertNotEquals("Buget nesetat", r3.riskLabelRo());
        assertEquals("OK", r3.riskLabelRo());
    }

    @Test
    public void testConditie_AccesItinerariuFaraProprietar() {
        // C5: trip.user == null || !equals(ids) - operand C1 TRUE - short-circuit
        when(walletRepo.findByTrip_Id(tripId)).thenReturn(Optional.of(wallet));

        trip.setUser(null);

        IllegalStateException e = assertThrows(IllegalStateException.class, () ->
                service.getOrCreateWalletOwnedByUser(tripId, login));

        assertNotNull(e.getMessage());
    }

    @Test
    public void testConditie_ItinerariuApartineAltuiUtilizator() {
        // C5: trip.user == null || !equals(ids) - operand C2 TRUE - id-uri diferite
        when(walletRepo.findByTrip_Id(tripId)).thenReturn(Optional.of(wallet));

        UserEntity altUser = new UserEntity();
        altUser.setId(999L);
        trip.setUser(altUser);

        IllegalStateException e = assertThrows(IllegalStateException.class, () ->
                service.getOrCreateWalletOwnedByUser(tripId, login));

        assertNotNull(e.getMessage());
    }

    @Test
    public void testConditie_TotalCheltuielilorNull() {
        // C6: if (totalSpent == null) - fallback la ZERO cand repo returneaza null
        when(walletRepo.findByTrip_Id(tripId)).thenReturn(Optional.of(wallet));
        wallet.setBudgetTotal(new BigDecimal("100"));
        when(txRepo.sumAmountByWalletId(any())).thenReturn(null);

        WalletSummary s = service.computeSummaryOwnedByUser(tripId, login);

        assertEquals(0, BigDecimal.ZERO.compareTo(s.getTotalSpent()));
        assertEquals(0, s.getPercent());
    }

    private void setIdPrinReflection(TripWallet w, Long id) throws Exception {
        Field f = TripWallet.class.getDeclaredField("id");
        f.setAccessible(true);
        f.set(w, id);
    }
}