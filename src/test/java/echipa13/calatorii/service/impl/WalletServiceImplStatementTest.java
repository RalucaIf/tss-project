package echipa13.calatorii.service.impl;

import echipa13.calatorii.models.Trip;
import echipa13.calatorii.models.TripWallet;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.models.WalletTransaction;
import echipa13.calatorii.repository.TripRepository;
import echipa13.calatorii.repository.TripWalletRepository;
import echipa13.calatorii.repository.UserRepository;
import echipa13.calatorii.repository.WalletTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceImplStatementTest {

    // Statement coverage
    /*
     * - fiecare instructiune din metodele tintite sa fie executata cel putin o data
     * Metode:
     *   updateBudgetOwnedByUser      - cascada de if-uri + fallback currency
     *   deleteTransactionOwnedByUser - 3 ramuri distincte (not found / alt wallet / ok)
     *   getOrCreateWalletOwnedByUser - ramura orElseGet -> createWallet (save wallet nou)
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
    private final String login = "raluca@test.com";

    @BeforeEach
    public void setUp() throws Exception {
        user = new UserEntity();
        user.setId(1L);

        trip = new Trip();
        trip.setId(tripId);
        trip.setUser(user);

        wallet = new TripWallet();
        wallet.setTrip(trip);
        // Id-ul este generat de DB in runtime, dar in teste unitare il setam manual
        // pentru a putea compara wallet-urile in deleteTransactionOwnedByUser
        setIdPrinReflection(wallet, 500L);

        when(userRepo.findByEmail(login)).thenReturn(user);
        when(tripRepo.findById(tripId)).thenReturn(Optional.of(trip));
    }

    @Test
    public void testInstructiuni_SetareaBugetului() {
        when(walletRepo.findByTrip_Id(tripId)).thenReturn(Optional.of(wallet));
        when(walletRepo.save(any(TripWallet.class))).thenAnswer(inv -> inv.getArgument(0));

        // Ramura 1: budgetTotal == null -> throw
        IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class, () ->
                service.updateBudgetOwnedByUser(tripId, login, null, "RON"));
        assertEquals("Bugetul este obligatoriu.", e1.getMessage());

        // Ramura 2: budgetTotal < 0 -> throw
        IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class, () ->
                service.updateBudgetOwnedByUser(tripId, login, new BigDecimal("-10"), "RON"));
        assertEquals("Bugetul nu poate fi negativ.", e2.getMessage());

        // Ramura 3: currency == null -> fallback "RON" direct din ternary
        TripWallet r1 = service.updateBudgetOwnedByUser(tripId, login,
                new BigDecimal("200"), null);
        assertEquals("RON", r1.getCurrency());

        // Ramura 4: currency neacceptata dupa normalizare -> fallback la "RON"
        TripWallet r2 = service.updateBudgetOwnedByUser(tripId, login,
                new BigDecimal("500"), "  usd  ");
        assertEquals("RON", r2.getCurrency());

        // Ramura 5: currency valida dupa normalizare -> setata ca atare
        // "eur" trece prin trim+toUpperCase -> "EUR", care este acceptat
        TripWallet r3 = service.updateBudgetOwnedByUser(tripId, login,
                new BigDecimal("1000"), "  eur  ");
        assertEquals("EUR", r3.getCurrency());
        assertEquals(0, new BigDecimal("1000").compareTo(r3.getBudgetTotal()));
    }

    @Test
    public void testInstructiuni_StergereaTranzactiei() throws Exception {
        when(walletRepo.findByTrip_Id(tripId)).thenReturn(Optional.of(wallet));

        // Ramura 1: tranzactie inexistenta -> orElseThrow
        when(txRepo.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class, () ->
                service.deleteTransactionOwnedByUser(tripId, login, 999L));
        assertNotNull(e1.getMessage());

        // Ramura 2: tranzactia apartine altui wallet -> throw IllegalState
        // o tranzactie cu un alt wallet, avand alt id
        TripWallet altWallet = new TripWallet();
        altWallet.setTrip(trip);
        setIdPrinReflection(altWallet, 999L);

        WalletTransaction txAltuia = new WalletTransaction();
        txAltuia.setWallet(altWallet);
        when(txRepo.findById(42L)).thenReturn(Optional.of(txAltuia));

        IllegalStateException e2 = assertThrows(IllegalStateException.class, () ->
                service.deleteTransactionOwnedByUser(tripId, login, 42L));
        assertNotNull(e2.getMessage());

        // Ramura 3: tranzactie valida -> delete
        WalletTransaction txValid = new WalletTransaction();
        txValid.setWallet(wallet);
        when(txRepo.findById(77L)).thenReturn(Optional.of(txValid));

        service.deleteTransactionOwnedByUser(tripId, login, 77L);
        verify(txRepo).delete(txValid);
    }

    @Test
    public void testInstructiuni_ObtinereSauCreareaPortofelului() {
        // Ramura 1: wallet existent -> se returneaza direct, fara save
        when(walletRepo.findByTrip_Id(tripId)).thenReturn(Optional.of(wallet));

        TripWallet existent = service.getOrCreateWalletOwnedByUser(tripId, login);
        assertSame(wallet, existent);
        verify(walletRepo, never()).save(any());

        // Ramura 2: wallet inexistent -> orElseGet apeleaza createWallet -> save
        reset(walletRepo);
        when(walletRepo.findByTrip_Id(tripId)).thenReturn(Optional.empty());
        when(walletRepo.save(any(TripWallet.class))).thenAnswer(inv -> inv.getArgument(0));

        TripWallet nou = service.getOrCreateWalletOwnedByUser(tripId, login);
        assertSame(trip, nou.getTrip());
        verify(walletRepo).save(any(TripWallet.class));
    }

    // ------------------ helpers ------------------

    private void setIdPrinReflection(TripWallet w, Long id) throws Exception {
        Field f = TripWallet.class.getDeclaredField("id");
        f.setAccessible(true);
        f.set(w, id);
    }
}