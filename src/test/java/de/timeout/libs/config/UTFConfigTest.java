package de.timeout.libs.config;

import be.seeseemelk.mockbukkit.MockBukkit;

import de.timeout.libs.LibsTestPlugin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class UTFConfigTest {

    private UTFConfig config;

    private LibsTestPlugin plugin;

    @Before
    public void init() {
        MockBukkit.mock();
        plugin = MockBukkit.load(LibsTestPlugin.class);
        config = new UTFConfig(Paths.get("src", "test", "resources", "config.yml").toFile());
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
    public void shouldLoadConfigFromInputStream() {

    }

    @Test
    public void shouldLoadConfigFromFile() {
        assertTrue(config.isConfigurationSection("mysql"));
        assertFalse(config.getComments("mysql").isEmpty());
        assertTrue(config.getComments("mysql.host").isEmpty());
    }

    @Test
    public void saveConfigWithCommentsCorrectly() throws IOException {
        File file = new File(plugin.getDataFolder(), "config.yml");
        config.save(file);

        System.out.println(file.getAbsolutePath());
        FileReader reader = new FileReader(file);
        BufferedReader lineReader = new BufferedReader(reader);

        assertTrue(lineReader.lines()
                .filter(line -> line.trim().startsWith("#"))
                .count() > 20);
        assertFalse(config.getComments("worldsettings.pigZone.enabled").isEmpty());
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}
