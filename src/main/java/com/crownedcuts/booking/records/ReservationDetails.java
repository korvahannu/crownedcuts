package com.crownedcuts.booking.records;

import java.time.ZonedDateTime;

public record ReservationDetails(UserDetails userDetails, ZonedDateTime time, BarberHairdresser barberHairdresser)
{
}
