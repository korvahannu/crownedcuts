package com.crownedcuts.booking.controllers;

import com.crownedcuts.booking.records.*;
import com.crownedcuts.booking.services.BarberHairdresserService;
import com.crownedcuts.booking.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

        // barberId of less than 0 means that the barber/hairdresser does not matter for the customer
        if(payload.barberId() < 0) {
            final var finalHour = payload.hour();
            var freeTimesAtHour = reservationService.getAllFreeTimesOnDay(payload.year(), payload.month(), payload.day())
                    .stream()
                    .filter(f -> f.hour() == finalHour)
                    .toList();

            var randomBarberNumber = ThreadLocalRandom
                    .current()
                    .nextInt(0, freeTimesAtHour.get(0).barbersAvailable().size()) -1;

            var finalRandomNumber = randomBarberNumber == -1 ? 0 : randomBarberNumber;

            long barberId = freeTimesAtHour
                    .get(0)
                    .barbersAvailable()
                    .get(finalRandomNumber)
                    .id();

            payload = new ReservationPayload(payload.serviceType(),
                    payload.hairLength(),
                    payload.services(),
                    payload.year(),
                    payload.month(),
                    payload.day(),
                    payload.hour(),
                    barberId
            );
        }

        var barber = barberHairdresserService.getBarber(payload.barberId()).orElseThrow();
        var timeDetails = new TimeDetails(payload.year(), payload.month(), payload.day(), payload.hour());
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        var user = UserDetails.of(username, null);

        if(reservationService.reserveTime(barber, user, timeDetails)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
