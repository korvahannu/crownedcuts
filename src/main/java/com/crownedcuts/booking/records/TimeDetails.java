package com.crownedcuts.booking.records;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public record TimeDetails(int year, int month, int day, int hour) implements Comparable<TimeDetails>
{
    public boolean isOutsideWorkingHoursAndDays()
    {
        var localDateTime = LocalDateTime.of(year(), month(), day(), hour(), 0);

        return hour() < 8 || hour() >= 16 || localDateTime.getDayOfWeek().equals(DayOfWeek.SUNDAY) || localDateTime.getDayOfWeek().equals(
                DayOfWeek.SATURDAY);
    }

    @Override
    public String toString()
    {
        return day() + "." + month() + "." + year() + " " + hour() + ":00-" + (hour() + 1) + ":00";
    }

    @Override
    public int compareTo(TimeDetails timeDetails)
    {
        if (timeDetails.year() > year())
        {
            return -1;
        }
        else if (timeDetails.year() < year())
        {
            return 1;
        }

        if (timeDetails.month() > month())
        {
            return -1;
        }
        else if (timeDetails.month() < month())
        {
            return 1;
        }

        if (timeDetails.day() > day())
        {
            return -1;
        }
        else if (timeDetails.day() < day())
        {
            return 1;
        }

        if (timeDetails.hour() > hour())
        {
            return -1;
        }
        else if (timeDetails.hour() < hour())
        {
            return 1;
        }

        return 0;
    }

    /**
     * Returns the TimeDetails as a LocalDateTime
     * Defaults the minutes to 0
     *
     * @return LocalDateTime
     */
    public LocalDateTime toLocalDateTime()
    {
        return LocalDateTime.of(year(),
                month(),
                day(),
                hour(),
                0);
    }
}
