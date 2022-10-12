package de.rytrox.spicy.sql;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * This Class is a Hook into the MySQL-Database
 * @author Timeout
 *
 */
public final class MySQL extends SQL {

    public MySQL(MysqlDataSource source) {
        this.source = source;
    }

    public MySQL(String host, int port, String database, String username, String password) {
        MysqlDataSource properties = new MysqlDataSource();

        properties.setUrl(String.format("jdbc:mysql://%s:%d/%s", host, port, database));
        properties.setDatabaseName(database);
        properties.setUser(username);
        properties.setPassword(password);

        this.source = properties;
    }
}
