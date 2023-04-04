package com.crownedcuts.booking.services;

import com.crownedcuts.booking.records.AvailableTime;
import com.crownedcuts.booking.records.BarberHairdresser;
import com.crownedcuts.booking.records.Reservation;
import com.crownedcuts.booking.records.UserDetails;
import com.crownedcuts.booking.repositories.DbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public List<AvailableTime> getAllFreeTimes(ZonedDateTime startDate, ZonedDateTime endDate)
    {
        if(startDate.plusWeeks(1).isBefore(endDate))
        {
            throw new IllegalArgumentException("You can only fetch free times up to one week at a time");
        }

        var reservations = getAllReservations(startDate, endDate);
        List<AvailableTime> result = new ArrayList<>();

        var currentTime = startDate.plusHours(1);
        var barbers = barberHairdresserService.getAllBarbers();

        do
        {
            var advancedTime = advanceIfOutsideWorkingHours(currentTime);

            if(!advancedTime.equals(currentTime))
            {
                currentTime = advancedTime;
                continue;
            }

            getAvailableTime(currentTime, reservations, barbers)
                    .ifPresent(result::add);

            currentTime = currentTime.plusHours(1);
        } while (currentTime.isBefore(endDate));

        return result;
    }

    @Override
    public boolean reserveTime(BarberHairdresser barberHairdresser, UserDetails user, ZonedDateTime time)
    {
        if(isWeekend(time.getDayOfWeek()) || time.getHour() < 8 || time.getHour() > 15)
        {
            throw new IllegalArgumentException("You may only reserve for weekends between 8-16 o'clock");
        }

        String query = "INSERT INTO reservations (username, dateAndTime, barberId) VALUES (?, ?, ?)";
        time = time.truncatedTo(ChronoUnit.HOURS);

        try(var statement = repository.getPreparedStatement(query))
        {
            statement.setString(1, user.username());
            statement.setLong(2, time.toInstant().toEpochMilli());
            statement.setLong(3, barberHairdresser.id());
            statement.execute();
        }
        catch(SQLException ex)
        {
            logger.warning(ex.getMessage());
            return false;
        }

        return true;
    }


    @Override
    public List<Reservation> getAllReservations(ZonedDateTime start, ZonedDateTime end)
    {
        long startDateEpoch = start.toInstant().toEpochMilli();
        long endDateEpoch = end.toInstant().toEpochMilli();

        String query = "SELECT * FROM reservations WHERE dateAndTime >= ? AND dateAndTime <= ?";
        List<Reservation> result = new ArrayList<>();

        try(var statement = repository.getPreparedStatement(query))
        {
            statement.setLong(1, startDateEpoch);
            statement.setLong(2, endDateEpoch);

            var reservationsResult = statement.executeQuery();

            while (reservationsResult.next())
            {
                var reservation = new Reservation(reservationsResult.getString("username"),
                        ZonedDateTime.ofInstant(Instant.ofEpochMilli(reservationsResult.getLong("dateAndTime")), ZoneId.systemDefault()),
                        reservationsResult.getInt("barberId"));

                result.add(reservation);
            }
        }
        catch(SQLException ex)
        {
            logger.warning(ex.getMessage());
        }

        return result;
    }

    @Override
    public List<Reservation> getAllReservations(BarberHairdresser barberHairdresser, ZonedDateTime start, ZonedDateTime end)
    {
        long startDateEpoch = start.toInstant().toEpochMilli();
        long endDateEpoch = end.toInstant().toEpochMilli();

        String query = "SELECT * FROM reservations WHERE dateAndTime >= ? AND dateAndTime <= ? AND barberId == ?";
        List<Reservation> result = new ArrayList<>();

        try(var statement = repository.getPreparedStatement(query))
        {
            statement.setLong(1, startDateEpoch);
            statement.setLong(2, endDateEpoch);
            statement.setLong(3, barberHairdresser.id());

            var reservationsResult = statement.executeQuery();

            while (reservationsResult.next())
            {
                var reservation = new Reservation(reservationsResult.getString("username"),
                        ZonedDateTime.ofInstant(Instant.ofEpochMilli(reservationsResult.getLong("dateAndTime")), ZoneId.systemDefault()),
                        reservationsResult.getInt("barberId"));

                result.add(reservation);
            }
        }
        catch(SQLException ex)
        {
            logger.warning(ex.getMessage());
        }

        return result;
    }

    /**
     * Helper function that determines if the dayOfWeek is a weekend day
     *
     * @param dayOfWeek to check
     * @return true if weekend, false otherwise
     */
    private boolean isWeekend(DayOfWeek dayOfWeek)
    {
        return dayOfWeek.equals(DayOfWeek.SATURDAY) || dayOfWeek.equals(DayOfWeek.SUNDAY);
    }

    /**
     * Helper function that checks if a given time is outside working hours or at a weekend
     * If the given time is a weekend, the time is advanced +1 day
     * If the given time is past 16 o'clock, the time is advanced +1 day -8 hours
     * If the given time is before 8, the time is advanced +1 hour
     *
     * @param zonedDateTime Time to advance
     * @return A copy of the given time with the proper advancements made
     */
    private ZonedDateTime advanceIfOutsideWorkingHours(ZonedDateTime zonedDateTime)
    {
        if(isWeekend(zonedDateTime.getDayOfWeek()))
        {
            zonedDateTime = zonedDateTime.plusDays(1);
        }
        else if(zonedDateTime.getHour() >= 16)
        {
            zonedDateTime = zonedDateTime.plusDays(1).minusHours(8);
        }
        else if(zonedDateTime.getHour() < 8)
        {
            zonedDateTime = zonedDateTime.plusHours(1);
        }

        return zonedDateTime;
    }

    /**
     * A helper function that gets the available time at an hour (if any)
     *
     * @param time to check the reservations at. Only the year, month, day and hour are taken into consideration
     * @param reservations List of ALL current reservations
     * @param barbers List of all barbers
     * @return Available time or empty
     */
    private Optional<AvailableTime> getAvailableTime(ZonedDateTime time, List<Reservation> reservations, List<BarberHairdresser> barbers)
    {
        var currentReservations = new ArrayList<Reservation>();

        for(Reservation r : reservations)
        {
            if(r.time().getHour() == time.getHour() && r.time().getDayOfYear() == time.getDayOfYear())
            {
                currentReservations.add(r);
            }
        }

        List<BarberHairdresser> currentBarbers = new ArrayList<>(barbers);
        for(Reservation r : currentReservations)
        {
            for(BarberHairdresser b : currentBarbers)
            {
                if(b.id() == r.barberId())
                {
                    currentBarbers = currentBarbers
                            .stream()
                            .filter(ba -> ba.id() != b.id())
                            .toList();
                }
            }
        }

        if(currentBarbers.isEmpty())
        {
            return Optional.empty();
        }

        return Optional.of(new AvailableTime(time, currentBarbers));
    }
}
