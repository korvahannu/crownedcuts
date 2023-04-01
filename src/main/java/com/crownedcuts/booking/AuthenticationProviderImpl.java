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
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        var userToCheck = UserDetails.of(username, password, null);

        if (userService.checkUserPassword(userToCheck))
        {
            return userService.getUser(username)
                    .map(userDetails -> new UsernamePasswordAuthenticationToken(userDetails.username(), null, userDetails.authorities()))
                    .orElse(null);
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication)
    {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
