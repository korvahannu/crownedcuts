package com.crownedcuts.booking.mappers;

import com.crownedcuts.booking.records.Service;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ServiceRowMapper implements RowMapper<Service>
{
    @Override
    public Service mapRow(ResultSet rs) throws SQLException
    {
        return new Service(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("name_en"),
                rs.getDouble("price"),
                rs.getBoolean("isBarberService")
        );
    }
}
