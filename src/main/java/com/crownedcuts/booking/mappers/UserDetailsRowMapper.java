package com.crownedcuts.booking.mappers;

import com.crownedcuts.booking.records.UserDetails;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Component
public class UserDetailsRowMapper implements RowMapper<UserDetails>
{
    @Override
    public UserDetails mapRow(ResultSet rs) throws SQLException
    {
        String username = rs.getString("username");
        String password = rs.getString("password");
        String firstname = rs.getString("firstname");
        String lastname = rs.getString("lastname");
        String phonenumber = getOrNull(rs, "phonenumber");
        String dateOfBirth = getOrNull(rs, "dateOfBirth");

        return new UserDetails(username, password, firstname, lastname, phonenumber, dateOfBirth, new ArrayList<>());
    }

    @SuppressWarnings("unchecked")
    private <T> T getOrNull(ResultSet resultSet, String key)
    {
        try
        {
            return (T) resultSet.getObject(key);
        }
        catch (Exception ex)
        {
            return null;
        }
    }
}
