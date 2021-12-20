package de.timeout.libs.item;

import be.seeseemelk.mockbukkit.MockBukkit;

import com.google.gson.JsonObject;

import de.timeout.libs.reflect.Reflections;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ItemStacksTest {

    private final ItemStack itemStack = new ItemStack(Material.STONE);

    @Before
    public void setup() throws IllegalAccessException {
        MockBukkit.mock();
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void shouldEncodeItemStackToJSON() throws IllegalAccessException {
        JsonObject element = ItemStacks.encodeJson(itemStack);

        assertNotNull(element);
        assertEquals("STONE", element.get("type").getAsString());
    }

    @Test
    public void shouldDecodeJSONToItemStack() throws IllegalAccessException {
        // Tear up mock-methods (MockBukkit doesn't support ItemStack.deserialize yet...)
        try (MockedStatic<ItemStack> mockedStatic = Mockito.mockStatic(ItemStack.class, Mockito.CALLS_REAL_METHODS)) {
            mockedStatic.when(() -> ItemStack.deserialize(Mockito.any()))
                    .thenAnswer((inheritance) -> new ItemStack(itemStack));

            JsonObject element = ItemStacks.encodeJson(itemStack);
            ItemStack copy = ItemStacks.decodeJson(element);

            assertEquals(this.itemStack, copy);
        }
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
        try (MockedStatic<WordUtils> mockedStatic = Mockito.mockStatic(WordUtils.class);
             MockedStatic<Reflections> reflectionsMockedStatic = Mockito.mockStatic(Reflections.class)) {
            mockedStatic.when(() -> WordUtils.capitalize(Mockito.anyString()))
                    .thenAnswer((invocationOnMock) -> "Test");
            reflectionsMockedStatic.when(() -> Reflections.getCraftBukkitClass(Mockito.eq("inventory.CraftItemStack")))
                    .thenAnswer(invocationOnMock -> CraftItemStack.class);


            String name = ItemStacks.getCustomizedName(itemStack);
            assertEquals("Test", name);

            mockedStatic.verify(() -> WordUtils.capitalize(Mockito.anyString()), Mockito.times(1));
        }
    }
}
