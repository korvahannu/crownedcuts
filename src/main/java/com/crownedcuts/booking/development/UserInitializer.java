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
@Profile("!prod")
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
        var adminRoles = new ArrayList<SimpleGrantedAuthority>();
        adminRoles.add(new SimpleGrantedAuthority("USER"));
        adminRoles.add(new SimpleGrantedAuthority("ADMIN"));
        var user = new UserDetails("admin@crownedcuts.fi", "admin", adminRoles);
        userService.addUser(user);

        var userRoles = new ArrayList<SimpleGrantedAuthority>();
        userRoles.add(new SimpleGrantedAuthority("USER"));
        user = new UserDetails("user@crownedcuts.fi", "user", adminRoles);
        userService.addUser(user);
    }
}
