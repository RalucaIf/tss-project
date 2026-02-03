package echipa13.calatorii.controller;

import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.repository.UserFollowRepository;
import echipa13.calatorii.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.Set;

@Controller
@RequestMapping("/users")
public class UserDiscoverController {

    private final UserRepository userRepo;
    private final UserFollowRepository followRepo;

    public UserDiscoverController(UserRepository userRepo, UserFollowRepository followRepo) {
        this.userRepo = userRepo;
        this.followRepo = followRepo;
    }

    @GetMapping
    public String usersPage(
            @RequestParam(value = "q", required = false, defaultValue = "") String q,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            Authentication auth,
            Model model
    ) {
        // ✅ doar logați
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        UserEntity me = userRepo.findByEmail(auth.getName());
        if (me == null) return "redirect:/login";

        int safePage = Math.max(page, 0);
        Page<UserEntity> users = userRepo.findByUsernameContainingIgnoreCase(q.trim(), PageRequest.of(safePage, 12));

        // ✅ ca să afișăm buton “Following” corect
        Set<Long> followingIds = followRepo.findFollowingIds(me.getId());

        model.addAttribute("me", me);
        model.addAttribute("q", q);
        model.addAttribute("usersPage", users);
        model.addAttribute("followingIds", followingIds);

        // IMPORTANT: numele template-ului tău
        return "users_list";
    }
}
