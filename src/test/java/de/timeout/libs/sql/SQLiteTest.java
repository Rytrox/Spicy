package de.timeout.libs.sql;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class SQLiteTest {

    private SQLite sql;

    @Before
    public void mock() {
        MockBukkit.mock();

        Path path = Paths.get("src", "test", "resources", "database.db");

        sql = new SQLite(path.toFile());
    }

    @Test
    public void shouldGetCorrectValues() throws SQLException {
        sql.prepare("SELECT * FROM Test2 WHERE 1")
                .query(Test1.class)
                .subscribe(Assert::assertNotNull);
    }

    @Test
    public void shouldInsertValues() throws SQLException {
        sql.prepare("INSERT INTO Test2(id, name) VALUES (?, ?)", 408, "Timeout")
                .update();
        sql.prepare("INSERT INTO Test2(id, name) VALUES (?, ?)", 701, "Sether")
                .update();

        sql.prepare("SELECT * FROM Test1 WHERE 1")
                .query(Test1.class)
                .subscribe((elements) -> {
                    Test1 timeout = elements.get(0);
                    Test1 sether = elements.get(1);

                    assertEquals(408, timeout.getId());
                    assertEquals("Timeout", timeout.getName());

                    assertEquals(701, sether.getId());
                    assertEquals("Sether", sether.getName());
                });
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}
