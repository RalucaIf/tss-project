package echipa13.calatorii.controller;

import echipa13.calatorii.Dto.PointsDto;
import echipa13.calatorii.models.*;
import echipa13.calatorii.repository.*;
import echipa13.calatorii.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.Transient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static java.util.Map.entry;

@RestController
@RequestMapping("/api/map")


public class MapController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserPointsRepository userPointsRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserPointsRepository userPointsService;
    @Autowired
    private TourPurchaseRepository tourPurchaseRepository;
    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private DestinationsRepository destinationsRepository;
    @Autowired
    private VisitedCountryRepository visitedCountryRepository;

    @PostMapping("/visit")
    public ResponseEntity<String> markCountryVisited(@RequestParam String country, Authentication auth) {
       // System.out.println("Utilizatorul a vizitat: " + country);

        // Aici vei face logica de salvare:
        // 1. Iei utilizatorul logat
        // 2. Salvezi în baza de date: user_id și country

        String username = auth.getName();

        UserEntity user = userService.findByUsername(username);
        UserPoints userPoints = userPointsService.findByUser_Id(user.getId()).orElse(null);
        int myPoints = userPoints.getPoints();
        Map<String, String> roToEn = Map.ofEntries(
                entry("Japonia", "Japan"),
                entry("Franta", "France"),
                entry("Italia", "Italy"),
                entry("Norvegia", "Norway"),
                entry("India", "India"),
                entry("Ungaria", "Hungary"),
                entry("Spania", "Spain"),
                entry("Brazilia", "Brazil"),
                entry("SUA", "United States"),
                entry("Africa de Sud", "South Africa"),
                entry("Maroc", "Morocco"),
                entry("Croatia", "Croatia"),
                entry("Canada", "Canada"),
                entry("Australia", "Australia"),
                entry("Egipt", "Egypt"),
                entry("Grecia", "Greece"),
                entry("Bulgaria", "Bulgaria")
        );

        boolean hasExcursion = false;

        if(visitedCountryRepository.findByUserAndCountry(user, country).isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Țara a fost deja vizitată!");
        }

        List<TourPurchase> purchases = tourPurchaseRepository.findByBuyer(user);
        for (TourPurchase tourPurchase : purchases) {
            Tour tour = tourRepository.findById(tourPurchase.getTour().getId()).orElse(null);
            Destinations destinations = destinationsRepository.findById(tour.getDestination().getId()).orElse(null);
            String countryFromDb = destinations.getName(); // ex: "Franta"
            String countryEng = roToEn.getOrDefault(countryFromDb.trim(), countryFromDb);

            if(countryEng.equalsIgnoreCase(country))
            {
                hasExcursion = true;
                myPoints += 5;
                userPoints.setPoints(myPoints);
                userPointsRepository.save(userPoints);

                VisitedCountry vc = new VisitedCountry();
                vc.setCountry(country);
                vc.setUser(user);
                visitedCountryRepository.save(vc);

                break;
            }
        }

        if(hasExcursion){
            return ResponseEntity.ok("Vizită înregistrată!");
        }else{
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Nu ai excursie în această țară!");
        }
    }

    @GetMapping("/visited")
    public ResponseEntity<List<String>> getVisitedCountries(Authentication auth) {
        UserEntity user = userService.findByUsername(auth.getName());
        List<String> countries = visitedCountryRepository.findByUser(user)
                .stream()
                .map(VisitedCountry::getCountry)
                .toList();
        return ResponseEntity.ok(countries);
    }
}
