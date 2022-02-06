package de.timeout.libs.config;

import net.minecraft.nbt.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class NBTConfigTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldReadSimpleTagCompound() {
        NBTTagCompound compound = new NBTTagCompound();

        compound.a("string", "Testwert1");
        compound.a("int", 124);
        compound.a("byte", (byte) 4);
        compound.a("short", (short) 16);

        NBTConfig config = new NBTConfig(compound);
        assertEquals("Testwert1", config.getString("string"));
        assertEquals(124, config.getInt("int"));
        assertEquals((byte) 4, config.getInt("byte"));
        assertEquals((short) 16, config.getInt("short"));
    }

    @Test
    public void shouldReadNestedTagCompounds() {
        NBTTagCompound root = new NBTTagCompound();
        NBTTagCompound child = new NBTTagCompound();
        NBTTagCompound grandchild = new NBTTagCompound();

        root.a("child", child);
        child.a("child", grandchild);
        grandchild.a("value", "Gefunden");

        NBTConfig config = new NBTConfig(root);

        assertEquals("Gefunden", config.getString("child.child.value"));
    }

    @Test
    public void shouldReadSimpleArrayCompounds() {
        NBTTagCompound compound = new NBTTagCompound();

        compound.a("intArray", new int[] { 8, 2, 65, 3, 6, -23, 46 });
        compound.a("byteArray", new byte[] { 45, 35, 2, 4, -56 });
        compound.a("longArray", new long[] { 36, 3, 78, 4, 45687, 5673, 324, 325, 34 });

        NBTTagList list = new NBTTagList();
        List<String> strings = IntStream.range(0, 12)
                .mapToObj((_v) -> String.valueOf(Math.random()))
                .toList();

        strings.forEach(element -> list.add(NBTTagString.a(element)));
        compound.a("stringArray", list);

        NBTConfig config = new NBTConfig(compound);

        assertEquals(Arrays.asList(8, 2, 65, 3, 6, -23, 46), config.getIntegerList("intArray"));
        assertEquals(Arrays.asList((byte) 45, (byte) 35, (byte) 2, (byte) 4, (byte) -56), config.getByteList("byteArray"));
        assertEquals(Arrays.asList(36L, 3L, 78L, 4L, 45687L, 5673L, 324L, 325L, 34L), config.getLongList("longArray"));
        assertEquals(strings, config.getStringList("stringArray"));
    }

    @Test
    public void shouldSaveConfigInFile() throws IOException {
        NBTTagCompound compound = new NBTTagCompound();

        compound.a("string", "Testwert1");
        compound.a("int", 124);
        compound.a("byte", (byte) 4);
        compound.a("short", (short) 16);

        NBTConfig config = new NBTConfig(compound);

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
}
