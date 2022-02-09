package de.timeout.libs.sql;

import org.jetbrains.annotations.NotNull;
import org.sqlite.JDBC;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class SQLite implements SQL {

    private final SQLiteDataSource source;

    public SQLite(@NotNull File databaseFile) {
        try {
            DriverManager.registerDriver(new JDBC());
        } catch (SQLException e) {
            throw new IllegalArgumentException("Unable to register JDBC-SQLite Driver");
        }

        SQLiteDataSource properties = new SQLiteDataSource();
        properties.setUrl(String.format("jdbc:sqlite:%s", databaseFile.getAbsolutePath()));

        this.source = properties;
    }

    @Override
    public @NotNull QueryBuilder prepare(@NotNull String statement, Object... args) {
        return new QueryBuilder(source, statement, args);
    }
}
