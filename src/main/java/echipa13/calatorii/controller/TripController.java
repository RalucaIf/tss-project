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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

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
        String login = principal.getUsername();

        UserEntity byEmail = userRepo.findByEmail(login);
        if (byEmail != null) return byEmail.getId();

        UserEntity byUsername = userRepo.findByUsername(login);
        if (byUsername != null) return byUsername.getId();

        throw new IllegalStateException("Nu am putut identifica utilizatorul curent.");
    }

    private Path tripFolder(Long tripId) {
        return Paths.get(System.getProperty("user.dir"), "uploads", "trips", String.valueOf(tripId));
    }

    private String findTripCoverUrl(Long tripId) {
        Path folder = tripFolder(tripId);
        if (!Files.exists(folder)) return null;
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(folder, "cover.*")) {
            for (Path p : ds) {
                return "/uploads/trips/" + tripId + "/" + p.getFileName().toString();
            }
        } catch (IOException ignore) { }
        return null;
    }

    private String fallbackCoverForCategory(String category) {
        String c = (category == null) ? "" : category.trim().toLowerCase();
        if (c.contains("city")) return "/walpaper/showcase-6.webp";
        if (c.contains("adventure")) return "/walpaper/showcase-7.webp";
        if (c.contains("business")) return "/walpaper/showcase-11.webp";
        if (c.contains("relax")) return "/walpaper/misc-3.webp";
        if (c.contains("family")) return "/walpaper/people.jpg";
        return "/walpaper/showcase-11.webp";
    }

    private String extensionOrJpg(String filename) {
        if (filename == null) return "jpg";
        String f = filename.toLowerCase(Locale.ROOT);
        int dot = f.lastIndexOf('.');
        if (dot < 0 || dot == f.length() - 1) return "jpg";
        String ext = f.substring(dot + 1);
        // whitelist simplu
        return switch (ext) {
            case "jpg", "jpeg", "png", "gif", "webp", "bmp" -> ext;
            default -> "jpg";
        };
    }

    private void deleteExistingTripCovers(Path folder) throws IOException {
        if (!Files.exists(folder)) return;
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(folder, "cover.*")) {
            for (Path p : ds) {
                Files.deleteIfExists(p);
            }
        }
    }

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails principal,
                       @RequestParam(name = "c", required = false) String c,
                       Model model) {
        Long uid = currentUserId(principal);

        var categories = java.util.List.of("City_break", "Adventure", "Business", "Relax", "Family");
        String current = (c == null || c.isBlank()) ? "all" : c;

        List<Trip> trips;
        if ("all".equalsIgnoreCase(current)) {
            trips = tripService.listForUser(uid);
        } else {
            boolean supported = categories.stream().anyMatch(cat -> cat.equalsIgnoreCase(current));
            trips = supported ? tripService.listForUserByCategory(uid, current) : java.util.List.of();
        }

        Map<Long, String> coverUrlByTripId = new HashMap<>();
        for (Trip t : trips) {
            String cover = findTripCoverUrl(t.getId());
            if (cover == null) cover = fallbackCoverForCategory(t.getCategory());
            coverUrlByTripId.put(t.getId(), cover);
        }

        model.addAttribute("categories", categories);
        model.addAttribute("trips", trips);
        model.addAttribute("c", current);
        model.addAttribute("noTripsForCategory",
                !"all".equalsIgnoreCase(current) && trips.isEmpty());
        model.addAttribute("coverUrlByTripId", coverUrlByTripId);

        return "trips/list";
    }

    @GetMapping("/new")
    public String formNew(Model model) {
        model.addAttribute("trip", new Trip());
        return "trips/form";
    }

    @PostMapping
    public String create(@AuthenticationPrincipal UserDetails principal, @ModelAttribute Trip trip) {
        Long uid = currentUserId(principal);
        tripService.create(uid, trip);
        return "redirect:/trips";
    }

    @GetMapping("/{id}/edit")
    public String formEdit(@AuthenticationPrincipal UserDetails principal,
                           @PathVariable Long id,
                           Model model) {
        Long uid = currentUserId(principal);
        model.addAttribute("trip", tripService.getOwned(id, uid));
        return "trips/form";
    }

    @PostMapping("/{id}")
    public String update(@AuthenticationPrincipal UserDetails principal,
                         @PathVariable Long id,
                         @ModelAttribute Trip trip) {
        Long uid = currentUserId(principal);
        tripService.update(uid, id, trip);
        return "redirect:/trips";
    }

    @GetMapping("/{id}")
    public String detail(@AuthenticationPrincipal UserDetails principal,
                         @PathVariable Long id,
                         Model model) {
        Long uid = currentUserId(principal);
        Trip trip = tripService.getOwned(id, uid);

        String cover = findTripCoverUrl(trip.getId());
        if (cover == null) cover = fallbackCoverForCategory(trip.getCategory());

        model.addAttribute("trip", trip);
        model.addAttribute("tripCoverUrl", cover);

        return "trips/view";
    }

    @PostMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal UserDetails principal,
                         @PathVariable Long id) {
        Long uid = currentUserId(principal);
        tripService.delete(uid, id);
        return "redirect:/trips";
    }

    // ✅ UPLOAD COPERTĂ (POST clasic + redirect)
    @PostMapping("/{id}/cover")
    public String uploadCover(@AuthenticationPrincipal UserDetails principal,
                              @PathVariable Long id,
                              @RequestParam("cover") MultipartFile coverFile,
                              @RequestHeader(value = "Referer", required = false) String referer) throws IOException {
        Long uid = currentUserId(principal);

        // ownership check
        tripService.getOwned(id, uid);

        if (coverFile == null || coverFile.isEmpty()) {
            return (referer != null) ? "redirect:" + referer : "redirect:/trips";
        }

        String ext = extensionOrJpg(coverFile.getOriginalFilename());
        Path folder = tripFolder(id);
        Files.createDirectories(folder);

        deleteExistingTripCovers(folder);

        Path target = folder.resolve("cover." + ext);
        Files.copy(coverFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return (referer != null) ? "redirect:" + referer : "redirect:/trips";
    }
}