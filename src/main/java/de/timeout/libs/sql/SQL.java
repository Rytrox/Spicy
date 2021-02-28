package de.timeout.libs.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface for SQL-Connector
 *
 */
public interface SQL {

    /**
     * Checks, if a connection exists and is used.
     * If there is no connection, or the connection is closed, the method will return false.
     * Else the method will return true.
     *
     * @return is the connection can be used.
     * @throws SQLException if there are unexpected errors
     */
    boolean isConnected() throws SQLException;

    /**
     * Disconnect from a MySQL-Database if this object is connected
     *
     * @return the result. true if the object is disconnected successful, false if the connection is still open
     * @throws SQLException if there are unexpected errors
     */
    boolean disconnect() throws SQLException;

    /**
     * return the actual connection
     * @return the actual connection as Connection-Interface
     */
    @Nullable
    Connection getConnection();

    /**
     * Prepares the MySQL-Connection for a new Statement
     * @param statement the raw statement
     * @param args any arguments if required
     * @return a builder which will be used for connections later
     */
    @NotNull
    QueryBuilder prepare(@NotNull String statement, @NotNull Object... args);
}
