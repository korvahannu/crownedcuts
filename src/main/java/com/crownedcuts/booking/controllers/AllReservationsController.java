package com.crownedcuts.booking.controllers;

import com.crownedcuts.booking.records.Reservation;
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

    private static final String VIEW_NAME = "allreservations";

    private final UserService userService;

    @Autowired
    public AllReservationsController(UserService userService)
    {
        this.userService = userService;
    }

    @GetMapping(value = {"/allreservations", "/kaikkiajanvaraukset"})
    public ModelAndView onGet()
    {
        var mvc = new ModelAndView(VIEW_NAME);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = ((UserDetails) authentication.getPrincipal()).username();

        List<Reservation> allreservations = userService.getReservations(username);
        List<String> earlierreservations = new ArrayList<>();
        List<String> upcomingreservations = new ArrayList<>();

        for (int i = 0; i < allreservations.size(); i++)
        {
            LocalDateTime now = LocalDateTime.now();
            var timeDetails = allreservations.get(i).reservationInformation();
            var localDateTime = LocalDateTime.of(timeDetails.year(),
                    timeDetails.month(),
                    timeDetails.day(),
                    timeDetails.hour(),
                    0);
            String date = timeDetails.day() + "." + timeDetails.month() + "." + timeDetails.year() + " " + timeDetails.hour() + ":00-" + (timeDetails.hour() + 1) + ":00";


            if (now.isBefore(localDateTime))
            {
                upcomingreservations.add(date);
            }
            else
            {
                earlierreservations.add(date);
            }
        }

        mvc.addObject("earlierreservationslist", earlierreservations);
        mvc.addObject("upcomingreservationslist", upcomingreservations);

        return mvc;
    }
}
