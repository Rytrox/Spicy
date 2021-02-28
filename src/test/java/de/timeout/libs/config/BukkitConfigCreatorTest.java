package de.timeout.libs.config;

import be.seeseemelk.mockbukkit.MockBukkit;
import de.timeout.libs.LibsTestPlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class BukkitConfigCreatorTest {

    private ConfigCreator creator;

    private LibsTestPlugin plugin;

    @Before
    public void start() {
        MockBukkit.mock();
        plugin = MockBukkit.load(LibsTestPlugin.class);
        creator = new ConfigCreator(plugin.getDataFolder(), Paths.get("/"));
    }

    @Test
    public void loadEmptyYamlConfig() throws IOException {
        assertEquals(0, plugin.getDataFolder().list().length);

        File file = creator.createFile(Paths.get("config.yml"));
        assertTrue(file.exists());
        assertEquals(0L, file.length());
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionWhenNullInput() throws IOException {

        creator.createFile(null);
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}
