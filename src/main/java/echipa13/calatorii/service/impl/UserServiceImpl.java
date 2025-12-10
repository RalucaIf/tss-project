package echipa13.calatorii.service.impl;

import echipa13.calatorii.Dto.RegistrationDto;
import echipa13.calatorii.models.Role;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.repository.RoleRepository;
import echipa13.calatorii.repository.UserRepository;
import echipa13.calatorii.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void saveUser(RegistrationDto registrationDto) {
        UserEntity user = new UserEntity();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword_hash(registrationDto.getPassword_hash());
        user.setAvatar(registrationDto.getAvatar());
        Role role= roleRepository.findByName("User");
        user.setRoles(Arrays.asList(role));
        userRepository.save(user);
    }
}
