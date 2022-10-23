package de.rytrox.spicy.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface NBTStorage<K> {

    /**
     * Removes a key from the storage
     * @param key the key you want to remove
     */
    void remove(@NotNull K key);

    /**
     * Returns a list. If the list doesn't exist it will return the default value. <br>
     * If the list was found it will filter all elements that are not instance of the type
     *
     * @param key the key of the path
     * @param type the type of the class
     * @param def the default value that is returned when the key does not exist
     * @return the value stored on that key or the default value
     */
    @Nullable
    <T> List<T> getList(@NotNull K key, @NotNull Class<T> type, @Nullable List<T> def);

    /**
     * Returns a list or null if the key doesn't exist. <br>
     * If the list was found it will filter all elements that are not instance of the type
     *
     * @param key the key of the path
     * @param type the type of the class
     * @return the value stored on that key or null
     */
    @Nullable
    default <T> List<T> getList(@NotNull K key, @NotNull Class<T> type) {
        return getList(key, type, null);
    }

    /**
     * Returns a list or null if the key doesn't exist
     *
     * @param key the key of the path
     * @return the value stored on that key or null
     */
    default List<Object> getList(@NotNull K key) {
        return getList(key, Object.class);
    }

    /**
     * Sets a list inside the storage. Override values that are stored before
     *
     * @param key the key you want to use
     * @param value the value itself
     */
    void setList(@NotNull K key, @Nullable List<?> value);

    /**
     * Returns a byte. If the byte doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not a byte
     * @return the value stored on the key or the default value
     */
    @Nullable
    Byte getByte(@NotNull K key, @Nullable Byte def);

    /**
     * Returns a byte. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    @Nullable
    default Byte getByte(@NotNull K key) {
        return getByte(key, null);
    }

    /**
     * Sets a byte at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    void setByte(@NotNull K key, @Nullable Byte value);

    /**
     * Returns a short. If the byte doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not a short
     * @return the value stored on the key or the default value
     */
    @Nullable
    Short getShort(@NotNull K key, @Nullable Short def);

    /**
     * Returns a short. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    @Nullable
    default Short getShort(@NotNull K key) {
        return getShort(key, null);
    }

    /**
     * Sets a short at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    void setShort(@NotNull K key, @Nullable Short value);

    /**
     * Returns an integer. If the integer doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not an integer
     * @return the value stored on the key or the default value
     */
    @Nullable
    Integer getInt(@NotNull K key, @Nullable Integer def);

    /**
     * Returns an integer. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    @Nullable
    default Integer getInt(@NotNull K key) {
        return getInt(key, null);
    }

    /**
     * Sets an integer at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    void setInt(@NotNull K key, @Nullable Integer value);

    /**
     * Returns a long. If the long doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not a long
     * @return the value stored on the key or the default value
     */
    @Nullable
    Long getLong(@NotNull K key, @Nullable Long def);

    /**
     * Returns a long. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    @Nullable
    default Long getLong(@NotNull K key) {
        return getLong(key, null);
    }

    /**
     * Sets a long at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    void setLong(@NotNull K key, @Nullable Long value);

    /**
     * Returns a float. If the float doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not a float
     * @return the value stored on the key or the default value
     */
    @Nullable
    Float getFloat(@NotNull K key, @Nullable Float def);

    /**
     * Returns a float. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    @Nullable
    default Float getFloat(@NotNull K key) {
        return getFloat(key, null);
    }

    /**
     * Sets a float at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    void setFloat(@NotNull K key, @Nullable Float value);

    /**
     * Returns a double. If the double doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not a double
     * @return the value stored on the key or the default value
     */
    @Nullable
    Double getDouble(@NotNull K key, @Nullable Double def);

    /**
     * Returns a double. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    @Nullable
    default Double getDouble(@NotNull K key) {
        return getDouble(key, null);
    }

    /**
     * Sets a double at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    void setDouble(@NotNull K key, @Nullable Double value);

    /**
     * Returns a byte array. If the byte array doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not a byte array
     * @return the value stored on the key or the default value
     */
    byte @Nullable [] getByteArray(@NotNull K key, byte @Nullable [] def);

    /**
     * Returns a byte array. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    default byte @Nullable [] getByteArray(@NotNull K key) {
        return getByteArray(key, null);
    }

    /**
     * Sets a byte array at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    void setByteArray(@NotNull K key, byte @Nullable [] value);

    /**
     * Returns a string. If the string doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not a string
     * @return the value stored on the key or the default value
     */
    @Nullable
    String getString(@NotNull K key, @Nullable String def);

    /**
     * Returns a string. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    @Nullable
    default String getString(@NotNull K key) {
        return getString(key, null);
    }

    /**
     * Sets a string at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    void setString(@NotNull K key, @Nullable String value);

    /**
     * Returns a nbt compound. If the compound doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not a nbt compound
     * @return the value stored on the key or the default value
     */
    @Nullable
    NBTCompoundStorage getCompound(@NotNull K key, @Nullable NBTCompoundStorage def);

    /**
     * Returns a nbt compound. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    @Nullable
    default NBTCompoundStorage getCompound(@NotNull K key) {
        return getCompound(key, null);
    }

    /**
     * Sets a compound at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    void setCompound(@NotNull K key, @Nullable NBTCompoundStorage value);

    /**
     * Returns an array of integers. If the integer array doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not a integer array
     * @return the value stored on the key or the default value
     */
    int @Nullable [] getIntArray(@NotNull K key, int @Nullable [] def);

    /**
     * Returns an array of integers. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    default int @Nullable [] getIntArray(@NotNull K key) {
        return getIntArray(key, null);
    }

    /**
     * Sets an array of integers at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    void setIntArray(@NotNull K key, int @Nullable [] value);

    /**
     * Returns a long array. If the byte array doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not a byte array
     * @return the value stored on the key or the default value
     */
    long @Nullable [] getLongArray(@NotNull K key, long @Nullable [] def);

    /**
     * Returns a long array. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    default long @Nullable [] getLongArray(@NotNull K key) {
        return getLongArray(key, null);
    }

    /**
     * Sets a long at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    void setLongArray(@NotNull K key, long @Nullable [] value);

    /**
     * Saves this storage into a mojang {@link CompoundTag}
     *
     * @return the nbt tag compound
     */
    CompoundTag save();

    /**
     * Saves this NBTTagCompound into a file and compress it with GZip
     *
     * @param file the file where you want to save this storage
     * @throws IOException If an IOError occurs
     */
    default void saveCompressed(@NotNull File file) throws IOException {
        NbtIo.writeCompressed(save(), file);
    }

    /**
     * Saves this NBTTagCompound into a file
     *
     * @param file the file where you want to save this storage
     * @throws IOException if an IOError occurs
     */
    default void saveUncompressed(@NotNull File file) throws IOException {
        NbtIo.write(save(), file);
    }
}
