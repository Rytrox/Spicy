package de.rytrox.spicy.sql;

import org.jetbrains.annotations.NotNull;
import org.sqlite.JDBC;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class SQLite extends SQL {

    static {
        try {
            DriverManager.registerDriver(new JDBC());
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to register JDBC-SQLite Driver");
        }
    }

    public SQLite(@NotNull File databaseFile) {
        SQLiteDataSource properties = new SQLiteDataSource();
        properties.setUrl(String.format("jdbc:sqlite:%s", databaseFile.getAbsolutePath()));

        this.source = properties;
    }
}
