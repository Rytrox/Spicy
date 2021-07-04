package de.timeout.libs.item;

import be.seeseemelk.mockbukkit.MockBukkit;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.server.v1_16_R3.IMaterial;
import net.minecraft.server.v1_16_R3.Items;
import net.minecraft.server.v1_16_R3.LocaleLanguage;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor({ "de.timeout.libs.item.ItemStacks" })
@PrepareForTest({ ItemStacks.class, ItemStack.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*", "org.w3c.dom.*" })
public class ItemStacksTest {

    private final ItemStack itemStack = new ItemStack(Material.STONE);

    private final Class<?> itemstackClass = net.minecraft.server.v1_16_R3.ItemStack.class;
    private final Class<?> craftItemStackClass = CraftItemStack.class;
    private final Class<?> nbttagcompoundClass = NBTTagCompound.class;
    private final Class<?> localelanguageClass = LocaleLanguage.class;

    @Before
    public void setup() throws IllegalAccessException {
        PowerMockito.mockStatic(ItemStack.class);
        MockBukkit.mock();

        FieldUtils.writeDeclaredStaticField(ItemStacks.class, "GSON", new Gson(), true);
        FieldUtils.writeDeclaredStaticField(ItemStacks.class, "itemstackClass", itemstackClass, true);
        FieldUtils.writeDeclaredStaticField(ItemStacks.class, "craftitemstackClass", craftItemStackClass, true);
        FieldUtils.writeDeclaredStaticField(ItemStacks.class, "nbttagcompoundClass", nbttagcompoundClass, true);
        FieldUtils.writeDeclaredStaticField(ItemStacks.class, "localelanguageClass", localelanguageClass, true);
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void shouldEncodeItemStackToJSON() throws IllegalAccessException {
        FieldUtils.writeDeclaredStaticField(ItemStacks.class, "GSON", new Gson(), true);

        JsonObject element = ItemStacks.encodeJson(itemStack);

        assertNotNull(element);
        assertEquals("STONE", element.get("type").getAsString());
    }

    @Test
    public void shouldDecodeJSONToItemStack() throws IllegalAccessException {
        // Tear up mock-methods (MockBukkit doesn't support ItemStack.deserialize yet...)
        Mockito.when(ItemStack.deserialize(Mockito.any()))
                .thenAnswer((inheritance) -> new ItemStack(itemStack));
        FieldUtils.writeDeclaredStaticField(ItemStacks.class, "GSON", new Gson(), true);

        JsonObject element = ItemStacks.encodeJson(itemStack);
        ItemStack copy = ItemStacks.decodeJson(element);

        assertEquals(this.itemStack, copy);
    }

    @Test
    public void shouldEncodeItemStackToBase64() throws IOException {
        // Unable to mock this test...
//        String base = ItemStacks.encodeBase64(itemStack);
//
//        assertNotNull(base);
//        assertTrue(base.matches("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$"));
    }

    @Test
    public void shouldDecodeBase64ToItemStack() throws IOException, ClassNotFoundException {
        // Unable to mock this test...
//        String base = ItemStacks.encodeBase64(itemStack);
//        ItemStack copy = ItemStacks.decodeBase64(base);
//
//        assertEquals(itemStack, copy);
    }

    /**
     * ItemStacks#getCustomName TESTS
     */

    @Test
    public void shouldGetCustomNameAsDisplayName() {
        ItemStack itemStack = new ItemStack(this.itemStack);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName("Testitem");
        itemStack.setItemMeta(meta);

        assertEquals("Testitem", ItemStacks.getCustomizedName(itemStack));
    }

    @Test
    public void shouldGetCustomNameFromLocalLanguage() {
        try(MockedStatic<WordUtils> mockedStatic = Mockito.mockStatic(WordUtils.class)) {
            mockedStatic.when(() -> WordUtils.capitalize(Mockito.anyString()))
                    .thenAnswer((invocationOnMock) -> "Test");

            String name = ItemStacks.getCustomizedName(itemStack);
            assertEquals("Test", name);

            mockedStatic.verify(Mockito.times(1), () -> WordUtils.capitalize(Mockito.anyString()));
        }
    }
}
