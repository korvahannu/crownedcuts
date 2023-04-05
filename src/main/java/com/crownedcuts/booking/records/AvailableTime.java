package com.crownedcuts.booking.records;

import java.util.List;

public record AvailableTime(int year, int month, int day, int hour, List<BarberHairdresser> barbersAvailable)
{
}
