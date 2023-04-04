package com.crownedcuts.booking.records;

import java.time.ZonedDateTime;
import java.util.List;

public record AvailableTime(ZonedDateTime time, List<BarberHairdresser> barbersAvailable)
{
}
