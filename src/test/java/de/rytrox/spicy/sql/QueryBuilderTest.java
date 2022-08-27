package de.rytrox.spicy.sql;

import de.rytrox.spicy.sql.entity.Developer;
import de.rytrox.spicy.sql.entity.DeveloperWithoutMatchingConstructor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class QueryBuilderTest {

    private static SQLite datasource;

    private final CountDownLatch lock = new CountDownLatch(1);

    @BeforeAll
    public static void setupDatasource() {
        datasource = new SQLite(Paths.get("src", "test", "resources", "database.db").toFile());
    }

    @Test
    public void shouldNotConvertIntoNonEntity() {
        assertThrows(RuntimeException.class, () -> datasource.prepare("SELECT * FROM Developers")
                .querySync(DeveloperWithoutMatchingConstructor.class)
                .get());

    }

    @Test
    public void shouldFailOnSyntaxError() {
        assertThrows(IllegalStateException.class, () -> datasource.prepare("SELECT FROM Developers")
                .querySync(Developer.class)
                .get());
    }

    @Test
    public void shouldAllowWildcardNotationInStatement() {
        List<Developer> developers = datasource.prepare("SELECT * FROM Developers WHERE id = ? AND name = ?", 408, "Timeout")
                .querySync(Developer.class)
                .get();

        assertEquals(408, developers.get(0).getId());
        assertEquals("Timeout", developers.get(0).getName());
    }

    @Test
    public void shouldExecuteAsyncSQLSelect() throws InterruptedException {
        AtomicBoolean resultPassed = new AtomicBoolean(false);

        datasource.prepare("SELECT * FROM Developers")
                .query(Developer.class)
                .subscribe((developers) -> {
                    Developer timeout = developers.get(0);
                    Developer sether = developers.get(1);

                    assertEquals(408, timeout.getId());
                    assertEquals("Timeout", timeout.getName());

                    assertEquals(701, sether.getId());
                    assertEquals("Sether", sether.getName());

                    resultPassed.set(true);
                    lock.countDown();
                });

        assertTrue(lock.await(3, TimeUnit.SECONDS));
        assertTrue(resultPassed.get());
    }

    @Test
    public void shouldExecuteSQLSelect() {
        List<Developer> developers = datasource.prepare("SELECT * FROM Developers")
                .querySync(Developer.class)
                .get();

        Developer timeout = developers.get(0);
        Developer sether = developers.get(1);

        assertEquals(701, sether.getId());
        assertEquals("Sether", sether.getName());

        assertEquals(408, timeout.getId());
        assertEquals("Timeout", timeout.getName());
    }

    @Test
    public void shouldConvertSQLSelectToJavaDefaultType() {
        List<Integer> ids = datasource.prepare("SELECT id FROM Developers")
                .querySync(Integer.class)
                .get();

        assertTrue(ids.contains(701));
        assertTrue(ids.contains(408));
    }

    @Test
    public void shouldNotConvertSQLSelectToJavaDefaultTypeWhenMultipleColumns() {
        assertThrows(RuntimeException.class, () ->
                datasource.prepare("SELECT id, name FROM Developers")
                        .querySync(Integer.class)
                        .get()
        );
    }

    @Test
    public void shouldExecuteAsyncSQLInsert() throws InterruptedException {
        AtomicBoolean result = new AtomicBoolean(false);

        datasource.prepare("INSERT INTO Developers(id, name) VALUES (404, 'Not Found')")
                .executeUpdate()
                .thenRun(() -> {

                    datasource.prepare("SELECT * FROM Developers WHERE id = 404")
                            .query(Developer.class)
                            .subscribe((res) -> {
                                assertEquals(404, res.get(0).getId());
                                assertEquals("Not Found", res.get(0).getName());

                                result.set(true);

                                datasource.prepare("DELETE FROM Developers WHERE id = 404")
                                        .executeSync();
                                lock.countDown();
                            });
                });

        assertTrue(lock.await(3, TimeUnit.SECONDS));
        assertTrue(result.get());
    }

    @Test
    public void shouldExecuteSQLInsert() {
        datasource.prepare("INSERT INTO Developers(id, name) VALUES (409, 'Conflict')")
                .executeUpdateSync();

        List<Developer> res = datasource.prepare("SELECT * FROM Developers WHERE id = 409")
                .querySync(Developer.class)
                .get();

        assertEquals(409, res.get(0).getId());
        assertEquals("Conflict", res.get(0).getName());

        datasource.prepare("DELETE FROM Developers WHERE id = 409")
                .executeSync();
    }

    @Test
    public void shouldUpdateAsyncSQLUpdate() throws InterruptedException {
        AtomicBoolean result = new AtomicBoolean(false);

        datasource.prepare("INSERT INTO Developers(id, name) VALUES (?, ?)", 105, "Testnutzer")
                .prepare("UPDATE Developers SET name = 'Sether701' WHERE id = ?", 105)
                .executeUpdate()
                .thenAccept((res) -> {
                    datasource.prepare("DELETE FROM Developers WHERE id = 105")
                            .executeUpdateSync();

                    assertEquals(1, res.get(1).intValue());
                    result.set(true);

                    lock.countDown();
                });

        assertTrue(lock.await(3, TimeUnit.SECONDS));
        assertTrue(result.get());
    }

    @Test
    public void shouldExecuteSQLUpdate() {
        assertEquals(List.of(1), datasource.prepare("UPDATE Developers SET name = 'Sether701' WHERE id = 701")
                .executeUpdateSync());

        datasource.prepare("UPDATE Developers SET name = 'Sether' WHERE id = 701")
                .executeUpdateSync();
    }

    @Test
    public void shouldChainSQLStatements() {
        datasource.prepare("INSERT INTO Developers(id, name) VALUES (?, ?)", 101, "Testnutzer1")
                .prepare("INSERT INTO Developers(id, name) VALUES (?, ?)", 102, "Testnutzer2")
                .executeUpdateSync();

        assertEquals(List.of(101, 102), datasource.prepare("SELECT id FROM Developers WHERE name LIKE ?", "Testnutzer%")
                .querySync(Integer.class)
                .get());

        datasource.prepare("DELETE FROM Developers WHERE name LIKE ?", "Testnutzer%")
                .executeUpdateSync();
    }
}
