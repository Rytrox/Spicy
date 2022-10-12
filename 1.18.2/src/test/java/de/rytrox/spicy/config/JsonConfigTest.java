package de.rytrox.spicy.config;

import be.seeseemelk.mockbukkit.MockBukkit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JsonConfigTest {

    @TempDir
    public Path temporaryFolder;

    @BeforeAll
    public static void mockServer() {
        MockBukkit.mock();
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
    public void shouldNotLoadJsonFromInvalidString() {
        JsonConfig config = new JsonConfig("{ \"test\": 6");

        assertNotNull(config);
        assertTrue(config.getKeys(true).isEmpty());
    }

    @Test
    public void loadJsonFromFile() {
        File file = Paths.get("src","test", "resources", "config.json").toFile();
        JsonConfig config = new JsonConfig(file);

        assertNotNull(config);
        checkTestConfig(config);
    }

    @Test
    public void shouldNotLoadJsonFromCorruptFile() {
        File file = Paths.get("src","test", "resources", "config.dat").toFile();
        JsonConfig config = new JsonConfig(file);

        assertNotNull(config);
        assertTrue(config.getKeys(true).isEmpty());
    }

    @Test
    public void shouldNotLoadJsonFromFileOnIOError() {
        try(MockedStatic<FileUtils> mockedStatic = Mockito.mockStatic(FileUtils.class)) {
            mockedStatic.when(() -> FileUtils.readFileToString(Mockito.any(File.class), Mockito.eq(StandardCharsets.UTF_8)))
                    .thenThrow(IOException.class);

            File file = Paths.get("src","test", "resources", "config.json").toFile();
            JsonConfig config = new JsonConfig(file);

            assertNotNull(config);
            assertTrue(config.getKeys(true).isEmpty());
        }
    }

    @Test
    public void shouldLoadJsonFromInputStream() throws FileNotFoundException {
        Path file = Paths.get("src", "test", "resources", "config.json");

        JsonConfig config = new JsonConfig(new FileInputStream(file.toFile()));
        checkTestConfig(config);
    }

    @Test
    public void shouldNotLoadJsonFromInvalidInputStream() {
        InputStream stream = IOUtils.toInputStream("{ \"id\": 6", StandardCharsets.UTF_8);

        JsonConfig config = new JsonConfig(stream);
        assertNotNull(config);
        assertTrue(config.getKeys(true).isEmpty());
    }

    @Test
    public void shouldCreateEmptyJson() {
        JsonConfig config = new JsonConfig();

        assertNotNull(config);
        assertTrue(config.getKeys(true).isEmpty());
    }

    @Test
    public void saveJsonCorrectly() throws IOException {
        Path file = Paths.get("src", "test", "resources", "config.json");
        JsonConfig config = new JsonConfig(file.toFile());

        File saved = Files.createFile(temporaryFolder.resolve(Paths.get("test.json"))).toFile();
        config.save(saved);

        JsonConfig config1 = new JsonConfig(saved);
        checkTestConfig(config1);
    }

    @Test
    public void shouldReturnEmptyCommentMethods() {
        JsonConfig config = new JsonConfig();
        FileConfigurationOptions options = config.options();

        assertFalse(options.parseComments());
        assertTrue(options.getHeader().isEmpty());
        assertTrue(options.getFooter().isEmpty());
        assertEquals("", options.header());
        assertFalse(options.copyHeader());
    }

    @Test
    public void shouldNotAllowHeaderInsertion() {
        JsonConfig config = new JsonConfig();
        FileConfigurationOptions options = config.options();

        assertThrows(UnsupportedOperationException.class, () -> options.setHeader(new ArrayList<>()));
    }

    @Test
    public void shouldNotAllowFooterInsertion() {
        JsonConfig config = new JsonConfig();
        FileConfigurationOptions options = config.options();

        assertThrows(UnsupportedOperationException.class, () -> options.setFooter(new ArrayList<>()));
    }

    @Test
    public void shouldNotAllowHeaderInsertionOverDeprecatedMethod() {
        JsonConfig config = new JsonConfig();
        FileConfigurationOptions options = config.options();

        assertThrows(UnsupportedOperationException.class, () -> options.header(""));
    }

    @Test
    public void shouldNotAllowParseComments() {
        JsonConfig config = new JsonConfig();
        FileConfigurationOptions options = config.options();

        assertThrows(UnsupportedOperationException.class, () -> options.parseComments(true));
    }

    @Test
    public void shouldNotAllowCopyHeader() {
        JsonConfig config = new JsonConfig();
        FileConfigurationOptions options = config.options();

        assertThrows(UnsupportedOperationException.class, () -> options.copyHeader(true));
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    private void checkTestConfig(JsonConfig config) {
        assertNotNull(config);
        assertEquals(2, config.getInt("byte"));
        assertEquals(46, config.getInt("short"));
        assertEquals(13243463, config.getInt("integer"));
        assertEquals(343423494964959360L, config.getLong("long"));
        assertFalse(config.getBoolean("boolean"));
        assertEquals(123.4643, config.getDouble("float"), 0);
        assertEquals(123434.466544356465432344D, config.getDouble("double"), 0);
        assertEquals("Hello World", config.getString("string"));

        assertEquals(Arrays.asList(12, -24, 32435), config.getIntegerList("lists.ints"));
        assertEquals(Arrays.asList(3543.3543F, 456.535643F, 3.43345F, 3443.533F), config.getFloatList("lists.floats"));

        List<Map<String, Object>> nestedList = (List<Map<String, Object>>) config.getList("developerList");
        assertNotNull(nestedList);
        assertEquals(408D, nestedList.get(0).get("id"));
        assertEquals("Timeout", nestedList.get(0).get("name"));
        assertEquals(701D, nestedList.get(1).get("id"));
        assertEquals("Sether", nestedList.get(1).get("name"));

        Map<String, Object> asedem = (Map<String, Object>) nestedList.get(0).get("assistant");
        assertEquals(409D, asedem.get("id"));
        assertEquals("Asedem", asedem.get("name"));

        Map<String, Object> kaigoe = (Map<String, Object>) nestedList.get(1).get("assistant");
        assertEquals(702D, kaigoe.get("id"));
        assertEquals("kaigoe", kaigoe.get("name"));
    }
}
