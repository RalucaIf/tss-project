package echipa13.calatorii.controller;

import echipa13.calatorii.service.UserFollowService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserFollowController {

    private final UserFollowService followService;

    public UserFollowController(UserFollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/{id}/follow-toggle")
    public String toggleFollow(
            @PathVariable("id") Long targetUserId,
            @RequestParam(value = "redirect", required = false, defaultValue = "/users") String redirect,
            Authentication auth
    ) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        boolean nowFollowing;
        try {
            nowFollowing = followService.toggleFollow(auth.getName(), targetUserId);
        } catch (IllegalArgumentException ex) {
            // ex: follow self / target missing
            return "redirect:" + safeRedirect(redirect) + "?follow=error";
        }

        return "redirect:" + safeRedirect(redirect) + (nowFollowing ? "?follow=on" : "?follow=off");
    }

    /**
     * Redirect safe minimal: permitem doar redirect-uri interne (încep cu "/")
     */
    private String safeRedirect(String redirect) {
        if (redirect == null || redirect.isBlank()) return "/users";
        if (!redirect.startsWith("/")) return "/users";
        return redirect;
    }
}

