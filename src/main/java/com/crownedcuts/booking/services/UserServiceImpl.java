package com.crownedcuts.booking.services;

import com.crownedcuts.booking.records.Reservation;
import com.crownedcuts.booking.records.UserDetails;
import com.crownedcuts.booking.repositories.DbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class UserServiceImpl implements UserService
{
    private final DbRepository repository;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(DbRepository repository, PasswordEncoder passwordEncoder)
    {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<UserDetails> getUser(String username)
    {
        var query = "select * from users where username=?";

        try (var statement = repository.getPreparedStatement(query))
        {
            statement.setString(1, username);
            var result = statement.executeQuery();

            if (!result.next())
            {
                return Optional.empty();
            }

            var roles = getUserRoles(username);
            String password = result.getString("password");

            return roles == null
                    ? Optional.empty()
                    : Optional.of(new UserDetails(username, password, roles));
        }
        catch (Exception ex)
        {
            logger.info("User not found: " + username);
            return Optional.empty();
        }
    }

    @Override
    public boolean checkUserPassword(UserDetails user)
    {
        return getUser(user.username())
                .filter(userDetails -> passwordEncoder.matches(user.password(), userDetails.password()))
                .isPresent();
    }

    @Override
    @Transactional
    public boolean addUser(UserDetails user)
    {
        try (var connection = repository.getIsolatedConnection())
        {
            connection.setAutoCommit(false);

            if (insertUserToDatabase(connection, user) &&
                    insertRolesToDatabase(connection, user.username(), user.authorities()))
            {
                connection.commit();
            }
            else
            {
                connection.rollback();
            }
        }
        catch (SQLException ex)
        {
            logger.warning("Failed to get isolated connection.");
            return false;
        }
        return true;
    }

    @Override
    public ArrayList<GrantedAuthority> getUserRoles(String username)
    {
        var query = "select * from roles where username=?";
        ArrayList<GrantedAuthority> roles = null;

        try (var statement = repository.getPreparedStatement(query))
        {
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

    @Override
    public List<Reservation> getReservations(String username)
    {
        String query = "SELECT * FROM reservations WHERE username = ?";
        List<Reservation> result = new ArrayList<>();

        try (var statement = repository.getPreparedStatement(query))
        {
            statement.setString(1, username);
            var resultSet = statement.executeQuery();

            while (resultSet.next())
            {
                result.add(new Reservation(username,
                        ZonedDateTime.ofInstant(
                                Instant.ofEpochMilli(resultSet.getLong("dateAndTime")),
                                ZoneId.systemDefault()),
                        resultSet.getLong("barberId")));
            }
        }
        catch (Exception ex)
        {
            logger.info("Failed to retrieve roles for user " + username);
        }

        return result;
    }

    /**
     * Helper function that inserts a user to the database.
     * Does NOT insert any roles that a UserDetails -instance might have
     *
     * @param connection This helper function needs a valid connection to the database
     * @param user User to add to the database. Only username and password are required
     * @return true if user added successfully, otherwise false
     */
    private boolean insertUserToDatabase(Connection connection, UserDetails user)
    {
        var query = "insert into users (username, password) values (?, ?)";
        try (var statement = connection.prepareStatement(query))
        {
            statement.setString(1, user.username());
            statement.setString(2, passwordEncoder.encode(user.password()));
            statement.execute();
        }
        catch (Exception ex)
        {
            logger.warning("Failed to add user "
                    + new UserDetails(user.username(), null, user.authorities()));
            return false;
        }

        return true;
    }

    /**
     * Helper function that inserts a list of authorities/roles to the database
     * It is highly recommended to pass in a connection with autoCommit set to false
     * and commit only according to the return value
     *
     * @param connection This helper function needs a valid connection to the database
     * @param username Username that the given roles belong to
     * @param roles List of roles to add to the database
     * @return true if everything went OK, otherwise false
     */
    private boolean insertRolesToDatabase(Connection connection, String username, Collection<? extends GrantedAuthority> roles)
    {
        String query = "insert into roles (username, userRole) values (?, ?)";

        try (var statement = connection.prepareStatement(query))
        {
            for (GrantedAuthority authority : roles)
            {

                statement.setString(1, username);
                statement.setString(2, authority.getAuthority());
                statement.execute();
            }
        }
        catch (Exception ex)
        {
            logger.warning("Failed to add roles "
                    + new UserDetails(username, null, roles));
            return false;
        }

        return true;
    }
}
