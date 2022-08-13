package de.rytrox.spicy.sql;

import com.mysql.cj.jdbc.MysqlDataSource;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

/**
 * This Class is a Hook into the MySQL-Database
 * @author Timeout
 *
 */
public final class MySQL implements SQL {

    private final MysqlDataSource source;

    public MySQL(String host, int port, String database, String username, String password) {
        MysqlDataSource properties = new MysqlDataSource();

        properties.setUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=false", host, port, database));
        properties.setDatabaseName(database);
        properties.setUser(username);
        properties.setPassword(password);

        this.source = properties;
    }

    @Override
    public @NotNull QueryBuilder prepare(@NotNull @Language("MySQL") String statement, Object... args) {
        return new QueryBuilder(source, statement, args);
    }
}
