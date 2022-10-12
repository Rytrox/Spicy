package de.rytrox.spicy.item;

import de.rytrox.spicy.config.NBTConfig;

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
    public NBTItemStackBuilder withNBTData(@NotNull NBTConfig config) {
        return this.withNBTData(config.save());
    }

    /**
     * This Method writes the NBT-Tag with an Int as value in a certain key
     * @param key the key of the tag
     * @param value the value you want to write in this key
     * @return the builder to continue
     */
    @NotNull
    public <T> NBTItemStackBuilder withNBTData(@NotNull String key, @NotNull T value) {
        NBTConfig config = new NBTConfig(NBTItemStacks.getNBTTagCompound(this.currentBuilding));

        config.set(key, value);

        // Set Compound in itemStack
        return this.withNBTData(config);
    }
}
