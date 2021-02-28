package de.timeout.libs.sql;

import java.sql.*;
import java.util.Optional;

import com.mysql.cj.jdbc.Driver;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This Class is a Hook into the MySQL-Database
 * @author timeout
 *
 */
public class MySQL implements SQL {

    private final String host;
    private final String database;
    private final int port;

    private Connection connection;

    public MySQL(String host, int port, String database) {
        this.host = host;
        this.database = database;
        this.port = port;
    }

    /**
     * Connect to MySQL-Database with some default settings. <br>
     * To use your own properties you need to use {@link MySQL#connect(String, String, MysqlDataSource)}.
     *
     * @param username the username
     * @param password the username's password
     * @return a bool which explains if the connection to MySQL is established or not. <br>
     *     Returns false if the database is already connected.
     *
     * @throws SQLException if something went wrong during the connection to the MySQL-Database
     */
    public boolean connect(@NotNull String username, @NotNull String password) throws SQLException {
        MysqlDataSource properties = new MysqlDataSource();

        properties.setAutoReconnect(true);
        properties.setUseOldAliasMetadataBehavior(true);
        properties.setAllowPublicKeyRetrieval(true);
        properties.setUseUnicode(true);
        properties.setUseJDBCCompliantTimezoneShift(true);
        properties.setUseLegacyDatetimeCode(false);
        properties.setServerTimezone("UTC");
        properties.setUseSSL(false);

        return this.connect(username, password, properties);
    }

    /**
     * Connect to MySQL-Database, but needs a username and its password
     *
     * @param username the username
     * @param password the username's password
     * @param properties some extra properties. Can be null
     * @return a bool which explains if the connection to MySQL is established or not. <br>
     *     Returns false if the database is already connected.
     *
     * @throws SQLException if something went wrong during the connection to the MySQL-Database
     */
    public boolean connect(@NotNull String username, @NotNull String password, @Nullable MysqlDataSource properties) throws SQLException {
        if(!isConnected()) {
            properties = Optional.ofNullable(properties).orElse(new MysqlDataSource());

            properties.setURL(host);
            properties.setPort(port);
            properties.setDatabaseName(database);

            DriverManager.registerDriver(new Driver());
            connection = properties.getConnection(username, password);

            return connection != null;
        }
        return false;
    }

    @Override
    public boolean isConnected() throws SQLException {
        return connection != null && !connection.isClosed();
    }

    @Override
    public boolean disconnect() throws SQLException {
        this.connection.close();
        return !isConnected();
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public @NotNull QueryBuilder prepare(@NotNull String statement, Object... args) {
        return new QueryBuilder(connection, statement, args);
    }
}
