package com.crownedcuts.booking.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController
{
    @GetMapping("/login")
    public ModelAndView getLoginPage()
    {
        ModelAndView mav = new ModelAndView("login");
        mav.addObject("message", "Hello from controller!");
        return mav;
    }
}
