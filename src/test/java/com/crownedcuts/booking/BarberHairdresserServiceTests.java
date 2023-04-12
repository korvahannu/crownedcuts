package com.crownedcuts.booking;

import com.crownedcuts.booking.services.BarberHairdresserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BarberHairdresserServiceTests
{
    private final BarberHairdresserService barberHairdresserService;

    @Autowired
    public BarberHairdresserServiceTests(BarberHairdresserService barberHairdresserService)
    {
        this.barberHairdresserService = barberHairdresserService;
    }

    @Test
    void barberServiceReturnsCorrectPersonWithUsername()
    {
        var barberOptional = barberHairdresserService.getBarber("Milla Pyrrö");
        Assertions.assertTrue(barberOptional.isPresent());

        var barber = barberOptional.orElseThrow();
        Assertions.assertEquals("Milla Pyrrö", barber.name());
    }

    @Test
    void barberServiceReturnsCorrectPersonWithId()
    {
        var barberOptional = barberHairdresserService.getBarber(1);
        Assertions.assertTrue(barberOptional.isPresent());
    }

    @Test
    void barberServiceReturnsEmptyIfNothingFoundUsername()
    {
        var barberOptional = barberHairdresserService.getBarber("This barber does not exist");
        Assertions.assertFalse(barberOptional.isPresent());
    }

    @Test
    void barberServiceReturnsEmptyIfNothingFoundId()
    {
        var barberOptional = barberHairdresserService.getBarber(5000);
        Assertions.assertFalse(barberOptional.isPresent());
        Assertions.assertFalse(barberHairdresserService.getBarber(null).isPresent());
    }
}
