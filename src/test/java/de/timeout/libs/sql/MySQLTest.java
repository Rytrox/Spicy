package de.timeout.libs.sql;

import be.seeseemelk.mockbukkit.MockBukkit;
import de.timeout.libs.config.UTFConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.nio.file.Paths;

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
    public void shouldPrepareStatement() {
        QueryBuilder builder = mysql.prepare("SELECT * FROM Test WHERE 1");

        assertNotNull(builder);
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}
