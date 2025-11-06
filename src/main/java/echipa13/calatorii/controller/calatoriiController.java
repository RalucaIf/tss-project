package echipa13.calatorii.controller;

import echipa13.calatorii.models.calatorii;
import echipa13.calatorii.Dto.calatoriiDto;
import echipa13.calatorii.service.calatoriiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller


public class calatoriiController {

    private calatoriiService calatoriiService;
@Autowired

    public calatoriiController(calatoriiService calatoriiService) {
        this.calatoriiService = calatoriiService;
    }



//    @GetMapping("/Itravel")
//    public String Itravel( Model model) {
////        List<calatoriiDto> calatorii = new ArrayList<>();
////        calatorii.addAll(calatoriiService.findByEmail(email));
////        calatorii.addAll(calatoriiService.findByName(nume));
////
////        System.out.println(calatorii); // verifică ce ajunge aici
//        List<calatoriiDto> calatorii = calatoriiService.findAll();
//        model.addAttribute("calatorii", calatorii);
//        return "Itravel";
//    }


@GetMapping("/Itravel")
public String Itravel(Model model) {
    List<calatoriiDto> calatorii = calatoriiService.findAll();
    model.addAttribute("calatorii", calatorii);
    return "Itravel-list";
}
}
