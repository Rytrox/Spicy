package de.rytrox.spicy.sql;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Paths;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SQLiteTest {

    @Test
    public void shouldPrepareStatement() {
        SQLite sql = new SQLite(Paths.get("test.db").toFile());

        QueryBuilder builder = sql.prepare("SELECT * FROM Test WHERE 1");

        assertNotNull(builder);
    }

    @Test
    public void shouldThrowExceptionWhenDriverCouldNotBeRegistered() {
        try(MockedStatic<DriverManager> manager = mockStatic(DriverManager.class)) {
            manager.when(() -> DriverManager.registerDriver(any(Driver.class)))
                    .thenThrow(SQLException.class);

            assertThrows(IllegalStateException.class, () -> new SQLite(Paths.get("test.db").toFile()));
        }
    }
}
