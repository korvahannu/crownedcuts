package com.crownedcuts.booking.services;

import com.crownedcuts.booking.records.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * UserService is responsible for getting user related information
 */
public interface UserService
{
    /**
     * Returns user information with a given username
     *
     * @param username Username to search for
     * @return Returns UserDetails that contains wanted information or null if user does not exist
     */
    UserDetails getUser(String username);

    /**
     * Checks if UserDetails instance has correct password stored
     *
     * @param user User to check
     * @return true if password matches, false otherwise
     */
    boolean checkUserPassword(UserDetails user);

    /**
     * Adds the user to the target database
     *
     * @param user User to add to the database
     * @return true if user was successfully added, false otherwise
     */
    boolean addUser(UserDetails user);

    /**
     * Gets the user roles using the provided username
     *
     * @return List of roles
     */
    Collection<GrantedAuthority> getUserRoles(String username);
}
