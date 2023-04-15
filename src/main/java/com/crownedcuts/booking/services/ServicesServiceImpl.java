package com.crownedcuts.booking.services;

import com.crownedcuts.booking.mappers.ServiceRowMapper;
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
    private final ServiceRowMapper mapper;

    public ServicesServiceImpl(DbRepository repository, ServiceRowMapper mapper)
    {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<Service> getAllServices()
    {
        String query = "SELECT * FROM services";
        List<Service> services = new ArrayList<>();

        try (var statement = repository.getPreparedStatement(query))
        {
            services = mapper.processResultSet(statement.executeQuery());
        }
        catch (SQLException ex)
        {
            logger.warning("Failed to fetch all services.");
        }

        return services;
    }
}
