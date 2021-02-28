package de.timeout.libs.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sqlite.JDBC;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class SQLite implements SQL {

    private final File file;

    private Connection connection;

    public SQLite(@NotNull File databaseFile) {
        this.file = databaseFile;
    }

    public boolean connect() throws SQLException {
        return this.connect(null);
    }

    public boolean connect(@Nullable SQLiteDataSource properties) throws SQLException {
        if(!isConnected()) {
            properties = Optional.ofNullable(properties).orElse(new SQLiteDataSource());
            properties.setUrl(String.format("jdbc:sqlite:%s", file.getAbsolutePath()));

            DriverManager.registerDriver(new JDBC());
            connection = properties.getConnection();

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
        connection.close();

        return !isConnected();
    }

    @Override
    public @Nullable Connection getConnection() {
        return connection;
    }

    @Override
    public @NotNull QueryBuilder prepare(@NotNull String statement, Object... args) {
        return new QueryBuilder(connection, statement, args);
    }
}
