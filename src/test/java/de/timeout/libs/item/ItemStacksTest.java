package de.timeout.libs.item;

import be.seeseemelk.mockbukkit.MockBukkit;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.timeout.libs.LibsTestPlugin;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor({ "de.timeout.libs.item.ItemStacks" })
@PrepareForTest({ ItemStacks.class, ItemStack.class })
@PowerMockIgnore("javax.management.*")
public class ItemStacksTest {

    private LibsTestPlugin libsTestPlugin;

    private final ItemStack itemStack = new ItemStack(Material.STONE);

    @Before
    public void setup() {
        PowerMockito.mockStatic(ItemStack.class);
        MockBukkit.mock();
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
}
