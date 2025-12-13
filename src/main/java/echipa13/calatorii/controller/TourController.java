package echipa13.calatorii.controller;

import echipa13.calatorii.models.*;
import echipa13.calatorii.Dto.TourDto;
import echipa13.calatorii.repository.*;
import echipa13.calatorii.service.GuideService;
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
import java.util.Comparator;
import java.util.List;

@Controller

public class TourController {

    private final TourService tourService;
    @Autowired
    private GuideService guideService;
    @Autowired
    private TourPurchaseRepository tourPurchaseRepository;

    @Autowired
    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/test500")
    public String test500() {
        throw new RuntimeException("Test 500");
    }

    @Autowired
    GuideRepository guideRepository;

    @Autowired
    TourRepository tourRepository;

    @Autowired
    UserService userService;

    @Autowired
    UserPointsRepository userPointsRepository;


    @GetMapping("/Itravel")
    public String Itravel(Model model) {
        List<TourDto> calatorii = tourService.findAll();
        model.addAttribute("calatorii", calatorii);

        // preluare user logat
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailOrUsername = auth.getName();

        UserEntity user = userRepository.findByEmail(emailOrUsername);
        if (user == null) {
            user = userRepository.findByUsername(emailOrUsername);
        }
        model.addAttribute("user", user);

        List<Long> boughtTourIds = tourPurchaseRepository
                .findByBuyer(user)
                .stream()
                .map(tp -> tp.getTour().getId())
                .toList();

        model.addAttribute("boughtTourIds", boughtTourIds);

        return "Itravel-list";  // numele HTML-ului de listare
    }


    @GetMapping("/About")

    public String About(Model model) {
        List<TourDto> calatorii = tourService.findAll();
        model.addAttribute("calatorii", calatorii);

        // preluare user logat
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("user", username);
        return "About-list";
    }

    @GetMapping("/Privacy")

    public String Privacy(Model model) {
        List<TourDto> calatorii = tourService.findAll();
        model.addAttribute("calatorii", calatorii);

        // preluare user logat
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("user", username);
        return "privacy";
    }

    @GetMapping("/Terms")

    public String Terms(Model model) {
        List<TourDto> calatorii = tourService.findAll();
        model.addAttribute("calatorii", calatorii);

        // preluare user logat
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("user", username);
        return "terms-list";
    }

    @GetMapping("/Destinations")
    public String Destinations(@RequestParam(defaultValue = "0") int page, Model model) {
        int size = 12;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Tour> calatoriiPage = tourRepository.findAll(pageable);

        List<TourDto> calatorii = calatoriiPage.stream().map(t -> {
            TourDto dto = new TourDto();
            dto.setId(t.getId());
            dto.setTitle(t.getTitle());
            dto.setSummary(t.getSummary());
            dto.setPricePoints(t.getPricePoints());
            dto.setCreatedAt(t.getCreatedAt());
            dto.setImage(t.getImage());
            dto.setContinent(t.getContinent());
            return dto;
        }).toList();

        model.addAttribute("calatorii", calatorii);
        model.addAttribute("page", calatoriiPage.getNumber());
        model.addAttribute("totalPages", calatoriiPage.getTotalPages());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("user", auth.getName());

        return "destinations-list";
    }





    @GetMapping("/Contact")

    public String Contact(Model model) {
        List<TourDto> calatorii = tourService.findAll();
        model.addAttribute("calatorii", calatorii);
        // preluare user logat
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("user", username);
        return "contact-list";
    }

    @GetMapping("/Tours")

    public String Tours(Model model) {
        List<TourDto> calatorii = tourService.findAll();
        model.addAttribute("calatorii", calatorii);
        // preluare user logat
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("user", username);
        return "tours-list";
    }

    @GetMapping("/Tour_details")

    public String Tour_details(Model model) {
        List<TourDto> calatorii = tourService.findAll();
        model.addAttribute("calatorii", calatorii);

        // preluare user logat
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("user", username);
        return "tour-details";
    }

    // Formular pentru adăugarea unei noi călătorii
    @GetMapping({"/Itravel/new", "/Itravel/edit/{id}"})
    public String ItravelNew(Model model, @PathVariable(required = false ) Long id) {
        Tour c;
        if (id == null) {
            c = new Tour();
        }
        else {
            c = tourRepository.findById(id).orElse(null);
        }
        model.addAttribute("calatorii", c);

        return "Itravel-new";
    }


    @GetMapping("/nuEstiGhid")
    public String nuEstiGhid(Model model) {
        return "nuEstiGhid";
    }

//    @PostMapping("/Itravel/new")
//    public String saveItravel(@ModelAttribute("calatorii") Tour c,
//                              @RequestParam("imagine") MultipartFile imagine) {
//        try {
//            if (!imagine.isEmpty()) {
//                // folderul "imagine" în directorul proiectului
//                String uploadDir = System.getProperty("user.dir") + "/imagine/";
//                File folder = new File(uploadDir);
//                if (!folder.exists()) folder.mkdirs();
//
//                File file = new File(uploadDir + imagine.getOriginalFilename());
//                imagine.transferTo(file);
//
//                c.setImage(imagine.getOriginalFilename());
//            }
//            tourService.saveTour(c);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "redirect:/Itravel";
//    }

    @PostMapping({"/Itravel/new", "/Itravel/edit/{id}"})
    public String addTour(@ModelAttribute("tour") Tour c,
                          @RequestParam("imagine") MultipartFile imagine) {

        try {
            // 1️⃣ Preluare user logat
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String emailOrUsername = auth.getName();

            UserEntity user = userRepository.findByEmail(emailOrUsername);
            if (user == null) {
                user = userRepository.findByUsername(emailOrUsername);
            }

            // 2️⃣ Preluare ghid asociat userului
            Guide guide = guideRepository.findByUser_Id(user.getId()).orElse(null);
            if (guide == null) {
                return "redirect:/nuEstiGhid";
            }

            // 3️⃣ Setări pentru tur nou sau editat
            if (c.getGuideId() == null) { // tur nou
                c.setGuideId(guide.getId());
                c.setStatus("PUBLISHED");
                c.setCreatedAt(LocalDateTime.now());
            } else { // edit tur existent
                Tour existingTour = tourRepository.findById(c.getId()).orElse(null);
                if (existingTour != null) {
                    c.setGuideId(existingTour.getGuideId());
                    c.setCreatedAt(existingTour.getCreatedAt());
                    c.setStatus(existingTour.getStatus());

                    // păstrează imaginea existentă dacă nu se încarcă alta
                    if (imagine == null || imagine.isEmpty()) {
                        c.setImage(existingTour.getImage());
                    }

                    // păstrează continentul existent dacă nu se schimbă
                    if (c.getContinent() == null) {
                        c.setContinent(existingTour.getContinent());
                    }
                }
            }

            // 4️⃣ Upload imagine dacă există
            if (imagine != null && !imagine.isEmpty()) {
                String uploadDir = new File("src/main/resources/static/uploads/").getAbsolutePath();
                File folder = new File(uploadDir);
                if (!folder.exists()) folder.mkdirs();

                String filename = System.currentTimeMillis() + "_" + imagine.getOriginalFilename();
                File file = new File(folder, filename);
                imagine.transferTo(file);

                c.setImage(filename);
            }

            // ✅ 5️⃣ Salvează turul cu tot, inclusiv continent
            tourService.saveTour(c);

        } catch (IOException e) {
            e.printStackTrace();
        }

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

