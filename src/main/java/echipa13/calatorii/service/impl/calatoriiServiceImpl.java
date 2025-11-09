package echipa13.calatorii.service.impl;

import echipa13.calatorii.Dto.calatoriiDto;
import echipa13.calatorii.models.calatorii;
import echipa13.calatorii.service.calatoriiService;
import echipa13.calatorii.repository.Calatorii_repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                .map(this::mapTocalatoriiDto) // folosește metoda de mapping
                .collect(Collectors.toList());
    }

    @Override
    public List<calatoriiDto> findAll() {
        return calatoriiRepository.findAll().stream()
                .map(this::mapTocalatoriiDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<calatoriiDto> findByName(String name) {
        return calatoriiRepository.findAll().stream()
                .filter(c -> c.getName() != null && c.getName().equalsIgnoreCase(name))
                .map(this::mapTocalatoriiDto)
                .collect(Collectors.toList());
    }

    public calatorii saveCalatorie(calatorii c) {
        return calatoriiRepository.save(c);
    }

    @Override
    public calatoriiDto findCalatorieById(long id) {
        calatorii c = calatoriiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Calatorie not found with id: " + id));
        return mapTocalatoriiDto(c);
    }

    @Override
    public void delete(long id) {
        calatoriiRepository.deleteById(id);
    }

    @Override
    public List<calatoriiDto> searchCalatorii(String query) {
        List<calatorii> calatoriiList = calatoriiRepository.searchCalatorii(query);
        return calatoriiList.stream()
                .map(this::mapTocalatoriiDto)
                .collect(Collectors.toList());
    }


    private calatoriiDto mapTocalatoriiDto(calatorii c) {
        calatoriiDto dto = new calatoriiDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setLastname(c.getLastname());
        dto.setEmail(c.getEmail());
        dto.setPhone(c.getPhone());
        dto.setTitle(c.getTitle());
        dto.setDescription(c.getDescription());
        dto.setImage(c.getImage());
        return dto;
    }






}
