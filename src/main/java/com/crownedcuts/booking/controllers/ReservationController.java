package com.crownedcuts.booking.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ReservationController
{
    @GetMapping("/ajanvaraus")
    public ModelAndView onGet()
    {
        return new ModelAndView("ajanvaraus");
    }
}
