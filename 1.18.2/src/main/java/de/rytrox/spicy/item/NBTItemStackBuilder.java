package de.rytrox.spicy.item;

import de.rytrox.spicy.nbt.NBTStorage;

import de.rytrox.spicy.reflect.ItemStacks;

import net.minecraft.nbt.CompoundTag;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Extends the ItemStackBuilder to implements and use NBT-Data for ItemStacks
 *
 * @author Timeout
 */
public class NBTItemStackBuilder extends ItemStackBuilder {

    @Override
    public @NotNull NBTItemStackBuilder displayName(String displayName) {
        return (NBTItemStackBuilder) super.displayName(displayName);
    }

    @Override
    public @NotNull NBTItemStackBuilder enchantment(Enchantment enchantment, int level) {
        return (NBTItemStackBuilder) super.enchantment(enchantment, level);
    }

    @Override
    public @NotNull NBTItemStackBuilder modelData(@Nullable Integer model) {
        return (NBTItemStackBuilder) super.modelData(model);
    }

    @Override
    public @NotNull NBTItemStackBuilder removeEnchantment(Enchantment enchantment) {
        return (NBTItemStackBuilder) super.removeEnchantment(enchantment);
    }

    @Override
    public @NotNull NBTItemStackBuilder lore(List<String> lore) {
        return (NBTItemStackBuilder) super.lore(lore);
    }

    @Override
    public @NotNull NBTItemStackBuilder hideEnchantments(boolean result) {
        return (NBTItemStackBuilder) super.hideEnchantments(result);
    }

    @Override
    public @NotNull NBTItemStackBuilder flags(ItemFlag... flags) {
        return (NBTItemStackBuilder) super.flags(flags);
    }

    @Override
    public @NotNull NBTItemStackBuilder amount(int amount) {
        return (NBTItemStackBuilder) super.amount(amount);
    }

    @Override
    public @NotNull NBTItemStackBuilder lore(String... lines) {
        return (NBTItemStackBuilder) super.lore(lines);
    }

    @NotNull
    public NBTItemStackBuilder withNBTData(@NotNull CompoundTag compound) {
        try {
            net.minecraft.world.item.ItemStack handle = (net.minecraft.world.item.ItemStack)
                    FieldUtils.readField(this.currentBuilding, "handle", true);

            CompoundTag merged = handle.getOrCreateTag();
            merged.merge(compound);

            return this;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to deep access ItemStack in Builder");
        }
    }

    @NotNull
    public NBTItemStackBuilder withNBTData(@NotNull NBTStorage config) {
        return this.withNBTData(config.save());
    }

    /**
     * This Method writes the NBT-Tag with a byte as value in a certain key
     * @param key the key of the tag
     * @param value the byte you want to write in this key
     * @return the builder to continue
     */
    public NBTItemStackBuilder withNBTData(@NotNull String key, byte value) {
        NBTStorage storage = new NBTStorage(ItemStacks.getNBTTagCompound(this.currentBuilding));
        storage.setByte(key, value);

        return this.withNBTData(storage);
    }

    /**
     * This Method writes the NBT-Tag with a short as value in a certain key
     * @param key the key of the tag
     * @param value the short you want to write in this key
     * @return the builder to continue
     */
    public NBTItemStackBuilder withNBTData(@NotNull String key, short value) {
        NBTStorage storage = new NBTStorage(ItemStacks.getNBTTagCompound(this.currentBuilding));
        storage.setShort(key, value);

        return this.withNBTData(storage);
    }

    /**
     * This Method writes the NBT-Tag with an integer as value in a certain key
     * @param key the key of the tag
     * @param value the integer you want to write in this key
     * @return the builder to continue
     */
    public NBTItemStackBuilder withNBTData(@NotNull String key, int value) {
        NBTStorage storage = new NBTStorage(ItemStacks.getNBTTagCompound(this.currentBuilding));
        storage.setInt(key, value);

        return this.withNBTData(storage);
    }

    /**
     * This Method writes the NBT-Tag with a long as value in a certain key
     * @param key the key of the tag
     * @param value the long you want to write in this key
     * @return the builder to continue
     */
    public NBTItemStackBuilder withNBTData(@NotNull String key, long value) {
        NBTStorage storage = new NBTStorage(ItemStacks.getNBTTagCompound(this.currentBuilding));
        storage.setLong(key, value);

        return this.withNBTData(storage);
    }

    /**
     * This Method writes the NBT-Tag with a float as value in a certain key
     * @param key the key of the tag
     * @param value the float you want to write in this key
     * @return the builder to continue
     */
    public NBTItemStackBuilder withNBTData(@NotNull String key, float value) {
        NBTStorage storage = new NBTStorage(ItemStacks.getNBTTagCompound(this.currentBuilding));
        storage.setFloat(key, value);

        return this.withNBTData(storage);
    }

    /**
     * This Method writes the NBT-Tag with a double as value in a certain key
     * @param key the key of the tag
     * @param value the double you want to write in this key
     * @return the builder to continue
     */
    public NBTItemStackBuilder withNBTData(@NotNull String key, double value) {
        NBTStorage storage = new NBTStorage(ItemStacks.getNBTTagCompound(this.currentBuilding));
        storage.setDouble(key, value);

        return this.withNBTData(storage);
    }

    /**
     * This Method writes the NBT-Tag with a string as value in a certain key
     * @param key the key of the tag
     * @param value the string you want to write in this key
     * @return the builder to continue
     */
    public NBTItemStackBuilder withNBTData(@NotNull String key, @Nullable String value) {
        NBTStorage storage = new NBTStorage(ItemStacks.getNBTTagCompound(this.currentBuilding));
        storage.setString(key, value);

        return this.withNBTData(storage);
    }
}
