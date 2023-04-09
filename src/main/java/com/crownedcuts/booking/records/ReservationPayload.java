package com.crownedcuts.booking.records;

import java.util.List;

public record ReservationPayload(String serviceType, String hairLength, List<String> services, int year, int month,
                                 int day, int hour, long barberId, boolean isBarberService)
{
}
