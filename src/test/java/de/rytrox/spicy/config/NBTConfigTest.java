package de.rytrox.spicy.config;

import be.seeseemelk.mockbukkit.MockBukkit;
import net.minecraft.core.SerializableUUID;
import net.minecraft.nbt.*;

import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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

public class NBTConfigTest {

    @TempDir
    public Path temporaryFolder;

    private CompoundTag testCompound;

    @BeforeAll
    public static void setupServer() {
        MockBukkit.mock();
    }

    @BeforeEach
    public void prepareConfig() {
        testCompound = new CompoundTag();

        // Map all primitives
        testCompound.putBoolean("boolean", true);
        testCompound.putByte("byte", (byte) 5);
        testCompound.putShort("short", (short) 2463);
        testCompound.putInt("integer", 343575324);
        testCompound.putLong("long", Long.MAX_VALUE);
        testCompound.putInt("char", 'r');
        testCompound.putFloat("float", 4.0F);
        testCompound.putDouble("double", 2356.232535D);

        // Supported full classes
        testCompound.putString("string", "Hello World");
        testCompound.putUUID("uuid", UUID.randomUUID());
        testCompound.putUUID("offlineplayer", UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"));

        // Support array classes
        testCompound.putIntArray("intArray", new int[] { 1, 2, 3, 4, 5}); // size 4 is a UUID!
        testCompound.putLongArray("longArray", new long[] { 6L, 7L, 8L, 9L});
        testCompound.putByteArray("byteArray", new byte[] { 10, 23, 0 });

        // Support List classes
        ListTag list = new ListTag();
        list.add(StringTag.valueOf("Hello"));
        list.add(StringTag.valueOf("World"));
        list.add(StringTag.valueOf("Test"));
        testCompound.put("stringList", list);

        ListTag intList = new ListTag();
        intList.add(LongTag.valueOf(12));
        intList.add(LongTag.valueOf(1));
        intList.add(LongTag.valueOf(12467));
        intList.add(LongTag.valueOf(76433));
        testCompound.put("longList", intList);

        ListTag developerList = new ListTag();
        CompoundTag timeout = new CompoundTag();
        timeout.putString("name", "Timeout");
        timeout.putInt("id", 408);
        timeout.putUUID("player", UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"));
        CompoundTag asedem = new CompoundTag();
        asedem.putString("name", "Asedem");
        asedem.putInt("id", 409);
        asedem.putUUID("player", UUID.fromString("121a9207-cd9a-4717-ba06-bb96667492f1"));
        timeout.put("assistant", asedem);

        CompoundTag sether = new CompoundTag();
        sether.putString("name", "Sether");
        sether.putInt("id", 701);
        sether.putUUID("player", UUID.fromString("58a6382a-3b85-4d7f-8a6a-0b920ecb88bd"));
        CompoundTag kaigoe = new CompoundTag();
        kaigoe.putString("name", "kaigoe");
        kaigoe.putInt("id", 702);
        kaigoe.putUUID("player", UUID.fromString("b24b275f-23ee-4c3f-ba98-6bce8442bd8a"));
        sether.put("assistant", kaigoe);

        developerList.add(timeout);
        developerList.add(sether);
        testCompound.put("developerList", developerList);

        ListTag team = new ListTag();
        team.add(new IntArrayTag(SerializableUUID.uuidToIntArray(UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"))));
        team.add(new IntArrayTag(SerializableUUID.uuidToIntArray(UUID.fromString("121a9207-cd9a-4717-ba06-bb96667492f1"))));
        team.add(new IntArrayTag(SerializableUUID.uuidToIntArray(UUID.fromString("58a6382a-3b85-4d7f-8a6a-0b920ecb88bd"))));
        team.add(new IntArrayTag(SerializableUUID.uuidToIntArray(UUID.fromString("b24b275f-23ee-4c3f-ba98-6bce8442bd8a"))));
        testCompound.put("teamList", team);
    }

    @Test
    public void shouldCreateConfig() {
        // Should create an empty config
        NBTConfig empty = new NBTConfig();

        assertTrue(empty.getKeys(true).isEmpty());

        NBTConfig filled = new NBTConfig(testCompound);
        assertFalse(filled.getKeys(false).isEmpty());
    }

    @Test
    public void shouldReadConfigFromCompound() {
        NBTConfig config = new NBTConfig(testCompound);

        assertTrue(config.getBoolean("boolean"));
        assertEquals(5, config.getInt("byte"));
        assertEquals(2463, config.getInt("short"));
        assertEquals(343575324, config.getInt("integer"));
        assertEquals(Long.MAX_VALUE, config.getLong("long"));
        assertEquals('r', config.getCharacter("char"));
        assertEquals(4F, config.getDouble("float"), 0);
        assertEquals(2356.232535D, config.getDouble("double"), 0);

        assertEquals("Hello World", config.getString("string"));
        assertEquals(testCompound.getUUID("uuid"),
                Objects.requireNonNull(config.getOfflinePlayer("uuid")).getUniqueId());
        assertEquals(UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"),
                Objects.requireNonNull(config.getOfflinePlayer("offlineplayer")).getUniqueId());

        assertEquals(Arrays.asList(1, 2, 3, 4, 5), config.getIntegerList("intArray"));
        assertEquals(Arrays.asList(6L, 7L, 8L, 9L), config.getLongList("longArray"));
        assertEquals(Arrays.asList((byte) 10, (byte) 23, (byte) 0), config.getByteList("byteArray"));

        HashMap<String, Object> timeout = (HashMap<String, Object>) config.getList("developerList").get(0);
        assertTrue(timeout instanceof Map<?,?>);
        assertEquals(408, timeout.get("id"));
        assertEquals("Timeout", timeout.get("name"));
        assertEquals(UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"),
                ((OfflinePlayer) timeout.get("player")).getUniqueId());
        HashMap<String, Object> sether = (HashMap<String, Object>) config.getList("developerList").get(1);
        assertTrue(sether instanceof Map<?,?>);
        assertEquals(701, sether.get("id"));
        assertEquals("Sether", sether.get("name"));
        assertEquals(UUID.fromString("58a6382a-3b85-4d7f-8a6a-0b920ecb88bd"),
                ((OfflinePlayer) sether.get("player")).getUniqueId());

        HashMap<String, Object> asedem = (HashMap<String, Object>) timeout.get("assistant");
        assertTrue(asedem instanceof Map<?,?>);
        assertEquals(409, asedem.get("id"));
        assertEquals("Asedem", asedem.get("name"));
        assertEquals(UUID.fromString("121a9207-cd9a-4717-ba06-bb96667492f1"),
                ((OfflinePlayer) asedem.get("player")).getUniqueId());

        HashMap<String, Object> kaigoe = (HashMap<String, Object>) sether.get("assistant");
        assertTrue(kaigoe instanceof Map<?,?>);
        assertEquals(702, kaigoe.get("id"));
        assertEquals("kaigoe", kaigoe.get("name"));
        assertEquals(UUID.fromString("b24b275f-23ee-4c3f-ba98-6bce8442bd8a"),
                ((OfflinePlayer) kaigoe.get("player")).getUniqueId());

        List<OfflinePlayer> team = (List<OfflinePlayer>) config.getList("teamList");
        assertNotNull(team);
        System.out.println(team.get(0));
        assertEquals(UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"), team.get(0).getUniqueId());
        assertEquals(UUID.fromString("121a9207-cd9a-4717-ba06-bb96667492f1"), team.get(1).getUniqueId());
        assertEquals(UUID.fromString("58a6382a-3b85-4d7f-8a6a-0b920ecb88bd"), team.get(2).getUniqueId());
        assertEquals(UUID.fromString("b24b275f-23ee-4c3f-ba98-6bce8442bd8a"), team.get(3).getUniqueId());
    }

    @Test
    public void shouldReadConfigFromUncompressedFile() throws IOException {
        NBTConfig config = NBTConfig.fromUncompressedFile(Paths.get("src", "test", "resources", "config.dat").toFile());

        assertTrue(config.getBoolean("boolean"));
        assertEquals(5, config.getInt("byte"));
        assertEquals(2463, config.getInt("short"));
        assertEquals(343575324, config.getInt("integer"));
        assertEquals(Long.MAX_VALUE, config.getLong("long"));
        assertEquals('r', config.getCharacter("char"));
        assertEquals(4F, config.getDouble("float"), 0);
        assertEquals(2356.232535D, config.getDouble("double"), 0);

        assertEquals("Hello World", config.getString("string"));
        assertEquals(UUID.fromString("4da098bc-9573-4524-855b-5cb418899f37"),
                Objects.requireNonNull(config.getOfflinePlayer("uuid")).getUniqueId());
        assertEquals(UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"),
                Objects.requireNonNull(config.getOfflinePlayer("offlineplayer")).getUniqueId());

        assertEquals(Arrays.asList(1, 2, 3, 4, 5), config.getIntegerList("intArray"));
        assertEquals(Arrays.asList(6L, 7L, 8L, 9L), config.getLongList("longArray"));
        assertEquals(Arrays.asList((byte) 10, (byte) 23, (byte) 0), config.getByteList("byteArray"));

        HashMap<String, Object> timeout = (HashMap<String, Object>) config.getList("developerList").get(0);
        assertEquals(408, timeout.get("id"));
        assertEquals("Timeout", timeout.get("name"));
        assertEquals(UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"),
                ((OfflinePlayer) timeout.get("player")).getUniqueId());
        HashMap<String, Object> sether = (HashMap<String, Object>) config.getList("developerList").get(1);
        assertEquals(701, sether.get("id"));
        assertEquals("Sether", sether.get("name"));
        assertEquals(UUID.fromString("58a6382a-3b85-4d7f-8a6a-0b920ecb88bd"),
                ((OfflinePlayer) sether.get("player")).getUniqueId());
    }

    @Test
    public void shouldReadConfigFromCompressedFile() throws IOException {
        NBTConfig config = NBTConfig.fromCompressedFile(Paths.get("src", "test", "resources", "config-compressed.dat").toFile());

        assertTrue(config.getBoolean("boolean"));
        assertEquals(5, config.getInt("byte"));
        assertEquals(2463, config.getInt("short"));
        assertEquals(343575324, config.getInt("integer"));
        assertEquals(Long.MAX_VALUE, config.getLong("long"));
        assertEquals('r', config.getCharacter("char"));
        assertEquals(4F, config.getDouble("float"), 0);
        assertEquals(2356.232535D, config.getDouble("double"), 0);

        assertEquals("Hello World", config.getString("string"));
        assertEquals(UUID.fromString("4da098bc-9573-4524-855b-5cb418899f37"),
                Objects.requireNonNull(config.getOfflinePlayer("uuid")).getUniqueId());
        assertEquals(UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"),
                Objects.requireNonNull(config.getOfflinePlayer("offlineplayer")).getUniqueId());

        assertEquals(Arrays.asList(1, 2, 3, 4, 5), config.getIntegerList("intArray"));
        assertEquals(Arrays.asList(6L, 7L, 8L, 9L), config.getLongList("longArray"));
        assertEquals(Arrays.asList((byte) 10, (byte) 23, (byte) 0), config.getByteList("byteArray"));

        HashMap<String, Object> timeout = (HashMap<String, Object>) config.getList("developerList").get(0);
        assertEquals(408, timeout.get("id"));
        assertEquals("Timeout", timeout.get("name"));
        assertEquals(UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"),
                ((OfflinePlayer) timeout.get("player")).getUniqueId());
        HashMap<String, Object> sether = (HashMap<String, Object>) config.getList("developerList").get(1);
        assertEquals(701, sether.get("id"));
        assertEquals("Sether", sether.get("name"));
        assertEquals(UUID.fromString("58a6382a-3b85-4d7f-8a6a-0b920ecb88bd"),
                ((OfflinePlayer) sether.get("player")).getUniqueId());
    }

    @Test
    public void shouldSaveConfigInFile() throws IOException {
        NBTConfig config = new NBTConfig(testCompound);

        File file = Files.createFile(temporaryFolder.resolve(Paths.get("file.dat"))).toFile();
        File compressed = Files.createFile(temporaryFolder.resolve(Paths.get("compressed.dat"))).toFile();

        config.saveUncompressed(file);
        assertTrue(file.length() > 0L);

        config.saveCompressed(compressed);
        assertTrue(compressed.length() > 0);

        NBTConfig readCompressed = NBTConfig.fromCompressedFile(compressed);
        NBTConfig readUncompressed = NBTConfig.fromUncompressedFile(file);

        assertFalse(readCompressed.getKeys(false).isEmpty());
        assertFalse(readUncompressed.getKeys(false).isEmpty());
    }

    @Test
    public void shouldSaveConfigInCompound() {
        CompoundTag compound = new CompoundTag();

        compound.putString("string", "Testwert1");
        compound.putInt("int", 124);
        compound.putByte("byte", (byte) 4);
        compound.putShort("short", (short) 16);

        NBTConfig config = new NBTConfig(compound);

        CompoundTag saved = config.save();

        assertEquals(saved, compound);
        assertEquals("Testwert1", saved.getString("string"));
        assertEquals(124, saved.getInt("int"));
        assertEquals((byte) 4, saved.getByte("byte"));
        assertEquals((short) 16, saved.getShort("short"));
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }
}
