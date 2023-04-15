package com.crownedcuts.booking.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract interface that is used to mark a RowMapper
 * A RowMapper is responsible for mapping a ResultSet to a List of type E
 * <p>
 * Example usage:
 * var rs = statement.executeQuery();
 * var mapper = new CarMapper();
 * var cars = mapper.processResultSet(rs);
 *
 * @param <E> Any time
 */
public interface RowMapper<E>
{
    /**
     * Takes in a result set and attempts to map it into a List
     *
     * @param rs ResultSet to use
     * @return List of type E
     * @throws SQLException If mapping fails
     */
    default List<E> processResultSet(ResultSet rs) throws SQLException
    {
        List<E> objectList = new ArrayList<>();

        while (rs.next())
        {
            objectList.add(mapRow(rs));
        }

        return objectList;
    }

    /**
     * Helper method for processResultSet. May be used as a standalone method as well
     * to map a single row of a ResultSet to type E.
     *
     * @param rs ResultSet to use
     * @return List of type E
     * @throws SQLException If mapping fails
     */
    E mapRow(ResultSet rs) throws SQLException;
}
