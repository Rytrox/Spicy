package de.rytrox.spicy.config;

import jakarta.xml.bind.DatatypeConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        assertEquals(getMD5Hash(Paths.get("src", "test", "resources", "config.yml")), getMD5Hash(Paths.get(file.getPath())));
        assertEquals(getSHA256Hash(Paths.get("src", "test", "resources", "config.yml")), getSHA256Hash(Paths.get(file.getPath())));
    }

    // Java method to create SHA-25 checksum
    private static String getSHA256Hash(Path path) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(Files.readAllBytes(path));
            return bytesToHex(hash); // make it printable
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // Java method to create MD5 checksum
    private static String getMD5Hash(Path path) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(Files.readAllBytes(path));
            return bytesToHex(hash); // make it printable
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Use javax.xml.bind.DatatypeConverter class in JDK to convert byte array
     * to a hexadecimal string. Note that this generates hexadecimal in lower case.
     * @param hash
     * @return
     */
    private static String  bytesToHex(byte[] hash) {
        return DatatypeConverter.printHexBinary(hash).toLowerCase();
    }
}
