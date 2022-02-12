package de.rytrox.spicy.config;

import be.seeseemelk.mockbukkit.MockBukkit;
import net.minecraft.core.MinecraftSerializableUUID;
import net.minecraft.nbt.*;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;

public class NBTConfigTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private NBTTagCompound testCompound;

    @Before
    public void prepareConfig() {
        MockBukkit.mock();

        testCompound = new NBTTagCompound();

        // Map all primitives
        testCompound.a("boolean", true);
        testCompound.a("byte", (byte) 5);
        testCompound.a("short", (short) 2463);
        testCompound.a("integer", 343575324);
        testCompound.a("long", Long.MAX_VALUE);
        testCompound.a("char", 'r');
        testCompound.a("float", 4.0F);
        testCompound.a("double", 2356.232535D);

        // Supported full classes
        testCompound.a("string", "Hello World");
        testCompound.a("uuid", UUID.randomUUID());
        testCompound.a("offlineplayer", UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"));

        // Support array classes
        testCompound.a("intArray", new int[] { 1, 2, 3, 4, 5}); // size 4 is a UUID!
        testCompound.a("longArray", new long[] { 6L, 7L, 8L, 9L});
        testCompound.a("byteArray", new byte[] { 10, 23, 0 });

        // Support List classes
        NBTTagList list = new NBTTagList();
        list.add(NBTTagString.a("Hello"));
        list.add(NBTTagString.a("World"));
        list.add(NBTTagString.a("Test"));
        testCompound.a("stringList", list);

        NBTTagList intList = new NBTTagList();
        intList.add(NBTTagLong.a(12));
        intList.add(NBTTagLong.a(1));
        intList.add(NBTTagLong.a(12467));
        intList.add(NBTTagLong.a(76433));
        testCompound.a("longList", intList);

        NBTTagList developerList = new NBTTagList();
        NBTTagCompound timeout = new NBTTagCompound();
        timeout.a("name", "Timeout");
        timeout.a("id", 408);
        timeout.a("player", UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"));
        NBTTagCompound asedem = new NBTTagCompound();
        asedem.a("name", "Asedem");
        asedem.a("id", 409);
        asedem.a("player", UUID.fromString("121a9207-cd9a-4717-ba06-bb96667492f1"));
        timeout.a("assistant", asedem);

        NBTTagCompound sether = new NBTTagCompound();
        sether.a("name", "Sether");
        sether.a("id", 701);
        sether.a("player", UUID.fromString("58a6382a-3b85-4d7f-8a6a-0b920ecb88bd"));
        NBTTagCompound kaigoe = new NBTTagCompound();
        kaigoe.a("name", "kaigoe");
        kaigoe.a("id", 702);
        kaigoe.a("player", UUID.fromString("b24b275f-23ee-4c3f-ba98-6bce8442bd8a"));
        sether.a("assistant", kaigoe);

        developerList.add(timeout);
        developerList.add(sether);
        testCompound.a("developerList", developerList);

        NBTTagList team = new NBTTagList();
        team.add(new NBTTagIntArray(MinecraftSerializableUUID.a(UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"))));
        team.add(new NBTTagIntArray(MinecraftSerializableUUID.a(UUID.fromString("121a9207-cd9a-4717-ba06-bb96667492f1"))));
        team.add(new NBTTagIntArray(MinecraftSerializableUUID.a(UUID.fromString("58a6382a-3b85-4d7f-8a6a-0b920ecb88bd"))));
        team.add(new NBTTagIntArray(MinecraftSerializableUUID.a(UUID.fromString("b24b275f-23ee-4c3f-ba98-6bce8442bd8a"))));
        testCompound.a("teamList", team);
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
        assertEquals(Bukkit.getOfflinePlayer(testCompound.a("uuid")).getUniqueId(),
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

        File file = temporaryFolder.newFile();

        config.save(file);
        assertTrue(file.length() > 0L);
    }

    @Test
    public void shouldSaveConfigInCompound() {
        NBTTagCompound compound = new NBTTagCompound();

        compound.a("string", "Testwert1");
        compound.a("int", 124);
        compound.a("byte", (byte) 4);
        compound.a("short", (short) 16);

        NBTConfig config = new NBTConfig(compound);

        NBTTagCompound saved = config.save();

        assertEquals(saved, compound);
        assertEquals("Testwert1", Objects.requireNonNull(saved.c("string")).e_());
        assertEquals(124, ((NBTTagInt) Objects.requireNonNull(saved.c("int"))).k());
        assertEquals((byte) 4, ((NBTTagByte) Objects.requireNonNull(saved.c("byte"))).k());
        assertEquals((short) 16, ((NBTTagShort) Objects.requireNonNull(saved.c("short"))).k());
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}
