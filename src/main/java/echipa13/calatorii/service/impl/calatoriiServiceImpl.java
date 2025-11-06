package echipa13.calatorii.service.impl;

import echipa13.calatorii.Dto.calatoriiDto;
import echipa13.calatorii.models.calatorii;
import echipa13.calatorii.service.calatoriiService;
import echipa13.calatorii.repository.Calatorii_repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Service
//aici vrem sa face implementarile de servicii, de ex daca vrem ca
// aplicatia noastra sa stie sa caute perosanele dupa nume aici facem implementarea
//in cazul de mai jos am facut implementarea sa poti cauta dupa e-mail
public class calatoriiServiceImpl implements calatoriiService {
    private final Calatorii_repository calatoriiRepository;

    @Autowired
    public calatoriiServiceImpl(Calatorii_repository calatoriiRepository) {
        this.calatoriiRepository = calatoriiRepository;
    }

    @Override
    public List<calatoriiDto> findByEmail(String email) {
        return calatoriiRepository.findAll().stream()
                .filter(c -> c.getEmail() != null && c.getEmail().equalsIgnoreCase(email))
                .map(c -> new calatoriiDto(
                        c.getName(),
                        c.getLastname(),
                        c.getEmail(),
                        c.getPhone(),
                        c.getDescription(),
                        c.getImage(),
                        c.getTitle()
                ))
                .collect(Collectors.toList());
    }

    public List<calatoriiDto> findAll() {
        return calatoriiRepository.findAll().stream()
                .map(c -> new calatoriiDto(
                        c.getName(),
                        c.getLastname(),
                        c.getEmail(),
                        c.getPhone(),
                        c.getDescription(),
                        c.getImage(),
                        c.getTitle()
                ))
                .collect(Collectors.toList());
    }



    public List<calatoriiDto> findByName(String name) {

        // filtrăm după nume
        return calatoriiRepository.findAll().stream()
                .filter(c -> c.getName() != null && c.getName().equalsIgnoreCase(name))
                .map(c -> new calatoriiDto(
                        c.getName(),
                        c.getLastname(),
                        c.getEmail(),
                        c.getPhone(),
                        c.getDescription(),
                        c.getImage(),
                        c.getTitle()
                ))
                .collect(Collectors.toList());
    }



}
