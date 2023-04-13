package com.crownedcuts.booking.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserInformationController {
    
    @GetMapping("/information")
    public ModelAndView onGet() {

        var mvc = new ModelAndView("information");
        mvc.addObject("message", "User information");


        return mvc;
    }
}
