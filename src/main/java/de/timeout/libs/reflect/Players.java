package de.timeout.libs.reflect;

import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class Players {

    private static final Class<?> packetClass = Reflections.getNMSClass("Packet");

    private Players() {
        /* UTIL-CLASSES DON'T NEED A CONSTRUCTOR */
    }
    /**
     * This Method returns the player's GameProfile
     * @param player the owner of the GameProfile
     * @return the Gameprofile
     */
    @Nullable
    public static GameProfile getGameProfile(@NotNull Player player) {
        try {
            Class<?> craftplayerClass = Reflections.getCraftBukkitClass("entity.CraftPlayer");
            return craftplayerClass != null ? (GameProfile) craftplayerClass.getMethod("getProfile").invoke(player) : null;
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            Bukkit.getLogger().log(Level.INFO, "Could not get GameProfile from Player " + player.getName(), e);
        }
        return new GameProfile(player.getUniqueId(), player.getName());
    }

    /**
     * This method returns an EntityPlayer-Object of a player
     * @param player the player
     * @return the EntityPlayer as Object
     * @throws ReflectiveOperationException if there was an error
     */
    @NotNull
    public static Object getEntityPlayer(@NotNull Player player) throws ReflectiveOperationException {
        Method getHandle = player.getClass().getMethod("getHandle");
        return getHandle.invoke(player);
    }

    /**
     * This method returns the PlayerConnection as an Object
     * @param player the owner of the player connection
     * @return the PlayerConnection as Object
     * @throws ReflectiveOperationException if there was an error
     */
    @NotNull
    public static Object getPlayerConnection(@NotNull Player player) throws ReflectiveOperationException {
        Object nmsp = getEntityPlayer(player);
        Field con = nmsp.getClass().getField("playerConnection");
        return con.get(nmsp);
    }

    /**
     * This method sends a Packet to a Player
     * @param player the Player
     * @param packet the packet
     * @throws ReflectiveOperationException if the object is not a packet
     */
    public static void sendPacket(@NotNull Player player, @NotNull Object packet) throws ReflectiveOperationException {
        Object playerConnection = getPlayerConnection(player);
        playerConnection.getClass().getMethod("sendPacket", packetClass).invoke(playerConnection, packet);
    }
}
