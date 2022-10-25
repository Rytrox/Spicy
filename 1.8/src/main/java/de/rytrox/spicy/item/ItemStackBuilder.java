package de.rytrox.spicy.item;

import java.util.*;

import de.rytrox.spicy.reflect.ItemStacks;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class ItemStackBuilder {

    protected ItemStack currentBuilding;

    public ItemStackBuilder() {
        this.currentBuilding = new ItemStack(Material.STONE);
    }

    public ItemStackBuilder(@NotNull ItemStack base) {
        this.currentBuilding = base.clone();
    }

    public ItemStackBuilder(@NotNull Material material) {
        this.currentBuilding = new ItemStack(material);
    }

    /**
     * This method converts the builder into an ItemStack and returns it
     * @return the itemstack
     */
    public ItemStack toItemStack() {
        return currentBuilding;
    }

    /**
     * This method set the display name of the item
     * @param displayName the display name
     * @return the builder to continue
     */
    @NotNull
    public ItemStackBuilder displayName(String displayName) {
        // set DisplayName
        ItemMeta meta = ItemStacks.getSafeItemMeta(currentBuilding);
        meta.setDisplayName(displayName);
        currentBuilding.setItemMeta(meta);
        // return this to continue
        return this;
    }

    /**
     * This Method add an Enchantment to the Item with a certain level
     * @param enchantment the Enchantment
     * @param level the level
     * @return the builder to continue
     */
    @NotNull
    public ItemStackBuilder enchantment(Enchantment enchantment, int level) {
        // set enchantment
        this.currentBuilding.addUnsafeEnchantment(enchantment, level);

        // return this to continue
        return this;
    }

    /**
     * This Method removes an Enchantment from the Item
     * @param enchantment the Enchant you want to remove
     * @return the builder to continue
     */
    @NotNull
    public ItemStackBuilder removeEnchantment(Enchantment enchantment) {
        // remove enchantment
        ItemMeta meta = ItemStacks.getSafeItemMeta(currentBuilding);
        meta.removeEnchant(enchantment);
        currentBuilding.setItemMeta(meta);
        // return this to continue
        return this;
    }

    /**
     * This Method sets the Lore of the Item
     * @param lore the Lore you want to set
     * @return the builder to continue
     */
    @NotNull
    public ItemStackBuilder lore(List<String> lore) {
        // Set Lore for currentBuilding
        ItemMeta meta = ItemStacks.getSafeItemMeta(currentBuilding);
        meta.setLore(lore);
        currentBuilding.setItemMeta(meta);
        // return this to continue
        return this;
    }

    /**
     * This Method hides all enchantments in the lore
     * @param result a bool which answers if you want to hide the enchantments. true means the enchantments will be hidden, false otherwise
     * @return the builder to continue
     */
    @NotNull
    public ItemStackBuilder hideEnchantments(boolean result) {
        // get Meta
        ItemMeta meta = ItemStacks.getSafeItemMeta(currentBuilding);
        // show or hide enchantments
        if(result) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        else meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        // set Meta
        currentBuilding.setItemMeta(meta);
        // return this to continue
        return this;
    }

    /**
     * This Method sets the flags of the ItemStack. <br>
     * It will remove all other flags before
     *
     * @param flags the flags of the ItemStack
     * @return the builder to continue
     */
    @NotNull
    public ItemStackBuilder flags(ItemFlag... flags) {
        ItemMeta meta = ItemStacks.getSafeItemMeta(currentBuilding);

        // Remove all old flags
        for(ItemFlag flag : meta.getItemFlags()) {
            meta.removeItemFlags(flag);
        }

        meta.addItemFlags(flags);
        currentBuilding.setItemMeta(meta);

        return this;
    }

    /**
     * This Method set the amount of the Item. The amount must be positive.
     * @param amount the amount
     * @return the builder to continue
     * @throws IllegalArgumentException if the amount is negative
     */
    @NotNull
    public ItemStackBuilder amount(int amount) {
        Validate.isTrue(amount >= 0, "Amount must be positive");

        // set Amount
        currentBuilding.setAmount(amount);
        // returng this to continue
        return this;
    }

    /**
     * This method sets the lore
     * @param lines the lines you want to add
     * @return the builder to continue
     * @throws IllegalArgumentException if the lines are empty or null
     */
    @NotNull
    public ItemStackBuilder lore(String... lines) {
        // Validate
        Validate.notEmpty(lines, "new Lines cannot be empty or null");
        // create new lore
        return this.lore(Arrays.asList(lines));
    }
}
