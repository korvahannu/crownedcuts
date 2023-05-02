package com.crownedcuts.booking.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import com.crownedcuts.booking.records.Reservation;
import com.crownedcuts.booking.records.UserDetails;
import com.crownedcuts.booking.services.UserService;

@Controller
public class IndexController
{

    private final UserService userService;

    @Autowired
    public IndexController(UserService userService) {
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

        List<Reservation> allreservations = userService.getReservations(username);
        List<String> upcomingreservations = new ArrayList<>();

        for(int i = 0; i < allreservations.size(); i++) {
            LocalDateTime now = LocalDateTime.now();
            var timeDetails = allreservations.get(i).reservationInformation();
            var localDateTime = LocalDateTime.of(timeDetails.year(), timeDetails.month(), timeDetails.day(), timeDetails.hour(), 0);
            String date = timeDetails.day() + "." + timeDetails.month() + "." + timeDetails.year() + " klo " + timeDetails.hour();

            if(now.isBefore(localDateTime)) {
                upcomingreservations.add(date);
            }
        }

        mav.addObject("upcomingreservationslist", upcomingreservations);

        return mav;
    }
}
