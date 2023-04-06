package com.crownedcuts.booking.records;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public record TimeDetails(int year, int month, int day, int hour)
{
    public boolean isOutsideWorkingHoursAndDays()
    {
        var localDateTime = LocalDateTime.of(year(), month(), day(), hour(), 0);

        return hour() < 8 || hour() >= 16 || localDateTime.getDayOfWeek().equals(DayOfWeek.SUNDAY) || localDateTime.getDayOfWeek().equals(DayOfWeek.SATURDAY);
    }
}
