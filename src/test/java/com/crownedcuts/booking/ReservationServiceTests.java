package com.crownedcuts.booking;

import com.crownedcuts.booking.services.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZonedDateTime;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ReservationServiceTests
{
    private final ReservationService reservationService;

    @Autowired
    public ReservationServiceTests(ReservationService reservationService)
    {
        this.reservationService = reservationService;
    }

    @Test
    public void getFreeTimesWorks()
    {
        var result = reservationService.getAllFreeTimes(ZonedDateTime.now(),
                ZonedDateTime.now().plusWeeks(1));
    }
}
