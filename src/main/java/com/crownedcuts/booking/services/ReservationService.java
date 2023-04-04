package com.crownedcuts.booking.services;

import com.crownedcuts.booking.records.AvailableTime;
import com.crownedcuts.booking.records.BarberHairdresser;
import com.crownedcuts.booking.records.Reservation;
import com.crownedcuts.booking.records.UserDetails;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * This service is responsible for getting the information about free times
 * and for making a reservation for the end user
 */
public interface ReservationService
{
    /**
     * Returns a list of free times
     * @param startDate Start date to start looking free times for
     * @param endDate End date to start looking free times for
     * @return List of available times
     */
    List<AvailableTime> getAllFreeTimes (ZonedDateTime startDate, ZonedDateTime endDate);

    /**
     * Returns a list of free times
     * @param barberHairdresser We can use this to specify that we want free times of a specific barber only
     * @param startDate Start date to start looking free times for
     * @param endDate End date to start looking free times for
     * @return List of available times
     */
    List<AvailableTime> getAllFreeTimes(BarberHairdresser barberHairdresser, ZonedDateTime startDate, ZonedDateTime endDate);

    /**
     * Reserves a barber/hairdresser for a user at a given date and time
     * @param barberHairdresser who does the hairdressing
     * @param user who the reservation is for
     * @param time when is the time
     * @return true if reservation was successful, otherwise false
     */
    boolean reserveTime(BarberHairdresser barberHairdresser, UserDetails user, ZonedDateTime time);

    /**
     * Gets all reservations between two ZonedDateTimes
     * @param start where to start checking
     * @param end where to end checking
     * @return List of reservations
     */
    List<Reservation> getAllReservations(ZonedDateTime start, ZonedDateTime end);

    /**
     * Gets all reservations between two ZonedDateTimes that a barber/haidresses has
     * @param start where to start checking
     * @param end where to end checking
     * @return List of reservations
     */
    List<Reservation> getAllReservations(BarberHairdresser barberHairdresser, ZonedDateTime start, ZonedDateTime end);
}
