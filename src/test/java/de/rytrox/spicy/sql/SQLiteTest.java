package de.rytrox.spicy.sql;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SQLiteTest {

    @Test
    public void shouldPrepareStatement() {
        SQLite sql = new SQLite(Paths.get("test.db").toFile());

        QueryBuilder builder = sql.prepare("SELECT * FROM Test WHERE 1");

        assertNotNull(builder);
    }
}
