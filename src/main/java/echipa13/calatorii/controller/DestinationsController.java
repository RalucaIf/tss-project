package echipa13.calatorii.controller;

import echipa13.calatorii.Dto.DestinationsDto;
import echipa13.calatorii.models.Continent;
import echipa13.calatorii.models.Destinations;
import echipa13.calatorii.service.DestinationsService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/destinations") // ✅ LOWERCASE
public class DestinationsController {

    private final DestinationsService destinationsService;

    public DestinationsController(DestinationsService destinationsService) {
        this.destinationsService = destinationsService;
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
                // 🔁 UPDATE – luăm entity-ul existent
                destination = destinationsService.findEntityById(id);
            } else {
                // ➕ CREATE
                destination = new Destinations();
            }

            // update câmpuri
            destination.setName(dto.getName());
            destination.setDescription(dto.getDescription());
            destination.setContinent(dto.getContinent());

            // 📸 doar dacă există imagine nouă
            if (imageFile != null && !imageFile.isEmpty()) {
                String uploadDir = new File("src/main/resources/static/uploads").getAbsolutePath();
                File folder = new File(uploadDir);
                if (!folder.exists()) folder.mkdirs();

                String filename = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                File file = new File(folder, filename);
                imageFile.transferTo(file);

                destination.setImage(filename);
            }
            // 🔥 dacă NU e imagine → nu atingi destination.image

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
       LIST + PAGINATION
       =============================== */

    @GetMapping
    public String listDestinations(
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        int size = 12;
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<Destinations> destinationsPage =
                destinationsService.findAll(pageable);

        List<DestinationsDto> calatorii = destinationsPage
                .stream()
                .map(destinationsService::toDto)
                .toList();

        model.addAttribute("calatorii", calatorii);
        model.addAttribute("page", destinationsPage.getNumber());
        model.addAttribute("totalPages", destinationsPage.getTotalPages());

        return "destinations-list";
    }
}
