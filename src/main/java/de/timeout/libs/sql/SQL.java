package de.timeout.libs.sql;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

/**
 * Interface for SQL-Connector
 *
 */
@FunctionalInterface
public interface SQL {

    /**
     * Prepares the MySQL-Connection for a new Statement
     * @param statement the raw statement
     * @param args any arguments if required
     * @return a builder which will be used for connections later
     */
    @NotNull
    QueryBuilder prepare(@NotNull String statement, @NotNull Object... args) throws SQLException;
}
