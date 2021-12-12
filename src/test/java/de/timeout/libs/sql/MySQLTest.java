package de.timeout.libs.sql;

import be.seeseemelk.mockbukkit.MockBukkit;

import de.timeout.libs.config.UTFConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.sql.SQLException;

import static org.junit.Assert.*;

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
    public void shouldInsertValueWithAutoGeneratedValue() {
        mysql.prepare("INSERT INTO Test2(key) VALUES (?)", "Test")
                .insert()
                .thenRun(() -> {
                    mysql.prepare("INSERT INTO Test2(key) VALUES (?)", "Test")
                            .insert()
                            .thenAccept((result) -> {
                                // get ID
                                assertNotNull(result);
                                assertTrue(result != -1);

                                mysql.prepare("INSERT INTO Test2(key) VALUES (?)", "Test2")
                                        .insert()
                                        .thenAccept((result2) -> {
                                            assertNotNull(result2);
                                            assertNotEquals(result, result2);
                                        });
                            });
                });
    }

    @Test
    public void shouldGetCorrectValues() {
        mysql.prepare("SELECT * FROM Test1 WHERE 1")
                .query(Test1.class)
                .subscribe((result) -> {
                    result.forEach(element -> {
                        System.out.println(element.getId() + ", " + element.getName());
                    });
                });
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}
