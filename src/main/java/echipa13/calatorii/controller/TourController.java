package echipa13.calatorii.controller;

import echipa13.calatorii.models.Tour;
import echipa13.calatorii.Dto.TourDto;
import echipa13.calatorii.service.TourService;
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
    public TourController(TourService tourService) {
        this.tourService = tourService;
    }




    // Listarea călătoriilor
    @GetMapping("/Itravel")
    public String Itravel(Model model) {
        List<TourDto> calatorii = tourService.findAll();
        model.addAttribute("calatorii", calatorii);

        // preluare user logat
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("user", username);
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


    @GetMapping("/Error/404")

    public String Error404(Model model) {
        List<TourDto> calatorii = tourService.findAll();
        model.addAttribute("calatorii", calatorii);


        return "404-list";
    }
    // Formular pentru adăugarea unei noi călătorii
    @GetMapping("/Itravel/new")
    public String ItravelNew(Model model) {
        Tour c = new Tour();
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

    @PostMapping("/Itravel/new")
    public String addTour(@ModelAttribute("tour") Tour c,
                          @RequestParam("imagine") MultipartFile imagine,
                          HttpSession session) {

        try {
            Long guideId = (Long) session.getAttribute("guideId");
            if (guideId == null) {
                return "redirect:/nuEstiGhid";
            }
            c.setGuideId(guideId);
            c.setStatus("ACTIVE");
            c.setCreatedAt(LocalDateTime.now());

            if (!imagine.isEmpty()) {
                // folderul "imagine" în directorul proiectului
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


    @GetMapping("/Itravel/{id}")
    public String calatorieDetail(@PathVariable("id") long id, Model model) {
        TourDto calatorieDto = tourService.findTourById(id);
        model.addAttribute("calatorie", calatorieDto);

        // preluare user logat
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("user", username);
        return "Itravel-detail";
    }

    @GetMapping("/Itravel/{id}/delete")
    public String deleteItravel(@PathVariable("id") long id) {

        tourService.delete(id);
        return "redirect:/Itravel";
    }
//    @GetMapping("/Itravel/search")
//        public String searchByTitle(@RequestParam(value="query") String query,  Model model) {
//        List<TourDto> c = tourService.searchByTitle(query);
//        model.addAttribute("calatorii", c);
//        return "Itravel-list";
//        }
}
