package echipa13.calatorii.controller;

import echipa13.calatorii.Dto.DestinationsDto;
import echipa13.calatorii.Dto.TourDto;
import echipa13.calatorii.models.Destinations;
import echipa13.calatorii.models.Guide;
import echipa13.calatorii.models.Tour;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.repository.GuideRepository;
import echipa13.calatorii.repository.TourRepository;
import echipa13.calatorii.repository.UserRepository;
import echipa13.calatorii.service.DestinationsService;
import echipa13.calatorii.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class BuyTourController {

    @Autowired
    private TourService tourService;

    @Autowired
    private DestinationsService destinationsService;

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TourRepository tourRepository;


    /* ===================== PAGINI SIMPLE ===================== */

    @GetMapping("/Itravel")
    public String itravel(Model model) {
        model.addAttribute("calatorii", tourService.findAll());

        return "Itravel-list";
    }

    @GetMapping("/About")
    public String about(Model model) {
        model.addAttribute("calatorii", tourService.findAll());

        return "About-list";
    }

    @GetMapping("/Privacy")
    public String privacy(Model model) {
        model.addAttribute("calatorii", tourService.findAll());

        return "privacy";
    }

    @GetMapping("/Terms")
    public String terms(Model model) {
        model.addAttribute("calatorii", tourService.findAll());

        return "terms-list";
    }

    @GetMapping("/Contact")
    public String contact(Model model) {
        model.addAttribute("calatorii", tourService.findAll());

        return "contact-list";

    }

    @GetMapping("/Itravel/{id}")
    public String showDestinationDetail(@PathVariable Long id, Model model) {
        DestinationsDto calatorie = destinationsService.findDtoById(id);
        model.addAttribute("calatorie", calatorie);
        return "Itravel-detail";
    }

    @GetMapping("/tours/{id}")
    public String showTourDetail(@PathVariable Long id, Model model) {
        // Preluăm turul după ID
        TourDto tur = tourService.findTourById(id);

        System.out.println(tur);


        // Adăugăm în model sub același nume ca la destinație (opțional, poți folosi "tur")
        model.addAttribute("calatorie", tur);

        // Returnăm pagina Thymeleaf pentru detalii tur
        return "tour-details";
    }



    /* ===================== LISTARE TURURI CU PAGINARE ===================== */

    @GetMapping("/Tours")
    public String tours(@RequestParam(defaultValue = "0") int page, Model model) {

        int size = 12;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Tour> pageResult = tourRepository.findAll(pageable);

        List<TourDto> calatorii = pageResult.getContent().stream()
                .map(t -> tourService.findTourById(t.getId())) // ✅ aici e fix-ul
                .toList();

        model.addAttribute("calatorii", calatorii);
        model.addAttribute("page", pageResult.getNumber());
        model.addAttribute("totalPages", pageResult.getTotalPages());

        return "tours-list";
    }


    // Formular pentru adăugarea/edita tur
    @GetMapping({"/Itravel/new", "/Itravel/edit/{id}"})
    public String showTourForm(Model model, @PathVariable(required = false) Long id) {
        TourDto dto;
        if (id == null) {
            dto = new TourDto();
        } else {
            Tour tour = tourRepository.findById(id).orElseThrow(() -> new RuntimeException("Tour not found"));
            dto = new TourDto(tour.getId(), tour.getTitle(), tour.getPricePoints());
            dto.setDestinationId(tour.getDestination() != null ? tour.getDestination().getId() : null);
        }

        model.addAttribute("calatorii", dto);
        model.addAttribute("destinations", destinationsService.findAllDTOs());
        return "Itravel-new";
    }

    // POST - adăugare/edita tur
    @PostMapping({"/Itravel/new", "/Itravel/edit/{id}"})
    public String saveTour(@ModelAttribute("calatorii") TourDto dto,
                           @RequestParam("imagine") MultipartFile imagine) {

        try {
            // 1️⃣ Preluare user logat
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String emailOrUsername = auth.getName();
            UserEntity user = userRepository.findByEmail(emailOrUsername);
            if (user == null) user = userRepository.findByUsername(emailOrUsername);

            // 2️⃣ Ghid asociat
            Guide guide = guideRepository.findByUser_Id(user.getId()).orElse(null);
            if (guide == null) return "redirect:/nuEstiGhid";

            // 3️⃣ Preluare destinație
            Destinations destination =
                    destinationsService.findEntityById(dto.getDestinationId());


            // 4️⃣ Transformare DTO -> Entity
            Tour tour = dto.toEntity(destination);
            tour.setGuideId(guide.getId());
            if (tour.getCreatedAt() == null) tour.setCreatedAt(LocalDateTime.now());
            if (tour.getStatus() == null) tour.setStatus("PUBLISHED");

            // 5️⃣ Upload imagine
            if (imagine != null && !imagine.isEmpty()) {
                String uploadDir = new File("src/main/resources/static/uploads/").getAbsolutePath();
                File folder = new File(uploadDir);
                if (!folder.exists()) folder.mkdirs();
                String filename = System.currentTimeMillis() + "_" + imagine.getOriginalFilename();
                File file = new File(folder, filename);
                imagine.transferTo(file);
                tour.setImage(filename);
            }

            // 6️⃣ Salvare tur
            tourService.saveTour(tour);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/Itravel";
    }
}
