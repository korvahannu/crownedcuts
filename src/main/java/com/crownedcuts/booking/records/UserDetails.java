package com.crownedcuts.booking.records;

import com.crownedcuts.booking.Regex;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Record that represents detailed information of a user
 *
 * @param username    Username, should be in the form of an email
 * @param password    Password container. Please note that password is not automatically encoded.
 * @param authorities List of authorities, or roles, that the user has
 */
public record UserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities)
{
    public UserDetails
    {
        // Username can be null, but if it is not null it must be in email form
        if (username != null && !Regex.isEmail(username))
        {
            throw new IllegalArgumentException("Username must be in the form of an email");
        }

        // User may have no roles. If user has roles, all roles must be validated
        if (authorities.size() > 0 && !isValidUserRoles(authorities))
        {
            throw new IllegalArgumentException("Roles can only be marked with all caps");
        }
    }

    /**
     * Static method that creates a UserDetails instance.
     *
     * @param username Username for UserDetails
     * @param password Password for UserDetails
     * @param roles    An array of Strings that represent the roles
     * @return UserDetails with the roles type being a SimpleGrantedAuthority
     */
    public static UserDetails of(String username, String password, String... roles)
    {
        var userRoles = new ArrayList<SimpleGrantedAuthority>();
        if (roles != null)
        {
            for (String role : roles)
            {
                userRoles.add(new SimpleGrantedAuthority(role));
            }
        }
        return new UserDetails(username, password, userRoles);
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
