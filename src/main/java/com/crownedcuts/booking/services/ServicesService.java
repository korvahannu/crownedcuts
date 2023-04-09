package com.crownedcuts.booking.services;

import com.crownedcuts.booking.records.Service;

import java.util.List;

public interface ServicesService
{
    /**
     * Gets a list of all the services in the database
     * @return List of all services
     */
    public List<Service> getAllServices();
}
