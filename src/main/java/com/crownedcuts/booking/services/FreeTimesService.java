package com.crownedcuts.booking.services;

import com.crownedcuts.booking.records.AvailableTime;
import java.util.List;

public interface FreeTimesService
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
}
