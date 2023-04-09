package com.crownedcuts.booking.services;

import com.crownedcuts.booking.records.Service;
import com.crownedcuts.booking.repositories.DbRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@org.springframework.stereotype.Service
public class ServicesServiceImpl implements ServicesService
{
    private final DbRepository repository;
    private final Logger logger = Logger.getLogger(ServicesServiceImpl.class.getName());

    public ServicesServiceImpl(DbRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public List<Service> getAllServices()
    {
        String query = "SELECT * FROM services";
        List<Service> services = new ArrayList<>();

        try (var statement = repository.getPreparedStatement(query))
        {
            var result = statement.executeQuery();

            while (result.next())
            {
                services.add(
                        new Service(
                                result.getString("id"),
                                result.getString("name"),
                                result.getDouble("price"),
                                result.getBoolean("isBarberService")
                        )
                );
            }
        }
        catch (SQLException ex)
        {
            logger.warning("Failed to fetch all services.");
        }

        return services;
    }
}
