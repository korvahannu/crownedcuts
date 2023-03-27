package com.crownedcuts.booking.repositories;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class SQLLiteRepository implements DbRepository
{
    @Value("${databaseFilepath}")
    private String databaseFilepath;

    @Value("${initializeDatabaseSQLFilepath}")
    private String initializeDatabaseSQLFilepath;

    private String connectionString;

    private Connection connection;

    @PostConstruct
    public void setupDatabase() throws IOException, SQLException
    {
        final File databaseFile = new File(databaseFilepath);
        boolean newDatabaseFileCreated = false;

        if (!databaseFile.exists() || databaseFile.isDirectory() && (databaseFile.createNewFile()))
        {
            newDatabaseFileCreated = true;
        }

        connectionString = "jdbc:sqlite:" + databaseFilepath;
        connection = DriverManager.getConnection(connectionString);

        if (newDatabaseFileCreated && initializeDatabaseSQLFilepath != null
                && !initializeDatabaseSQLFilepath.isBlank())
        {
            initializeDatabase();
        }
    }

    /**
     * {@inheritDoc} Please note, that by default the connection is opened post-construction
     */
    @Override
    public void openConnection() throws SQLException
    {
        connection = DriverManager.getConnection(connectionString);
    }

    @Override
    public void closeConnection() throws SQLException
    {
        connection.close();
    }

    @Override
    public PreparedStatement getPreparedStatement(String query) throws SQLException
    {
        return connection.prepareStatement(query);
    }

    /**
     * Helper function that initializes the SQLite database
     *
     * @throws IOException  The SQL queries are run from an .sql file. If that file is not found or
     *                      can't be read, throw IOException. Define the query file path at
     *                      application.properties as initializeDatabaseSQLFilepath
     * @throws SQLException If initializeDatabaseSQLFile contains invalid queries or the database
     *                      file is inaccessible
     */
    private void initializeDatabase() throws IOException, SQLException
    {
        var file = ResourceUtils.getFile("classpath:" + initializeDatabaseSQLFilepath);

        try (var reader = new BufferedReader(new FileReader(file)))
        {
            String line = reader.readLine();

            while (line != null)
            {
                var preparedStatement = getPreparedStatement(line);
                preparedStatement.execute();
                line = reader.readLine();
            }
        }
    }
}
