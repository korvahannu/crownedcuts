package com.crownedcuts.booking.controllers;

import com.crownedcuts.booking.records.UserDetails;
import com.crownedcuts.booking.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

@Controller
public class IndexController
{

    private final UserService userService;

    @Autowired
    public IndexController(UserService userService)
    {
        this.userService = userService;
    }

    @GetMapping(value = {"/", "/index"})
    public ModelAndView onGet()
    {
        ModelAndView mav = new ModelAndView("index");

        var auth = SecurityContextHolder.getContext().getAuthentication();
        var username = ((UserDetails) auth.getPrincipal()).username();

        try
        {
            mav.addObject("userDetails", auth.getPrincipal());
        }
        catch (Exception exception)
        {
            mav.addObject("userDetails", "anonymous user");
        }

        var allreservations = userService.getReservations(username)
                .stream()
                .sorted((o1, o2) -> o2.reservationInformation().compareTo(o1.reservationInformation()))
                .toList();

        var upcomingReservations = allreservations
                .stream()
                .filter(r -> LocalDateTime.now().isBefore(r.reservationInformation().toLocalDateTime()))
                .limit(4)
                .map(r -> r.reservationInformation().toString())
                .toList();

        mav.addObject("upcomingReservations", upcomingReservations);

        return mav;
    }
}
