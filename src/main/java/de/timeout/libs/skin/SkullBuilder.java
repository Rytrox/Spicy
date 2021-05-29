package de.timeout.libs.skin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import de.timeout.libs.item.ItemStackBuilder;
import org.apache.commons.lang.reflect.MethodUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.logging.Level;

public class SkullBuilder extends ItemStackBuilder {

    private OfflinePlayer player;
    private GameProfile profile;

    public SkullBuilder() {
        super(Material.PLAYER_HEAD);
    }

    /**
     * Sets the player of the Skull you want to build
     *
     * @param player the player of the Item
     * @return the builder to continue
     */
    @NotNull
    public SkullBuilder player(@Nullable OfflinePlayer player) {
        this.player = player;

        return this;
    }

    /**
     * Sets the profile of the skull you want to build
     *
     * @param profile the profile you want to set
     * @return
     */
    @NotNull
    public SkullBuilder profile(@Nullable GameProfile profile) {
        this.profile = profile;

        return this;
    }

    @NotNull
    public SkullBuilder file(@NotNull File skinFile) {
        // TODO: Mineskin Logic HERE!

        return this;
    }

    @NotNull
    public SkullBuilder mineskin(int id) {
        // TODO: Mineskin Logic HERE!

        return this;
    }

    /**
     * Sets the skin to a url inside the skull
     *
     * @param url the url to the string data
     * @return this to continue
     */
    public SkullBuilder url(@NotNull String url) {
        this.profile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap properties = this.profile.getProperties();

        // encode data
        byte[] data = new Base64().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());

        properties.put("textures", new Property("textures", new String(data)));

        return this;
    }

    /**
     * This method converts the builder into an ItemStack and returns it
     *
     * @return the itemstack
     */
    @Override
    public ItemStack toItemStack() {
        SkullMeta meta = (SkullMeta) currentBuilding.getItemMeta();

        if(meta != null) {
            if(profile != null) {
                try {
                    MethodUtils.invokeExactMethod(meta, "setProfile", profile);
                } catch (NoSuchMethodException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Unable to find Method CraftMetaSkull#setProfile(GameProfile)", e);
                } catch (IllegalAccessException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Unable to access Method CraftMetaSkull#setProfile(GameProfile)", e);
                } catch (InvocationTargetException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Unable to call Method CraftMetaSkull#setProfile(GameProfile) in CraftSkullMeta", e);
                }
            } else if(player != null)
                meta.setOwningPlayer(player);
        }

        currentBuilding.setItemMeta(meta);

        return this.currentBuilding;
    }
}
