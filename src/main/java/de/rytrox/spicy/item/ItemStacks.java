package de.rytrox.spicy.item;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Level;

import de.rytrox.spicy.reflect.Reflections;

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
        return WordUtils.capitalize(itemStack.getType().toString());
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

    @NotNull
    public static ItemMeta getSafeItemMeta(@NotNull ItemStack item) {
        // an itemmeta is only null when the item is air or null
        if(item.getItemMeta() != null) {
            return item.getItemMeta();
        }

        // Throws a new IllegalArgumentException if the ItemMeta is null
        throw new IllegalStateException();
    }

}
