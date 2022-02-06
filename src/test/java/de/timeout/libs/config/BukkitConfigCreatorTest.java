package de.timeout.libs.config;

import be.seeseemelk.mockbukkit.MockBukkit;
import de.timeout.libs.LibsTestPlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.Assert.*;

public class BukkitConfigCreatorTest {

    private ConfigCreator creator;

    private LibsTestPlugin plugin;

    @Before
    public void start() {
        MockBukkit.mock();
        plugin = MockBukkit.load(LibsTestPlugin.class);
        creator = new ConfigCreator(plugin.getDataFolder(), Paths.get(""));
    }

    @Test
    public void loadEmptyYamlConfig() throws IOException {
        assertEquals(0, Objects.requireNonNull(plugin.getDataFolder().list()).length);

        File file = creator.createFile(Paths.get("test.yml"));
        assertTrue(file.exists());
        assertEquals(0L, file.length());

//        Files.delete(file.toPath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionWhenNullInput() throws IOException {

        creator.createFile(null);
    }

    @Test
    public void shouldLoadYamlConfiguration() throws IOException {
        File file = creator.copyDefaultFile(Paths.get("config.yml"), Files.createTempFile("temp", "suff").toAbsolutePath());

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}
