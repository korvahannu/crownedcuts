package com.crownedcuts.booking.repositories;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Repository interface used to communicate with the underlying database
 */
public interface DbRepository
{
    /**
     * Opens the connection to the database
     *
     * @throws SQLException if opening the connection fails
     */
    void openConnection() throws SQLException;

    /**
     * Closes the connection to the database
     *
     * @throws SQLException if closing the database connection fails
     */
    void closeConnection() throws SQLException;

    /**
     * Returns a PreparedStatement that can be then used to query the database
     *
     * @param query Query to run
     * @return Returns the PreparedStatement
     * @throws SQLException If forming a PreparedStatement fails
     */
    PreparedStatement getPreparedStatement(String query) throws SQLException;
}
