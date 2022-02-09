package de.timeout.libs.sql;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.nio.file.Paths;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SQLiteTest {

    @Test
    public void shouldPrepareStatement() {
        SQLite sql = new SQLite(Paths.get("test.db").toFile());

        QueryBuilder builder = sql.prepare("SELECT * FROM Test WHERE 1");

        assertNotNull(builder);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenDriverCouldNotBeRegistered() {
        try(MockedStatic<DriverManager> manager = mockStatic(DriverManager.class)) {
            manager.when(() -> DriverManager.registerDriver(any(Driver.class)))
                    .thenThrow(SQLException.class);

            new SQLite(Paths.get("test.db").toFile());
        }
    }
}
