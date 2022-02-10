package de.timeout.libs.config;

import be.seeseemelk.mockbukkit.MockBukkit;

import de.timeout.libs.LibsTestPlugin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class JsonConfigTest {

    private LibsTestPlugin plugin;

    @Before
    public void mockServer() {
        MockBukkit.mock();
        plugin = MockBukkit.load(LibsTestPlugin.class);
    }

    @Test
    public void loadJsonFromString() {
        JsonConfig config = new JsonConfig("""
                {
                    "firstName": "Rack",
                    "lastName": "Jackon",
                    "gender": "man",
                    "age": 24,
                    "address": {
                        "streetAddress": "126",
                        "city": "San Jone",
                        "state": "CA",
                        "postalCode": "394221"
                    },
                    "phoneNumbers": [
                        { "type": "home", "number": "7383627627" }
                    ]
                }""");

        assertNotNull(config);
        assertFalse(config.getKeys(false).isEmpty());
        assertEquals("Rack", config.getString("firstName"));
        assertEquals("Jackon", config.getString("lastName"));
        assertNotNull(config.getConfigurationSection("address"));
        assertEquals("San Jone", config.getString("address.city"));
        assertEquals(24, config.getInt("age"));
    }

    @Test
    public void loadJsonFromFile() {
        File file = Paths.get("src","test", "resources", "config.json").toFile();
        JsonConfig config = new JsonConfig(file);

        assertNotNull(config);
        assertEquals("8b450b2d-65a1-4996-b2aa-b95e5ae9f894", config.getString("guid"));
        assertEquals(6, config.getInt("index"));
        assertTrue(config.get("range") instanceof List);
    }

    @Test
    public void handleValues() {
        JsonConfig config = new JsonConfig("{}");

        config.set("libs.developer", "Timeout");
        config.set("libs.number", 2);

        assertNotNull(config.getConfigurationSection("libs"));
        assertEquals("Timeout", config.getString("libs.developer"));
        assertEquals(2, config.getInt("libs.number"));

        config.set("libs.number", null);
        assertNull(config.get("libs.number"));
    }

    @Test
    public void saveJsonCorrectly() throws IOException {
        Path file = Paths.get("src", "test", "resources", "config.json");
        JsonConfig config = new JsonConfig(file.toFile());

        File saved = new File(plugin.getDataFolder(), "config.json");
        config.save(saved);

        JsonConfig config1 = new JsonConfig(saved);

        assertEquals(config1.getString("guid"), config.getString("guid"));
        assertEquals(config1.getInt("index"), config.getInt("index"));
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}
