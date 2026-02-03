// ======================================================================
// File: src/main/java/echipa13/calatorii/controller/WalletController.java
// ======================================================================
package echipa13.calatorii.controller;

import echipa13.calatorii.Dto.WalletAlert;
import echipa13.calatorii.Dto.WalletInsights;
import echipa13.calatorii.Dto.WalletSummary;
import echipa13.calatorii.models.TripWallet;
import echipa13.calatorii.models.WalletCategory;
import echipa13.calatorii.service.WalletService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    // ------------------------------------------------------------------
    // GET: Pagina portofelului (KPI + avertizări + insights + tranzacții)
    // + Extensia A: Buget inteligent (smart)
    // ------------------------------------------------------------------
    @GetMapping("/trips/{id}/wallet")
    public String tripWallet(@PathVariable("id") Long tripId,
                             Authentication authentication,
                             Model model) {

        try {
            String login = authentication.getName();

            TripWallet wallet = walletService.getOrCreateWalletOwnedByUser(tripId, login);
            WalletSummary summary = walletService.computeSummaryOwnedByUser(tripId, login);
            WalletInsights insights = walletService.computeInsightsOwnedByUser(tripId, login);

            // Extensia A: Buget inteligent
            WalletService.SmartBudgetAdvice smart = walletService.computeSmartBudgetAdviceOwnedByUser(tripId, login);

            model.addAttribute("trip", wallet.getTrip());
            model.addAttribute("wallet", wallet);
            model.addAttribute("summary", summary);
            model.addAttribute("alert", WalletAlert.fromSummary(summary));
            model.addAttribute("insights", insights);
            model.addAttribute("smart", smart);

            model.addAttribute("transactions",
                    walletService.listTransactionsOwnedByUser(tripId, login));
            // după ce ai insights
            if (insights != null && insights.getTopCategories() != null) {
                var chartLabels = insights.getTopCategories().stream()
                        .map(ct -> {
                            var c = ct.getCategory();
                            // labelRo dacă există, altfel enum name
                            return (c != null && c.getLabelRo() != null) ? c.getLabelRo() : String.valueOf(c);
                        })
                        .toList();

                var chartValues = insights.getTopCategories().stream()
                        .map(ct -> ct.getAmount())
                        .toList();

                model.addAttribute("chartLabels", chartLabels);
                model.addAttribute("chartValues", chartValues);
            }


            return "trips/wallet";
        } catch (RuntimeException ex) {
            return "redirect:/trips?error=wallet";
        }
    }

    // ------------------------------------------------------------------
    // POST: Setare buget (POST + redirect)
    // ------------------------------------------------------------------
    @PostMapping("/trips/{id}/wallet/budget")
    public String setBudget(@PathVariable("id") Long tripId,
                            @RequestParam("budgetTotal") BigDecimal budgetTotal,
                            @RequestParam(value = "currency", required = false) String currency,
                            Authentication authentication) {
        try {
            walletService.updateBudgetOwnedByUser(tripId, authentication.getName(), budgetTotal, currency);
            return "redirect:/trips/" + tripId + "/wallet?msg=budgetSaved";
        } catch (RuntimeException ex) {
            return "redirect:/trips/" + tripId + "/wallet?error=budget";
        }
    }

    // ------------------------------------------------------------------
    // POST: Adăugare cheltuială (POST + redirect)
    // ------------------------------------------------------------------
    @PostMapping("/trips/{id}/wallet/tx/add")
    public String addExpense(@PathVariable("id") Long tripId,
                             @RequestParam("amount") BigDecimal amount,
                             @RequestParam("category") WalletCategory category,
                             @RequestParam("title") String title,
                             @RequestParam(value = "note", required = false) String note,
                             @RequestParam(value = "spentAt", required = false) LocalDate spentAt,
                             @RequestParam(value = "dayIndex", required = false) Integer dayIndex,
                             Authentication authentication) {
        try {
            walletService.addExpenseOwnedByUser(
                    tripId,
                    authentication.getName(),
                    amount,
                    category,
                    title,
                    note,
                    spentAt,
                    dayIndex
            );
            return "redirect:/trips/" + tripId + "/wallet?msg=txAdded";
        } catch (RuntimeException ex) {
            return "redirect:/trips/" + tripId + "/wallet?error=tx";
        }
    }

    // ------------------------------------------------------------------
    // POST: Ștergere cheltuială (POST + redirect)
    // ------------------------------------------------------------------
    @PostMapping("/trips/{id}/wallet/tx/{txId}/delete")
    public String deleteExpense(@PathVariable("id") Long tripId,
                                @PathVariable("txId") Long txId,
                                Authentication authentication) {
        try {
            walletService.deleteTransactionOwnedByUser(tripId, authentication.getName(), txId);
            return "redirect:/trips/" + tripId + "/wallet?msg=txDeleted";
        } catch (RuntimeException ex) {
            return "redirect:/trips/" + tripId + "/wallet?error=txDelete";
        }
    }
}
