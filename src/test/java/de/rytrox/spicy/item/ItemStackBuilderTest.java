package de.rytrox.spicy.item;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ItemStackBuilderTest {

    @Before
    public void mock() {
        MockBukkit.mock();
    }

    @Test
    public void shouldCreateBuilderWithDefaultItem() {
        ItemStackBuilder builder = new ItemStackBuilder();

        assertEquals(new ItemStack(Material.STONE), builder.currentBuilding);
    }

    @Test
    public void shouldCreateBuilderWithBaseItem() {
        ItemStack itemStack = new ItemStack(Material.DIAMOND_SWORD);
        ItemStackBuilder builder = new ItemStackBuilder(itemStack);

        assertEquals(itemStack, builder.currentBuilding);
    }

    @After
    public void unmock() {
        MockBukkit.unmock();
    }
}
