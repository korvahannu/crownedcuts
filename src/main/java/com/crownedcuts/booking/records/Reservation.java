package com.crownedcuts.booking.records;

public record Reservation(String username, TimeDetails reservationInformation, long barberId)
{
}
