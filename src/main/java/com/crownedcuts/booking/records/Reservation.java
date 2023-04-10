package com.crownedcuts.booking.records;

public record Reservation(String username, TimeDetails reservationInformation, String hairLength, long barberId)
{
    public Reservation
    {
        if (!hairLength.equals("long") && !hairLength.equals("medium") && !hairLength.equals("short"))
        {
            throw new IllegalArgumentException("Hair length must be long, medium or short");
        }
    }
}
