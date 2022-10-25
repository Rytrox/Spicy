package de.rytrox.spicy.reflect;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class NBTItemStacks {
    private static final Class<?> compoundTag = Reflections.getNMSClass("NBTTagCompound");

    /**
     * Returns an NMS-Copy of the itemstack as object
     * @param item the item you want to copy
     * @return the nms itemstack as object type
     */
    @NotNull
    public static Object asNMSCopy(@NotNull ItemStack item) {
        try {
            return MethodUtils.invokeStaticMethod(Reflections.getCraftBukkitClass("inventory.CraftItemStack"), "asNMSCopy", item);
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

        throw new IllegalStateException("Cannot execute CraftBukkit Methods. Please open a new issue on https://github.com/Rytrox/Spicy/issues. This is a bug inside Spicy");
    }

    @NotNull
    public static Object getNBTTagCompound(@NotNull ItemStack item) throws ReflectiveOperationException {
        // return null if itemstack is null otherwise return nbt-tag compound
        Object nbtItem = asNMSCopy(item);

        if(MethodUtils.invokeMethod(nbtItem, "hasTag") != Boolean.TRUE) {
            MethodUtils.invokeMethod(nbtItem, "setTag", ConstructorUtils.invokeConstructor(compoundTag));
        }

        return MethodUtils.invokeMethod(nbtItem, "getTag");
    }

}
