package echipa13.calatorii.controller;

import echipa13.calatorii.Dto.DestinationsDto;
import echipa13.calatorii.models.Continent;
import echipa13.calatorii.models.Destinations;
import echipa13.calatorii.service.DestinationsService;
import echipa13.calatorii.service.FavoriteDestinationService;
import echipa13.calatorii.service.UserService;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/destinations")
public class DestinationsController {

    private final DestinationsService destinationsService;
    private final FavoriteDestinationService favoriteDestinationService;
    private final UserService userService;

    public DestinationsController(DestinationsService destinationsService,
                                  FavoriteDestinationService favoriteDestinationService,
                                  UserService userService) {
        this.destinationsService = destinationsService;
        this.favoriteDestinationService = favoriteDestinationService;
        this.userService = userService;
    }

    /* ===============================
       FORM CREATE / EDIT
       =============================== */

    @GetMapping({"/new", "/edit/{id}"})
    public String destinationForm(
            @PathVariable(required = false) Long id,
            Model model
    ) {
        DestinationsDto destination = (id == null)
                ? new DestinationsDto()
                : destinationsService.findById(id);

        model.addAttribute("destination", destination);
        model.addAttribute("continents", Continent.values());

        return "destinations-new";
    }

    /* ===============================
       SAVE (CREATE + UPDATE)
       =============================== */

    @PostMapping({"/new", "/edit/{id}"})
    public String saveDestination(
            @PathVariable(required = false) Long id,
            @ModelAttribute("destination") DestinationsDto dto,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            Destinations destination;

            if (id != null) {
                destination = destinationsService.findEntityById(id);
            } else {
                destination = new Destinations();
            }

            destination.setName(dto.getName());
            destination.setDescription(dto.getDescription());
            destination.setContinent(dto.getContinent());

            if (imageFile != null && !imageFile.isEmpty()) {
                String uploadDir = new File("uploads/destinations").getAbsolutePath();
                File folder = new File(uploadDir);
                if (!folder.exists()) folder.mkdirs();

                String filename = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                File file = new File(folder, filename);
                imageFile.transferTo(file);

                destination.setImage("destinations/" + filename);
            }

            destinationsService.save(destination);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/destinations";
    }

    /* ===============================
       DELETE
       =============================== */

    @PostMapping("/delete/{id}")
    public String deleteDestination(@PathVariable Long id) {
        destinationsService.delete(id);
        return "redirect:/destinations";
    }

    /* ===============================
       LIST + PAGINATION (+ FAVORITES)
       =============================== */

    @GetMapping
    public String listDestinations(
            @RequestParam(defaultValue = "0") int page,
            Model model,
            Authentication auth
    ) {
        int size = 12;
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<Destinations> destinationsPage = destinationsService.findAll(pageable);

        List<DestinationsDto> calatorii = destinationsPage
                .stream()
                .map(destinationsService::toDto)
                .toList();

        model.addAttribute("calatorii", calatorii);
        model.addAttribute("page", destinationsPage.getNumber());
        model.addAttribute("totalPages", destinationsPage.getTotalPages());

        // ✅ IMPORTANT pentru pasul 6: trimitem id-urile favoritelor
        Set<Long> favoriteDestinationIds = Collections.emptySet();

        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            Long userId = userService.getCurrentUserId(auth);
            // Asigură-te că metoda există în service:
            // Set<Long> getFavoriteDestinationIds(Long userId)
            favoriteDestinationIds = favoriteDestinationService.getFavoriteDestinationIds(userId);
        }

        model.addAttribute("favoriteDestinationIds", favoriteDestinationIds);

        return "destinations-list";
    }
}
