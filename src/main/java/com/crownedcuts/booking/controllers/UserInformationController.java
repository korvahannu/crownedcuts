package com.crownedcuts.booking.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserInformationController
{

    @GetMapping("/information")
    public ModelAndView onGet()
    {
        var mvc = new ModelAndView("information");
        mvc.addObject("userDetails", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return mvc;
    }

    @GetMapping("/omattiedot")
    public ModelAndView onGetAjanvaraus()
    {
        var mvc = new ModelAndView("information");
        mvc.addObject("userDetails", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return mvc;
    }
}
