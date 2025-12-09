package echipa13.calatorii.repository;

import echipa13.calatorii.models.Role;
import echipa13.calatorii.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);

}
