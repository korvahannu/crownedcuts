package com.crownedcuts.booking;

import com.crownedcuts.booking.records.UserDetails;
import com.crownedcuts.booking.services.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationProviderImpl implements AuthenticationProvider
{
    private final UserService userService;

    public AuthenticationProviderImpl(UserService userService)
    {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        var userToCheck = new UserDetails(username, password, null);

        if (userService.checkUserPassword(userToCheck))
        {
            var user = userService.getUser(username);
            return new UsernamePasswordAuthenticationToken(user.username(), null, user.authorities());
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication)
    {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
