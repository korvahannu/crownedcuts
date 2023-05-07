package com.crownedcuts.booking.services;

import com.crownedcuts.booking.mappers.ReservationRowMapper;
import com.crownedcuts.booking.mappers.UserDetailsRowMapper;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class UserServiceImpl implements UserService
{
    private final DbRepository repository;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsRowMapper userDetailsRowMapper;
    private final ReservationRowMapper reservationRowMapper;

    @Autowired
    public UserServiceImpl(DbRepository repository,
                           PasswordEncoder passwordEncoder,
                           UserDetailsRowMapper userDetailsRowMapper,
                           ReservationRowMapper reservationRowMapper)
    {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsRowMapper = userDetailsRowMapper;
        this.reservationRowMapper = reservationRowMapper;
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
            var userDetailsRaw = userDetailsRowMapper
                    .mapRow(result);

            var userDetailsWithRoles = new UserDetails(
                    userDetailsRaw.username(),
                    userDetailsRaw.password(),
                    userDetailsRaw.firstname(),
                    userDetailsRaw.lastname(),
                    userDetailsRaw.phonenumber(),
                    userDetailsRaw.dateOfBirth(),
                    roles
            );

            return roles.isEmpty()
                    ? Optional.empty()
                    : Optional.of(userDetailsWithRoles);
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
        ArrayList<GrantedAuthority> roles = new ArrayList<>();

        try (var statement = repository.getPreparedStatement(query))
        {
            statement.setString(1, username);
            var result = statement.executeQuery();

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
            var rs = statement.executeQuery();
            result = reservationRowMapper
                    .processResultSet(rs);
        }
        catch (Exception ex)
        {
            logger.info("Failed to retrieve roles for user " + username);
        }

        return result;
    }

    @Override
    public boolean updateUserInformation(String username,
                                         String firstname,
                                         String lastname,
                                         String phonenumber,
                                         String dateOfBirth)
    {
        String query = "UPDATE users SET firstname = ?, lastname = ?, phonenumber = ?, dateOfBirth = ? WHERE username = ?";

        try (var statement = repository.getPreparedStatement(query))
        {
            statement.setString(1, firstname);
            statement.setString(2, lastname);
            statement.setString(3, phonenumber);
            statement.setString(4, dateOfBirth);
            statement.setString(5, username);
            statement.executeUpdate();

            if (statement.getUpdateCount() != 1)
            {
                logger.log(Level.SEVERE, "Possible destructive query processed: {0}", statement);
            }
        }
        catch (SQLException ex)
        {
            logger.warning("Failed to update user information.");
            return false;
        }

        return true;
    }

    /**
     * Helper function that inserts a user to the database.
     * Does NOT insert any roles that a UserDetails -instance might have
     *
     * @param connection This helper function needs a valid connection to the database
     * @param user       User to add to the database. Only username and password are required
     * @return true if user added successfully, otherwise false
     */
    private boolean insertUserToDatabase(Connection connection, UserDetails user)
    {
        var query = "insert into users (username, password, firstname, lastname, phonenumber, dateOfBirth) values (?, ?, ?, ?, ?, ?)";
        try (var statement = connection.prepareStatement(query))
        {
            statement.setString(1, user.username());
            statement.setString(2, passwordEncoder.encode(user.password()));
            statement.setString(3, user.firstname());
            statement.setString(4, user.lastname());
            statement.setString(5, user.phonenumber());
            statement.setString(6, user.dateOfBirth());
            statement.execute();
        }
        catch (Exception ex)
        {
            logger.warning("Failed to add user "
                    + new UserDetails(user.username(), null, null, null, null, null, user.authorities()));
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
     * @param username   Username that the given roles belong to
     * @param roles      List of roles to add to the database
     * @return true if everything went OK, otherwise false
     */
    private boolean insertRolesToDatabase(Connection connection,
                                          String username,
                                          Collection<? extends GrantedAuthority> roles)
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
                    + new UserDetails(username, null, null, null, null, null, roles));
            return false;
        }

        return true;
    }

    @Override
    public boolean updateUserPassword(String username, String oldpassword, String newpassword) {
        String query = "UPDATE users SET password = ? WHERE username = ? AND password = ?";

        try (var statement = repository.getPreparedStatement(query))
        {
            statement.setString(1, newpassword);
            statement.setString(2, username);
            statement.setString(3, oldpassword);
            statement.executeUpdate();

            if (statement.getUpdateCount() != 1)
            {
                logger.log(Level.SEVERE, "Possible destructive query processed: {0}", statement);
            }
        }
        catch (SQLException ex)
        {
            logger.warning("Failed to update user information.");
            return false;
        }
        return true;
    }
}
