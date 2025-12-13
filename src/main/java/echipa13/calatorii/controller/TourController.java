package echipa13.calatorii.controller;

import echipa13.calatorii.models.*;
import echipa13.calatorii.Dto.TourDto;
import echipa13.calatorii.repository.*;
import echipa13.calatorii.service.GuideService;
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
import echipa13.calatorii.service.UserService;
import jakarta.servlet.http.HttpSession;
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
public class TourController {

    @Autowired
    private TourService tourService;
    private GuideService guideService;
    @Autowired
    private TourPurchaseRepository tourPurchaseRepository;

    @Autowired
    private DestinationsService destinationsService;

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TourRepository tourRepository;


    /* ===================== PAGINI SIMPLE ===================== */
    @Autowired
    UserService userService;

    @Autowired
    UserPointsRepository userPointsRepository;


    @GetMapping("/Itravel")
    public String itravel(Model model) {
        model.addAttribute("calatorii", tourService.findAll());

        return "Itravel-list";
        List<Long> boughtTourIds = tourPurchaseRepository
                .findByBuyer(user)
                .stream()
                .map(tp -> tp.getTour().getId())
                .toList();

        model.addAttribute("boughtTourIds", boughtTourIds);

        return "Itravel-list";  // numele HTML-ului de listare
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

    @GetMapping("/tours/{id}")  // schimbat T mare → mic
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

        System.out.println(c.getImage());

        model.addAttribute("calatorii", dto);
        model.addAttribute("destinations", destinationsService.findAllDTOs());
        return "Itravel-new";
    }

    // POST - adăugare/edita tur
    @PostMapping({"/Itravel/new", "/Itravel/edit/{id}"})
    public String saveTour(@ModelAttribute("calatorii") TourDto dto,
                           @RequestParam("imagine") MultipartFile imagine) {
    public String addTour(@ModelAttribute("tour") Tour formTour,
                          @RequestParam("imagine") MultipartFile imagine) {

        try {
            // 1️⃣ Preluare user logat
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String emailOrUsername = auth.getName();
            UserEntity user = userRepository.findByEmail(emailOrUsername);
            if (user == null) user = userRepository.findByUsername(emailOrUsername);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailOrUsername = auth.getName();

        UserEntity user = userRepository.findByEmail(emailOrUsername);
        if (user == null) {
            user = userRepository.findByUsername(emailOrUsername);
        }

            // 2️⃣ Ghid asociat
            Guide guide = guideRepository.findByUser_Id(user.getId()).orElse(null);
            if (guide == null) return "redirect:/nuEstiGhid";
        Guide guide = guideRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("Nu ești ghid"));

            // 3️⃣ Preluare destinație
            Destinations destination =
                    destinationsService.findEntityById(dto.getDestinationId());
        Tour tour;

        if (formTour.getId() == null) {
            // 🆕 CREATE
            tour = new Tour();
            tour.setGuideId(guide.getId());
            tour.setCreatedAt(LocalDateTime.now());
            tour.setStatus("PUBLISHED");
        } else {
            // ✏️ EDIT
            tour = tourRepository.findById(formTour.getId())
                    .orElseThrow(() -> new RuntimeException("Tour not found"));
        }

            // 4️⃣ Transformare DTO -> Entity
            Tour tour = dto.toEntity(destination);
            tour.setGuideId(guide.getId());
            if (tour.getCreatedAt() == null) tour.setCreatedAt(LocalDateTime.now());
            if (tour.getStatus() == null) tour.setStatus("PUBLISHED");
        // ✅ COPIEM DOAR CE VINE DIN FORM
        tour.setTitle(formTour.getTitle());
        tour.setSummary(formTour.getSummary());
        tour.setPricePoints(formTour.getPricePoints());
        tour.setContinent(formTour.getContinent());

            // 5️⃣ Upload imagine
            if (imagine != null && !imagine.isEmpty()) {
        // 🖼️ imagine DOAR dacă userul a ales una nouă
        if (imagine != null && !imagine.isEmpty()) {
            try {
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

                tour.setImage(filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // ❗ dacă nu vine imagine → NU atingem tour.getImage()

        tourRepository.save(tour);

        return "redirect:/Itravel";
    }

    @PostMapping("/tours/delete/{id}")
    public String deleteTour(@PathVariable Long id) {
        tourService.delete(id);
        return "redirect:/user_profile";
        }

    @PostMapping("/tours/buy/{id}")
    public String buyTours(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        System.out.println("Logged in username: " + username);

        UserEntity user = userService.findByUsername(username);
        if (user == null) {
            System.out.println("User not found in DB!");
            return "redirect:/login";
        }

        TourDto tour = tourService.findTourById(id);
        UserPoints userPoints = userPointsRepository.findByUserId(user.getId()).orElse(null);

        int tourCost = tour.getPricePoints();
        int myPoints = userPoints.getPoints();

        if(myPoints < tourCost){
            return "redirect:/Itravel?error=" + id;
        }

        myPoints = myPoints - tourCost;
        userPoints.setPoints(myPoints);
        userPointsRepository.save(userPoints);

        Tour tourEntity = tourService.findEntityById(tour.getId());

        TourPurchase tourPurchase = new TourPurchase();
        tourPurchase.setBuyer(user);
        tourPurchase.setTour(tourEntity);
        tourPurchase.setPointsPaid(tourCost);
        tourPurchaseRepository.save(tourPurchase);

        return "redirect:/Itravel?success=" + id;
    }
}


//    @GetMapping("/Itravel/search")
//        public String searchByTitle(@RequestParam(value="query") String query,  Model model) {
//        List<TourDto> c = tourService.searchByTitle(query);
//        model.addAttribute("calatorii", c);
//        return "Itravel-list";
//        }

