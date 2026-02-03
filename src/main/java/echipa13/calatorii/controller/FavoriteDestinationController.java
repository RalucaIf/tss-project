package echipa13.calatorii.controller;

import echipa13.calatorii.service.FavoriteDestinationService;
import echipa13.calatorii.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Controller
@RequestMapping("/favorites")
public class FavoriteDestinationController {

    private final FavoriteDestinationService favoriteService;
    private final UserService userService;

    public FavoriteDestinationController(FavoriteDestinationService favoriteService, UserService userService) {
        this.favoriteService = favoriteService;
        this.userService = userService;
    }

    @PostMapping("/destinations/{destinationId}/toggle")
    public String toggle(@PathVariable Long destinationId,
                         @RequestParam(required = false) String redirect,
                         Authentication auth,
                         HttpServletRequest req) {

        // userId din utilizatorul logat (adaptezi metoda ta reală dacă diferă)
        Long userId = userService.getCurrentUserId(auth);

        boolean nowFav = favoriteService.toggleFavorite(userId, destinationId);

        String back = resolveSafeRedirect(redirect, req);
        return "redirect:" + appendQueryParam(back, "fav", nowFav ? "added" : "removed");
    }

    @PostMapping("/destinations/{destinationId}/remove")
    public String remove(@PathVariable Long destinationId,
                         @RequestParam(required = false) String redirect,
                         Authentication auth,
                         HttpServletRequest req) {

        Long userId = userService.getCurrentUserId(auth);
        favoriteService.removeFavorite(userId, destinationId);

        String back = resolveSafeRedirect(redirect, req);
        return "redirect:" + appendQueryParam(back, "fav", "removed");
    }

    /**
     * Acceptă doar redirect-uri SAFE:
     *  - relative (încep cu "/")
     *  - sau referer de pe același host
     * Altfel => fallback "/profile"
     */
    private String resolveSafeRedirect(String redirectParam, HttpServletRequest req) {
        // 1) dacă vine explicit redirect, îl folosim doar dacă e relative
        if (redirectParam != null && !redirectParam.isBlank() && redirectParam.startsWith("/")) {
            return redirectParam;
        }

        // 2) altfel folosim Referer doar dacă e de pe același host
        String ref = req.getHeader("Referer");
        if (ref != null && !ref.isBlank()) {
            try {
                URI uri = URI.create(ref);
                String host = uri.getHost();
                String reqHost = req.getServerName();

                // Dacă referer nu are host (rare) sau host-ul e același => acceptăm path+query
                if (host == null || host.equalsIgnoreCase(reqHost)) {
                    String path = (uri.getPath() == null || uri.getPath().isBlank()) ? "/" : uri.getPath();
                    String query = uri.getQuery();
                    return (query == null || query.isBlank()) ? path : (path + "?" + query);
                }
            } catch (Exception ignored) {
                // dacă e ceva invalid, ignorăm și mergem pe fallback
            }
        }

        // 3) fallback safe
        return "/profile";
    }

    private String appendQueryParam(String url, String key, String value) {
        if (url == null || url.isBlank()) return "/profile?" + key + "=" + value;
        return url + (url.contains("?") ? "&" : "?") + key + "=" + value;
    }
}
