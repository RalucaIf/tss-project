package echipa13.calatorii.controller;

import echipa13.calatorii.models.Trip;
import echipa13.calatorii.models.TripItem;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.repository.TripItemRepository;
import echipa13.calatorii.repository.UserRepository;
import echipa13.calatorii.service.TripService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        if (u == null) u = userRepo.findByEmail(login);
        if (u == null) throw new IllegalStateException("Utilizator inexistent: " + login);
        return u.getId();
    }

    // ---- LISTĂ JURNAL
    @GetMapping
    public String journalHome(@AuthenticationPrincipal UserDetails principal, Model model) {
        Long uid = currentUserId(principal);
        model.addAttribute("trips", tripService.listForUser(uid));
        return "journal/list";
    }

    // ---- DETALIU JURNAL (cu poze + cover)
    @GetMapping("/{id}")
    public String journalDetail(@AuthenticationPrincipal UserDetails principal,
                                @PathVariable Long id,
                                @RequestParam(value = "openDay", required = false) Integer openDay,
                                Model model) {
        Long uid = currentUserId(principal);
        Trip trip = tripService.getOwned(id, uid);

        List<TripItem> items = tripItemRepository.findByTrip_IdOrderByDayIndexAscIdAsc(id);
        Map<Integer, List<TripItem>> days = new LinkedHashMap<>();
        for (TripItem it : items) {
            int day = (it.getDayIndex() != null) ? it.getDayIndex() : 1;
            days.computeIfAbsent(day, k -> new ArrayList<>()).add(it);
        }

        // Poze existente pe fiecare zi
        Map<Integer, List<String>> photosByDay = new HashMap<>();
        Map<Integer, String> coverByDay = new HashMap<>();
        for (Integer d : days.keySet()) {
            photosByDay.put(d, listPhotoUrls(id, d));
            coverByDay.put(d, findCoverUrl(id, d));
        }

        model.addAttribute("trip", trip);
        model.addAttribute("days", days);
        model.addAttribute("photosByDay", photosByDay);
        model.addAttribute("coverByDay", coverByDay);
        model.addAttribute("openDay", openDay);
        return "journal/detail";
    }

    // ---- Adaugă zi (există deja, neschimbat)
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
        it.setCategory(trip.getCategory() != null ? trip.getCategory() : "GENERAL");

        tripItemRepository.save(it);
        return "redirect:/journal/" + id;
    }

    // ---- Salvează notițe pe zi (există deja)
    @PostMapping("/{id}/days/{day}/notes")
    @Transactional
    public String saveDayNotes(@AuthenticationPrincipal UserDetails principal,
                               @PathVariable Long id,
                               @PathVariable Integer day,
                               @RequestParam(name = "note", required = false) String note) {
        Long uid = currentUserId(principal);
        Trip trip = tripService.getOwned(id, uid);

        if (day == null || day < 1) return "redirect:/journal/" + id;

        List<TripItem> items = tripItemRepository.findByTrip_IdOrderByDayIndexAscIdAsc(id);
        TripItem target = null;
        for (TripItem it : items) {
            if (it.getDayIndex() != null && it.getDayIndex().intValue() == day.intValue()) {
                target = it; break;
            }
        }
        if (target == null) {
            target = new TripItem();
            target.setTrip(trip);
            target.setDayIndex(day);
            target.setTitle("Plan ziua " + day);
            target.setCategory(trip.getCategory() != null ? trip.getCategory() : "GENERAL");
        }
        target.setNotes(note == null ? "" : note.trim());
        tripItemRepository.save(target);

        return "redirect:/journal/" + id + "#day-" + day;
    }

    // ---- Upload poze (există deja)
    @PostMapping("/{id}/photos")
    @Transactional
    public String uploadPhotos(@AuthenticationPrincipal UserDetails principal,
                               @PathVariable Long id,
                               @RequestParam("day") Integer day,
                               @RequestParam("files") MultipartFile[] files) {

        Long uid = currentUserId(principal);
        tripService.getOwned(id, uid);

        if (day == null || day < 1 || files == null || files.length == 0) {
            return "redirect:/journal/" + id + "#day-" + (day!=null?day:1);
        }

        Path root = dayFolder(id, day);
        try {
            Files.createDirectories(root);
            for (MultipartFile mf : files) {
                if (mf == null || mf.isEmpty()) continue;
                String safe = sanitizeFilename(mf.getOriginalFilename());
                if (safe.isBlank()) safe = "photo-" + System.currentTimeMillis() + ".bin";
                Path target = root.resolve(safe).normalize();
                Files.copy(mf.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                log.info("Salvat: {}", target.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Eroare la salvarea pozelor pentru trip {} zi {}: {}", id, day, e.getMessage(), e);
        }

        return "redirect:/journal/" + id + "?openDay=" + day + "#day-" + day;
    }
    @PostMapping("/{id}/photos/delete")
    @Transactional
    public String deletePhoto(@AuthenticationPrincipal UserDetails principal,
                              @PathVariable Long id,
                              @RequestParam("day") Integer day,
                              @RequestParam("name") String name) {
        Long uid = currentUserId(principal);
        tripService.getOwned(id, uid);

        if (day == null || day < 1 || name == null || name.isBlank()) {
            return "redirect:/journal/" + id + "#day-" + (day != null ? day : 1);
        }

        Path folder = Paths.get("uploads").resolve("trips")
                .resolve(String.valueOf(id)).resolve("day-" + day);
        Path file = folder.resolve(name).normalize();

        try {
            Files.deleteIfExists(file);
            // dacă ștergi cover-ul curent, nu mai e nimic de făcut (cover va dispărea dacă nu mai există cover.*)
            if (name.startsWith("cover.")) {
                // nimic special
            }
        } catch (IOException e) {
            log.warn("Nu am putut șterge {}: {}", file, e.getMessage());
        }

        return "redirect:/journal/" + id + "?openDay=" + day + "#day-" + day;
    }

    // ---- NEW: setează "cover photo" pentru o zi
    @PostMapping("/{id}/photos/cover")
    @Transactional
    public String setCover(@AuthenticationPrincipal UserDetails principal,
                           @PathVariable Long id,
                           @RequestParam("day") Integer day,
                           @RequestParam("name") String name) {
        Long uid = currentUserId(principal);
        tripService.getOwned(id, uid);

        if (day == null || day < 1 || name == null || name.isBlank()) {
            return "redirect:/journal/" + id + "#day-" + (day!=null?day:1);
        }

        Path folder = dayFolder(id, day);
        Path src = folder.resolve(name).normalize();
        if (!Files.exists(src)) {
            log.warn("Set cover: fisierul nu exista: {}", src);
            return "redirect:/journal/" + id + "#day-" + day;
        }

        String ext = extensionOf(name).orElse("jpg");
        try {
            // Stergem eventualele cover.* existente
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(folder, "cover.*")) {
                for (Path p : ds) {
                    try { Files.deleteIfExists(p); } catch (Exception ignore) {}
                }
            }
            Path dest = folder.resolve("cover." + ext);
            Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
            log.info("Set cover: {}", dest.toAbsolutePath());
        } catch (IOException e) {
            log.error("Eroare set cover pt trip {} zi {}: {}", id, day, e.getMessage(), e);
        }

        // Nu redeschidem modalul; ramanem pe zi
        return "redirect:/journal/" + id + "#day-" + day;
    }

    // ---- Helpers

    private Path dayFolder(Long tripId, Integer day){
        return Paths.get("uploads").resolve("trips")
                .resolve(String.valueOf(tripId)).resolve("day-" + day);
    }

    private List<String> listPhotoUrls(Long tripId, Integer day){
        Path folder = dayFolder(tripId, day);
        List<String> urls = new ArrayList<>();
        if (!Files.exists(folder)) return urls;
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(folder)) {
            for (Path p : ds) {
                String fn = p.getFileName().toString();
                if (fn.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|webp|bmp)$") && !fn.startsWith("cover.")) {
                    urls.add("/uploads/trips/" + tripId + "/day-" + day + "/" + fn);
                }
            }
        } catch (IOException ignore) {}
        return urls;
    }

    private String findCoverUrl(Long tripId, Integer day){
        Path folder = dayFolder(tripId, day);
        if (!Files.exists(folder)) return null;
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(folder, "cover.*")) {
            for (Path p : ds) {
                return "/uploads/trips/" + tripId + "/day-" + day + "/" + p.getFileName().toString();
            }
        } catch (IOException ignore) {}
        return null;
    }

    private String sanitizeFilename(String name) {
        if (name == null) return "";
        String cleaned = name.replaceAll("[^\\p{L}\\p{N}._\\- ]+", "_").trim();
        if (cleaned.isBlank() || cleaned.equals(".") || cleaned.equals("..")) return "";
        return cleaned;
    }

    private Optional<String> extensionOf(String name){
        if (name == null) return Optional.empty();
        int i = name.lastIndexOf('.');
        if (i < 0 || i == name.length()-1) return Optional.empty();
        return Optional.of(name.substring(i+1).toLowerCase());
    }
}
