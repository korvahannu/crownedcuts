package com.crownedcuts.booking;

import com.crownedcuts.booking.records.UserDetails;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthenticationProviderImpl implements AuthenticationProvider
{
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
    	String username = authentication.getName();
    	String password = authentication.getPrincipal().toString();
    	
        if (username.equals("crowned") && password.equals("cuts"))
        {
            List<SimpleGrantedAuthority> roles = new ArrayList<>();
            roles.add(new SimpleGrantedAuthority("ADMIN"));
            roles.add(new SimpleGrantedAuthority("USER"));

            var userDetails = new UserDetails(username, roles);

            return new UsernamePasswordAuthenticationToken(userDetails, "cuts", roles);
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication)
    {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
