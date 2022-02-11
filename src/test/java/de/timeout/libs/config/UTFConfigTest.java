package de.timeout.libs.config;

import be.seeseemelk.mockbukkit.MockBukkit;

import de.timeout.libs.LibsTestPlugin;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class UTFConfigTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void init() {
        MockBukkit.mock();
    }

    @Test
    public void shouldLoadConfigFromString() {
        UTFConfig config = new UTFConfig(
                 "name:\n" +
                        "  key1:\n" +
                        "    value: 'Hello World'\n" +
                        "    number: 12");

        assertEquals("Hello World", config.getString("name.key1.value"));
        assertEquals(12, config.getInt("name.key1.number"));
    }

    @Test
    public void shouldLoadEmptyConfiguration() throws IOException {
        File test = Paths.get("src", "test", "resources", "empty.yml").toFile();
        UTFConfig file = new UTFConfig(test);

        assertNotNull(file);
    }

    @Test
    public void shouldLoadConfigFromInputStream() {

    }

    @Test
    public void shouldLoadConfigFromFile() {
        UTFConfig config = new UTFConfig(Paths.get("src", "test", "resources", "config.yml").toFile());

        assertTrue(config.isConfigurationSection("mysql"));
        assertFalse(config.getComments("mysql").isEmpty());
        assertTrue(config.getComments("mysql.host").isEmpty());
    }

    @Test
    public void saveConfigWithCommentsCorrectly() throws IOException {
        UTFConfig config = new UTFConfig(Paths.get("src", "test", "resources", "config.yml").toFile());
        File file = temporaryFolder.newFile();
        config.save(file);

        FileReader reader = new FileReader(file);
        BufferedReader lineReader = new BufferedReader(reader);

        assertTrue(lineReader.lines()
                .filter(line -> line.trim().startsWith("#"))
                .count() > 20);
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}
