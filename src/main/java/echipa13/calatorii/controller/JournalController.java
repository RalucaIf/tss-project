package echipa13.calatorii.controller;

import echipa13.calatorii.models.Trip;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.repository.UserRepository;
import echipa13.calatorii.service.TripService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/journal")
@PreAuthorize("hasRole('USER')")
public class JournalController {

    private final TripService tripService;
    private final UserRepository userRepo;

    public JournalController(TripService tripService, UserRepository userRepo) {
        this.tripService = tripService;
        this.userRepo = userRepo;
    }

    private Long currentUserId(UserDetails principal) {
        if (principal == null) throw new IllegalStateException("Neautentificat.");
        String login = principal.getUsername();

        UserEntity byEmail = userRepo.findByEmail(login);
        if (byEmail != null) return byEmail.getId();

        UserEntity byUsername = userRepo.findByUsername(login);
        if (byUsername != null) return byUsername.getId();

        throw new IllegalStateException("Nu am putut identifica utilizatorul curent.");
    }

    // LISTĂ JURNAL (toate itinerariile userului ca pe "Destinations")
    @GetMapping
    public String journalHome(@AuthenticationPrincipal UserDetails principal, Model model) {
        Long uid = currentUserId(principal);
        model.addAttribute("trips", tripService.listForUser(uid));
        return "journal/list";
    }

    // DETALIU JURNAL – momentan afișăm doar titlul, cum ai cerut
    @GetMapping("/{id}")
    public String journalDetail(@AuthenticationPrincipal UserDetails principal,
                                @PathVariable Long id,
                                Model model) {
        Long uid = currentUserId(principal);
        Trip trip = tripService.getOwned(id, uid);
        model.addAttribute("trip", trip);
        return "journal/detail";
    }
}

