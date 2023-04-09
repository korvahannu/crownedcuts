package com.crownedcuts.booking.records;

import com.crownedcuts.booking.Regex;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Record that represents detailed information of a user
 * @param username Username in the form of an email. Throws IllegalArgumentException if wrong format
 * @param password the password, duh!
 * @param firstname First name of user
 * @param lastname Last name of user
 * @param phonenumber Phonenumber of user (optional)
 * @param dateOfBirth Date of birth (optional)
 * @param authorities List of roles user has
 */
public record UserDetails(String username, String password, String firstname, String lastname, String phonenumber, String dateOfBirth, Collection<? extends GrantedAuthority> authorities)
{
    public UserDetails
    {
        // Username can be null, but if it is not null it must be in email form
        if (username != null && !Regex.isEmail(username))
        {
            throw new IllegalArgumentException("Username must be in the form of an email");
        }

        // User may have no roles. If user has roles, all roles must be validated
        if (!authorities.isEmpty() && !isValidUserRoles(authorities))
        {
            throw new IllegalArgumentException("Roles can only be marked with all caps");
        }
    }

    /**
     * Static method that creates a UserDetails instance with only required information
     *
     * @param username Username for UserDetails
     * @param password Password for UserDetails
     * @param roles    An array of Strings that represent the roles
     * @return UserDetails with the roles type being a SimpleGrantedAuthority
     */
    public static UserDetails of(String username, String password, String firstname, String lastname, String... roles)
    {
        var userRoles = new ArrayList<SimpleGrantedAuthority>();
        if (roles != null)
        {
            for (String role : roles)
            {
                userRoles.add(new SimpleGrantedAuthority(role));
            }
        }
        return new UserDetails(username, password, firstname, lastname, null, null, userRoles);
    }

    /**
     * Helper method to check if user roles are compliant with specifications
     *
     * @return True if roles are OK, otherwise false
     */
    private boolean isValidUserRoles(Collection<? extends GrantedAuthority> authorities)
    {
        for (GrantedAuthority a : authorities)
        {
            if (!a.getAuthority().chars().allMatch(Character::isUpperCase))
            {
                return false;
            }
        }

        return true;
    }
}
