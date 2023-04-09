package com.crownedcuts.booking.controllers;

import com.crownedcuts.booking.records.AvailableTime;
import com.crownedcuts.booking.records.BarberHairdresser;
import com.crownedcuts.booking.records.Service;
import com.crownedcuts.booking.services.BarberHairdresserService;
import com.crownedcuts.booking.services.ReservationService;
import com.crownedcuts.booking.services.ServicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReservationRestController
{
    private final ReservationService reservationService;
    private final BarberHairdresserService barberHairdresserService;
    private final ServicesService servicesService;

    @Autowired
    public ReservationRestController(ReservationService reservationService, BarberHairdresserService barberHairdresserService, ServicesService servicesService)
    {
        this.reservationService = reservationService;
        this.barberHairdresserService = barberHairdresserService;
        this.servicesService = servicesService;
    }

    @GetMapping("/rest/getBarbers")
    public List<BarberHairdresser> onGetBarbers()
    {
        return barberHairdresserService.getAllBarbers();
    }

    @GetMapping("/rest/getAvailableTimes")
    public ResponseEntity<List<AvailableTime>> getAvailableTimes(@RequestParam int year, @RequestParam int month, @RequestParam int day)
    {
        try
        {
            var result = reservationService.getAllFreeTimesOnDay(year, month, day);
            return ResponseEntity.ok(result);
        }
        catch (IllegalArgumentException ex)
        {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/rest/getAllServices")
    public List<Service> getAllServices()
    {
        return servicesService.getAllServices();
    }
}
