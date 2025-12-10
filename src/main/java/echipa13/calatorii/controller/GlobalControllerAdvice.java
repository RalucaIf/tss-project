package echipa13.calatorii.controller;

import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserRepository userRepository;

    /** WHY: expune ${user} pe toate paginile; funcționează și dacă principalul e username. */
    @ModelAttribute("user")
    public UserEntity addUserToModel(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return null;
        }
        String principal = auth.getName();
        UserEntity u = userRepository.findByEmail(principal);
        if (u == null) {
            u = userRepository.findByUsername(principal); // fallback
        }
        return u;
    }
}
