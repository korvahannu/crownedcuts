package com.crownedcuts.booking.development;

import com.crownedcuts.booking.records.UserDetails;
import com.crownedcuts.booking.services.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Helper method for development environments
 * Adds two users to the database: admin and a regular user
 */
@Component
@Profile("dev")
public class UserInitializer
{
    private final UserService userService;

    @Autowired
    public UserInitializer(UserService userService)
    {
        this.userService = userService;
    }

    @PostConstruct
    public void initializeUsers()
    {
        userService.addUser(UserDetails.of("admin@crownedcuts.fi",
                "admin",
                "admin firstname",
                "admin lastname",
                "USER",
                "ADMIN"));
        userService.addUser(UserDetails.of("user@crownedcuts.fi", "user", "user firstname", "user lastname", "USER"));
    }
}
