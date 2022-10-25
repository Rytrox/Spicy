package de.rytrox.spicy.reflect;

import com.mojang.authlib.GameProfile;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public static GameProfile getGameProfile(@NotNull Player player) throws ReflectiveOperationException {
        return (GameProfile) MethodUtils.invokeMethod(getEntityPlayer(player), "getProfile");
    }

    /**
     * This method returns an EntityPlayer-Object of a player
     * @param player the player
     * @return the EntityPlayer as Object
     * @throws ReflectiveOperationException if there was an error
     */
    @NotNull
    public static Object getEntityPlayer(@NotNull Player player) throws ReflectiveOperationException {
        return MethodUtils.invokeExactMethod(player, "getHandle");
    }

    /**
     * This method returns the PlayerConnection as an Object
     * @param player the owner of the player connection
     * @return the PlayerConnection as Object
     * @throws ReflectiveOperationException if there was an error
     */
    @NotNull
    public static Object getPlayerConnection(@NotNull Player player) throws ReflectiveOperationException {
        return FieldUtils.readField(getEntityPlayer(player), "playerConnection");
    }

    /**
     * This method sends a Packet to a Player
     * @param player the Player
     * @param packet the packet
     * @throws ReflectiveOperationException if the object is not a packet
     */
    public static void sendPacket(@NotNull Player player, @NotNull Object packet) throws ReflectiveOperationException {
        Validate.isTrue(packetClass.isAssignableFrom(packet.getClass()));

        MethodUtils.invokeMethod(getPlayerConnection(player), "sendPacket", packet);
    }
}
