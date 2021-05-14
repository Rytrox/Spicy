package de.timeout.libs.sql;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import org.jetbrains.annotations.NotNull;

/**
 * This Class is a Hook into the MySQL-Database
 * @author Timeout
 *
 */
public class MySQL implements SQL {

    private final MysqlDataSource source;

    public MySQL(String host, int port, String database, String username, String password) {
        MysqlDataSource properties = new MysqlDataSource();

        properties.setUseUnicode(true);
        properties.setUseJDBCCompliantTimezoneShift(true);
        properties.setUseLegacyDatetimeCode(false);
        properties.setServerTimezone("UTC");
        properties.setUrl(String.format("jdbc:mysql://%s:%d/", host, port));
        properties.setDatabaseName(database);
        properties.setUser(username);
        properties.setPassword(password);

        this.source = properties;
    }

    @Override
    public @NotNull QueryBuilder prepare(@NotNull String statement, Object... args) {
        return new QueryBuilder(source, statement, args);
    }
}
