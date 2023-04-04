package com.crownedcuts.booking.services;

import com.crownedcuts.booking.records.BarberHairdresser;

import java.util.List;
import java.util.Optional;

/**
 * BarberHairdresserService is responsible for barber -specific data
 */
public interface BarberHairdresserService
{
    /**
     * Gets barber/hairdresser information from the database and returns it
     * @param id to search the barber/hairdresser with
     * @return Optional
     */
    Optional<BarberHairdresser> getBarber(long id);

    /**
     * Gets barber/hairdresser information from the database and returns it
     * @param name to search the barber/hairdresser with
     * @return Optional
     */
    Optional<BarberHairdresser> getBarber(String name);

    /**
     * Gets the list of all barbers/hairdressers in the database
     * @return List of barbers/hairdressers
     */
    List<BarberHairdresser> getAllBarbers();
}
