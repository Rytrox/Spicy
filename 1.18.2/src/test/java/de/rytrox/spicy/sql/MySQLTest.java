package de.rytrox.spicy.sql;

import be.seeseemelk.mockbukkit.MockBukkit;

import de.rytrox.spicy.config.UTFConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLTest {

    private static MySQL mysql;

    private static UTFConfig config;

    @BeforeAll
    public static void startMock() {
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
    public void shouldPrepareStatement() {
        QueryBuilder builder = mysql.prepare("SELECT * FROM Test WHERE 1");

        assertNotNull(builder);
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }
}
