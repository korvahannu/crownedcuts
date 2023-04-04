package com.crownedcuts.booking;

import com.crownedcuts.booking.services.BarberHairdresserService;
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
import java.time.*;
import java.util.TimeZone;
import java.util.logging.Logger;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(locations="classpath:test.reservationservicetests.properties")
class ReservationServiceTests
{
    private final ReservationService reservationService;
    private  final BarberHairdresserService barberHairdresserService;
    private final UserService userService;
    private static final Logger logger = Logger.getLogger(ReservationServiceTests.class.getName());
    private final int testBarbersCount = 5;

    private static String databaseFilepath;

    @Value("${databaseFilepath}")
    public void setNameStatic(String databaseFilepath){
        ReservationServiceTests.databaseFilepath = databaseFilepath;
    }

    @Autowired
    public ReservationServiceTests(ReservationService reservationService, BarberHairdresserService barberHairdresserService, UserService userService)
    {
        this.reservationService = reservationService;
        this.barberHairdresserService = barberHairdresserService;
        this.userService = userService;
    }

    /**
     * Helper function that returns a testable time that is 8 o'clock at the next monday
     * @return ZoneDateTime
     */
    private ZonedDateTime getTestableTime()
    {
        var time = ZonedDateTime.of(LocalDate.now(), LocalTime.of(8, 0), TimeZone.getDefault().toZoneId());

        while (!time.getDayOfWeek().equals(DayOfWeek.MONDAY))
        {
            time = time.plusDays(1);
        }

        return time;
    }

    @Test
    @Order(1)
    void gettingFreeTimeForOneHourWorks()
    {
        var result = reservationService.getAllFreeTimes(getTestableTime(),
                getTestableTime().plusHours(1));

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(testBarbersCount, result.get(0).barbersAvailable().size());
    }

    @Test
    @Order(1)
    void getFreeTimeForOneDayWorks()
    {
        var result = reservationService.getAllFreeTimes(getTestableTime(),
                getTestableTime().plusHours(8));

        Assertions.assertEquals(7, result.size());
    }

    @Test
    @Order(1)
    void gettingFreeTimeForOverAWeekThrows()
    {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            reservationService.getAllFreeTimes(getTestableTime(),
                    getTestableTime().plusWeeks(1).plusHours(1));
        });
    }

    @Test
    @Order(1)
    void gettingFreeTimeForAWeekWorks()
    {
        var result = reservationService.getAllFreeTimes(getTestableTime(),
                getTestableTime().plusWeeks(1));

        Assertions.assertEquals(39, result.size());
    }

    @Test
    @Order(2)
    void reservingATimeWorks()
    {
        var barber = barberHairdresserService.getAllBarbers().get(0);
        var user = userService.getUser("user@crownedcuts.fi").get();

        var didReservationWork = reservationService.reserveTime(barber, user, getTestableTime().plusHours(1));

        Assertions.assertTrue(didReservationWork);

        var result = reservationService.getAllFreeTimes(getTestableTime(),
                getTestableTime().plusHours(1));

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(testBarbersCount - 1, result.get(0).barbersAvailable().size());
        Assertions.assertFalse(result.get(0).barbersAvailable().contains(barber));

        var reservations = userService.getReservations(user.username());
        Assertions.assertEquals(1, reservations.size());
        Assertions.assertEquals(user.username(), reservations.get(0).username());

        Assertions.assertTrue(reservationService.reserveTime(barber, user, getTestableTime().plusHours(2)));
        Assertions.assertTrue(reservationService.reserveTime(barber, user, getTestableTime().plusHours(3)));
        Assertions.assertTrue(reservationService.reserveTime(barber, user, getTestableTime().plusHours(4)));
        Assertions.assertTrue(reservationService.reserveTime(barber, user, getTestableTime().plusHours(5)));
        Assertions.assertTrue(reservationService.reserveTime(barber, user, getTestableTime().plusHours(6)));
        Assertions.assertTrue(reservationService.reserveTime(barber, user, getTestableTime().plusHours(7)));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            reservationService.reserveTime(barber, user, getTestableTime().plusHours(8));
        });

        Assertions.assertEquals(7, userService.getReservations(user.username()).size());

        var barberTimes = reservationService.getAllFreeTimes(getTestableTime(), getTestableTime().plusHours(8));

        for(var time : barberTimes)
        {
            Assertions.assertTrue(!time.barbersAvailable().contains(barber));
        }

        var targetBarberTimes = reservationService.getAllReservations(barber, getTestableTime(), getTestableTime().plusHours(8));

        Assertions.assertEquals(7, targetBarberTimes.size());

        for(var time : targetBarberTimes)
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
