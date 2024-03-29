package de.rytrox.spicy.config;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigCreatorTest {

    private ConfigCreator creator;

    @TempDir
    public Path temporaryFolder;

    @BeforeEach
    public void start() {
        creator = new ConfigCreator(temporaryFolder.toFile());
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
}
