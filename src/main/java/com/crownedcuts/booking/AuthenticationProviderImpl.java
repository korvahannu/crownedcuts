package com.crownedcuts.booking;

import com.crownedcuts.booking.records.UserDetails;
import com.crownedcuts.booking.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

/**
 * {@inheritDoc}
 * Custom implementation of AuthenticationProvider
 * This authentication requires an implementation of UserService to check user credentials
 */
@Service
public class AuthenticationProviderImpl implements AuthenticationProvider
{
    private final UserService userService;

    @Autowired
    public AuthenticationProviderImpl(UserService userService)
    {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        var username = authentication.getName();
        var password = authentication.getCredentials().toString();
        UserDetails userDetails;

        try
        {
            userDetails = UserDetails.of(username, password);
        }
        catch (IllegalArgumentException ex)
        {
            return null;
        }


        if (!userService.checkUserPassword(userDetails))
        {
            return null;

        }

        return userService.getUser(username)
                .map(u -> new UsernamePasswordAuthenticationToken(u.username(), null, u.authorities()))
                .orElse(null);
    }

    @Override
    public boolean supports(Class<?> authentication)
    {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
