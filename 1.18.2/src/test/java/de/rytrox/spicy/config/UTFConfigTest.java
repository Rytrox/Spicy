package de.rytrox.spicy.config;

import be.seeseemelk.mockbukkit.MockBukkit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UTFConfigTest {

    @TempDir
    public Path temporaryFolder;

    @BeforeAll
    public static void init() {
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
    public void shouldNotLoadConfigFromWrongString() {
        UTFConfig config = new UTFConfig("Hello WOrld");

        assertNotNull(config);
        assertTrue(config.getKeys(true).isEmpty());
    }

    @Test
    public void shouldLoadEmptyConfiguration() {
        File test = Paths.get("src", "test", "resources", "empty.yml").toFile();
        UTFConfig file = new UTFConfig(test);

        assertNotNull(file);
    }

    @Test
    public void shouldNotLoadOtherFileFormats() {
        File test = Paths.get("src", "test", "resources", "config.dat").toFile();

        UTFConfig file = new UTFConfig(test);
        assertTrue(file.getKeys(true).isEmpty());
    }

    @Test
    public void shouldLoadConfigFromFile() {
        UTFConfig config = new UTFConfig(Paths.get("src", "test", "resources", "config.yml").toFile());

        checkTestConfig(config);
    }

    @Test
    public void shouldLoadFromInputStream() throws FileNotFoundException {
        UTFConfig config = new UTFConfig(new FileInputStream(Paths.get("src", "test", "resources", "config.yml").toFile()));

        checkTestConfig(config);
    }

    @Test
    public void shouldNotLoadFromInvalidInputStream() throws FileNotFoundException {
        UTFConfig config = new UTFConfig(new FileInputStream(Paths.get("src", "test", "resources", "config.dat").toFile()));

        assertNotNull(config);
        assertTrue(config.getKeys(true).isEmpty());
    }

    @Test
    public void saveConfigWithCommentsCorrectly() throws IOException {
        UTFConfig config = new UTFConfig(Paths.get("src", "test", "resources", "config.yml").toFile());
        File file = Files.createFile(temporaryFolder.resolve(Paths.get("file.yml"))).toFile();
        config.save(file);

        FileReader reader = new FileReader(file);
        BufferedReader lineReader = new BufferedReader(reader);

        assertEquals(3, lineReader.lines()
                .filter(line -> line.trim().startsWith("#"))
                .toList().size());
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }


    private void checkTestConfig(UTFConfig config) {
        assertNotNull(config);
        assertEquals(2, config.getInt("byte"));
        assertEquals(46, config.getInt("short"));
        assertEquals(13243463, config.getInt("integer"));
        assertEquals(343423494964959354L, config.getLong("long"));
        assertFalse(config.getBoolean("boolean"));
        assertEquals(123.4643, config.getDouble("float"), 0);
        assertEquals(123434.466544356465432344D, config.getDouble("double"), 0);
        assertEquals("Hello World", config.getString("string"));

        assertEquals(Arrays.asList(12, -24, 32435), config.getIntegerList("lists.ints"));
        assertEquals(Arrays.asList(3543.3543F, 456.535643F, 3.43345F, 3443.533F), config.getFloatList("lists.floats"));

        List<HashMap<String, Object>> nestedList = (List<HashMap<String, Object>>) config.getList("developerList");
        assertNotNull(nestedList);
        assertEquals(408, nestedList.get(0).get("id"));
        assertEquals("Timeout", nestedList.get(0).get("name"));
        assertEquals(701, nestedList.get(1).get("id"));
        assertEquals("Sether", nestedList.get(1).get("name"));

        HashMap<String, Object> asedem = (HashMap<String, Object>) nestedList.get(0).get("assistant");
        assertEquals(409, asedem.get("id"));
        assertEquals("Asedem", asedem.get("name"));

        HashMap<String, Object> kaigoe = (HashMap<String, Object>) nestedList.get(1).get("assistant");
        assertEquals(702, kaigoe.get("id"));
        assertEquals("kaigoe", kaigoe.get("name"));
    }
}
