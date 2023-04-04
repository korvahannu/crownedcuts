package com.crownedcuts.booking.services;

import com.crownedcuts.booking.records.Reservation;
import com.crownedcuts.booking.records.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * UserService is responsible for getting user related information
 */
public interface UserService
{
    /**
     * Returns user information with a given username
     *
     * @param username Username to search for
     * @return Returns an optional UserDetails that contains wanted information
     */
    Optional<UserDetails> getUser(String username);

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

    /**
     * Gets the list of reservations the user has made
     * @param username to search for
     * @return List of reservations
     */
    List<Reservation> getReservations(String username);
}
