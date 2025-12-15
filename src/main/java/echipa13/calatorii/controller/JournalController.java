package echipa13.calatorii.controller;

import echipa13.calatorii.models.Trip;
import echipa13.calatorii.models.TripItem;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.repository.TripItemRepository;
import echipa13.calatorii.repository.UserRepository;
import echipa13.calatorii.service.TripService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/journal")
@PreAuthorize("hasRole('USER')")
public class JournalController {

    private static final Logger log = LoggerFactory.getLogger(JournalController.class);
    private final TripService tripService;
    private final TripItemRepository tripItemRepository;
    private final UserRepository userRepo;

    public JournalController(TripService tripService, TripItemRepository tripItemRepository, UserRepository userRepo) {
        this.tripService = tripService;
        this.tripItemRepository = tripItemRepository;
        this.userRepo = userRepo;
    }

    private Long currentUserId(UserDetails principal) {
        String login = principal.getUsername();
        UserEntity u = userRepo.findByUsername(login);
        if (u == null) {
            u = userRepo.findByEmail(login);
        }
        if (u == null) {
            throw new IllegalStateException("Utilizator inexistent: " + login);
        }
        return u.getId();
    }

    // LISTĂ JURNAL (toate itinerariile userului ca pe "Destinations")
    @GetMapping
    public String journalHome(@AuthenticationPrincipal UserDetails principal, Model model) {
        Long uid = currentUserId(principal);
        model.addAttribute("trips", tripService.listForUser(uid));
        return "journal/list";
    }

    @GetMapping("/{id}")
    public String journalDetail(@AuthenticationPrincipal UserDetails principal,
                                @PathVariable Long id,
                                Model model) {
        Long uid = currentUserId(principal);
        Trip trip = tripService.getOwned(id, uid);

        List<TripItem> items = tripItemRepository.findByTrip_IdOrderByDayIndexAscIdAsc(id);
        Map<Integer, List<TripItem>> days = new LinkedHashMap<>();
        for (TripItem it : items) {
            int day = (it.getDayIndex() != null) ? it.getDayIndex() : 1;
            days.computeIfAbsent(day, k -> new ArrayList<>()).add(it);
        }

        model.addAttribute("trip", trip);
        model.addAttribute("days", days);
        return "journal/detail";
    }


    @PostMapping("/{id}/days")
    @Transactional
    public String addDay(@AuthenticationPrincipal UserDetails principal,
                         @PathVariable Long id,
                         @RequestParam(required = false) String title,
                         @RequestParam(required = false) String notes) {
        Long uid = currentUserId(principal);
        Trip trip = tripService.getOwned(id, uid);

        int max = tripItemRepository.findMaxDayIndex(id);
        int nextDay = Math.max(1, max + 1);

        TripItem it = new TripItem();
        it.setTrip(trip);
        it.setDayIndex(nextDay);
        it.setTitle((title == null || title.isBlank()) ? ("Plan ziua " + nextDay) : title.trim());
        it.setNotes(notes == null ? "" : notes.trim());

        // ✅ FIX: coloana NOT NULL 'category'
        it.setCategory(trip.getCategory() != null ? trip.getCategory() : "GENERAL");

        tripItemRepository.save(it);
        return "redirect:/journal/" + id;
    }
}