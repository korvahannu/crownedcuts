package com.crownedcuts.booking.controllers;

import com.crownedcuts.booking.records.ReservationPayload;
import com.crownedcuts.booking.records.TimeDetails;
import com.crownedcuts.booking.records.UserDetails;
import com.crownedcuts.booking.services.BarberHairdresserService;
import com.crownedcuts.booking.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.concurrent.ThreadLocalRandom;

@Controller
public class ReservationController
{
    private final ReservationService reservationService;
    private final BarberHairdresserService barberHairdresserService;

    @Autowired
    public ReservationController(ReservationService reservationService, BarberHairdresserService barberHairdresserService)
    {
        this.reservationService = reservationService;
        this.barberHairdresserService = barberHairdresserService;
    }

    @GetMapping("/ajanvaraus")
    public ModelAndView onGet()
    {
        return new ModelAndView("ajanvaraus");
    }

    @GetMapping("/newreservation")
    public ModelAndView onGetEn()
    {
        return new ModelAndView("ajanvaraus");
    }

    @PostMapping("/ajanvaraus")
    public ResponseEntity<Object> sendReservation(@RequestBody ReservationPayload payload)
    {
        payload = payload.barberId() > 0 ? payload : getPayloadWithRandomBarber(payload);

        var barber = barberHairdresserService.getBarber(payload.barberId()).orElseThrow();
        var timeDetails = new TimeDetails(payload.year(), payload.month(), payload.day(), payload.hour());
        var username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        var user = UserDetails.of(username, null, null, null);

        if (reservationService.reserveTime(barber, user, timeDetails, payload.hairLength(), payload.services()))
        {
            return ResponseEntity.ok().build();
        }
        else
        {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/ajanvarausonnistui")
    public ModelAndView onGetSuccess()
    {
        return new ModelAndView("ajanvarausonnistui");
    }

    private ReservationPayload getPayloadWithRandomBarber(ReservationPayload payload)
    {
        final var finalHour = payload.hour();

        var freeTimesAtHour = reservationService.getAllFreeTimesOnDay(payload.year(), payload.month(), payload.day())
                .stream()
                .filter(f -> f.hour() == finalHour)
                .toList();

        var randomBarberNumber = ThreadLocalRandom
                .current()
                .nextInt(0, freeTimesAtHour.get(0).barbersAvailable().size()) - 1;

        var finalRandomNumber = randomBarberNumber == -1 ? 0 : randomBarberNumber;

        var barberId = freeTimesAtHour
                .get(0)
                .barbersAvailable()
                .get(finalRandomNumber)
                .id();

        return new ReservationPayload(payload.serviceType(),
                payload.hairLength(),
                payload.services(),
                payload.year(),
                payload.month(),
                payload.day(),
                payload.hour(),
                barberId,
                payload.isBarberService()
        );
    }
}
