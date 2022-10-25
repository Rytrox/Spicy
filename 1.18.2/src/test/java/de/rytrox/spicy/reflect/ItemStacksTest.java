package de.rytrox.spicy.reflect;

import be.seeseemelk.mockbukkit.MockBukkit;

import com.google.gson.JsonObject;

import de.rytrox.spicy.reflect.ItemStacks;
import de.rytrox.spicy.reflect.Reflections;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ItemStacksTest {

    private final ItemStack itemStack = new ItemStack(Material.STONE);

    @BeforeAll
    public static void setup() {
        MockBukkit.mock();
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void shouldEncodeItemStackToJSON() {
        JsonObject element = ItemStacks.encodeJson(itemStack);

        assertNotNull(element);
        assertEquals("STONE", element.get("type").getAsString());
    }

    @Test
    public void shouldDecodeJSONToItemStack() {
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
        String base = ItemStacks.encodeBase64(itemStack);

        assertNotNull(base);
        assertTrue(base.matches("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$"));
    }

//    // https://github.com/MockBukkit/MockBukkit/issues/300
//    @Test
//    public void shouldDecodeBase64ToItemStack() throws IOException, ClassNotFoundException {
//        // Unable to mock this test...
//        String base = ItemStacks.encodeBase64(itemStack);
//        ItemStack copy = ItemStacks.decodeBase64(base);
//
//        assertEquals(itemStack, copy);
//    }

    // Unable to test NMS-Copy successful: Not bootstrapped (called from registry ResourceKey[minecraft:root / minecraft:root])
    // => Wait for MockBukkit to support NMS
//    @Test
//    public void shouldGetNMSCopy() {
//        try(MockedStatic<Reflections> mockedStatic = Mockito.mockStatic(Reflections.class);
//            MockedStatic<CraftItemStack> craftItemStackMockedStatic = Mockito.mockStatic(CraftItemStack.class)) {
//            mockedStatic.when(() -> Reflections.getCraftBukkitClass(Mockito.anyString()))
//                    .thenAnswer((invocationOnMock) -> CraftItemStack.class);
//            craftItemStackMockedStatic.when(() -> CraftItemStack.asNMSCopy(Mockito.eq(itemStack)))
//                    .thenReturn(nmsStack);
//
//            ItemStack itemStack = new ItemStack(this.itemStack);
//            net.minecraft.world.item.ItemStack copy = ItemStacks.asNMSCopy(itemStack);
//
//            assertNotNull(copy);
//        }
//    }

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

    @Test
    public void shouldGetSafeItemMeta() {
        ItemMeta meta = ItemStacks.getSafeItemMeta(this.itemStack);

        assertNotNull(meta);
        assertEquals(itemStack.getItemMeta(), meta);
    }

    @Test
    public void shouldThrowErrorWhenItemMetaIsNull() {
        ItemStack itemStack = Mockito.mock(ItemStack.class);
        Mockito.doReturn(null).when(itemStack).getItemMeta();

        assertThrows(IllegalStateException.class, () -> ItemStacks.getSafeItemMeta(itemStack));
    }
}
