package de.timeout.libs.item;

import be.seeseemelk.mockbukkit.MockBukkit;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ItemStacksTest {

    private static final ItemStack ITEM = new ItemStack(Material.COOKIE, 7);


    @Before
    public void mock() {
        MockBukkit.mock();
    }

    @Test
    public void shouldHandleBase64() {
        String base64 = ItemStacks.encodeBase64(ITEM);
        assertNotNull(base64);

        ItemStack itemStack = ItemStacks.decodeBase64(base64);
        assertEquals(ITEM, itemStack);
    }

    @Test
    public void shouldHandleJSON() {
        JsonObject obj = ItemStacks.encodeJson(ITEM);
        assertNotNull(obj);

        ItemStack itemStack = ItemStacks.decodeJson(obj);
        assertEquals(ITEM, itemStack);
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}
