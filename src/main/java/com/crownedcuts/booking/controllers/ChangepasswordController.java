package com.crownedcuts.booking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.crownedcuts.booking.services.UserService;
import com.crownedcuts.booking.records.UserDetails;

@Controller
public class ChangepasswordController {

    private static final String VIEW_NAME = "changepassword";
    private static final String VIEW_UD_ATTRIBUTE_NAME = "userDetails";
    private final UserService userService;

    @Autowired
    public ChangepasswordController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = { "/changepassword", "/vaihdasalasana" })
    public ModelAndView onGet() {
        var mvc = new ModelAndView(VIEW_NAME);
        mvc.addObject(VIEW_UD_ATTRIBUTE_NAME, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return mvc;
    }

    @PostMapping(value = { "/changepassword", "/vaihdasalasana" })
    public ModelAndView onChangePassword(@RequestParam String oldpassword,
                                         @RequestParam String newpassword) {

        var mvc = new ModelAndView(VIEW_NAME);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = ((UserDetails) authentication.getPrincipal()).username();
        var userdetails = userService.getUser(username);

        if (userdetails.isPresent()) {

            var ud = userdetails.get();
            var checkPassword = UserDetails.of(ud.username(), oldpassword, "", "");

            if (userService.checkUserPassword(checkPassword)) {
                var result = userService.updateUserPassword(username, newpassword);
                reloadSecurityContext();

                if (result) {
                    mvc.addObject("updateSuccess", true);
                } else {
                    mvc.addObject("updateFail", true);
                }
            } else {
                mvc.addObject("updateFail", true);
            }
        } else {
            mvc.addObject("updateFail", true);
        }

        mvc.addObject(VIEW_UD_ATTRIBUTE_NAME, SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        return mvc;
    }

    /**
     * Helper function that retrieves user information again and updates the
     * authentication.
     * Used when updating user information at database level
     */
    void reloadSecurityContext() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = ((UserDetails) authentication.getPrincipal()).username();

        var newAuth = userService.getUser(username)
                .map(u -> new UsernamePasswordAuthenticationToken(u, null, u.authorities()))
                .orElse(null);

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
