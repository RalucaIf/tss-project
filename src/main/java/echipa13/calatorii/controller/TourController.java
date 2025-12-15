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
    private TripRepository tripRepository;

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


    @GetMapping("/nuEstiGhid")
    public String nuEstiGhid(Model model) {
        return "nuEstiGhid";
    }

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

        return "Itravel-list" ;  // numele HTML-ului de listare
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

    @PostMapping("/tours/add-to-itinerary/{id}")
    public String addToItinerary(@PathVariable Long id, Authentication authentication, @RequestParam Long tripId) {
        String username = authentication.getName();
        System.out.println("Logged in username: " + username);

        UserEntity user = userService.findByUsername(username);
        if (user == null) {
            System.out.println("User not found in DB!");
            return "redirect:/login";
        }

        TourDto tour = tourService.findTourById(id);

        Trip trip = tripRepository.findById(tripId).orElse(null);

        Tour tourEntity = tourService.findEntityById(tour.getId());

        boolean alreadyUsed = tripRepository
                .existsByUserAndExcursii_Id(user, id);

        if (alreadyUsed) {
            return "redirect:/tour/" + id + "?error=alreadyInTrip";
        }
        tourEntity.setTrip(trip);
        trip.getExcursii().add(tourEntity);
        tripRepository.save(trip);

        return "redirect:/trips/" + tripId;

    }
}


//    @GetMapping("/Itravel/search")
//        public String searchByTitle(@RequestParam(value="query") String query,  Model model) {
//        List<TourDto> c = tourService.searchByTitle(query);
//        model.addAttribute("calatorii", c);
//        return "Itravel-list";
//        }