package com.crownedcuts.booking.controllers;

import com.crownedcuts.booking.records.*;
import com.crownedcuts.booking.services.BarberHairdresserService;
import com.crownedcuts.booking.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReservationRestController
{
    private final ReservationService reservationService;
    private final BarberHairdresserService barberHairdresserService;

    @Autowired
    public ReservationRestController(ReservationService reservationService, BarberHairdresserService barberHairdresserService) {
        this.reservationService = reservationService;
        this.barberHairdresserService = barberHairdresserService;
    }

    @GetMapping("/rest/getBarbers")
    public List<BarberHairdresser> onGetBarbers() {
        return barberHairdresserService.getAllBarbers();
    }

    @GetMapping("/rest/getAvailableTimes")
    public ResponseEntity<List<AvailableTime>> getAvailableTimes(@RequestParam int year, @RequestParam int month, @RequestParam int day)
    {
        try {
            var result= reservationService.getAllFreeTimesOnDay(year, month, day);
            return ResponseEntity.ok(result);
        }
        catch(IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/rest/sendReservation")
    public ResponseEntity<Object> sendReservation(@RequestBody ReservationPayload payload) {

        // TODO: Payload can have barberId of -1, in that case, fetch any available barber

        var barber = barberHairdresserService.getBarber(payload.barberId()).orElseThrow();
        var timeDetails = new TimeDetails(payload.year(), payload.month(), payload.day(), payload.hour());
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        var user = UserDetails.of(username, null, null);

        if(reservationService.reserveTime(barber, user, timeDetails)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
