package com.crownedcuts.booking.development;

import com.crownedcuts.booking.records.UserDetails;
import com.crownedcuts.booking.services.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

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
        initializerUser("admin@crownedcuts.fi", "admin", "USER", "ADMIN");
        initializerUser("user@crownedcuts.fi", "user", "USER");
    }

    private void initializerUser(String username, String password, String... roles)
    {
        var userRoles = new ArrayList<SimpleGrantedAuthority>();

        for (String role : roles)
        {
            userRoles.add(new SimpleGrantedAuthority(role));
        }

        userService.addUser(new UserDetails(username, password, userRoles));
    }
}
