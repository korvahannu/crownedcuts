package com.crownedcuts.booking.services;

import com.crownedcuts.booking.records.*;

import java.util.List;

/**
 * This service is responsible for getting and making reservations
 */
public interface ReservationService
{
    /**
     * Reserves a time for a user and a barber/haidresser with no services
     *
     * @param barberHairdresser barber/haidresser that does the job
     * @param user              that comes to the saloon
     * @param timeDetails       details of the time (year, month, day, hour)
     * @param hairLength        For each reservation we need the hair length of the customer
     * @return true if time was successfully reserved
     */
    boolean reserveTime(BarberHairdresser barberHairdresser, UserDetails user, TimeDetails timeDetails, String hairLength);

    /**
     * Reserves a time for a user and a barber/haidresser and sets the correct service relationships
     *
     * @param barberHairdresser barber/haidresser that does the job
     * @param user              that comes to the saloon
     * @param timeDetails       details of the time (year, month, day, hour)
     * @param hairLength        For each reservation we need the hair length of the customer
     * @param serviceIds        List of serviceIds
     * @return true if time was successfully reserved
     */
    boolean reserveTime(BarberHairdresser barberHairdresser, UserDetails user, TimeDetails timeDetails, String hairLength, List<String> serviceIds);

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
