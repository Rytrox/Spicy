package de.rytrox.spicy.item;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

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

    @Test
    public void shouldCreateBuilderWithMaterial() {
        ItemStackBuilder builder = new ItemStackBuilder(Material.ALLIUM);

        assertEquals(Material.ALLIUM, builder.currentBuilding.getType());
    }

    @Test
    public void shouldBuildItemStack() {
        ItemStackBuilder builder = new ItemStackBuilder();
        ItemStack built = builder.toItemStack();

        assertEquals(builder.currentBuilding, built);
    }

    @Test
    public void shouldSetDisplayName() {
        ItemStackBuilder builder = new ItemStackBuilder();

        assertFalse(Objects.requireNonNull(builder.currentBuilding.getItemMeta()).hasDisplayName());
        builder.displayName("Test");

        assertEquals("Test", Objects.requireNonNull(builder.currentBuilding.getItemMeta()).getDisplayName());
    }

    @Test
    public void shouldAddAnyEnchantment() {
        ItemStackBuilder builder = new ItemStackBuilder();

        assertTrue(builder.currentBuilding.getEnchantments().isEmpty());

        builder.enchantment(Enchantment.LURE, 1)
                .enchantment(Enchantment.DURABILITY, 2);

        assertTrue(builder.currentBuilding.getEnchantments().containsKey(Enchantment.LURE));
        assertEquals(1, Objects.requireNonNull(builder.currentBuilding.getItemMeta()).getEnchantLevel(Enchantment.LURE));
        assertTrue(builder.currentBuilding.getEnchantments().containsKey(Enchantment.DURABILITY));
        assertEquals(2, Objects.requireNonNull(builder.currentBuilding.getItemMeta()).getEnchantLevel(Enchantment.DURABILITY));
    }

    @Test
    public void shouldRemoveAnyEnchantment() {
        ItemStackBuilder builder = new ItemStackBuilder();

        builder.enchantment(Enchantment.DURABILITY, 1)
                .removeEnchantment(Enchantment.DURABILITY);

        assertFalse(builder.currentBuilding.getEnchantments().containsKey(Enchantment.DURABILITY));
    }

    @Test
    public void shouldSetModelData() {
        ItemStackBuilder builder = new ItemStackBuilder();

        assertFalse(Objects.requireNonNull(builder.currentBuilding.getItemMeta()).hasCustomModelData());
        builder.modelData(2552);

        assertEquals(2552, Objects.requireNonNull(builder.currentBuilding.getItemMeta()).getCustomModelData());
    }

    @Test
    public void shouldAddLore() {
        ItemStackBuilder builder = new ItemStackBuilder();

        builder.lore(Arrays.asList("Test", "Test1", "Test2"));

        assertEquals(Arrays.asList("Test", "Test1", "Test2"), Objects.requireNonNull(builder.currentBuilding.getItemMeta()).getLore());

        builder.lore("Test3", "Test4", "Test5");
        assertEquals(Arrays.asList("Test3", "Test4", "Test5"), builder.currentBuilding.getItemMeta().getLore());
    }

    @Test
    public void shouldHideEnchantments() {
        ItemStackBuilder builder = new ItemStackBuilder();

        builder.enchantment(Enchantment.DURABILITY, 3)
                .hideEnchantments(true);

        assertFalse(builder.currentBuilding.getEnchantments().isEmpty());
        assertTrue(Objects.requireNonNull(builder.currentBuilding.getItemMeta()).getItemFlags().contains(ItemFlag.HIDE_ENCHANTS));
    }

    @Test
    public void shouldNotHideEnchantments() {
        ItemStackBuilder builder = new ItemStackBuilder();

        builder.enchantment(Enchantment.DURABILITY, 3)
                .hideEnchantments(false);

        assertFalse(builder.currentBuilding.getEnchantments().isEmpty());
        assertFalse(Objects.requireNonNull(builder.currentBuilding.getItemMeta()).getItemFlags().contains(ItemFlag.HIDE_ENCHANTS));
    }

    @Test
    public void shouldSetFlags() {
        ItemStackBuilder builder = new ItemStackBuilder();

        builder.flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS);
        assertEquals(new HashSet<>(List.of(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS)),
                Objects.requireNonNull(builder.currentBuilding.getItemMeta()).getItemFlags());

        builder.flags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_DYE);
        assertEquals(new HashSet<>(List.of(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_DYE)),
                Objects.requireNonNull(builder.currentBuilding.getItemMeta()).getItemFlags());
    }

    @Test
    public void shouldSetAmount() {
        ItemStackBuilder builder = new ItemStackBuilder();

        builder.amount(23);
        assertEquals(23, builder.currentBuilding.getAmount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotSetNegativeAmount() {
        ItemStackBuilder builder = new ItemStackBuilder();

        builder.amount(-23);
    }

    @After
    public void unmock() {
        MockBukkit.unmock();
    }
}
