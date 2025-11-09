package echipa13.calatorii.service;

import java.util.List;
import echipa13.calatorii.models.calatorii;
import echipa13.calatorii.models.calatorii;
import echipa13.calatorii.Dto.calatoriiDto;
import org.springframework.stereotype.Service;

@Service
public interface calatoriiService {
 List<calatoriiDto> findByEmail(String email);
 List<calatoriiDto> findByName(String name);
List<calatoriiDto> findAll();
 calatorii saveCalatorie(calatorii c);

 calatoriiDto findCalatorieById(long id);

 void delete(long id);

 List<calatoriiDto> searchCalatorii(String query);
}
