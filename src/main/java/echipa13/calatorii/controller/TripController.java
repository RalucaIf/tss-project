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
@RequestMapping("/trips")
@PreAuthorize("hasRole('USER')")
public class TripController {

    private final TripService tripService;
    private final UserRepository userRepo;

    public TripController(TripService tripService, UserRepository userRepo) {
        this.tripService = tripService;
        this.userRepo = userRepo;
    }

    private Long currentUserId(UserDetails principal) {
        if (principal == null) throw new IllegalStateException("Neautentificat.");
        String login = principal.getUsername(); // la tine poate fi email sau username

        UserEntity byEmail = userRepo.findByEmail(login);
        if (byEmail != null) return byEmail.getId();

        UserEntity byUsername = userRepo.findByUsername(login);
        if (byUsername != null) return byUsername.getId();

        throw new IllegalStateException("Nu am putut identifica utilizatorul curent.");
    }

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails principal,
                       @RequestParam(name = "c", required = false) String c,
                       Model model) {
        Long uid = currentUserId(principal);

        // categorii fixe – apar mereu în UI
        var categories = java.util.List.of("City_break", "Adventure", "Business", "Relax", "Family");

        // normalizăm param c
        String current = (c == null || c.isBlank()) ? "all" : c;

        java.util.List<echipa13.calatorii.models.Trip> trips;

        if ("all".equalsIgnoreCase(current)) {
            trips = tripService.listForUser(uid);
        } else {
            // dacă e o categorie ne-suportată, forțăm "fără rezultate"
            boolean supported = categories.stream().anyMatch(cat -> cat.equalsIgnoreCase(current));
            if (!supported) {
                trips = java.util.List.of(); // gol => va apărea mesajul
            } else {
                trips = tripService.listForUserByCategory(uid, current);
            }
        }

        model.addAttribute("categories", categories);    // pastilele din UI
        model.addAttribute("trips", trips);              // rezultatele
        model.addAttribute("c", current);                // pt. starea „active”
        model.addAttribute("noTripsForCategory",
                !"all".equalsIgnoreCase(current) && trips.isEmpty()); // pt. mesajul special

        return "trips/list";
    }



    // FORM CREATE
    @GetMapping("/new")
    public String formNew(Model model) {
        model.addAttribute("trip", new Trip());
        return "trips/form";
    }

    // CREATE
    @PostMapping
    public String create(@AuthenticationPrincipal UserDetails principal, @ModelAttribute Trip trip) {
        Long uid = currentUserId(principal);
        tripService.create(uid, trip);
        return "redirect:/trips";
    }

    // FORM EDIT
    @GetMapping("/{id}/edit")
    public String formEdit(@AuthenticationPrincipal UserDetails principal,
                           @PathVariable Long id,
                           Model model) {
        Long uid = currentUserId(principal);
        model.addAttribute("trip", tripService.getOwned(id, uid));
        return "trips/form";
    }

    // UPDATE
    @PostMapping("/{id}")
    public String update(@AuthenticationPrincipal UserDetails principal,
                         @PathVariable Long id,
                         @ModelAttribute Trip trip) {
        Long uid = currentUserId(principal);
        tripService.update(uid, id, trip);
        return "redirect:/trips";
    }

    // VIEW
    @GetMapping("/{id}")
    public String detail(@AuthenticationPrincipal UserDetails principal,
                         @PathVariable Long id,
                         Model model) {
        Long uid = currentUserId(principal);
        model.addAttribute("trip", tripService.getOwned(id, uid));
        return "trips/view";
    }

    // DELETE
    @PostMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal UserDetails principal,
                         @PathVariable Long id) {
        Long uid = currentUserId(principal);
        tripService.delete(uid, id);
        return "redirect:/trips";
    }
}
