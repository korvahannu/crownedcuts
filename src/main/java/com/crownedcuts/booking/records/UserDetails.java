package com.crownedcuts.booking.records;

import org.springframework.security.core.GrantedAuthority;

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
}
