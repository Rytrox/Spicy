package de.timeout.libs.config;

import de.timeout.libs.config.NBTConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class NBTConfigTest {

    @Test
    public void shouldReadSimpleTagCompound() {
        NBTTagCompound compound = new NBTTagCompound();

        compound.a("string", "Testwert1");
        compound.a("int", 124);
        compound.a("byte", (byte) 4);
        compound.a("short", (short) 16);

        NBTConfig config = NBTConfig.fromTagCompound(compound);
        assertEquals("Testwert1", config.get("string"));
        assertEquals(124, config.get("int"));
        assertEquals((byte) 4, config.get("byte"));
        assertEquals((short) 16, config.get("short"));
    }

    @Test
    public void shouldReadNestedTagCompounds() {
        NBTTagCompound root = new NBTTagCompound();
        NBTTagCompound child = new NBTTagCompound();
        NBTTagCompound grandchild = new NBTTagCompound();

        root.a("child", child);
        child.a("child", grandchild);
        grandchild.a("value", "Gefunden");

        NBTConfig config = NBTConfig.fromTagCompound(root);

        assertEquals("Gefunden", config.get("child.child.value"));
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

        NBTConfig config = NBTConfig.fromTagCompound(compound);

        assertEquals(Arrays.asList(8, 2, 65, 3, 6, -23, 46), config.getIntegerList("intArray"));
        assertEquals(Arrays.asList((byte) 45, (byte) 35, (byte) 2, (byte) 4, (byte) -56), config.getByteList("byteArray"));
        assertEquals(Arrays.asList(36L, 3L, 78L, 4L, 45687L, 5673L, 324L, 325L, 34L), config.getLongList("longArray"));
        assertEquals(strings, config.getStringList("stringArray"));
    }

    @Test
    public void shouldSaveConfig() {

    }
}
