package de.rytrox.spicy.config;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class ConfigCreatorTest {

    private ConfigCreator creator;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void start() throws IOException {
        MockBukkit.mock();
        creator = new ConfigCreator(temporaryFolder.newFolder(), Paths.get(""));
    }

    @Test
    public void loadEmptyYamlConfig() throws IOException {
        File file = creator.createFile(Paths.get("test.yml"));
        assertTrue(file.exists());
        assertEquals(0L, file.length());
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
