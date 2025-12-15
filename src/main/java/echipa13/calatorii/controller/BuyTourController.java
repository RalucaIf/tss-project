package echipa13.calatorii.controller;

import echipa13.calatorii.Dto.DestinationsDto;
import echipa13.calatorii.Dto.ItinerariuZiDto;
import echipa13.calatorii.Dto.TourDto;
import echipa13.calatorii.models.*;
import echipa13.calatorii.repository.GuideRepository;
import echipa13.calatorii.repository.TourRepository;
import echipa13.calatorii.repository.UserRepository;
import echipa13.calatorii.service.DestinationsService;
import echipa13.calatorii.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        TourDto tur = tourService.findTourById(id);
        model.addAttribute("calatorie", tur);
        return "tour-details";
    }

    /* ===================== LISTARE TURURI CU PAGINARE ===================== */
    @GetMapping("/Tours")
    public String tours(@RequestParam(defaultValue = "0") int page, Model model) {
        int size = 12;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Tour> pageResult = tourRepository.findAll(pageable);

        List<TourDto> calatorii = pageResult.getContent().stream()
                .map(t -> tourService.findTourById(t.getId()))
                .toList();

        model.addAttribute("calatorii", calatorii);
        model.addAttribute("page", pageResult.getNumber());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        return "tours-list";
    }

    /* ===================== FORMULAR CREATE / EDIT ===================== */
    @GetMapping({"/Itravel/new", "/Itravel/edit/{id}"})
    public String showTourForm(Model model, @PathVariable(required = false) Long id) {
        TourDto dto;
        if (id == null) {
            dto = new TourDto(); // create new
        } else {
            Tour tour = tourRepository.findById(id).orElseThrow(() -> new RuntimeException("Tour not found"));

            // Mapare itinerariu entity -> DTO
            List<ItinerariuZiDto> itinerariuDtoList = tour.getItinerariu().stream()
                    .map(zi -> new ItinerariuZiDto(
                            zi.getZi(),
                            zi.getTitlu(),
                            zi.getLocatie(),
                            zi.getDescriere(),
                            zi.getFeatures()
                    )).collect(Collectors.toList());

            dto = new TourDto(
                    tour.getId(),
                    tour.getTitle(),
                    tour.getPricePoints(),
                    tour.getSummary(),
                    tour.getStatus(),
                    tour.getImage(),
                    tour.getDescription(),
                    tour.getDuration(),
                    tour.getMaxGuests(),
                    tour.getSubtitle(),
                    tour.getCategory(),
                    tour.getLocations(),
                    tour.getHighlights(),
                    itinerariuDtoList
            );

            dto.setDestinationId(tour.getDestination() != null ? tour.getDestination().getId() : null);
        }

        model.addAttribute("calatorii", dto);
        model.addAttribute("destinations", destinationsService.findAllDTOs());
        model.addAttribute("highlights", Highlight.values());
        model.addAttribute("itinerariu", dto.getItinerariu() != null ? dto.getItinerariu() : new ArrayList<>());

        return "Itravel-new";
    }

    /* ===================== POST CREATE / EDIT ===================== */
    @PostMapping({"/Itravel/new", "/Itravel/edit/{id}"})
    public String saveTour(@ModelAttribute("calatorii") TourDto dto,
                           @RequestParam("imagine") MultipartFile imagine) {
        try {
            // 1️⃣ user logat
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String emailOrUsername = auth.getName();

            UserEntity user = userRepository.findByEmail(emailOrUsername);
            if (user == null) user = userRepository.findByUsername(emailOrUsername);

            // 2️⃣ ghid
            Guide guide = guideRepository.findByUser_Id(user.getId()).orElse(null);
            if (guide == null) return "redirect:/nuEstiGhid";

            // 3️⃣ destinație
            Destinations destination = destinationsService.findEntityById(dto.getDestinationId());

            // 4️⃣ CREATE vs EDIT
            Tour tour = dto.getId() != null ?
                    tourRepository.findById(dto.getId()).orElseThrow(() -> new RuntimeException("Tour not found"))
                    : new Tour();

            if (dto.getId() == null) {
                tour.setCreatedAt(LocalDateTime.now());
                tour.setStatus("PUBLISHED");
            }

            // 5️⃣ setăm câmpurile
            tour.setTitle(dto.getTitle());
            tour.setSummary(dto.getSummary());
            tour.setPricePoints(dto.getPricePoints());
            tour.setStatus(dto.getStatus());
            tour.setGuideId(guide.getId());
            tour.setDestination(destination);
            tour.setDescription(dto.getDescription());
            tour.setDuration(dto.getDuration());
            tour.setMaxGuests(dto.getMaxGuests());
            tour.setSubtitle(dto.getSubtitle());
            tour.setCategory(dto.getCategory());
            tour.setLocations(dto.getLocations());
            tour.setHighlights(dto.getHighlights());

            // 6️⃣ mapare itinerariu string -> List<String> și entity
            List<ItinerariuZi> itinerariuEntities = new ArrayList<>();
            if (dto.getItinerariu() != null) {
                itinerariuEntities = dto.getItinerariu().stream().map(dayDto -> {
                    if (dayDto.getFeatures() != null && dayDto.getFeatures().size() == 1) {
                        dayDto.setFeatures(Arrays.stream(dayDto.getFeatures().get(0).split(","))
                                .map(String::trim).toList());
                    }
                    ItinerariuZi zi = new ItinerariuZi();
                    zi.setZi(dayDto.getZi());
                    zi.setTitlu(dayDto.getTitlu());
                    zi.setLocatie(dayDto.getLocatie());
                    zi.setDescriere(dayDto.getDescriere());
                    zi.setFeatures(dayDto.getFeatures());
                    zi.setTour(tour);
                    return zi;
                }).collect(Collectors.toList());
            }

// În loc să înlocuiești lista, curăță și adaugă
            tour.getItinerariu().clear();
            tour.getItinerariu().addAll(itinerariuEntities);


            // 7️⃣ imagine
            if (imagine != null && !imagine.isEmpty()) {
                String uploadDir = new File("src/main/resources/static/uploads/").getAbsolutePath();
                File folder = new File(uploadDir);
                if (!folder.exists()) folder.mkdirs();
                String filename = System.currentTimeMillis() + "_" + imagine.getOriginalFilename();
                imagine.transferTo(new File(folder, filename));
                tour.setImage(filename);
            }

            // 8️⃣ save
            tourRepository.save(tour);
            return "redirect:/Itravel";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/Itravel";
        }
    }
}
