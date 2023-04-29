package com.crownedcuts.booking.controllers;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import com.crownedcuts.booking.services.UserService;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import ch.qos.logback.core.model.Model;
import com.crownedcuts.booking.records.Reservation;
import com.crownedcuts.booking.records.TimeDetails;
import com.crownedcuts.booking.records.UserDetails;
import org.springframework.stereotype.Controller;


@Controller
public class AllreservationsController {

    private static final String VIEW_NAME = "allreservations";

    private final UserService userService;

    @Autowired
    public AllreservationsController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping(value = {"/allreservations", "/kaikkiajanvaraukset"})
    public ModelAndView onGet()
    {
        System.out.println("/allreservations " + VIEW_NAME);
        var mvc = new ModelAndView(VIEW_NAME);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = ((UserDetails) authentication.getPrincipal()).username();

        List<Reservation> allreservations = userService.getReservations(username);
        List<TimeDetails> earlierreservations = new ArrayList<>();
        List<TimeDetails> upcomingreservations = new ArrayList<>();

        for(int i = 0; i < allreservations.size(); i++) {
            LocalDateTime now = LocalDateTime.now();
            var timeDetails = allreservations.get(i).reservationInformation();
            var localDateTime = LocalDateTime.of(timeDetails.year(), timeDetails.month(), timeDetails.day(), timeDetails.hour(), 0);

            if(now.isBefore(localDateTime)) {
                earlierreservations.add(allreservations.get(i).reservationInformation());
            } else {
                upcomingreservations.add(allreservations.get(i).reservationInformation());
            }
        }

        mvc.addObject("earlierreservationslist", earlierreservations);
        mvc.addObject("upcomingreservationslist", upcomingreservations);

        return mvc;
    }
}
