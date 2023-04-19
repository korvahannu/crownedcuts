package com.crownedcuts.booking.controllers;

import com.crownedcuts.booking.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserInformationController
{
    private final UserService userService;

    @Autowired
    public UserInformationController(UserService userService)
    {
        this.userService = userService;
    }

    @GetMapping("/information")
    public ModelAndView onGet()
    {
        var mvc = new ModelAndView("information");
        mvc.addObject("userDetails", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return mvc;
    }

    @GetMapping("/omattiedot")
    public ModelAndView onGetOwnInformation()
    {
        var mvc = new ModelAndView("information");
        mvc.addObject("userDetails", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return mvc;
    }

    @PostMapping("/omattiedot")
    public ModelAndView onPostOwnInformation(@RequestParam String username,
                                                             @RequestParam String firstname,
                                                             @RequestParam String lastname,
                                                             @RequestParam(required = false) String phonenumber,
                                                             @RequestParam(required = false) String dateOfBirth)
    {
        var result = userService.updateUserInformation(username, firstname, lastname, phonenumber, dateOfBirth);

        if(result)
        {
            var mvc = new ModelAndView("information");
            mvc.addObject("updateSuccess", true);
            mvc.addObject("userDetails", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return mvc;
        }
        else
        {
            var mvc = new ModelAndView("information");
            mvc.addObject("updateFail", true);
            mvc.addObject("userDetails", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return mvc;
        }
    }

    @PostMapping("/information")
    public ModelAndView onPost(@RequestParam String username,
                                       @RequestParam String firstname,
                                       @RequestParam String lastname,
                                       @RequestParam(required = false) String phonenumber,
                                       @RequestParam(required = false) String dateOfBirth)
    {
        var result = userService.updateUserInformation(username, firstname, lastname, phonenumber, dateOfBirth);

        if(result)
        {
            var mvc = new ModelAndView("information");
            mvc.addObject("updateSuccess", true);
            mvc.addObject("userDetails", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return mvc;
        }
        else
        {
            var mvc = new ModelAndView("information");
            mvc.addObject("updateFail", true);
            mvc.addObject("userDetails", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return mvc;
        }
    }
}
