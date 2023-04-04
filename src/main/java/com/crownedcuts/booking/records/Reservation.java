package com.crownedcuts.booking.records;

import java.time.ZonedDateTime;

public record Reservation(String username, ZonedDateTime time, long barberId)
{
}
