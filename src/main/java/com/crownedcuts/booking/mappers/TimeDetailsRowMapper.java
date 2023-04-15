package com.crownedcuts.booking.mappers;

import com.crownedcuts.booking.records.TimeDetails;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class TimeDetailsRowMapper implements RowMapper<TimeDetails>
{
    @Override
    public TimeDetails mapRow(ResultSet rs) throws SQLException
    {
        return new TimeDetails(
                rs.getInt("year"),
                rs.getInt("month"),
                rs.getInt("day"),
                rs.getInt("hour")
        );
    }
}
