package de.timeout.libs.sql;

import be.seeseemelk.mockbukkit.MockBukkit;

import de.timeout.libs.config.UTFConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.sql.SQLException;

public class MySQLTest {

    private MySQL mysql;
    private UTFConfig config;

    @Before
    public void startMock() {
        MockBukkit.mock();

        // DriverManager.registerDriver(new Driver());
        config = new UTFConfig(Paths.get("src", "test", "resources", "mysql.yml").toFile());
        mysql = new MySQL(config.getString("host"),
                config.getInt("port"),
                config.getString("database"),
                config.getString("username"),
                config.getString("password"));
    }

    @Test
    public void shouldHandleExecuteStatements() throws SQLException {
        mysql.prepare(
                "CREATE TABLE " +
                         "IF NOT EXISTS " +
                         "Test1(id INT NOT NULL, name VARCHAR(32) NOT NULL, PRIMARY KEY (id))")
                .execute();

        mysql.prepare("INSERT INTO Test1(id, name) VALUES (?, ?)", 408, "Timeout")
            .execute();

        mysql.prepare("INSERT INTO Test1(id, name) VALUES (?, ?)", 701, "Sether")
            .execute();
    }

    @Test
    public void shouldGetCorrectValues() throws SQLException {
        mysql.prepare("SELECT * FROM Test1 WHERE 1")
                .query((table) -> {
                    while(table.next()) {
                        String name = table.getString("name");
                        int id = table.getInt("id");

                        System.out.println(id + ", " + name);
                    }
                });
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}
