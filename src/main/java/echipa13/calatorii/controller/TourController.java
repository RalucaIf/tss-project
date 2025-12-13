package echipa13.calatorii.controller;

import echipa13.calatorii.models.*;
import echipa13.calatorii.Dto.TourDto;
import echipa13.calatorii.repository.*;
import echipa13.calatorii.service.GuideService;
import echipa13.calatorii.service.TourService;
import echipa13.calatorii.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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

    public String Destinations(Model model) {
        List<TourDto> calatorii = tourService.findAll();
        model.addAttribute("calatorii", calatorii);

        // preluare user logat
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("user", username);
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
            // preluare user logat
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String emailOrUsername = auth.getName();

            // găsim userul
            UserEntity user = userRepository.findByEmail(emailOrUsername);
            if (user == null) {
                user = userRepository.findByUsername(emailOrUsername);
            }
            System.out.println(user.getId());

            // căutăm ghidul asociat
            Guide guide = guideRepository.findByUser_Id(user.getId()).orElse(null);
            if (guide == null) {
                return "redirect:/nuEstiGhid";
            }
            System.out.println(guide.getId());

            // setăm guideId din tabela guides
            if(c.getGuideId()==null)
            {
                c.setGuideId(guide.getId());
                c.setStatus("PUBLISHED");
                c.setCreatedAt(LocalDateTime.now());
            }else {
                Tour existingTour = tourRepository.findById(c.getId()).orElse(null);
                if (existingTour != null) {
                    c.setGuideId(existingTour.getGuideId());
                    c.setCreatedAt(existingTour.getCreatedAt());
                    c.setStatus(existingTour.getStatus());

                    if (imagine == null || imagine.isEmpty()) {
                        c.setImage(existingTour.getImage());
                    }
                }
            }

            if (imagine !=null &&!imagine.isEmpty()) {
                String uploadDir = System.getProperty("user.dir") + "/imagine/";
                File folder = new File(uploadDir);
                if (!folder.exists()) folder.mkdirs();

                File file = new File(uploadDir + imagine.getOriginalFilename());
                imagine.transferTo(file);

                c.setImage(imagine.getOriginalFilename());
            }

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

