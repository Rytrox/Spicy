package de.rytrox.spicy.item;

import de.rytrox.spicy.config.NBTConfig;
import net.minecraft.nbt.CompoundTag;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Extends the ItemStackBuilder to implements and use NBT-Data for ItemStacks
 *
 * @author Timeout
 */
public class NBTItemStackBuilder extends ItemStackBuilder {

    @NotNull
    public ItemStackBuilder withNBTData(@NotNull CompoundTag compound) {
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
    public ItemStackBuilder withNBTData(@NotNull NBTConfig config) {
        return this.withNBTData(config.save());
    }

    /**
     * This Method writes the NBT-Tag with an Int as value in a certain key
     * @param key the key of the tag
     * @param value the value you want to write in this key
     * @return the builder to continue
     */
    @NotNull
    public <T> ItemStackBuilder withNBTData(@NotNull String key, @NotNull T value) {
        NBTConfig config = new NBTConfig(Optional.ofNullable(NBTItemStacks.getNBTTagCompound(this.currentBuilding))
                .orElse(new CompoundTag()));

        config.set(key, value);

        // Set Compound in itemStack
        return this.withNBTData(config);
    }
}
