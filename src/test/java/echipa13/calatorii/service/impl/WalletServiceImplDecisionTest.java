package echipa13.calatorii.service.impl;

import echipa13.calatorii.Dto.WalletCategoryTotal;
import echipa13.calatorii.models.Trip;
import echipa13.calatorii.models.TripWallet;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.models.WalletCategory;
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
public class WalletServiceImplDecisionTest {

    // Decision coverage
    /*
     * - fiecare decizie (if / while / ternary / compareTo) sa fie evaluata
     * si pe ramura TRUE si pe ramura FALSE cel putin o data
     *
     * Metoda: computeSmartBudgetAdviceOwnedByUser
     * Aceasta apeleaza la randul ei 3 metode private:
     *   - buildRiskUi                (cascada 4 if-uri pentru label-ul de risc)
     *   - computeDaysRemainingSafe   (4 ramuri: null / end < start / today > end / today < start / normal)
     *   - buildRecommendation        (ramuri pentru status, ritm, prognoza, allowance)
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
    private final String login = "ioana@test.com";

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

        when(userRepo.findByEmail(login)).thenReturn(user);
        when(tripRepo.findById(tripId)).thenReturn(Optional.of(trip));
        when(walletRepo.findByTrip_Id(tripId)).thenReturn(Optional.of(wallet));
    }

    @Test
    public void testDecizie_BugetInteligentDupaStareaBugetului() {
        // Scenariu pe 3 stari ale bugetului din computeSmartBudgetAdviceOwnedByUser

        // Decizia 1: buget nesetat -> mesaj
        // Trip fara date => daysRemaining null; budget null => percent null
        wallet.setBudgetTotal(null);
        trip.setStartDate(null);
        trip.setEndDate(null);
        when(txRepo.sumAmountByWalletId(any())).thenReturn(BigDecimal.ZERO);
        when(txRepo.countDistinctSpentDays(any())).thenReturn(0L);
        when(txRepo.sumByCategory(any())).thenReturn(new ArrayList<>());

        WalletService.SmartBudgetAdvice advBugetNul = service
                .computeSmartBudgetAdviceOwnedByUser(tripId, login);

        assertEquals("Buget nesetat", advBugetNul.riskLabelRo());
        assertEquals("bg-secondary", advBugetNul.uiClass());
        assertNull(advBugetNul.percentUsed());
        assertNull(advBugetNul.dailyAllowance());
        assertTrue(advBugetNul.recommendation().toLowerCase().contains("setează"));

        // Decizia 2: buget setat, fara zile consumate (burnRate = 0)
        // -> daysToExceed ramane null
        wallet.setBudgetTotal(new BigDecimal("1000"));
        trip.setStartDate(LocalDate.now().minusDays(2));
        trip.setEndDate(LocalDate.now().plusDays(5));
        when(txRepo.sumAmountByWalletId(any())).thenReturn(BigDecimal.ZERO);
        when(txRepo.countDistinctSpentDays(any())).thenReturn(0L);

        WalletService.SmartBudgetAdvice advNeconsumat = service
                .computeSmartBudgetAdviceOwnedByUser(tripId, login);

        assertEquals("OK", advNeconsumat.riskLabelRo());
        assertEquals(0, advNeconsumat.percentUsed());
        assertNull(advNeconsumat.daysToExceed());
        assertNotNull(advNeconsumat.dailyAllowance());

        // Decizia 3: buget consumat partial, cu burnRate > 0 si zile ramase
        // -> toate ramurile TRUE: daysToExceed calculat, dailyAllowance calculat
        wallet.setBudgetTotal(new BigDecimal("1000"));
        trip.setStartDate(LocalDate.now().minusDays(2));
        trip.setEndDate(LocalDate.now().plusDays(5));
        when(txRepo.sumAmountByWalletId(any())).thenReturn(new BigDecimal("400"));
        when(txRepo.countDistinctSpentDays(any())).thenReturn(2L);

        WalletService.SmartBudgetAdvice advActiv = service
                .computeSmartBudgetAdviceOwnedByUser(tripId, login);

        assertEquals(40, advActiv.percentUsed());
        assertNotNull(advActiv.burnRatePerDay());
        assertTrue(advActiv.burnRatePerDay().compareTo(BigDecimal.ZERO) > 0);
        assertNotNull(advActiv.daysToExceed());
        assertTrue(advActiv.daysToExceed() > 0);
        assertNotNull(advActiv.dailyAllowance());
    }

    @Test
    public void testDecizie_ClasificareaRiscului() {
        // Toate ramurile din buildRiskUi
        // FALSE pe primul if general si apoi cascada de if-uri pe praguri

        wallet.setBudgetTotal(new BigDecimal("100"));
        trip.setStartDate(LocalDate.now().minusDays(1));
        trip.setEndDate(LocalDate.now().plusDays(3));
        when(txRepo.countDistinctSpentDays(any())).thenReturn(1L);
        when(txRepo.sumByCategory(any())).thenReturn(new ArrayList<>());

        // Decizia 1: percent sub 75 -> OK
        when(txRepo.sumAmountByWalletId(any())).thenReturn(new BigDecimal("30"));
        assertEquals("OK", service.computeSmartBudgetAdviceOwnedByUser(tripId, login).riskLabelRo());

        // Decizia 2: percent intre 75 si 89 -> atentie
        when(txRepo.sumAmountByWalletId(any())).thenReturn(new BigDecimal("80"));
        assertEquals("Atenție",
                service.computeSmartBudgetAdviceOwnedByUser(tripId, login).riskLabelRo());

        // Decizia 3: percent intre 90 si 99 -> critic
        when(txRepo.sumAmountByWalletId(any())).thenReturn(new BigDecimal("95"));
        assertEquals("Critic",
                service.computeSmartBudgetAdviceOwnedByUser(tripId, login).riskLabelRo());

        // Decizia 4: percent >= 100 -> depasit
        when(txRepo.sumAmountByWalletId(any())).thenReturn(new BigDecimal("120"));
        WalletService.SmartBudgetAdvice advDepasit = service
                .computeSmartBudgetAdviceOwnedByUser(tripId, login);
        assertEquals("Depășit", advDepasit.riskLabelRo());
        assertEquals("bg-danger", advDepasit.uiClass());

        // Decizia 5: buget nesetat -> prima conditie TRUE
        wallet.setBudgetTotal(null);
        assertEquals("Buget nesetat",
                service.computeSmartBudgetAdviceOwnedByUser(tripId, login).riskLabelRo());
    }
}
