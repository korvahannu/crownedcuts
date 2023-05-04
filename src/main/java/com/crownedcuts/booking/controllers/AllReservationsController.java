package com.crownedcuts.booking.controllers;

import com.crownedcuts.booking.records.UserDetails;
import com.crownedcuts.booking.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Controller
public class AllReservationsController
{
    private final UserService userService;

    @Autowired
    public AllReservationsController(UserService userService)
    {
        this.userService = userService;
    }

    @GetMapping(value = {"/allreservations", "/kaikkiajanvaraukset"})
    public ModelAndView onGet()
    {
        var mvc = new ModelAndView("allreservations");

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = ((UserDetails) authentication.getPrincipal()).username();

        List<String> earlierReservations = new ArrayList<>();
        List<String> upcomingReservations = new ArrayList<>();

        userService.getReservations(username)
                .stream()
                .sorted((o1, o2) -> o2.reservationInformation().compareTo(o1.reservationInformation()))
                .forEach(r ->
                {
                    var timeDetails = r.reservationInformation();
                    if (LocalDateTime.now().isBefore(timeDetails.toLocalDateTime()))
                    {
                        upcomingReservations.add(timeDetails.toString());
                    }
                    else
                    {
                        earlierReservations.add(timeDetails.toString());
                    }
                });

        mvc.addObject("earlierReservations", earlierReservations);
        mvc.addObject("upcomingReservations", upcomingReservations);

        return mvc;
    }
}
