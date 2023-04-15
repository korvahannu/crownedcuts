package com.crownedcuts.booking.mappers;

import com.crownedcuts.booking.records.Reservation;
import com.crownedcuts.booking.records.TimeDetails;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReservationRowMapper implements RowMapper<Reservation>
{

    @Override
    public Reservation mapRow(ResultSet rs) throws SQLException
    {
        var timeDetails = new TimeDetails(
                rs.getInt("year"),
                rs.getInt("month"),
                rs.getInt("day"),
                rs.getInt("hour")
        );

        return new Reservation(rs.getString("username"),
                timeDetails,
                rs.getString("hairLength"),
                rs.getInt("barberId"));
    }
}
