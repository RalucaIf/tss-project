package echipa13.calatorii.service.impl;

import echipa13.calatorii.Dto.WalletInsights;
import echipa13.calatorii.models.Trip;
import echipa13.calatorii.models.TripWallet;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.models.WalletCategory;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class WalletServiceImplCircuitsTest {

    // testarea circuitelor independente
    /*
     * pentru fiecare metoda, se parcurg toate circuitele independente
     * din graful de control. Numarul de circuite = complexitatea ciclomatica
     *   V(G) = e - n + 2 = numar predicate + 1
     *
     * Metode (total: 17 circuite independente):
     *  - findUserByUsernameOrEmail       (V(G) = 5)
     *  - computeDaysRemainingSafe        (V(G) = 6)
     *  - computeInsightsOwnedByUser      (V(G) = 6)
     *
     * metodele private sunt testate indirect prin metode publice care le folosesc
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
    private final String login = "diana@test.com";

    @BeforeEach
    public void setUp() {
        user = new UserEntity();
        user.setId(1L);

        trip = new Trip();
        trip.setId(tripId);
        trip.setUser(user);

        wallet = new TripWallet();
        wallet.setTrip(trip);
        wallet.setCurrency("RON");
    }

    // n = 9 noduri, e = 12 arce => V(G) = 12 - 9 + 2 = 5
    // C1: login null -> Optional.empty()
    // C2: login blank -> Optional.empty()
    // C3: login valid, gasit dupa email
    // C4: login valid, negasit dupa email, gasit dupa username
    // C5: login valid, negasit deloc -> Optional.empty()
    @Test
    public void testCircuite_IdentificareaUtilizatorului() {
        when(tripRepo.findById(tripId)).thenReturn(Optional.of(trip));
        when(walletRepo.findByTrip_Id(tripId)).thenReturn(Optional.of(wallet));

        // C1: login null -> exceptie "Utilizator inexistent"
        IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class, () ->
                service.getOrCreateWalletOwnedByUser(tripId, null));
        assertTrue(e1.getMessage().toLowerCase().contains("utilizator"));

        // C2: login blank -> exceptie
        IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class, () ->
                service.getOrCreateWalletOwnedByUser(tripId, "   "));
        assertTrue(e2.getMessage().toLowerCase().contains("utilizator"));

        // C3: gasit dupa email
        String loginEmail = "diana@test.com";
        when(userRepo.findByEmail(loginEmail)).thenReturn(user);
        TripWallet r3 = service.getOrCreateWalletOwnedByUser(tripId, loginEmail);
        assertSame(wallet, r3);

        // C4: negasit dupa email, gasit dupa username
        String loginUsername = "diana_user";
        when(userRepo.findByEmail(loginUsername)).thenReturn(null);
        when(userRepo.findByUsername(loginUsername)).thenReturn(user);
        TripWallet r4 = service.getOrCreateWalletOwnedByUser(tripId, loginUsername);
        assertSame(wallet, r4);

        // C5: nu e gasit nicaieri
        String loginInexistent = "necunoscut@test.com";
        when(userRepo.findByEmail(loginInexistent)).thenReturn(null);
        when(userRepo.findByUsername(loginInexistent)).thenReturn(null);
        IllegalArgumentException e5 = assertThrows(IllegalArgumentException.class, () ->
                service.getOrCreateWalletOwnedByUser(tripId, loginInexistent));
        assertTrue(e5.getMessage().toLowerCase().contains("utilizator"));
    }

    // n = 8 noduri, e = 12 arce => V(G) = 12 - 8 + 2 = 6
    // C1: start null -> null
    // C2: end null -> null
    // C3: end inainte de start -> null
    // C4: today dupa end -> 0
    // C5: today inainte de start -> durata completa
    // C6: caz normal -> de la azi la end
    @Test
    public void testCircuite_CalculareaZilelorRamase() {
        when(userRepo.findByEmail(login)).thenReturn(user);
        when(tripRepo.findById(tripId)).thenReturn(Optional.of(trip));
        when(walletRepo.findByTrip_Id(tripId)).thenReturn(Optional.of(wallet));
        wallet.setBudgetTotal(new BigDecimal("500"));
        when(txRepo.sumAmountByWalletId(any())).thenReturn(new BigDecimal("100"));
        when(txRepo.countDistinctSpentDays(any())).thenReturn(1L);
        when(txRepo.sumByCategory(any())).thenReturn(new ArrayList<>());

        // C1: start null
        trip.setStartDate(null);
        trip.setEndDate(LocalDate.now().plusDays(5));
        assertNull(service.computeSmartBudgetAdviceOwnedByUser(tripId, login).daysRemaining());

        // C2: end null
        trip.setStartDate(LocalDate.now().minusDays(1));
        trip.setEndDate(null);
        assertNull(service.computeSmartBudgetAdviceOwnedByUser(tripId, login).daysRemaining());

        // C3: end inainte de start - interval invalid
        trip.setStartDate(LocalDate.now().plusDays(5));
        trip.setEndDate(LocalDate.now().plusDays(1));
        assertNull(service.computeSmartBudgetAdviceOwnedByUser(tripId, login).daysRemaining());

        // C4: today dupa end
        trip.setStartDate(LocalDate.now().minusDays(10));
        trip.setEndDate(LocalDate.now().minusDays(1));
        Integer r4 = service.computeSmartBudgetAdviceOwnedByUser(tripId, login).daysRemaining();
        assertNotNull(r4);
        assertEquals(0, r4);

        // C5: today inainte de start -> durata completa
        trip.setStartDate(LocalDate.now().plusDays(3));
        trip.setEndDate(LocalDate.now().plusDays(9));
        Integer r5 = service.computeSmartBudgetAdviceOwnedByUser(tripId, login).daysRemaining();
        assertNotNull(r5);
        assertEquals(7, r5);

        // C6: caz normal -> de la azi la end
        trip.setStartDate(LocalDate.now().minusDays(2));
        trip.setEndDate(LocalDate.now().plusDays(4));
        Integer r6 = service.computeSmartBudgetAdviceOwnedByUser(tripId, login).daysRemaining();
        assertNotNull(r6);
        assertEquals(5, r6);
    }

    // n = 10 noduri, e = 14 arce => V(G) = 14 - 10 + 2 = 6
    // C1: totalSpent null, activeDays = 0, rows goala
    // C2: totalSpent non-null, activeDays = 0, rows goala
    // C3: activeDays > 0, rows goala
    // C4: rows cu un rand avand amount null
    // C5: rows cu un rand valid, totalSpent > 0
    // C6: rows cu un rand, totalSpent = 0 (percent ramane 0)
    @Test
    public void testCircuite_CalculareaSumarizariiInsights() {
        when(userRepo.findByEmail(login)).thenReturn(user);
        when(tripRepo.findById(tripId)).thenReturn(Optional.of(trip));
        when(walletRepo.findByTrip_Id(tripId)).thenReturn(Optional.of(wallet));

        // C1: totalSpent null, activeDays = 0, rows goala
        when(txRepo.sumAmountByWalletId(any())).thenReturn(null);
        when(txRepo.countDistinctSpentDays(any())).thenReturn(0L);
        when(txRepo.sumByCategory(any())).thenReturn(new ArrayList<>());

        WalletInsights r1 = service.computeInsightsOwnedByUser(tripId, login);
        assertNotNull(r1);
        assertEquals(0, r1.getActiveDays());
        assertEquals(0, BigDecimal.ZERO.compareTo(r1.getAvgPerActiveDay()));
        assertTrue(r1.getTopCategories().isEmpty());

        // C2: totalSpent non-null, activeDays = 0, rows goala
        when(txRepo.sumAmountByWalletId(any())).thenReturn(new BigDecimal("50"));
        when(txRepo.countDistinctSpentDays(any())).thenReturn(0L);
        when(txRepo.sumByCategory(any())).thenReturn(new ArrayList<>());

        WalletInsights r2 = service.computeInsightsOwnedByUser(tripId, login);
        assertEquals(0, BigDecimal.ZERO.compareTo(r2.getAvgPerActiveDay()));
        assertTrue(r2.getTopCategories().isEmpty());

        // C3: activeDays > 0, rows goala
        when(txRepo.sumAmountByWalletId(any())).thenReturn(new BigDecimal("100"));
        when(txRepo.countDistinctSpentDays(any())).thenReturn(2L);
        when(txRepo.sumByCategory(any())).thenReturn(new ArrayList<>());

        WalletInsights r3 = service.computeInsightsOwnedByUser(tripId, login);
        assertEquals(0, new BigDecimal("50.00").compareTo(r3.getAvgPerActiveDay()));
        assertTrue(r3.getTopCategories().isEmpty());

        // C4: rand cu amount null
        Object[] randCuAmountNull = new Object[]{WalletCategory.FOOD, null};
        List<Object[]> rowsCuNull = new ArrayList<>();
        rowsCuNull.add(randCuAmountNull);
        when(txRepo.sumAmountByWalletId(any())).thenReturn(new BigDecimal("100"));
        when(txRepo.countDistinctSpentDays(any())).thenReturn(1L);
        when(txRepo.sumByCategory(any())).thenReturn(rowsCuNull);

        WalletInsights r4 = service.computeInsightsOwnedByUser(tripId, login);
        assertEquals(1, r4.getTopCategories().size());
        assertEquals(0, BigDecimal.ZERO.compareTo(r4.getTopCategories().get(0).getAmount()));

        // C5: rand valid, totalSpent > 0 -> procent calculat
        Object[] randValid = new Object[]{WalletCategory.TRANSPORT, new BigDecimal("40")};
        List<Object[]> rowsValide = new ArrayList<>();
        rowsValide.add(randValid);
        when(txRepo.sumAmountByWalletId(any())).thenReturn(new BigDecimal("100"));
        when(txRepo.countDistinctSpentDays(any())).thenReturn(1L);
        when(txRepo.sumByCategory(any())).thenReturn(rowsValide);

        WalletInsights r5 = service.computeInsightsOwnedByUser(tripId, login);
        assertEquals(1, r5.getTopCategories().size());
        assertEquals(40, r5.getTopCategories().get(0).getPercentOfSpent());

        // C6: rand cu totalSpent = 0 -> procent ramane 0
        Object[] randTotalZero = new Object[]{WalletCategory.OTHER, new BigDecimal("10")};
        List<Object[]> rowsTotalZero = new ArrayList<>();
        rowsTotalZero.add(randTotalZero);
        when(txRepo.sumAmountByWalletId(any())).thenReturn(BigDecimal.ZERO);
        when(txRepo.countDistinctSpentDays(any())).thenReturn(0L);
        when(txRepo.sumByCategory(any())).thenReturn(rowsTotalZero);

        WalletInsights r6 = service.computeInsightsOwnedByUser(tripId, login);
        assertEquals(1, r6.getTopCategories().size());
        assertEquals(0, r6.getTopCategories().get(0).getPercentOfSpent());
    }
}