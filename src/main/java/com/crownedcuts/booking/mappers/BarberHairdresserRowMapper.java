package com.crownedcuts.booking.mappers;

import com.crownedcuts.booking.records.BarberHairdresser;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class BarberHairdresserRowMapper implements RowMapper<BarberHairdresser>
{
    @Override
    public BarberHairdresser mapRow(ResultSet rs) throws SQLException
    {
        return new BarberHairdresser(rs.getLong("id"),
                rs.getString("name"));
    }
}
