package com.crownedcuts.booking.services;

import com.crownedcuts.booking.records.AvailableTime;
import com.crownedcuts.booking.records.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FreeTimesServiceImpl implements FreeTimesService
{
    private final BarberHairdresserService barberHairdresserService;
    private final ReservationService reservationService;

    @Autowired
    public FreeTimesServiceImpl(BarberHairdresserService barberHairdresserService,
                                ReservationService reservationService)
    {
        this.barberHairdresserService = barberHairdresserService;
        this.reservationService = reservationService;
    }

    @Override
    public List<AvailableTime> getAllFreeTimesOnDay(int year, int month, int day)
    {
        var localDate = LocalDate.of(year, month, day);

        if (localDate.getDayOfWeek().equals(DayOfWeek.SATURDAY) || localDate.getDayOfWeek().equals(DayOfWeek.SUNDAY))
        {
            throw new IllegalArgumentException("Crowned Cuts does not offer service on weekends.");
        }

        var reservations = reservationService.getReservationsOfDay(year, month, day);

        var barberHairdressers = barberHairdresserService.getAllBarbers();

        List<AvailableTime> result = new ArrayList<>();

        for (int i = 8; i < 16; i++)
        {
            int finalI = i;

            var currentHoursReservations = reservations
                    .stream()
                    .filter(r -> r.reservationInformation().hour() == finalI)
                    .toList();

            var currentHoursReservationBarberIds = currentHoursReservations
                    .stream()
                    .map(Reservation::barberId)
                    .toList();

            var currentBarberHairdressers = new ArrayList<>(barberHairdressers)
                    .stream()
                    .filter(b -> !currentHoursReservationBarberIds.contains(b.id()))
                    .toList();

            if (!currentBarberHairdressers.isEmpty())
            {
                var availableTime = new AvailableTime(year, month, day, i, currentBarberHairdressers);
                result.add(availableTime);
            }
        }

        if (localDate.getDayOfMonth() == LocalDate.now().getDayOfMonth())
        {
            result = result
                    .stream()
                    .filter(r -> r.hour() > LocalDateTime.now().getHour())
                    .toList();
        }

        return result;
    }
}
