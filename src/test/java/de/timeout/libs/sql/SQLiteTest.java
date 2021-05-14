package de.timeout.libs.sql;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

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
        sql.prepare("SELECT * FROM Test1 WHERE 1")
                .query(Assert::assertNotNull);
    }

    @Test
    public void shouldInsertValues() throws SQLException {
        sql.prepare("INSERT INTO Test1(id, name) VALUES (?, ?)", 408, "Timeout")
                .update();
        sql.prepare("INSERT INTO Test1(id, name) VALUES (?, ?)", 701, "Sether")
                .update();

        sql.prepare("SELECT * FROM Test1 WHERE 1")
                .query(resultSet -> {
                    while(resultSet.next()) {
                        System.out.println("ID: " + resultSet.getInt("id"));
                        System.out.println("Name: " + resultSet.getString("name"));
                    }
//                    resultSet.next();
//                    assertEquals(408, resultSet.getInt("id"));
//                    assertEquals("Timeout", resultSet.getString("name"));
//
//                    resultSet.next();
//                    assertEquals(701, resultSet.getInt("id"));
//                    assertEquals("Sether", resultSet.getString("name"));
                });
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}
