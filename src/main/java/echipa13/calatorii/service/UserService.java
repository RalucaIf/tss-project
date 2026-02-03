package echipa13.calatorii.service;

import echipa13.calatorii.Dto.RegistrationDto;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.models.UserPoints;

import java.util.List;

public interface UserService {
    void saveUser(RegistrationDto registrationDto);
    UserEntity findByUsername(String username);
    UserEntity findByEmail(String email);

    List<UserPoints>  findAllUserPoints();
}
