package com.crownedcuts.booking.services;

import com.crownedcuts.booking.records.*;

import java.util.List;

/**
 * This service is responsible for getting the information about free times
 * and for making a reservation for the end user
 */
public interface ReservationService
{
    /**
     * Returns a list of available times that can be reserved on a given date.
     *
     * @param year  in which we want to inspect free times
     * @param month in which we want to inspect free times
     * @param day   in which we want to inspect free times
     * @return a list of AvailableTimes, length of 0 if there are none
     */
    List<AvailableTime> getAllFreeTimesOnDay(int year, int month, int day);

    /**
     * Reserves a time for a user and a barber/haidresser
     *
     * @param barberHairdresser barber/haidresser that does the job
     * @param user              that comes to the saloon
     * @param timeDetails       details of the time (year, month, day, hour)
     * @return true if time was successfully reserved
     */
    boolean reserveTime(BarberHairdresser barberHairdresser, UserDetails user, TimeDetails timeDetails);

    /**
     * Gets list of all reservations for a given day
     *
     * @param year  at which the reservations should be looked up at
     * @param month at which the reservations should be looked up at
     * @param day   at which the reservations should be looked up at
     * @return
     */
    List<Reservation> getReservationsOfDay(int year, int month, int day);

    /**
     * Gets list of all reservations for a given day of a specific barber
     *
     * @param barberHairdresser Whose reservations we want
     * @param year              at which the reservations should be looked up at
     * @param month             at which the reservations should be looked up at
     * @param day               at which the reservations should be looked up at
     * @return
     */
    List<Reservation> getReservationsOfDay(BarberHairdresser barberHairdresser, int year, int month, int day);
}
