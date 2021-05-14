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

    private final SQLiteDataSource source;

    public SQLite(@NotNull File databaseFile) {
        SQLiteDataSource properties = new SQLiteDataSource();
        properties.setUrl(String.format("jdbc:sqlite:%s", databaseFile.getAbsolutePath()));

        this.source = properties;
    }

    @Override
    public @NotNull QueryBuilder prepare(@NotNull String statement, Object... args) throws SQLException {
        DriverManager.registerDriver(new JDBC());

        return new QueryBuilder(source, statement, args);
    }
}
