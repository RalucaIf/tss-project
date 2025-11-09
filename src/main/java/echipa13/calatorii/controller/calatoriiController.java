package echipa13.calatorii.controller;

import echipa13.calatorii.models.calatorii;
import echipa13.calatorii.Dto.calatoriiDto;
import echipa13.calatorii.service.calatoriiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Controller
public class calatoriiController {

    private calatoriiService calatoriiService;

    @Autowired
    public calatoriiController(calatoriiService calatoriiService) {
        this.calatoriiService = calatoriiService;
    }



    // Listarea călătoriilor
    @GetMapping("/Itravel")
    public String Itravel(Model model) {
        List<calatoriiDto> calatorii = calatoriiService.findAll();
        model.addAttribute("calatorii", calatorii);
        return "Itravel-list";  // numele HTML-ului de listare
    }

    // Formular pentru adăugarea unei noi călătorii
    @GetMapping("/Itravel/new")
    public String ItravelNew(Model model) {
        calatorii c = new calatorii();
        model.addAttribute("calatorii", c);
        return "Itravel-new";
    }

    @PostMapping("/Itravel/new")
    public String saveItravel(@ModelAttribute("calatorii") calatorii c,
                              @RequestParam("imagine") MultipartFile imagine) {
        try {
            if (!imagine.isEmpty()) {
                // folderul "imagine" în directorul proiectului
                String uploadDir = System.getProperty("user.dir") + "/imagine/";
                File folder = new File(uploadDir);
                if (!folder.exists()) folder.mkdirs();

                File file = new File(uploadDir + imagine.getOriginalFilename());
                imagine.transferTo(file);

                c.setImage(imagine.getOriginalFilename());
            }
            calatoriiService.saveCalatorie(c);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/Itravel";
    }


    @GetMapping("/Itravel/{id}")
    public String calatorieDetail(@PathVariable("id") long id, Model model) {
        calatoriiDto calatorieDto = calatoriiService.findCalatorieById(id);
        model.addAttribute("calatorie", calatorieDto);
        return "Itravel-detail";
    }

    @GetMapping("/Itravel/{id}/delete")
    public String deleteItravel(@PathVariable("id") long id) {
        calatoriiService.delete(id);
        return "redirect:/Itravel";
    }
    @GetMapping("/Itravel/search")
        public String searchCalatorie(@RequestParam(value="query") String query,  Model model) {
        List<calatoriiDto> c = calatoriiService.searchCalatorii(query);
        model.addAttribute("calatorii", c);
        return "Itravel-list";
        }
}
