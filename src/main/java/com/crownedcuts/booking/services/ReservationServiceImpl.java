package com.crownedcuts.booking.services;

import com.crownedcuts.booking.records.*;
import com.crownedcuts.booking.repositories.DbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ReservationServiceImpl implements ReservationService
{
    private final DbRepository repository;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    public ReservationServiceImpl(DbRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public boolean reserveTime(BarberHairdresser barberHairdresser,
                               UserDetails user,
                               TimeDetails timeDetails,
                               String hairLength)
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
    @Transactional
    public boolean reserveTime(BarberHairdresser barberHairdresser,
                               UserDetails user,
                               TimeDetails timeDetails,
                               String hairLength,
                               List<String> serviceIds)
    {
        if (timeDetails.isOutsideWorkingHoursAndDays())
        {
            throw new IllegalArgumentException("You may only reserve for weekends between 8-16 o'clock");
        }

        try (var connection = repository.getIsolatedConnection())
        {
            connection.setAutoCommit(false);

            insertIntoReservations(connection,
                    user.username(),
                    timeDetails,
                    barberHairdresser.id(),
                    hairLength);

            int reservationId = getReservationId(connection, timeDetails, barberHairdresser.id());

            for (String serviceId : serviceIds)
            {
                insertIntoServices(connection, reservationId, serviceId);
            }

            connection.commit();
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

    /**
     * Helper function that inserts a reservation into the database
     *
     * @param connection          Connection that will be used
     * @param username            Username who the reservation is for
     * @param timeDetails         Details when the reservation is (year, month, day, hour)
     * @param barberHairdresserId id of barber/hairdresser
     * @param hairLength          Length of the hair
     * @throws SQLException If SQL connection fails
     */
    private void insertIntoReservations(Connection connection,
                                        String username,
                                        TimeDetails timeDetails,
                                        long barberHairdresserId,
                                        String hairLength) throws SQLException
    {
        String query = "INSERT INTO reservations (username, year, month, day, hour, barberId, hairLength) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (var statement = connection.prepareStatement(query))
        {
            statement.setString(1, username);
            statement.setInt(2, timeDetails.year());
            statement.setInt(3, timeDetails.month());
            statement.setInt(4, timeDetails.day());
            statement.setInt(5, timeDetails.hour());
            statement.setLong(6, barberHairdresserId);
            statement.setString(7, hairLength);
            statement.execute();
        }
    }

    /**
     * Helper function that gets a single reservation of the database using provided information and returns the id of it
     *
     * @param connection          Connection to use
     * @param timeDetails         Details of when the reservation should be
     * @param barberHairdresserId The unique id of barber/hairdresser
     * @return id as type int
     * @throws SQLException If connection fails
     */
    private int getReservationId(Connection connection,
                                 TimeDetails timeDetails,
                                 long barberHairdresserId) throws SQLException
    {
        String reservationQuery = "SELECT * FROM reservations WHERE year=? AND month=? AND day=? AND hour=? AND barberId=?";
        try (var statement = connection.prepareStatement(reservationQuery))
        {
            statement.setInt(1, timeDetails.year());
            statement.setInt(2, timeDetails.month());
            statement.setInt(3, timeDetails.day());
            statement.setInt(4, timeDetails.hour());
            statement.setLong(5, barberHairdresserId);

            var result = statement.executeQuery();

            if (result.next())
            {
                return result.getInt("id");
            }
        }

        throw new SQLException("Failed to find reservation id.");
    }

    /**
     * Helper function that inserts a service_of_reservation to the database
     * Reservations and services have many-to-many relationship, this is the glue that holds them together
     *
     * @param connection    Connection to use
     * @param reservationId Id of reservation (int)
     * @param serviceId     Id of service (String)
     * @throws SQLException If connection fails
     */
    private void insertIntoServices(Connection connection, int reservationId, String serviceId) throws SQLException
    {
        String serviceQuery = "INSERT INTO service_of_reservation(reservationid, serviceid) VALUES (?, ?)";
        try (var statement = connection.prepareStatement(serviceQuery))
        {
            statement.setInt(1, reservationId);
            statement.setString(2, serviceId);
            statement.execute();
        }
    }
}
