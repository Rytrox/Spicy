package de.rytrox.spicy.nbt;

import net.minecraft.core.SerializableUUID;
import net.minecraft.nbt.*;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class NBTStorage {

    private final Map<String, Object> elements = new HashMap<>();

    public NBTStorage(@NotNull CompoundTag nbttagcompound) {
        nbttagcompound.getAllKeys().forEach((key) -> {
            Tag value = nbttagcompound.get(key);

            switch (value) {
                case CompoundTag tag -> elements.put(key, new NBTStorage(tag));
                case ByteTag byteTag -> elements.put(key, byteTag.getAsByte());
                case ShortTag shortTag -> elements.put(key, shortTag.getAsShort());
                case IntTag intTag -> elements.put(key, intTag.getAsInt());
                case LongTag longTag -> elements.put(key, longTag.getAsLong());
                case FloatTag floatTag -> elements.put(key, floatTag.getAsFloat());
                case DoubleTag doubleTag -> elements.put(key, doubleTag.getAsDouble());
                case ByteArrayTag byteArray -> elements.put(key, byteArray.getAsByteArray());
                case IntArrayTag intArray -> elements.put(key, intArray.getAsIntArray());
                case LongArrayTag longArrayTag -> elements.put(key, longArrayTag.getAsLongArray());
                case StringTag stringTag -> elements.put(key, stringTag.getAsString());
                case ListTag list -> elements.put(key, fromListTag(list));
                case null, EndTag ignored -> {} // ignore null. It isn't necessary
                default -> throw new IllegalStateException("Unexpected value: " + value);
            }
        });
    }

    public NBTStorage() {
        // Empty Default Constructor
    }

    @Contract("_ -> new")
    static @NotNull NBTStorage fromCompressedFile(@NotNull File file) throws IOException {
        return new NBTStorage(NbtIo.readCompressed(file));
    }

    @Contract("_ -> new")
    static @NotNull NBTStorage fromUncompressedFile(@NotNull File file) throws IOException {
        return new NBTStorage(Objects.requireNonNull(NbtIo.read(file)));
    }

    @NotNull
    private static ListTag toListTag(@NotNull List<?> list) {
        ListTag tag = new ListTag();

        list.forEach((element) -> {
            switch(element) {
                case Byte b -> tag.add(ByteTag.valueOf(b));
                case Short s -> tag.add(ShortTag.valueOf(s));
                case Integer i -> tag.add(IntTag.valueOf(i));
                case Long l -> tag.add(LongTag.valueOf(l));
                case Float f -> tag.add(FloatTag.valueOf(f));
                case Double d -> tag.add(DoubleTag.valueOf(d));
                case String s -> tag.add(StringTag.valueOf(s));
                case byte[] bytes -> tag.add(new ByteArrayTag(bytes));
                case int[] ints -> tag.add(new IntArrayTag(ints));
                case long[] longs -> tag.add(new LongArrayTag(longs));
                case List<?> listStorage -> tag.add(toListTag(listStorage));
                case NBTStorage compound -> tag.add(compound.save());
                case null -> {} // ignore null
                default -> throw new IllegalStateException("Unexpected value: " + element);
            }
        });

        return tag;
    }

    @NotNull
    private static List<?> fromListTag(@NotNull ListTag listTag) {
        List<Object> list = new LinkedList<>();

        listTag.forEach((tag) -> {
            switch (tag) {
                case CompoundTag compound -> list.add(new NBTStorage(compound));
                case ByteTag byteTag -> list.add(byteTag.getAsByte());
                case ShortTag shortTag -> list.add(shortTag.getAsShort());
                case IntTag intTag -> list.add(intTag.getAsInt());
                case LongTag longTag -> list.add(longTag.getAsLong());
                case FloatTag floatTag -> list.add(floatTag.getAsFloat());
                case DoubleTag doubleTag -> list.add(doubleTag.getAsDouble());
                case ByteArrayTag byteArray -> list.add(byteArray.getAsByteArray());
                case IntArrayTag intArray -> list.add(intArray.getAsIntArray());
                case LongArrayTag longArrayTag -> list.add(longArrayTag.getAsLongArray());
                case StringTag stringTag -> list.add(stringTag.getAsString());
                case ListTag innerList -> list.add(fromListTag(innerList));
                case null, EndTag ignored -> {} // ignore null. It isn't necessary
                default -> throw new IllegalStateException("Unexpected value: " + tag);
            }
        });

        return list;
    }

    private <T> Optional<T> findSafe(@NotNull String key, @NotNull Class<T> type) {
        return Optional.ofNullable(this.elements.get(key))
                .filter((element) -> type.isAssignableFrom(element.getClass()))
                .map((element) -> (T) element);
    }

    private NBTStorage prepareKey(@NotNull String @NotNull [] key) {
        NBTStorage current = this;

        for(int i = 0; i < key.length - 1; i++) {
            assert current != null;

            // Replace values with tag compound if it's not a compound
            if(current.getCompound(key[i]) == null) {
                current.setCompound(key[i], new NBTStorage());
            }

            current = current.getCompound(key[i]);
        }

        return current;
    }

    private void set(@NotNull String key, @Nullable Object value) {
        if(value == null) {
            remove(key);
        } else {
            String[] split = key.split("\\.");
            NBTStorage storage = prepareKey(split);

            storage.elements.put(split[split.length - 1], value);
        }
    }

    /**
     * Returns true when this storage is empty
     *
     * @return if this storage is empty
     */
    public boolean isEmpty() {
        return this.elements.isEmpty();
    }

    /**
     * Returns the size of the storage
     *
     * @return the size of the storage
     */
    public int size() {
        return this.elements.size();
    }

    /**
     * Removes a key from the storage
     * @param key the key you want to remove
     */
    public void remove(@NotNull String key) {
        String[] split = key.split("\\.");
        NBTStorage storage = prepareKey(split);

        storage.elements.remove(split[split.length - 1]);
    }

    /**
     * Returns a list. If the list doesn't exist it will return the default value. <br>
     * If the list was found it will filter all elements that are not instance of the type
     *
     * @param key the key of the path
     * @param type the type of the class
     * @param def the default value that is returned when the key does not exist
     * @return the value stored on that key or the default value
     */
    public @Nullable <T> List<T> getList(@NotNull String key, @NotNull Class<T> type, @Nullable List<T> def) {
        List<T> list = findSafe(key, List.class)
                .orElse(def)
                .stream()
                .filter((element) -> type.isAssignableFrom(element.getClass()))
                .toList();

        return list.isEmpty() ? def : list;
    }

    /**
     * Returns a list or null if the key doesn't exist. <br>
     * If the list was found it will filter all elements that are not instance of the type
     *
     * @param key the key of the path
     * @param type the type of the class
     * @return the value stored on that key or null
     */
    @Nullable
    public <T> List<T> getList(@NotNull String key, @NotNull Class<T> type) {
        return getList(key, type, null);
    }

    /**
     * Returns a list or null if the key doesn't exist
     *
     * @param key the key of the path
     * @return the value stored on that key or null
     */
    public List<Object> getList(@NotNull String key) {
        return getList(key, Object.class);
    }

    /**
     * Sets a list inside the storage. Override values that are stored before
     *
     * @param key the key you want to use
     * @param value the value itself
     */
    public void setList(@NotNull String key, @Nullable List<?> value) {
        set(key, value);
    }

    /**
     * Returns a byte. If the byte doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not a byte
     * @return the value stored on the key or the default value
     */
    public @Nullable Byte getByte(@NotNull String key, @Nullable Byte def) {
        return findSafe(key, Byte.class)
                .orElse(def);
    }

    /**
     * Returns a byte. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    @Nullable
    public Byte getByte(@NotNull String key) {
        return getByte(key, null);
    }

    /**
     * Sets a byte at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    public void setByte(@NotNull String key, @Nullable Byte value) {
        set(key, value);
    }

    /**
     * Returns a short. If the byte doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not a short
     * @return the value stored on the key or the default value
     */
    public @Nullable Short getShort(@NotNull String key, @Nullable Short def) {
        return findSafe(key, Short.class)
                .orElse(def);
    }

    /**
     * Returns a short. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    @Nullable
    public Short getShort(@NotNull String key) {
        return getShort(key, null);
    }

    /**
     * Sets a short at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    public void setShort(@NotNull String key, @Nullable Short value) {
        set(key, value);
    }

    /**
     * Returns an integer. If the integer doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not an integer
     * @return the value stored on the key or the default value
     */
    public @Nullable Integer getInt(@NotNull String key, @Nullable Integer def) {
        return findSafe(key, Integer.class)
                .orElse(def);
    }

    /**
     * Returns an integer. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    @Nullable
    public Integer getInt(@NotNull String key) {
        return getInt(key, null);
    }

    /**
     * Sets an integer at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    public void setInt(@NotNull String key, @Nullable Integer value) {
        set(key, value);
    }

    /**
     * Returns a long. If the long doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not a long
     * @return the value stored on the key or the default value
     */
    public @Nullable Long getLong(@NotNull String key, @Nullable Long def) {
        return findSafe(key, Long.class)
                .orElse(def);
    }

    /**
     * Returns a long. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    @Nullable
    public Long getLong(@NotNull String key) {
        return getLong(key, null);
    }

    /**
     * Sets a long at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    public void setLong(@NotNull String key, @Nullable Long value) {
        set(key, value);
    }

    /**
     * Returns a float. If the float doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not a float
     * @return the value stored on the key or the default value
     */
    public @Nullable Float getFloat(@NotNull String key, @Nullable Float def) {
        return findSafe(key, Float.class)
                .orElse(def);
    }

    /**
     * Returns a float. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    @Nullable
    public Float getFloat(@NotNull String key) {
        return getFloat(key, null);
    }

    /**
     * Sets a float at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    public void setFloat(@NotNull String key, @Nullable Float value) {
        set(key, value);
    }

    /**
     * Returns a double. If the double doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not a double
     * @return the value stored on the key or the default value
     */
    public @Nullable Double getDouble(@NotNull String key, @Nullable Double def) {
        return findSafe(key, Double.class)
                .orElse(def);
    }

    /**
     * Returns a double. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    @Nullable
    public Double getDouble(@NotNull String key) {
        return getDouble(key, null);
    }


    /**
     * Sets a double at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    public void setDouble(@NotNull String key, @Nullable Double value) {
        set(key, value);
    }

    /**
     * Returns a byte array. If the byte array doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not a byte array
     * @return the value stored on the key or the default value
     */
    public byte @Nullable [] getByteArray(@NotNull String key, byte @Nullable [] def) {
        return findSafe(key, byte[].class)
                .orElse(def);
    }

    /**
     * Returns a byte array. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    public byte @Nullable [] getByteArray(@NotNull String key) {
        return getByteArray(key, null);
    }

    /**
     * Sets a byte array at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    public void setByteArray(@NotNull String key, byte @Nullable [] value) {
        set(key, value);
    }

    /**
     * Returns a string. If the string doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not a string
     * @return the value stored on the key or the default value
     */
    public @Nullable String getString(@NotNull String key, @Nullable String def) {
        return findSafe(key, String.class)
                .orElse(def);
    }

    /**
     * Returns a string. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    @Nullable
    public String getString(@NotNull String key) {
        return getString(key, null);
    }

    /**
     * Sets a string at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    public void setString(@NotNull String key, @Nullable String value) {
        set(key, value);
    }

    /**
     * Returns a nbt compound. If the compound doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not a nbt compound
     * @return the value stored on the key or the default value
     */
    public @Nullable NBTStorage getCompound(@NotNull String key, @Nullable NBTStorage def) {
        return findSafe(key, NBTStorage.class)
                .orElse(def);
    }

    /**
     * Returns a nbt compound. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    @Nullable
    public NBTStorage getCompound(@NotNull String key) {
        return getCompound(key, null);
    }

    /**
     * Sets a compound at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    public void setCompound(@NotNull String key, @Nullable NBTStorage value) {
        set(key, value);
    }

    /**
     * Returns an array of integers. If the integer array doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not a integer array
     * @return the value stored on the key or the default value
     */
    public int @Nullable [] getIntArray(@NotNull String key, int @Nullable [] def) {
        return findSafe(key, int[].class)
                .orElse(def);
    }

    /**
     * Returns an array of integers. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    public int @Nullable [] getIntArray(@NotNull String key) {
        return getIntArray(key, null);
    }

    /**
     * Sets an array of integers at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    public void setIntArray(@NotNull String key, int @Nullable [] value) {
        set(key, value);
    }

    /**
     * Returns a long array. If the byte array doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not a byte array
     * @return the value stored on the key or the default value
     */
    public long @Nullable [] getLongArray(@NotNull String key, long @Nullable [] def) {
        return findSafe(key, long[].class)
                .orElse(def);
    }

    /**
     * Returns a long array. Returns null on default
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    public long @Nullable [] getLongArray(@NotNull String key) {
        return getLongArray(key, null);
    }

    /**
     * Sets a long at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    public void setLongArray(@NotNull String key, long @Nullable [] value) {
        set(key, value);
    }

    /**
     * Returns an uuid. If the string doesn't exist it will return the default value
     *
     * @param key the key of the path
     * @param def the default value that is returned when the key does not exist or if the value is not an uuid
     * @return the value stored on the key or the default value
     */
    public @Nullable UUID getUniqueID(@NotNull String key, @Nullable UUID def) {
        return findSafe(key, int[].class)
                .map(SerializableUUID::uuidFromIntArray)
                .orElse(def);
    }

    /**
     * Returns an uuid.
     *
     * @param key the key of the path
     * @return the value stored on the key or the default value
     */
    @Nullable
    public UUID getUniqueID(@NotNull String key) {
        return getUniqueID(key, null);
    }

    /**
     * Sets an uuid at a certain key. Override values that are stored before
     *
     * @param key the key you want to see
     * @param value the value you want to
     */
    public void setUniqueID(@NotNull String key, @Nullable UUID value) {
        set(key, value != null ? SerializableUUID.uuidToIntArray(value) : null);
    }

    /**
     * Saves this storage into a mojang {@link CompoundTag}
     *
     * @return the nbt tag compound
     */
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();

        this.elements.forEach((key, value) -> {
            switch (value) {
                case Byte b -> tag.put(key, ByteTag.valueOf(b));
                case Short s -> tag.put(key, ShortTag.valueOf(s));
                case Integer i -> tag.put(key, IntTag.valueOf(i));
                case Long l -> tag.put(key, LongTag.valueOf(l));
                case Float f -> tag.put(key, FloatTag.valueOf(f));
                case Double d -> tag.put(key, DoubleTag.valueOf(d));
                case String s -> tag.put(key, StringTag.valueOf(s));
                case byte[] bytes -> tag.put(key, new ByteArrayTag(bytes));
                case int[] ints -> tag.put(key, new IntArrayTag(ints));
                case long[] longs -> tag.put(key, new LongArrayTag(longs));
                case List<?> list -> tag.put(key, toListTag(list));
                case NBTStorage compound -> tag.put(key, compound.save());
                default -> throw new IllegalStateException("Unexpected value: " + value);
            }
        });

        return tag;
    }

    /**
     * Saves this NBTTagCompound into a file and compress it with GZip
     *
     * @param file the file where you want to save this storage
     * @throws IOException If an IOError occurs
     */
    public void saveCompressed(@NotNull File file) throws IOException {
        NbtIo.writeCompressed(save(), file);
    }

    /**
     * Saves this NBTTagCompound into a file
     *
     * @param file the file where you want to save this storage
     * @throws IOException if an IOError occurs
     */
    public void saveUncompressed(@NotNull File file) throws IOException {
        NbtIo.write(save(), file);
    }
}
