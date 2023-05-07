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
     *
     * @param username to search for
     * @return List of reservations
     */
    List<Reservation> getReservations(String username);

    /**
     * Updates information of user. Does NOT update password.
     *
     * @param username    Username is used to find the correct user to update. Username can not be updated
     * @param firstname   Firstname to set for the user
     * @param lastname    Lastname to set for the user
     * @param phonenumber Phonenumber of user
     * @param dateOfBirth Date of Birth of user
     * @return true if update was successful, otherwise false
     */
    boolean updateUserInformation(String username,
                                  String firstname,
                                  String lastname,
                                  String phonenumber,
                                  String dateOfBirth);

    /**
     * Updates user's password.
     * @param username      Username is used to find the correct user to update. Username can not be updated
     * @param password   User's password to be changed
     * @return true is update was succesful, otherwise false 
     */
    boolean updateUserPassword(String username,
                               String password);

}
