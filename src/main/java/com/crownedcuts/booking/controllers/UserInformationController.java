package com.crownedcuts.booking.controllers;

import com.crownedcuts.booking.records.UserDetails;
import com.crownedcuts.booking.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserInformationController
{
    private static final String VIEW_NAME = "information";
    private static final String VIEW_UD_ATTRIBUTE_NAME = "userDetails";
    private final UserService userService;

    @Autowired
    public UserInformationController(UserService userService)
    {
        this.userService = userService;
    }

    @GetMapping(value = {"/information", "/omattiedot"})
    public ModelAndView onGet()
    {
        var mvc = new ModelAndView(VIEW_NAME);
        mvc.addObject(VIEW_UD_ATTRIBUTE_NAME, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return mvc;
    }

    @PostMapping(value = {"/information", "/omattiedot"})
    public ModelAndView onPostOwnInformation(@RequestParam String username,
                                             @RequestParam String firstname,
                                             @RequestParam String lastname,
                                             @RequestParam(required = false) String phonenumber,
                                             @RequestParam(required = false) String dateOfBirth)
    {
        var result = userService.updateUserInformation(username, firstname, lastname, phonenumber, dateOfBirth);
        reloadSecurityContext();

        var mvc = new ModelAndView(VIEW_NAME);
        if (result)
        {
            mvc.addObject("updateSuccess", true);
        }
        else
        {
            mvc.addObject("updateFail", true);
        }
        mvc.addObject(VIEW_UD_ATTRIBUTE_NAME,
                SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return mvc;
    }

    /**
     * Helper function that retrieves user information again and updates the authentication.
     * Used when updating user information at database level
     */
    void reloadSecurityContext()
    {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = ((UserDetails) authentication.getPrincipal()).username();

        var newAuth = userService.getUser(username)
                .map(u -> new UsernamePasswordAuthenticationToken(u, null, u.authorities()))
                .orElse(null);

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
