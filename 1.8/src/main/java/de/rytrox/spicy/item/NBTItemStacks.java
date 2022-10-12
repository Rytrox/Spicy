package de.rytrox.spicy.item;

import de.rytrox.spicy.reflect.Reflections;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.logging.Level;

public class NBTItemStacks {

    /**
     * Returns an NMS-Copy of the itemstack as object
     * @param item the item you want to copy
     * @return the nms itemstack as object type
     */
    @Nullable
    public static net.minecraft.server.v1_8_R3.ItemStack asNMSCopy(ItemStack item) {
        try {
            return (net.minecraft.server.v1_8_R3.ItemStack) MethodUtils.invokeStaticMethod(
                    Reflections.getCraftBukkitClass("inventory.CraftItemStack"), "asNMSCopy", item);
        } catch (IllegalAccessException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Unable to create NMS-Copy of an itemstack: ", e);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Invalid argument format: ", e);
        } catch (InvocationTargetException e) {
            Bukkit.getLogger().log(Level.WARNING, "Invocated target: ", e);
        } catch (SecurityException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Security error while accessing with reflections: ", e);
        } catch (NoSuchMethodException e) {
            Bukkit.getLogger().log(Level.WARNING, "Unable to Find Method CraftItemStack#asNMSCopy", e);
        }

        return null;
    }

    @NotNull
    public static NBTTagCompound getNBTTagCompound(@NotNull ItemStack item) {
        // return null if itemstack is null otherwise return nbt-tag compound
        return Optional.ofNullable(asNMSCopy(item))
                .map((nbtItem) -> {
                    if(nbtItem.hasTag()) {
                        nbtItem.setTag(new NBTTagCompound());
                    }

                    return nbtItem.getTag();
                })
                .orElse(new NBTTagCompound());
    }

    /**
     * Returns a boolean value that is stored inside the NBT-Data under a certain key
     *
     * @param item the item where the value is stored
     * @param key the NBT-Key where the data is stored
     * @deprecated Use {@link de.rytrox.spicy.config.NBTConfig} instead for more functionality
     *
     * @return the stored boolean or false if the item does not have tags or the key does not exist
     */
    @Deprecated
    public static boolean getNBTBoolean(ItemStack item, String key) {
        return Optional.of(getNBTTagCompound(item))
                .map((compound) -> compound.getBoolean(key))
                .orElse(false);
    }

    /**
     * Returns a boolean value that is stored inside the NBT-Data under a certain key
     *
     * @param item the item where the value is stored
     * @param key the NBT-Key where the data is stored
     * @param def the default value that will be returned when no such key exists
     * @deprecated Use {@link de.rytrox.spicy.config.NBTConfig} instead for more functionality
     *
     * @return the stored value or the def
     */
    @Deprecated
    public static boolean getNBTBoolean(ItemStack item, String key, boolean def) {
        return Optional.of(getNBTTagCompound(item))
                .filter((compound) -> compound.hasKey(key))
                .map((compound) -> compound.getBoolean(key))
                .orElse(def);
    }

    /**
     * Returns a byte value that is stored inside the NBT-Data under a certain key
     *
     * @param item the item where the value is stored
     * @param key the key destination of the value
     * @deprecated Use {@link de.rytrox.spicy.config.NBTConfig} instead for more functionality
     *
     * @return the stored value or the byte 0 if no such key exists
     */
    @Deprecated
    public static byte getNBTByte(ItemStack item, String key) {
        return Optional.of(getNBTTagCompound(item))
                .map((compound) -> compound.getByte(key))
                .orElse((byte) 0);
    }

    /**
     * Returns a byte value that is stored inside the NBT-Date under a certain key
     *
     * @param item the item where the value is stored
     * @param key the key destination of the value
     * @param def the default value that will be returned if no such key exists
     * @deprecated Use {@link de.rytrox.spicy.config.NBTConfig} instead for more functionality
     *
     * @return the stored value or def
     */
    @Deprecated
    public static byte getNBTByte(ItemStack item, String key, byte def) {
        return Optional.of(getNBTTagCompound(item))
                .filter((compound) -> compound.hasKey(key))
                .map((compound) -> compound.getByte(key))
                .orElse(def);
    }

    /**
     * Returns a short value that is stored inside the NBT-Data under a certain key
     *
     * @param item the item where the value is stored
     * @param key the key destination of the value
     * @deprecated Use {@link de.rytrox.spicy.config.NBTConfig} instead for more functionality
     *
     * @return the stored value or the byte 0 if no such key exists
     */
    @Deprecated
    public static short getNBTShort(ItemStack item, String key) {
        return Optional.of(getNBTTagCompound(item))
                .map((compound) -> compound.getShort(key))
                .orElse((short) 0);
    }

    /**
     * Returns a short value that is stored inside the NBT-Data under a certain key
     *
     * @param item the item where the value is stored
     * @param key the key destination of the value
     * @param def the default value that will be returned if no such key exists
     * @deprecated Use {@link de.rytrox.spicy.config.NBTConfig} instead for more functionality
     *
     * @return the stored value or def
     */
    @Deprecated
    public static short getNBTShort(ItemStack item, String key, short def) {
        return Optional.of(getNBTTagCompound(item))
                .filter((compound) -> compound.hasKey(key))
                .map((compound) -> compound.getShort(key))
                .orElse(def);
    }

    /**
     * Returns an integer value that is stored inside the NBT-Data under a certain key
     *
     * @param item the item where the value is stored
     * @param key the key destination of the value
     * @deprecated Use {@link de.rytrox.spicy.config.NBTConfig} instead for more functionality
     *
     * @return the stored value or value 0 if no such key exists
     */
    @Deprecated
    public static int getNBTInt(ItemStack item, String key) {
        return Optional.of(getNBTTagCompound(item))
                .map((compound) -> compound.getInt(key))
                .orElse(0);
    }

    /**
     * Returns an integer value that is stored inside the NBT-Data under a certain key
     *
     * @param item the item where the value is stored
     * @param key the key destination of the value
     * @param def the default value that will be returned if no such key exists
     * @deprecated Use {@link de.rytrox.spicy.config.NBTConfig} instead for more functionality
     *
     * @return the stored value or def
     */
    @Deprecated
    public static int getNBTInt(ItemStack item, String key, int def) {
        return Optional.of(getNBTTagCompound(item))
                .filter((compound) -> compound.hasKey(key))
                .map((compound) -> compound.getInt(key))
                .orElse(def);
    }

    /**
     * Returns a float value that is stored inside the NBT-Data under a certain key
     *
     * @param item the item where the value is stored
     * @param key the key destination of the value
     * @deprecated Use {@link de.rytrox.spicy.config.NBTConfig} instead for more functionality
     *
     * @return the stored value or value 0 if no such key exists
     */
    @Deprecated
    public static float getNBTFloat(ItemStack item, String key) {
        return Optional.of(getNBTTagCompound(item))
                .map((compound) -> compound.getFloat(key))
                .orElse(0F);
    }

    /**
     * Returns a float value that is stored inside the NBT-Data under a certain key
     *
     * @param item the item where the value is stored
     * @param key the key destination of the value
     * @param def the default value that will be returned if no such key exists
     * @deprecated Use {@link de.rytrox.spicy.config.NBTConfig} instead for more functionality
     *
     * @return the stored value or def
     */
    @Deprecated
    public static float getNBTFloat(ItemStack item, String key, float def) {
        return Optional.of(getNBTTagCompound(item))
                .filter((compound) -> compound.hasKey(key))
                .map((compound) -> compound.getFloat(key))
                .orElse(def);
    }

    /**
     * Returns a double value that is stored inside the NBT-Data under a certain key
     *
     * @param item the item where the value is stored
     * @param key the key destination of the value
     * @deprecated Use {@link de.rytrox.spicy.config.NBTConfig} instead for more functionality
     *
     * @return the stored value or value 0 if no such key exists
     */
    @Deprecated
    public static double getNBTDouble(ItemStack item, String key) {
        return Optional.of(getNBTTagCompound(item))
                .map((compound) -> compound.getDouble(key))
                .orElse(0D);
    }

    /**
     * Returns a double value that is stored inside the NBT-Data under a certain key
     *
     * @param item the item where the value is stored
     * @param key the key destination of the value
     * @param def the default value that will be returned if no such key exists
     * @deprecated Use {@link de.rytrox.spicy.config.NBTConfig} instead for more functionality
     *
     * @return the stored value or def
     */
    @Deprecated
    public static double getNBTDouble(ItemStack item, String key, double def) {
        return Optional.of(getNBTTagCompound(item))
                .filter((compound) -> compound.hasKey(key))
                .map((compound) -> compound.getDouble(key))
                .orElse(def);
    }

    /**
     * Returns a String value that is stored inside the NBT-Data under a certain key
     *
     * @param item the item where the value is stored
     * @param key the key destination of the value
     * @deprecated Use {@link de.rytrox.spicy.config.NBTConfig} instead for more functionality
     *
     * @return the stored value or an empty string if no such key exists
     */
    @Deprecated
    public static String getNBTString(ItemStack item, String key) {
        return Optional.of(getNBTTagCompound(item))
                .map((compound) -> compound.getString(key))
                .orElse("");
    }

    /**
     * Returns a double value that is stored inside the NBT-Data under a certain key
     *
     * @param item the item where the value is stored
     * @param key the key destination of the value
     * @param def the default value that will be returned if no such key exists
     * @deprecated Use {@link de.rytrox.spicy.config.NBTConfig} instead for more functionality
     *
     * @return the stored value or def
     */
    @Deprecated
    public static String getNBTDouble(ItemStack item, String key, String def) {
        return Optional.of(getNBTTagCompound(item))
                .filter((compound) -> compound.hasKey(key))
                .map((compound) -> compound.getString(key))
                .orElse(def);
    }

    /**
     * Returns a byte array that is stored inside the NBT-Data under a certain key
     *
     * @param item the item where the array is stored
     * @param key the key destination of the array
     * @deprecated Use {@link de.rytrox.spicy.config.NBTConfig} instead for more functionality
     *
     * @return the stored array or an empty byte array with length 0 if no such key exists
     */
    @Deprecated
    public static byte[] getNBTByteArray(ItemStack item, String key) {
        return Optional.of(getNBTTagCompound(item))
                .map((compound) -> compound.getByteArray(key))
                .orElse(new byte[0]);
    }

    /**
     * Returns a byte array that is stored inside the NBT-Data under a certain key
     *
     * @param item the item where the array is stored
     * @param key the key destination of the array
     * @param def the default array that will be returned if no such key exists
     * @deprecated Use {@link de.rytrox.spicy.config.NBTConfig} instead for more functionality
     *
     * @return the stored array or def
     */
    @Deprecated
    public static byte[] getNBTByteArray(ItemStack item, String key, byte[] def) {
        return Optional.of(getNBTTagCompound(item))
                .filter((compound) -> compound.hasKey(key))
                .map((compound) -> compound.getByteArray(key))
                .orElse(def);
    }


    /**
     * Returns an integer array that is stored inside the NBT-Data under a certain key
     *
     * @param item the item where the array is stored
     * @param key the key destination of the array
     * @deprecated Use {@link de.rytrox.spicy.config.NBTConfig} instead for more functionality
     *
     * @return the stored array or an empty integer array with length 0 if no such key exists
     */
    @Deprecated
    public static int[] getNBTIntArray(ItemStack item, String key) {
        return Optional.of(getNBTTagCompound(item))
                .map((compound) -> compound.getIntArray(key))
                .orElse(new int[0]);
    }

    /**
     * Returns an integer array that is stored inside the NBT-Data under a certain key
     *
     * @param item the item where the array is stored
     * @param key the key destination of the array
     * @param def the default array that will be returned if no such key exists
     * @deprecated Use {@link de.rytrox.spicy.config.NBTConfig} instead for more functionality
     *
     * @return the stored array or def
     */
    @Deprecated
    public static int[] getNBTIntArray(ItemStack item, String key, int[] def) {
        return Optional.of(getNBTTagCompound(item))
                .filter((compound) -> compound.hasKey(key))
                .map((compound) -> compound.getIntArray(key))
                .orElse(def);
    }

}
