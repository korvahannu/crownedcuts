package com.crownedcuts.booking.services;

import com.crownedcuts.booking.records.*;
import com.crownedcuts.booking.repositories.DbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ReservationServiceImpl implements ReservationService
{
    private final DbRepository repository;
    private final BarberHairdresserService barberHairdresserService;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    public ReservationServiceImpl(DbRepository repository, BarberHairdresserService barberHairdresserService)
    {
        this.repository = repository;
        this.barberHairdresserService = barberHairdresserService;
    }

    @Override
    public List<AvailableTime> getAllFreeTimesOnDay(int year, int month, int day)
    {
        var localDate = LocalDate.of(year, month, day);

        if (localDate.getDayOfWeek().equals(DayOfWeek.SATURDAY) || localDate.getDayOfWeek().equals(DayOfWeek.SUNDAY))
        {
            throw new IllegalArgumentException("Crowned Cuts does not offer service on weekends.");
        }

        var reservations = getReservationsOfDay(year, month, day);

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

    @Override
    public boolean reserveTime(BarberHairdresser barberHairdresser, UserDetails user, TimeDetails timeDetails, String hairLength)
    {
        if (timeDetails.isOutsideWorkingHoursAndDays())
        {
            throw new IllegalArgumentException("You may only reserve for weekends between 8-16 o'clock");
        }

        String query = "INSERT INTO reservations (username, year, month, day, hour, barberId, hairLength) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (var statement = repository.getPreparedStatement(query))
        {
            statement.setString(1, user.username());
            statement.setInt(2, timeDetails.year());
            statement.setInt(3, timeDetails.month());
            statement.setInt(4, timeDetails.day());
            statement.setInt(5, timeDetails.hour());
            statement.setLong(6, barberHairdresser.id());
            statement.setString(7, hairLength);
            statement.execute();
        }
        catch (SQLException ex)
        {
            logger.warning(ex.getMessage());
            return false;
        }

        return true;
    }


    @Override
    public List<Reservation> getReservationsOfDay(int year, int month, int day)
    {
        String query = "SELECT * FROM reservations WHERE year == ? AND month == ? AND day == ?";
        List<Reservation> result = new ArrayList<>();

        try (var statement = repository.getPreparedStatement(query))
        {
            statement.setLong(1, year);
            statement.setLong(2, month);
            statement.setLong(3, day);

            var reservationsResult = statement.executeQuery();

            while (reservationsResult.next())
            {
                var timeDetails = new TimeDetails(
                        reservationsResult.getInt("year"),
                        reservationsResult.getInt("month"),
                        reservationsResult.getInt("day"),
                        reservationsResult.getInt("hour")
                );

                var reservation = new Reservation(reservationsResult.getString("username"),
                        timeDetails,
                        reservationsResult.getString("hairLength"),
                        reservationsResult.getInt("barberId"));

                result.add(reservation);
            }
        }
        catch (SQLException ex)
        {
            logger.warning(ex.getMessage());
        }

        return result;
    }

    @Override
    public List<Reservation> getReservationsOfDay(BarberHairdresser barberHairdresser, int year, int month, int day)
    {
        String query = "SELECT * FROM reservations WHERE year == ? AND month == ? AND day == ? AND barberId == ?";
        List<Reservation> result = new ArrayList<>();

        try (var statement = repository.getPreparedStatement(query))
        {
            statement.setLong(1, year);
            statement.setLong(2, month);
            statement.setLong(3, day);
            statement.setLong(4, barberHairdresser.id());

            var reservationsResult = statement.executeQuery();

            while (reservationsResult.next())
            {
                var timeDetails = new TimeDetails(
                        reservationsResult.getInt("year"),
                        reservationsResult.getInt("month"),
                        reservationsResult.getInt("day"),
                        reservationsResult.getInt("hour")
                );

                var reservation = new Reservation(reservationsResult.getString("username"),
                        timeDetails,
                        reservationsResult.getString("hairLength"),
                        reservationsResult.getInt("barberId"));

                result.add(reservation);
            }
        }
        catch (SQLException ex)
        {
            logger.warning(ex.getMessage());
        }

        return result;
    }
}
