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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class WalletServiceImplBoundaryTest {

    // Boundary Value Analysis
    /*
     * Frontiere pe clasele din equivalence partitioning + praguri din computeSummaryOwnedByUser
     *
     *  amount:   0 (invalid, exact la <= 0)
     *  titlu:    "" (invalid, blank direct)
     *  zi:       0 (invalid, exact la <= 0)
     *  procent:  74 (sub pragul ATENTIE=75)
     *            99 (sub pragul DEPASIT=100)
     */

    @Mock private TripRepository tripRepo;
    @Mock private TripWalletRepository walletRepo;
    @Mock private UserRepository userRepo;
    @Mock private WalletTransactionRepository txRepo;

    @InjectMocks private WalletServiceImpl service;

    private TripWallet wallet;
    private final Long tripId = 10L;
    private Trip trip;
    private final String login = "raluca@test.com";

    @BeforeEach
    public void setUp() {
        UserEntity user = new UserEntity();
        user.setId(1L);

        trip = new Trip();
        trip.setId(tripId);
        trip.setUser(user);

        wallet = new TripWallet();
        wallet.setTrip(trip);

        when(userRepo.findByEmail(login)).thenReturn(user);
        when(tripRepo.findById(tripId)).thenReturn(Optional.of(trip));
        when(walletRepo.findByTrip_Id(tripId)).thenReturn(Optional.of(wallet));
    }

    //  addExpenseOwnedByUser - frontiere input

    @Test
    public void testAdaugaCheltuiala_SumaMinimaValida() {
        // 0.01 - cea mai mica valoare acceptata
        service.addExpenseOwnedByUser(tripId, login,
                new BigDecimal("0.01"), WalletCategory.FOOD,
                "x", null, LocalDate.now(), 1);

        ArgumentCaptor<WalletTransaction> cap = ArgumentCaptor.forClass(WalletTransaction.class);
        verify(txRepo).save(cap.capture());
        assertEquals(0, new BigDecimal("0.01").compareTo(cap.getValue().getAmount()));
    }

    @Test
    public void testAdaugaCheltuiala_LimitaSumaZero() {
        // 0 - exact la pragul <= 0, invalid
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                service.addExpenseOwnedByUser(tripId, login,
                        BigDecimal.ZERO, WalletCategory.FOOD,
                        "x", null, LocalDate.now(), 1));

        assertEquals("Suma trebuie sa fie > 0", e.getMessage());
        verify(txRepo, never()).save(any());
    }

    @Test
    public void testAdaugaCheltuiala_TitluMinimNonBlank() {
        // "a" - cel mai scurt titlu valid
        service.addExpenseOwnedByUser(tripId, login,
                new BigDecimal("10"), WalletCategory.FOOD,
                "a", null, LocalDate.now(), 1);

        ArgumentCaptor<WalletTransaction> cap = ArgumentCaptor.forClass(WalletTransaction.class);
        verify(txRepo).save(cap.capture());
        assertEquals("a", cap.getValue().getTitle());
    }

    @Test
    public void testAdaugaCheltuiala_LimitaTitluFaraText() {
        // "" - string gol, blank direct fara trim
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                service.addExpenseOwnedByUser(tripId, login,
                        new BigDecimal("10"), WalletCategory.FOOD,
                        "", null, LocalDate.now(), 1));

        assertEquals("Titlul este obligatoriu", e.getMessage());
        verify(txRepo, never()).save(any());
    }

    @Test
    public void testAdaugaCheltuiala_ZiMinima() {
        // 1 - cea mai mica valoare valida pentru dayIndex
        service.addExpenseOwnedByUser(tripId, login,
                new BigDecimal("10"), WalletCategory.FOOD,
                "x", null, LocalDate.now(), 1);

        ArgumentCaptor<WalletTransaction> cap = ArgumentCaptor.forClass(WalletTransaction.class);
        verify(txRepo).save(cap.capture());
        assertEquals(1, cap.getValue().getDayIndex());
    }

    @Test
    public void testAdaugaCheltuiala_LimitaZiZero() {
        // 0 - exact la pragul <= 0, invalid
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                service.addExpenseOwnedByUser(tripId, login,
                        new BigDecimal("10"), WalletCategory.FOOD,
                        "x", null, LocalDate.now(), 0));

        assertEquals("Ziua trebuie sa fie >= 1", e.getMessage());
        verify(txRepo, never()).save(any());
    }

    // computeSummaryOwnedByUser - praguri

    @Test
    public void testSumar_SubPragAtentie_74LaSuta() {
        // 74% - chiar sub pragul ATENTIE
        wallet.setBudgetTotal(new BigDecimal("100"));
        when(txRepo.sumAmountByWalletId(any())).thenReturn(new BigDecimal("74"));

        WalletSummary s = service.computeSummaryOwnedByUser(tripId, login);

        assertEquals(74, s.getPercent());
        assertEquals(WalletSummary.Status.OK, s.getStatus());
    }

    @Test
    public void testSumar_ExactPragAtentie_75LaSuta() {
        // 75% - exact la pragul ATENTIE
        wallet.setBudgetTotal(new BigDecimal("100"));
        when(txRepo.sumAmountByWalletId(any())).thenReturn(new BigDecimal("75"));

        WalletSummary s = service.computeSummaryOwnedByUser(tripId, login);

        assertEquals(75, s.getPercent());
        assertEquals(WalletSummary.Status.ATENTIE, s.getStatus());
    }

    @Test
    public void testSumar_SubPragDepasit_99LaSuta() {
        // 99% - chiar sub pragul DEPASIT
        wallet.setBudgetTotal(new BigDecimal("100"));
        when(txRepo.sumAmountByWalletId(any())).thenReturn(new BigDecimal("99"));

        WalletSummary s = service.computeSummaryOwnedByUser(tripId, login);

        assertEquals(99, s.getPercent());
        assertEquals(WalletSummary.Status.ATENTIE, s.getStatus());
    }

    @Test
    public void testSumar_ExactPragDepasit_100LaSuta() {
        // 100% - exact la pragul DEPASIT
        wallet.setBudgetTotal(new BigDecimal("100"));
        when(txRepo.sumAmountByWalletId(any())).thenReturn(new BigDecimal("100"));

        WalletSummary s = service.computeSummaryOwnedByUser(tripId, login);

        assertEquals(100, s.getPercent());
        assertEquals(WalletSummary.Status.DEPASIT, s.getStatus());
    }
}
