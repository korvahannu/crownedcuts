package com.crownedcuts.booking.services;

import com.crownedcuts.booking.records.UserDetails;
import com.crownedcuts.booking.repositories.DbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.logging.Logger;

@Service
public class UserServiceImpl implements UserService
{
    private final DbRepository repository;
    private final Logger logger;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(DbRepository repository, Logger logger, PasswordEncoder passwordEncoder)
    {
        this.repository = repository;
        this.logger = logger;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails getUser(String username)
    {
        String query = "select * from users where username=?";

        try
        {
            var statement = repository.getPreparedStatement(query);
            statement.setString(1, username);
            var result = statement.executeQuery();

            if (result.next())
            {
                var roles = getUserRoles(username);

                if (roles == null)
                {
                    return null;
                }

                return new UserDetails(
                        result.getString("username"),
                        result.getString("password"),
                        roles
                );
            }
        }
        catch (Exception ex)
        {
            logger.info("User not found: " + username);
        }

        return null;
    }

    @Override
    public boolean checkUserPassword(UserDetails user)
    {
        var userToCheck = getUser(user.username());

        if (userToCheck == null)
        {
            return false;
        }

        return passwordEncoder.matches(user.password(), userToCheck.password());
    }

    @Override
    @Transactional
    public boolean addUser(UserDetails user)
    {
        try
        {
            String query = "insert into users (username, password) values (?, ?)";
            var statement = repository.getPreparedStatement(query);
            statement.setString(1, user.username());
            statement.setString(2, passwordEncoder.encode(user.password()));
            statement.execute();
        }
        catch (Exception ex)
        {
            logger.warning("Failed to add user " + new UserDetails(user.username(), null, user.authorities()));
            return false;
        }

        // Todo: If adding the roles fails, we should delete the added user and roles
        for (GrantedAuthority authority : user.authorities())
        {
            String query = "insert into roles (username, userRole) values (?, ?)";
            try
            {
                var statement = repository.getPreparedStatement(query);
                statement.setString(1, user.username());
                statement.setString(2, authority.getAuthority());
                statement.execute();
            }
            catch (Exception ex)
            {
                logger.warning("Failed to add user " + new UserDetails(user.username(), null, user.authorities()));
                return false;
            }
        }

        return true;
    }

    @Override
    public ArrayList<GrantedAuthority> getUserRoles(String username)
    {
        String query = "select * from roles where username=?";
        ArrayList<GrantedAuthority> roles = null;

        try
        {
            var statement = repository.getPreparedStatement(query);
            statement.setString(1, username);
            var result = statement.executeQuery();
            roles = new ArrayList<>();

            while (result.next())
            {
                roles.add(new SimpleGrantedAuthority(result.getString("userRole").toUpperCase()));
            }
        }
        catch (Exception ex)
        {
            logger.info("Failed to retrieve roles for user " + username);
        }

        return roles;
    }
}
