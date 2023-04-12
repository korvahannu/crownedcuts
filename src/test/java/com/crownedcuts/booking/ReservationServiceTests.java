package com.crownedcuts.booking;

import com.crownedcuts.booking.records.TimeDetails;
import com.crownedcuts.booking.services.BarberHairdresserService;
import com.crownedcuts.booking.services.FreeTimesService;
import com.crownedcuts.booking.services.ReservationService;
import com.crownedcuts.booking.services.UserService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:test.reservationservicetests.properties")
class ReservationServiceTests
{
    private final ReservationService reservationService;
    private final BarberHairdresserService barberHairdresserService;
    private final UserService userService;
    private final FreeTimesService freeTimesService;
    private static final Logger logger = Logger.getLogger(ReservationServiceTests.class.getName());

    private static String databaseFilepath;

    @Value("${databaseFilepath}")
    public void setNameStatic(String databaseFilepath)
    {
        ReservationServiceTests.databaseFilepath = databaseFilepath;
    }

    @Autowired
    public ReservationServiceTests(ReservationService reservationService,
                                   BarberHairdresserService barberHairdresserService,
                                   UserService userService,
                                   FreeTimesService freeTimesService)
    {
        this.reservationService = reservationService;
        this.barberHairdresserService = barberHairdresserService;
        this.userService = userService;
        this.freeTimesService = freeTimesService;
    }

    @Test
    @Order(1)
    void gettingFreeTimeForADayWorks()
    {
        var result = freeTimesService.getAllFreeTimesOnDay(2023, 1, 20);

        Assertions.assertEquals(8, result.size());
    }

    @Test
    @Order(2)
    void reservingATimeWorks()
    {
        var barber = barberHairdresserService.getAllBarbers().get(0);
        var user = userService.getUser("user@crownedcuts.fi").get();
        var timeDetails = new TimeDetails(2023, 5, 5, 8);

        var didReservationWork = reservationService.reserveTime(barber, user, timeDetails, "long");

        Assertions.assertTrue(didReservationWork);

        var result = freeTimesService.getAllFreeTimesOnDay(2023, 5, 5);

        Assertions.assertEquals(8, result.size());
        int testBarbersCount = 5;
        Assertions.assertEquals(testBarbersCount - 1, result.get(0).barbersAvailable().size());
        Assertions.assertFalse(result.get(0).barbersAvailable().contains(barber));

        var reservations = userService.getReservations(user.username());
        Assertions.assertEquals(1, reservations.size());
        Assertions.assertEquals(user.username(), reservations.get(0).username());

        Assertions.assertTrue(reservationService.reserveTime(barber, user, new TimeDetails(2023, 5, 5, 9), "long"));
        Assertions.assertTrue(reservationService.reserveTime(barber, user, new TimeDetails(2023, 5, 5, 10), "long"));
        Assertions.assertTrue(reservationService.reserveTime(barber, user, new TimeDetails(2023, 5, 5, 11), "long"));
        Assertions.assertTrue(reservationService.reserveTime(barber, user, new TimeDetails(2023, 5, 5, 12), "long"));
        Assertions.assertTrue(reservationService.reserveTime(barber, user, new TimeDetails(2023, 5, 5, 13), "long"));
        Assertions.assertTrue(reservationService.reserveTime(barber, user, new TimeDetails(2023, 5, 5, 14), "long"));
        Assertions.assertTrue(reservationService.reserveTime(barber, user, new TimeDetails(2023, 5, 5, 15), "long"));
        Assertions.assertFalse(reservationService.reserveTime(barber, user, new TimeDetails(2023, 5, 5, 15), "long"));

        var invalidReservationTimeTooLate = new TimeDetails(2023, 5, 5, 16);
        Assertions.assertThrows(IllegalArgumentException.class, () ->
        {
            reservationService.reserveTime(barber, user, invalidReservationTimeTooLate, "long");
        });

        var invalidReservationTimeTooEarly = new TimeDetails(2023, 5, 5, 16);
        Assertions.assertThrows(IllegalArgumentException.class, () ->
        {
            reservationService.reserveTime(barber, user, invalidReservationTimeTooEarly, "long");
        });

        Assertions.assertEquals(8, userService.getReservations(user.username()).size());

        var barberTimes = freeTimesService.getAllFreeTimesOnDay(2023, 5, 5);

        for (var time : barberTimes)
        {
            Assertions.assertFalse(time.barbersAvailable().contains(barber));
        }

        var targetBarberTimes = reservationService.getReservationsOfDay(barber, 2023, 5, 5);

        Assertions.assertEquals(8, targetBarberTimes.size());

        for (var time : targetBarberTimes)
        {
            Assertions.assertEquals(barber.id(), time.barberId());
        }
    }

    @AfterAll
    static void cleanup()
    {
        try
        {
            Files.delete(Paths.get(databaseFilepath));
        }
        catch (IOException ex)
        {
            logger.warning(ex.getMessage());
        }
    }
}
