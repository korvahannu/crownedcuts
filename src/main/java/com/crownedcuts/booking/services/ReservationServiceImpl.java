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
    public List<AvailableTime> getAllFreeTimes(ZonedDateTime startDate, ZonedDateTime endDate)
    {
        if(startDate.plusWeeks(1).isAfter(endDate))
        {
            throw new IllegalArgumentException("To save resources, you can only fetch free times up to one week at a time");
        }

        var reservations = getAllReservations(startDate, endDate);
        List<AvailableTime> result = new ArrayList<>();

        var currentTime = startDate.plusHours(1);
        var barbers = barberHairdresserService.getAllBarbers();

        do
        {

            if(currentTime.getDayOfWeek().equals(DayOfWeek.SATURDAY) || currentTime.getDayOfWeek().equals(DayOfWeek.SUNDAY))
            {
                currentTime = currentTime.plusDays(1);
                continue;
            }

            if(currentTime.getHour() >= 16)
            {
                currentTime = currentTime.plusDays(1);
                currentTime = currentTime.minusHours(8);
                continue;
            }

            if(currentTime.getHour() < 8)
            {
                currentTime = currentTime.plusHours(1);
                continue;
            }

            // current reservations for this hour
            var currentReservations = new ArrayList<Reservation>();
            for(Reservation r : reservations)
            {
                if(r.time().getHour() == currentTime.getHour() && r.time().getDayOfYear() == currentTime.getDayOfYear())
                {
                    currentReservations.add(r);
                }
            }

            // current available barbers for this hour
            List<BarberHairdresser> currentBarbers = new ArrayList<>(barbers);
            for(Reservation r : currentReservations)
            {
                for(BarberHairdresser b : currentBarbers)
                {
                    if(b.id() == r.barberId())
                    {
                        currentBarbers = currentBarbers
                                .stream()
                                .filter(ba -> ba.id() == b.id())
                                .toList();
                    }
                }
            }

            result.add(new AvailableTime(currentTime, currentBarbers));

            currentTime = currentTime.plusHours(1);
        } while (currentTime.isBefore(endDate));

        return result;
    }

    @Override
    public List<AvailableTime> getAllFreeTimes(BarberHairdresser barberHairdresser, ZonedDateTime startDate, ZonedDateTime endDate)
    {
        if(startDate.plusWeeks(1).isAfter(endDate))
        {
            throw new IllegalArgumentException("To save resources, you can only fetch free times up to one week at a time");
        }

        var reservations = getAllReservations(startDate, endDate);
        List<AvailableTime> result = new ArrayList<>();

        var currentTime = startDate.plusHours(1);
        var barbers = barberHairdresserService.getAllBarbers();

        do
        {

            if(currentTime.getDayOfWeek().equals(DayOfWeek.SATURDAY) || currentTime.getDayOfWeek().equals(DayOfWeek.SUNDAY))
            {
                currentTime = currentTime.plusDays(1);
                continue;
            }

            if(currentTime.getHour() >= 16)
            {
                currentTime = currentTime.plusDays(1);
                currentTime = currentTime.minusHours(8);
                continue;
            }

            if(currentTime.getHour() < 8)
            {
                currentTime = currentTime.plusHours(1);
                continue;
            }

            boolean isBarberAvailable = true;
            for(Reservation r : reservations)
            {
                if(r.time().getHour() == currentTime.getHour() && r.time().getDayOfYear() == currentTime.getDayOfYear())
                {
                    if(r.barberId() == barberHairdresser.id())
                    {
                        isBarberAvailable = false;
                    }
                }
            }

            if(isBarberAvailable)
            {
                result.add(new AvailableTime(currentTime, List.of(barberHairdresser)));
            }

            currentTime = currentTime.plusHours(1);
        } while (currentTime.isBefore(endDate));

        return result;
    }

    @Override
    public boolean reserveTime(BarberHairdresser barberHairdresser, UserDetails user, ZonedDateTime time)
    {
        String query = "INSERT INTO reservations (username, dateAndTime, barberId) VALUES (?, ?, ?)";

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

        String query = "SELECT * FROM reservations WHERE dateAndTime >= ? AND dateAndTime < ?";
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

        String query = "SELECT * FROM reservations WHERE dateAndTime >= ? AND dateAndTime < ? AND barberId == ?";
        List<Reservation> result = new ArrayList<>();

        try(var statement = repository.getPreparedStatement(query))
        {
            statement.setLong(1, startDateEpoch);
            statement.setLong(2, endDateEpoch);
            statement.setLong(2, barberHairdresser.id());

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
}
