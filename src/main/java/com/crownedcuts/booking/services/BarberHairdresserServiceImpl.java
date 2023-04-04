package com.crownedcuts.booking.services;

import com.crownedcuts.booking.records.BarberHairdresser;
import com.crownedcuts.booking.repositories.DbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class BarberHairdresserServiceImpl implements BarberHairdresserService
{

    private final DbRepository repository;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    public BarberHairdresserServiceImpl(DbRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public Optional<BarberHairdresser> getBarber(long id)
    {
        String query = "SELECT * FROM barbers WHERE id = ?";

        try(var statement = repository.getPreparedStatement(query))
        {
            statement.setLong(1, id);

            var result = statement.executeQuery();

            if(result.next()) {
                return Optional.of(
                  new BarberHairdresser(result.getLong("id"),
                          result.getString("name"))
                );
            }
        }
        catch(SQLException ex)
        {
            logger.warning(ex.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public Optional<BarberHairdresser> getBarber(String name)
    {
        String query = "SELECT * FROM barbers WHERE name = ?";

        try(var statement = repository.getPreparedStatement(query))
        {
            statement.setString(1, name);

            var result = statement.executeQuery();

            if(result.next()) {
                return Optional.of(
                        new BarberHairdresser(result.getLong("id"),
                                result.getString("name"))
                );
            }
        }
        catch(SQLException ex)
        {
            logger.warning(ex.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public List<BarberHairdresser> getAllBarbers()
    {
        String query = "SELECT * from barbers";
        List<BarberHairdresser> result = new ArrayList<>();

        try(var statement = repository.getPreparedStatement(query))
        {
            try(var resultSet = statement.executeQuery())
            {
                while (resultSet.next())
                {
                    result.add(new BarberHairdresser(
                            resultSet.getInt("id"),
                            resultSet.getString("name")
                    ));
                }
            }
        }
        catch(SQLException ex)
        {
            logger.warning(ex.getMessage());
        }

        return  result;
    }
}
