package echipa13.calatorii.service.impl;

import echipa13.calatorii.models.calatorii;
import echipa13.calatorii.service.calatoriiService;
import echipa13.calatorii.repository.Calatorii_repository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class calatoriiServiceImpl implements calatoriiService {
    private final Calatorii_repository calatoriiRepository;

    public calatoriiServiceImpl(Calatorii_repository calatoriiRepository) {
        this.calatoriiRepository = calatoriiRepository;
    }

    @Override
    public List<calatorii> findByEmail(String email) {
        // luăm toate călătoriile din baza de date
        List<calatorii> toateCalatoriile = calatoriiRepository.findAll();

        // filtrăm după email
        return toateCalatoriile.stream()
                .filter(c -> c.getEmail() != null && c.getEmail().equalsIgnoreCase(email))
                .collect(Collectors.toList());
    }
}
