package echipa13.calatorii.service.impl;

import echipa13.calatorii.models.Trip;
import echipa13.calatorii.models.TripWallet;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.models.WalletCategory;
import echipa13.calatorii.models.WalletTransaction;
import echipa13.calatorii.repository.TripRepository;
import echipa13.calatorii.repository.TripWalletRepository;
import echipa13.calatorii.repository.UserRepository;
import echipa13.calatorii.repository.WalletTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceImplEquivalenceTest {

    // a) Partitionare de echivalenta
    /*
     * Metoda testata: addExpenseOwnedByUser
     * Partitii:
     *  P1: suma > 0, titlu non-blank, zi valida, categorie si data non-null -> se salveaza
     *  P2: categorie == null                -> se salveaza cu OTHER
     *  P3: data (spentAt) == null           -> se salveaza cu data curenta
     *  P4: suma == null sau <= 0            -> se arunca exceptie
     *  P5: titlu == null sau doar spatii    -> se arunca exceptie
     *  P6: zi <= 0                          -> se arunca exceptie
     */

    @Mock private TripRepository tripRepo;
    @Mock private TripWalletRepository walletRepo;
    @Mock private UserRepository userRepo;
    @Mock private WalletTransactionRepository txRepo;

    @InjectMocks private WalletServiceImpl service;

    private TripWallet wallet;
    private final Long tripId = 10L;
    private final String login = "ana@test.com";

    @BeforeEach
    public void setUp() {
        UserEntity user = new UserEntity();
        user.setId(1L);

        Trip trip = new Trip();
        trip.setId(tripId);
        trip.setUser(user);

        wallet = new TripWallet();
        wallet.setTrip(trip);

        when(userRepo.findByEmail(login)).thenReturn(user);
        when(tripRepo.findById(tripId)).thenReturn(Optional.of(trip));
        when(walletRepo.findByTrip_Id(tripId)).thenReturn(Optional.of(wallet));
    }

    @Test
    public void testAdaugaCheltuiala_InputValid() {
        // Partitie 1
        LocalDate data = LocalDate.of(2026, 4, 18);

        service.addExpenseOwnedByUser(tripId, login,
                new BigDecimal("50.00"), WalletCategory.FOOD,
                "Pizza", "  cina rapida ", data, 1);

        ArgumentCaptor<WalletTransaction> cap = ArgumentCaptor.forClass(WalletTransaction.class);
        verify(txRepo).save(cap.capture());

        WalletTransaction tx = cap.getValue();
        assertSame(wallet, tx.getWallet());
        assertEquals(0, new BigDecimal("50.00").compareTo(tx.getAmount()));
        assertEquals(WalletCategory.FOOD, tx.getCategory());
        assertEquals("Pizza", tx.getTitle());
        assertEquals("cina rapida", tx.getNote());
        assertEquals(data, tx.getSpentAt());
        assertEquals(1, tx.getDayIndex());
    }

    @Test
    public void testAdaugaCheltuiala_CategorieLipsa() {
        // Partitie 2: categorie null -> fallback OTHER
        service.addExpenseOwnedByUser(tripId, login,
                new BigDecimal("25.00"), null, "Taxi", null,
                LocalDate.of(2026, 4, 18), 2);

        ArgumentCaptor<WalletTransaction> cap = ArgumentCaptor.forClass(WalletTransaction.class);
        verify(txRepo).save(cap.capture());

        assertEquals(WalletCategory.OTHER, cap.getValue().getCategory());
    }

    @Test
    public void testAdaugaCheltuiala_FaraData() {
        // Partitie 3: spentAt null -> LocalDate.now()
        LocalDate inainte = LocalDate.now();

        service.addExpenseOwnedByUser(tripId, login,
                new BigDecimal("10.50"), WalletCategory.TRANSPORT,
                "Metrou", null, null, 3);

        LocalDate dupa = LocalDate.now();

        ArgumentCaptor<WalletTransaction> cap = ArgumentCaptor.forClass(WalletTransaction.class);
        verify(txRepo).save(cap.capture());

        LocalDate data = cap.getValue().getSpentAt();
        assertNotNull(data);
        assertFalse(data.isBefore(inainte));
        assertFalse(data.isAfter(dupa));
    }

    @Test
    public void testAdaugaCheltuiala_SumaNegativa() {
        // Partitie 4: reprezentant arbitrar -1
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                service.addExpenseOwnedByUser(tripId, login,
                        new BigDecimal("-1"), WalletCategory.FOOD,
                        "Pizza", null, LocalDate.of(2026, 4, 18), 1));

        assertEquals("Suma trebuie sa fie > 0", e.getMessage());
        verify(txRepo, never()).save(any());
    }

    @Test
    public void testAdaugaCheltuiala_TitluGol() {
        // Partitie 5: doar spatii -> blank dupa trim
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                service.addExpenseOwnedByUser(tripId, login,
                        new BigDecimal("20.00"), WalletCategory.FOOD,
                        "   ", null, LocalDate.of(2026, 4, 18), 1));

        assertEquals("Titlul este obligatoriu", e.getMessage());
        verify(txRepo, never()).save(any());
    }

    @Test
    public void testAdaugaCheltuiala_ZiZero() {
        // Partitie 6: dayIndex = 0
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                service.addExpenseOwnedByUser(tripId, login,
                        new BigDecimal("20.00"), WalletCategory.FOOD,
                        "Pizza", null, LocalDate.of(2026, 4, 18), 0));

        assertEquals("Ziua trebuie sa fie >= 1", e.getMessage());
        verify(txRepo, never()).save(any());
    }
}
