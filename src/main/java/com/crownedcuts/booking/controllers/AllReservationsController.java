package com.crownedcuts.booking.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import ch.qos.logback.core.model.Model;

public class AllReservationsController {
    
    @GetMapping("/allreservations")
    public ModelAndView onGet()
    {
        var mvc = new ModelAndView("allreservations");
        return mvc;
    }
}
