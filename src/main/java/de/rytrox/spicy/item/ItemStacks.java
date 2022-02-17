package de.rytrox.spicy.item;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import de.rytrox.spicy.reflect.Reflections;

import net.minecraft.nbt.NBTTagCompound;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.bind.JsonTreeReader;


/**
 * Utilities for ItemStacks
 *
 * @author Timeout
 *
 */
public final class ItemStacks {

    private static final Gson GSON = new Gson();

    private ItemStacks() {
        // No need for Util-Class to create an Object
    }

    /**
     * Encode item stack.
     *
     * @param item the item
     * @return the string
     */
    @NotNull
    public static String encodeBase64(@NotNull ItemStack item) throws IOException {
        try(ByteArrayOutputStream str = new ByteArrayOutputStream();
            BukkitObjectOutputStream data = new BukkitObjectOutputStream(str)) {
            data.writeObject(item);

            return Base64.getEncoder().encodeToString(str.toByteArray());
        }
    }

    /**
     * Decode item stack.
     *
     * @param base64 the base 64
     * @return the item stack
     */
    @NotNull
    public static ItemStack decodeBase64(@NotNull String base64) throws IOException, ClassNotFoundException {
        try(ByteArrayInputStream str = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
            BukkitObjectInputStream data = new BukkitObjectInputStream(str)) {

            return (ItemStack) data.readObject();
        }
    }

    /**
     * Encodes an ItemStack into a JSON-Object
     * @param item the itemstack you want to encode
     * @return the json object of the itemstack. Cannot be null
     */
    @NotNull
    public static JsonObject encodeJson(@NotNull ItemStack item) {
        return JsonParser.parseString(GSON.toJson(item.serialize())).getAsJsonObject();
    }

    /**
     * Decodes an JsonObject of an ItemStack into the ItemStack
     * @param data the json data of the ItemStack
     * @return the ItemStack
     */
    @NotNull
    public static ItemStack decodeJson(JsonObject data) {
        return ItemStack.deserialize(GSON.fromJson(new JsonTreeReader(data), Map.class));
    }

    @NotNull
    public static String getCustomizedName(@NotNull ItemStack itemStack) {
        // return displayname if item has one
        if(itemStack.getItemMeta() != null) {
            if(itemStack.getItemMeta().hasDisplayName()) {
                return itemStack.getItemMeta().getDisplayName();
            } else if(itemStack.getItemMeta().hasLocalizedName()) {
                return itemStack.getItemMeta().getLocalizedName();
            }
        }

        // only continue if the item could be found
        // otherwise return ItemStack type name
        return Optional.ofNullable(asNMSCopy(itemStack))
                .map((item) -> item.v().a())
                .orElse(WordUtils.capitalize(itemStack.getType().toString()));
    }

    /**
     * Returns an NMS-Copy of the itemstack as object
     * @param item the item you want to copy
     * @return the nms itemstack as object type
     */
    @Nullable
    public static net.minecraft.world.item.ItemStack asNMSCopy(ItemStack item) {
        try {
            return (net.minecraft.world.item.ItemStack) MethodUtils.invokeStaticMethod(
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

    @Nullable
    public static ItemStack asBukkitCopy(@NotNull net.minecraft.world.item.ItemStack nmsItem) {
        try {
            Class<?> craftitemstackClass = Reflections.getCraftBukkitClass("inventory.CraftItemStack");

            return (ItemStack) craftitemstackClass
                    .getMethod("asBukkitCopy", net.minecraft.world.item.ItemStack.class)
                    .invoke(craftitemstackClass, nmsItem);
        } catch (IllegalAccessException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Unable to create Bukkit-Copy of an itemstack: ", e);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Invalid argument format: ", e);
        } catch (InvocationTargetException e) {
            Bukkit.getLogger().log(Level.WARNING, "Invocated target: ", e);
        } catch (NoSuchMethodException e) {
            Bukkit.getLogger().log(Level.WARNING, "Method does not exist: ", e);
        } catch (SecurityException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Security error while accessing with reflections: ", e);
        }

        return null;
    }

    public static @NotNull ItemMeta getSafeItemMeta(@NotNull ItemStack item) {
        // an itemmeta is only null when the item is air or null
        if(item.getItemMeta() != null) {
            return item.getItemMeta();
        }

        // Throws a new IllegalArgumentException if the ItemMeta is null
        throw new IllegalStateException();
    }

    @Nullable
    public static NBTTagCompound getNBTTagCompound(@NotNull ItemStack item) {
        // return null if itemstack is null otherwise return nbt-tag compound
        return Optional.ofNullable(asNMSCopy(item))
                .map(net.minecraft.world.item.ItemStack::s)
                .orElse(null);
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
        return Optional.ofNullable(getNBTTagCompound(item))
                .map((compound) -> compound.q(key))
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
        return Optional.ofNullable(getNBTTagCompound(item))
                .filter((compound) -> compound.e(key))
                .map((compound) -> compound.q(key))
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
        return Optional.ofNullable(getNBTTagCompound(item))
                .map((compound) -> compound.f(key))
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
        return Optional.ofNullable(getNBTTagCompound(item))
                .filter((compound) -> compound.e(key))
                .map((compound) -> compound.f(key))
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
        return Optional.ofNullable(getNBTTagCompound(item))
                .map((compound) -> compound.g(key))
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
        return Optional.ofNullable(getNBTTagCompound(item))
                .filter((compound) -> compound.e(key))
                .map((compound) -> compound.g(key))
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
        return Optional.ofNullable(getNBTTagCompound(item))
                .map((compound) -> compound.h(key))
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
        return Optional.ofNullable(getNBTTagCompound(item))
                .filter((compound) -> compound.e(key))
                .map((compound) -> compound.h(key))
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
        return Optional.ofNullable(getNBTTagCompound(item))
                .map((compound) -> compound.j(key))
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
        return Optional.ofNullable(getNBTTagCompound(item))
                .filter((compound) -> compound.e(key))
                .map((compound) -> compound.j(key))
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
        return Optional.ofNullable(getNBTTagCompound(item))
                .map((compound) -> compound.k(key))
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
        return Optional.ofNullable(getNBTTagCompound(item))
                .filter((compound) -> compound.e(key))
                .map((compound) -> compound.k(key))
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
        return Optional.ofNullable(getNBTTagCompound(item))
                .map((compound) -> compound.l(key))
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
        return Optional.ofNullable(getNBTTagCompound(item))
                .filter((compound) -> compound.e(key))
                .map((compound) -> compound.l(key))
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
        return Optional.ofNullable(getNBTTagCompound(item))
                .map((compound) -> compound.m(key))
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
        return Optional.ofNullable(getNBTTagCompound(item))
                .filter((compound) -> compound.e(key))
                .map((compound) -> compound.m(key))
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
        return Optional.ofNullable(getNBTTagCompound(item))
                .map((compound) -> compound.n(key))
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
        return Optional.ofNullable(getNBTTagCompound(item))
                .filter((compound) -> compound.e(key))
                .map((compound) -> compound.n(key))
                .orElse(def);
    }

    /**
     * Returns a long array that is stored inside the NBT-Data under a certain key
     *
     * @param item the item where the array is stored
     * @param key the key destination of the array
     * @deprecated Use {@link de.rytrox.spicy.config.NBTConfig} instead for more functionality
     *
     * @return the stored array or an empty integer array with length 0 if no such key exists
     */
    @Deprecated
    public static long[] getNBTLongArray(ItemStack item, String key) {
        return Optional.ofNullable(getNBTTagCompound(item))
                .map((compound) -> compound.o(key))
                .orElse(new long[0]);
    }

    /**
     * Returns a long array that is stored inside the NBT-Data under a certain key
     *
     * @param item the item where the array is stored
     * @param key the key destination of the array
     * @param def the default array that will be returned if no such key exists
     * @deprecated Use {@link de.rytrox.spicy.config.NBTConfig} instead for more functionality
     *
     * @return the stored array or def
     */
    @Deprecated
    public static long[] getNBTLongArray(ItemStack item, String key, long[] def) {
        return Optional.ofNullable(getNBTTagCompound(item))
                .filter((compound) -> compound.e(key))
                .map((compound) -> compound.o(key))
                .orElse(def);
    }
}
