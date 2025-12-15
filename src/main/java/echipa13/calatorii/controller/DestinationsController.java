package echipa13.calatorii.controller;

import echipa13.calatorii.Dto.DestinationsDto;
import echipa13.calatorii.models.Continent;
import echipa13.calatorii.models.Destinations;
import echipa13.calatorii.service.DestinationsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/destinations") // 🔹 lowercase pentru URL
public class DestinationsController {

    private final DestinationsService destinationsService;

    public DestinationsController(DestinationsService destinationsService) {
        this.destinationsService = destinationsService;
    }

    // 🔹 Formular creare / editare destinație
    @GetMapping({"/new", "/edit/{id}"})
    public String newDestination(Model model, @PathVariable(required = false) Long id) {
        DestinationsDto destinationDto = (id == null)
                ? new DestinationsDto()
                : destinationsService.findById(id);

        model.addAttribute("destination", destinationDto);
        model.addAttribute("continents", Continent.values());
        return "destinations-new";
    }

    @PostMapping({"/new", "/edit/{id}"})
    public String saveDestination(@ModelAttribute("destination") DestinationsDto dto,
                                  @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            Destinations destination = dto.toEntity();

            // upload imagine
            if (imageFile != null && !imageFile.isEmpty()) {
                String uploadDir = new File("src/main/resources/static/uploads/").getAbsolutePath();
                File folder = new File(uploadDir);
                if (!folder.exists()) folder.mkdirs();

                String filename = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                File file = new File(folder, filename);
                imageFile.transferTo(file);

                destination.setImage(filename);
            }

            // salvează destinația împreună cu tururile
            destinationsService.save(destination);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/destinations";
    }


    // 🔹 Ștergere destinație
    @PostMapping("/delete/{id}")
    public String deleteDestination(@PathVariable Long id) {
        destinationsService.delete(id);
        return "redirect:/destinations";
    }

    // 🔹 Listare destinații cu paginare
    @GetMapping
    public String listDestinations(@RequestParam(defaultValue = "0") int page, Model model) {
        int size = 12;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Destinations> destinationsPage = destinationsService.findAll(pageable);

        List<DestinationsDto> calatorii = destinationsPage.stream().map(d -> {
            DestinationsDto dto = destinationsService.toDto(d);
            if (dto.getTours() == null) {
                dto.setTours(List.of()); // prevenim null pointer
            }
            return dto;
        }).toList();

        model.addAttribute("calatorii", calatorii);
        model.addAttribute("page", destinationsPage.getNumber());
        model.addAttribute("totalPages", destinationsPage.getTotalPages());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("user", auth.getName());

        return "destinations-list";
    }
}
