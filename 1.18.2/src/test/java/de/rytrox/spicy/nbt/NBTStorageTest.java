package de.rytrox.spicy.nbt;

import net.minecraft.nbt.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class NBTStorageTest {

    @TempDir
    public Path temporaryFolder;

    private CompoundTag testCompound;

    @BeforeEach
    public void prepareConfig() {
        NBTStorage storage = new NBTStorage();

        storage.setByte("byte", (byte) 5);
        storage.setShort("short", (short) 2463);
        storage.setInt("integer", 343575324);
        storage.setLong("long", Long.MAX_VALUE);
        storage.setFloat("float", 4.0F);
        storage.setDouble("double", 2356.232535D);
        storage.setString("string", "Hello World");
        storage.setUniqueID("uuid", UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"));
        storage.setByteArray("byteArray", new byte[] { 10, 23, 0 });
        storage.setIntArray("intArray", new int[] { 1, 2, 3, 4, 5 });
        storage.setLongArray("longArray", new long[] { 6, 7, 8, 9 });
        storage.setList("stringList", List.of("Hello", "World", "Test"));
        storage.setList("longList", List.of(12L, 1L, 12467L, 76433L));

        NBTStorage timeout = new NBTStorage();
        timeout.setString("name", "Timeout");
        timeout.setInt("id", 408);
        timeout.setUniqueID("player", UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"));
        NBTStorage asedem = new NBTStorage();
        asedem.setString("name", "Asedem");
        asedem.setInt("id", 409);
        asedem.setUniqueID("player", UUID.fromString("121a9207-cd9a-4717-ba06-bb96667492f1"));
        timeout.setCompound("member", asedem);

        NBTStorage sether = new NBTStorage();
        sether.setString("name", "Sether");
        sether.setInt("id", 701);
        sether.setUniqueID("player", UUID.fromString("58a6382a-3b85-4d7f-8a6a-0b920ecb88bd"));
        NBTStorage kaigoe = new NBTStorage();
        kaigoe.setString("name", "kaigoe");
        kaigoe.setInt("id", 702);
        kaigoe.setUniqueID("player", UUID.fromString("b24b275f-23ee-4c3f-ba98-6bce8442bd8a"));
        sether.setCompound("member", kaigoe);

        storage.setList("developerList", List.of(timeout, sether));
        testCompound = storage.save();
    }

    @Test
    public void shouldCreateEmptyStorage() {
        // Should create an empty config
        NBTStorage empty = new NBTStorage();

        assertTrue(empty.isEmpty());
    }

    @Test
    public void shouldReadCompressedFile() throws IOException {
        NBTStorage compressed = NBTStorage.fromCompressedFile(Paths.get("src", "test", "resources", "storage", "compressed.dat").toFile());
        assertFalse(compressed.isEmpty());

        List<NBTStorage> developerList = compressed.getList("developerList", NBTStorage.class);
        assertNotNull(developerList);
        assertEquals(2, developerList.size());

        NBTStorage timeout = developerList.get(0);
        assertEquals(408, timeout.getInt("id"));
        assertEquals(UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"), timeout.getUniqueID("player"));
        assertEquals("Timeout", timeout.getString("name"));
        NBTStorage asedem = timeout.getCompound("assistant");
        assertNotNull(asedem);
        assertEquals(409, asedem.getInt("id"));
        assertEquals(UUID.fromString("121a9207-cd9a-4717-ba06-bb96667492f1"), asedem.getUniqueID("player"));
        assertEquals("Asedem", asedem.getString("name"));

        NBTStorage sether = developerList.get(1);
        assertEquals(701, sether.getInt("id"));
        assertEquals(UUID.fromString("58a6382a-3b85-4d7f-8a6a-0b920ecb88bd"), sether.getUniqueID("player"));
        assertEquals("Sether", sether.getString("name"));
        NBTStorage kaigoe = sether.getCompound("assistant");
        assertNotNull(kaigoe);
        assertEquals(702, kaigoe.getInt("id"));
        assertEquals(UUID.fromString("b24b275f-23ee-4c3f-ba98-6bce8442bd8a"), kaigoe.getUniqueID("player"));
        assertEquals("kaigoe", kaigoe.getString("name"));

        assertEquals("Hello World", compressed.getString("string"));
        assertEquals((byte) 5, compressed.getByte("byte"));
        assertEquals(2356.232535D, compressed.getDouble("double"));
        assertArrayEquals(new long[] { 6L, 7L, 8L, 9L }, compressed.getLongArray("longArray"));
        assertArrayEquals(new byte[] { 10, 23, 0 }, compressed.getByteArray("byteArray"));
        assertEquals(343575324, compressed.getInt("integer"));
        assertEquals(4F, compressed.getFloat("float"));
        assertEquals(UUID.fromString("0fc1c0ef-18a8-42e5-af1b-07f8c07376e5"), compressed.getUniqueID("uuid"));
        assertEquals(List.of(12L, 1L, 12467L, 76433L), compressed.getList("longList", Long.class));
        assertEquals(9223372036854775807L, compressed.getLong("long"));
        assertArrayEquals(new int[] { 1, 2, 3, 4, 5 }, compressed.getIntArray("intArray"));
        assertEquals(List.of("Hello", "World", "Test"), compressed.getList("stringList", String.class));
        assertEquals((short) 2463, compressed.getShort("short"));
        assertEquals(UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"), compressed.getUniqueID("offlineplayer"));
    }

    @Test
    public void shouldReadUncompressedFile() throws IOException {
        NBTStorage uncompressed = NBTStorage.fromUncompressedFile(Paths.get("src", "test", "resources", "storage", "uncompressed.dat").toFile());
        assertFalse(uncompressed.isEmpty());

        List<NBTStorage> developerList = uncompressed.getList("developerList", NBTStorage.class);
        assertNotNull(developerList);
        assertEquals(2, developerList.size());

        NBTStorage timeout = developerList.get(0);
        assertEquals(408, timeout.getInt("id"));
        assertEquals(UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"), timeout.getUniqueID("player"));
        assertEquals("Timeout", timeout.getString("name"));
        NBTStorage asedem = timeout.getCompound("assistant");
        assertNotNull(asedem);
        assertEquals(409, asedem.getInt("id"));
        assertEquals(UUID.fromString("121a9207-cd9a-4717-ba06-bb96667492f1"), asedem.getUniqueID("player"));
        assertEquals("Asedem", asedem.getString("name"));

        NBTStorage sether = developerList.get(1);
        assertEquals(701, sether.getInt("id"));
        assertEquals(UUID.fromString("58a6382a-3b85-4d7f-8a6a-0b920ecb88bd"), sether.getUniqueID("player"));
        assertEquals("Sether", sether.getString("name"));
        NBTStorage kaigoe = sether.getCompound("assistant");
        assertNotNull(kaigoe);
        assertEquals(702, kaigoe.getInt("id"));
        assertEquals(UUID.fromString("b24b275f-23ee-4c3f-ba98-6bce8442bd8a"), kaigoe.getUniqueID("player"));
        assertEquals("kaigoe", kaigoe.getString("name"));

        assertEquals("Hello World", uncompressed.getString("string"));
        assertEquals((byte) 5, uncompressed.getByte("byte"));
        assertEquals(2356.232535D, uncompressed.getDouble("double"));
        assertArrayEquals(new long[] { 6L, 7L, 8L, 9L }, uncompressed.getLongArray("longArray"));
        assertArrayEquals(new byte[] { 10, 23, 0 }, uncompressed.getByteArray("byteArray"));
        assertEquals(343575324, uncompressed.getInt("integer"));
        assertEquals(4F, uncompressed.getFloat("float"));
        assertEquals(UUID.fromString("5066c77f-033a-40d4-aaea-a53cbe1613e6"), uncompressed.getUniqueID("uuid"));
        assertEquals(List.of(12L, 1L, 12467L, 76433L), uncompressed.getList("longList", Long.class));
        assertEquals(9223372036854775807L, uncompressed.getLong("long"));
        assertArrayEquals(new int[] { 1, 2, 3, 4, 5 }, uncompressed.getIntArray("intArray"));
        assertEquals(List.of("Hello", "World", "Test"), uncompressed.getList("stringList", String.class));
        assertEquals((short) 2463, uncompressed.getShort("short"));
        assertEquals(UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"), uncompressed.getUniqueID("offlineplayer"));
    }

    @Test
    public void shouldReadStorageFromTagCompound() {
        NBTStorage uncompressed = new NBTStorage(testCompound);
        assertFalse(uncompressed.isEmpty());

        testCompound(uncompressed);
    }

    @Test
    public void shouldWriteTagCompoundCompressed() throws IOException {
        NBTStorage storage = new NBTStorage(testCompound);

        File compressed = Files.createFile(temporaryFolder.resolve(Paths.get("compressed.dat"))).toFile();

        storage.saveCompressed(compressed);
        assertNotNull(compressed);
        assertTrue(compressed.exists());

        NBTStorage loaded = NBTStorage.fromCompressedFile(compressed);
        testCompound(loaded);
    }

    @Test
    public void shouldWriteCompoundUncompressed() throws IOException {
        NBTStorage storage = new NBTStorage(testCompound);

        File uncompressed = Files.createFile(temporaryFolder.resolve(Paths.get("uncompressed.dat"))).toFile();

        storage.saveUncompressed(uncompressed);
        assertNotNull(uncompressed);
        assertTrue(uncompressed.exists());

        NBTStorage loaded = NBTStorage.fromUncompressedFile(uncompressed);
        testCompound(loaded);
    }

    private void testCompound(NBTStorage compressed) {
        assertFalse(compressed.isEmpty());

        List<NBTStorage> developerList = compressed.getList("developerList", NBTStorage.class);
        assertNotNull(developerList);
        assertEquals(2, developerList.size());

        NBTStorage timeout = developerList.get(0);
        assertEquals(408, timeout.getInt("id"));
        assertEquals(UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"), timeout.getUniqueID("player"));
        assertEquals("Timeout", timeout.getString("name"));
        NBTStorage asedem = timeout.getCompound("member");
        assertNotNull(asedem);
        assertEquals(409, asedem.getInt("id"));
        assertEquals(UUID.fromString("121a9207-cd9a-4717-ba06-bb96667492f1"), asedem.getUniqueID("player"));
        assertEquals("Asedem", asedem.getString("name"));

        NBTStorage sether = developerList.get(1);
        assertEquals(701, sether.getInt("id"));
        assertEquals(UUID.fromString("58a6382a-3b85-4d7f-8a6a-0b920ecb88bd"), sether.getUniqueID("player"));
        assertEquals("Sether", sether.getString("name"));
        NBTStorage kaigoe = sether.getCompound("member");
        assertNotNull(kaigoe);
        assertEquals(702, kaigoe.getInt("id"));
        assertEquals(UUID.fromString("b24b275f-23ee-4c3f-ba98-6bce8442bd8a"), kaigoe.getUniqueID("player"));
        assertEquals("kaigoe", kaigoe.getString("name"));

        assertEquals("Hello World", compressed.getString("string"));
        assertEquals((byte) 5, compressed.getByte("byte"));
        assertEquals(2356.232535D, compressed.getDouble("double"));
        assertArrayEquals(new long[] { 6L, 7L, 8L, 9L }, compressed.getLongArray("longArray"));
        assertArrayEquals(new byte[] { 10, 23, 0 }, compressed.getByteArray("byteArray"));
        assertEquals(343575324, compressed.getInt("integer"));
        assertEquals(4F, compressed.getFloat("float"));
        assertEquals(UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"), compressed.getUniqueID("uuid"));
        assertEquals(List.of(12L, 1L, 12467L, 76433L), compressed.getList("longList", Long.class));
        assertEquals(9223372036854775807L, compressed.getLong("long"));
        assertArrayEquals(new int[] { 1, 2, 3, 4, 5 }, compressed.getIntArray("intArray"));
        assertEquals(List.of("Hello", "World", "Test"), compressed.getList("stringList", String.class));
        assertEquals((short) 2463, compressed.getShort("short"));
    }
}
