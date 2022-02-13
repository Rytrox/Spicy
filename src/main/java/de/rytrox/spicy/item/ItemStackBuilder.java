package de.rytrox.spicy.item;

import java.util.*;
import java.util.logging.Level;

import de.rytrox.spicy.config.NBTConfig;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.IChatBaseComponent;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftChatMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemStackBuilder {

    private static final String NBT_ERROR = "Cannot write NBT-Data in ";

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
    public ItemStackBuilder setDisplayName(String displayName) {
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
    public ItemStackBuilder addEnchantment(Enchantment enchantment, int level) {
        // set enchantment
        this.currentBuilding.addUnsafeEnchantment(enchantment, level);

        // return this to continue
        return this;
    }

    /**
     * This Method sets the Custom Model Data which is associated with the client model
     *
     * @param model the number of the model
     * @return the builder to continue
     */
    public ItemStackBuilder setModelData(@Nullable Integer model) {
        // set model data
        ItemMeta meta = ItemStacks.getSafeItemMeta(currentBuilding);
        meta.setCustomModelData(model);
        this.currentBuilding.setItemMeta(meta);

        return this;
    }

    /**
     * This Method removes an Enchantment from the Item
     * @param enchantment the Enchant you want to remove
     * @return the builder to continue
     */
    public ItemStackBuilder removeEnchantment(Enchantment enchantment) {
        // remove enchantment
        ItemMeta meta = ItemStacks.getSafeItemMeta(currentBuilding);
        meta.removeEnchant(enchantment);
        currentBuilding.setItemMeta(meta);
        // return this to continue
        return this;
    }

    /**
     * This Method sets the custom model data id of the itemstack
     *
     * @param data the custom-model id of the data
     * @return the builder itself to continue
     */
    public ItemStackBuilder setCustomModelData(int data) {
        ItemMeta meta = ItemStacks.getSafeItemMeta(this.currentBuilding);
        meta.setCustomModelData(data);
        currentBuilding.setItemMeta(meta);

        // return this to continue
        return this;
    }

    /**
     * This Method sets the Lore of the Item
     * @param lore the Lore you want to set
     * @return the builder to continue
     */
    public ItemStackBuilder setLore(List<String> lore) {
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
     * This Method set the amount of the Item. The amount must be positive.
     * @param amount the amount
     * @return the builder to continue
     * @throws IllegalArgumentException if the amount is negative
     */
    public ItemStackBuilder setAmount(int amount) {
        Validate.isTrue(amount >= 0, "Amount must be positive");

        // set Amount
        currentBuilding.setAmount(amount);
        // returng this to continue
        return this;
    }

    /**
     * This method adds new lines to your lore
     * @param lines the lines you want to add
     * @return the builder to continue
     * @throws IllegalArgumentException if the lines are empty or null
     */
    public ItemStackBuilder addLore(String... lines) {
        // Validate
        Validate.notEmpty(lines, "new Lines cannot be empty or null");
        // create new lore
        List<String> newLore = new ArrayList<>(
                Optional.ofNullable(ItemStacks.getSafeItemMeta(currentBuilding).getLore())
                        .orElse(new ArrayList<>())
        );
        // add elements to lore
        newLore.addAll(Arrays.asList(lines));
        // execute setlore and return this to continue
        return setLore(newLore);
    }

    @NotNull
    public ItemStackBuilder withNBTData(@NotNull NBTTagCompound compound) {
        try {
            net.minecraft.world.item.ItemStack handle = (net.minecraft.world.item.ItemStack)
                    FieldUtils.readField(this.currentBuilding, "handle", true);

            NBTTagCompound merged = Optional.ofNullable(handle.s())
                    .orElse(new NBTTagCompound()).a(compound);

            handle.c(merged);

            return this;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to deep access ItemStack in Builder");
        }
    }

    @NotNull
    public ItemStackBuilder withNBTData(@NotNull NBTConfig config) {
        return this.withNBTData(config.save());
    }

    /**
     * This Method writes the NBT-Tag with an Int as value in a certain key
     * @param key the key of the tag
     * @param value the value you want to write in this key
     * @return the builder to continue
     */
    public <T> ItemStackBuilder withNBTData(@NotNull String key, @NotNull T value) {
        NBTConfig config = new NBTConfig(Optional.ofNullable(ItemStacks.getNBTTagCompound(this.currentBuilding))
                .orElse(new NBTTagCompound()));

        config.set(key, value);

        // Set Compound in itemStack
        return this.withNBTData(config);
    }
}
