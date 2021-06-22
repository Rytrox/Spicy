package de.timeout.libs.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import de.timeout.libs.reflect.Reflections;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class ItemStackBuilder {

    private static final Class<?> itemstackClass = Reflections.getNMSClass("ItemStack");
    private static final Class<?> nbttagcompoundClass = Reflections.getNMSClass("NBTTagCompound");

    private static final String HAS_TAG = "hasTag";
    private static final String GET_TAG = "getTag";
    private static final String SET_TAG = "setTag";

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
        ItemMeta meta = getSafeItemMeta(currentBuilding);
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
     * This Method removes an Enchantment from the Item
     * @param enchantment the Enchant you want to remove
     * @return the builder to continue
     */
    public ItemStackBuilder removeEnchantment(Enchantment enchantment) {
        // remove enchantment
        ItemMeta meta = getSafeItemMeta(currentBuilding);
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
    public ItemStackBuilder setLore(List<String> lore) {
        // Set Lore for currentBuilding
        ItemMeta meta = getSafeItemMeta(currentBuilding);
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
        ItemMeta meta = getSafeItemMeta(currentBuilding);
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
                Optional.ofNullable(getSafeItemMeta(currentBuilding).getLore())
                        .orElse(new ArrayList<>())
        );
        // add elements to lore
        newLore.addAll(Arrays.asList(lines));
        // execute setlore and return this to continue
        return setLore(newLore);
    }

    /**
     * This Method writes the NBT-Tag with an Int as value in a certain key
     * @param key the key of the tag
     * @param value the value you want to write in this key
     * @return the builder to continue
     */
    public ItemStackBuilder writeNBTInt(String key, int value) {
        try {
            writeNBT(key, value, "setInt", int.class);
        } catch (ReflectiveOperationException e) {
            Bukkit.getLogger().log(Level.SEVERE, e, () -> NBT_ERROR + key);
        }
        // return this to continue
        return this;
    }

    /**
     * This Method writes the NBT-Tag with an Boolean as value in a certain key
     * @param key the key of the tag
     * @param value the value you want to write in this key
     * @return the builder to continue
     */
    public ItemStackBuilder writeNBTBoolean(@NotNull String key, boolean value) {
        // Validate

        try {
            writeNBT(key, value, "setBoolean", boolean.class);
        } catch (ReflectiveOperationException e) {
            Bukkit.getLogger().log(Level.SEVERE, e, () -> NBT_ERROR + key);
        }
        // return this to continue
        return this;
    }

    /**
     * This Method writes the NBT-Tag with an String as value in a certain key
     * @param key the key of the tag
     * @param value the value you want to write in this key
     * @return the builder to continue
     */
    public ItemStackBuilder writeNBTString(@NotNull String key, String value) {
        // Validate
        Validate.isTrue(!key.isEmpty(), "Unable to write NBT without a key");

        try {
            writeNBT(key, value, "setString", String.class);
        } catch (ReflectiveOperationException e) {
            Bukkit.getLogger().log(Level.SEVERE, e, () -> NBT_ERROR + key);
        }
        // return this to continue
        return this;
    }

    /**
     * Internal method for accessing and modifying NBT-TagCompound.
     *
     * @param <T> the datatype which will be accessed
     * @param key the name of the key where this data will be stored
     * @param value the data itself
     * @param methodName the name of the internal NMS-Method to insert the data with the right type at the right place
     * @param clazz the class instance of the type T
     * @throws ReflectiveOperationException if working with reflections failed
     */
    protected <T> void writeNBT(String key, T value, String methodName, Class<T> clazz) throws ReflectiveOperationException {
        // create nms-copy
        Object nms = ItemStacks.asNMSCopy(currentBuilding);
        if(nms != null) {
            // get tagcompound
            Object compound = (boolean) itemstackClass.getMethod(HAS_TAG).invoke(nms) ?
                    itemstackClass.getMethod(GET_TAG).invoke(nms) : nbttagcompoundClass.getConstructor().newInstance();

            if(compound != null) {
                // write data in compound
                nbttagcompoundClass.getMethod(methodName, String.class, clazz).invoke(compound, key, value);
                // set tagcompound in item
                itemstackClass.getMethod(SET_TAG, nbttagcompoundClass).invoke(nms, compound);

                // save new itemstack
                currentBuilding = ItemStacks.asBukkitCopy(nms);
            }
        }
    }

    protected static @NotNull ItemMeta getSafeItemMeta(ItemStack item) {
        // an itemmeta is only null when the item is air or null
        if(item != null && item.getItemMeta() != null) {
            return item.getItemMeta();
        }

        // Throws a new IllegalArgumentException if the ItemMeta is null
        throw new IllegalStateException();
    }
}
