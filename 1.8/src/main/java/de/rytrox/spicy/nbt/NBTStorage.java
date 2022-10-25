package de.rytrox.spicy.nbt;

import de.rytrox.spicy.reflect.Reflections;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class NBTStorage {

    private static final Class<?> compoundTagClass = Reflections.getNMSClass("NBTTagCompound");
    private static final Class<?> byteTagClass = Reflections.getNMSClass("NBTTagByte");
    private static final Class<?> shortTagClass = Reflections.getNMSClass("NBTTagShort");
    private static final Class<?> intTagClass = Reflections.getNMSClass("NBTTagInt");
    private static final Class<?> longTagClass = Reflections.getNMSClass("NBTTagLong");
    private static final Class<?> floatTagClass = Reflections.getNMSClass("NBTTagFloat");
    private static final Class<?> doubleTagClass = Reflections.getNMSClass("NBTTagDouble");
    private static final Class<?> byteArrayTagClass = Reflections.getNMSClass("NBTTagByteArray");
    private static final Class<?> intArrayTagClass = Reflections.getNMSClass("NBTTagIntArray");
    private static final Class<?> listTagClass = Reflections.getNMSClass("NBTTagList");
    private static final Class<?> stringTagClass = Reflections.getNMSClass("NBTTagString");
    private static final Class<?> nbtIoClass = Reflections.getNMSClass("NBTCompressedStreamTools");

    private final Map<String, Object> elements = new HashMap<>();

    public NBTStorage(@NotNull Object nbttagcompound) {
        try {
            Set<String> keys = (Set<String>) MethodUtils.invokeMethod(nbttagcompound, "c");

            for(String key : keys) {
                Object value = MethodUtils.invokeMethod(nbttagcompound, "get", key);

                if(compoundTagClass.isAssignableFrom(value.getClass())) {
                    elements.put(key, new NBTStorage(value));
                } else if(byteTagClass.isAssignableFrom(value.getClass())) {
                    elements.put(key, MethodUtils.invokeMethod(value, "f"));
                } else if(shortTagClass.isAssignableFrom(value.getClass())) {
                    elements.put(key, MethodUtils.invokeMethod(value, "e"));
                } else if(intTagClass.isAssignableFrom(value.getClass())) {
                    elements.put(key, MethodUtils.invokeMethod(value, "d"));
                } else if(longTagClass.isAssignableFrom(value.getClass())) {
                    elements.put(key, MethodUtils.invokeMethod(value, "c"));
                } else if(floatTagClass.isAssignableFrom(value.getClass())) {
                    elements.put(key, MethodUtils.invokeMethod(value, "h"));
                } else if(doubleTagClass.isAssignableFrom(value.getClass())) {
                    elements.put(key, MethodUtils.invokeMethod(value, "g"));
                } else if(byteArrayTagClass.isAssignableFrom(value.getClass())) {
                    elements.put(key, MethodUtils.invokeMethod(value, "c"));
                } else if(intArrayTagClass.isAssignableFrom(value.getClass())) {
                    elements.put(key, MethodUtils.invokeMethod(value, "c"));
                } else if(listTagClass.isAssignableFrom(value.getClass())) {
                    elements.put(key, fromListTag(value));
                } else if(stringTagClass.isAssignableFrom(value.getClass())) {
                    elements.put(key, MethodUtils.invokeMethod(value, "a_"));
                } else {
                    throw new IllegalStateException("Unexpected Value: " + value);
                }
            }
        } catch (ReflectiveOperationException e) {
            Bukkit.getLogger().log(Level.SEVERE,
                    "Unable to load NBTTagCompound into NBTStorage. " +
                            "This is a bug. Please open a new issue on https://github.com/Rytrox/Spicy/issues with this stacktrace and the exact Spigot Version.",
                    e);
        }
    }

    public NBTStorage() {
        // Empty Default Constructor
    }

    @Contract("_ -> new")
    public static @NotNull NBTStorage fromCompressedFile(@NotNull File file) throws IOException {
        try {
            return new NBTStorage(MethodUtils.invokeStaticMethod(
                    Reflections.getNMSClass("NBTCompressedStreamTools"),
                    "a",
                    Files.newInputStream(file.toPath())
            ));
        } catch (ReflectiveOperationException e) {
            throw new IOException("Unable to read File from file", e);
        }
    }

    @NotNull
    private static Object toListTag(@NotNull List<?> list) throws ReflectiveOperationException {
        Object tag = ConstructorUtils.invokeConstructor(listTagClass);

        for(Object element : list) {
            if(element instanceof Byte) {
                MethodUtils.invokeMethod(tag, "add", ConstructorUtils.invokeConstructor(byteTagClass, element));
            } else if(element instanceof Short) {
                MethodUtils.invokeMethod(tag, "add", ConstructorUtils.invokeConstructor(shortTagClass, element));
            } else if(element instanceof Integer) {
                MethodUtils.invokeMethod(tag, "add", ConstructorUtils.invokeConstructor(intTagClass, element));
            } else if(element instanceof Long) {
                MethodUtils.invokeMethod(tag, "add", ConstructorUtils.invokeConstructor(longTagClass, element));
            } else if(element instanceof Float) {
                MethodUtils.invokeMethod(tag, "add", ConstructorUtils.invokeConstructor(floatTagClass, element));
            } else if(element instanceof Double) {
                MethodUtils.invokeMethod(tag, "add", ConstructorUtils.invokeConstructor(doubleTagClass, element));
            } else if(element instanceof String) {
                MethodUtils.invokeMethod(tag, "add",
                        ((String) element).isEmpty() ?
                                ConstructorUtils.invokeConstructor(stringTagClass, element) :
                                ConstructorUtils.invokeConstructor(stringTagClass)
                );
            } else if(element instanceof byte[]) {
                MethodUtils.invokeMethod(tag, "add", ConstructorUtils.invokeConstructor(byteArrayTagClass, element));
            } else if(element instanceof int[]) {
                MethodUtils.invokeMethod(tag, "add", ConstructorUtils.invokeConstructor(intArrayTagClass, element));
            } else if(element instanceof List<?>) {
                MethodUtils.invokeMethod(tag, "add", toListTag((List<?>) element));
            } else if(element instanceof NBTStorage) {
                MethodUtils.invokeMethod(tag, "add", ((NBTStorage) element).save());
            } else if(element != null) {
                throw new IllegalStateException("Unexpected value: " + element);
            }
        }

        return tag;
    }

    @NotNull
    private static List<?> fromListTag(@NotNull Object listTag) throws ReflectiveOperationException {
        List<Object> list = new LinkedList<>();

        int size = (Integer) MethodUtils.invokeMethod(listTag, "size");
        for(int i = 0; i < size; i++) {
            Object tag = MethodUtils.invokeMethod(listTag, "g", i);

            if(tag != null) {
                if(compoundTagClass.isAssignableFrom(tag.getClass())) {
                    list.add(new NBTStorage(tag));
                } else if(byteTagClass.isAssignableFrom(tag.getClass())) {
                    list.add(MethodUtils.invokeMethod(tag, "f"));
                } else if(shortTagClass.isAssignableFrom(tag.getClass())) {
                    list.add(MethodUtils.invokeMethod(tag, "e"));
                } else if(intTagClass.isAssignableFrom(tag.getClass())) {
                    list.add(MethodUtils.invokeMethod(tag, "d"));
                } else if(longTagClass.isAssignableFrom(tag.getClass())) {
                    list.add(MethodUtils.invokeMethod(tag, "c"));
                } else if(floatTagClass.isAssignableFrom(tag.getClass())) {
                    list.add(MethodUtils.invokeMethod(tag, "h"));
                } else if(doubleTagClass.isAssignableFrom(tag.getClass())) {
                    list.add(MethodUtils.invokeMethod(tag, "g"));
                } else if(byteArrayTagClass.isAssignableFrom(tag.getClass())) {
                    list.add(MethodUtils.invokeMethod(tag, "c"));
                } else if(intArrayTagClass.isAssignableFrom(tag.getClass())) {
                    list.add(MethodUtils.invokeMethod(tag, "c"));
                } else if(listTagClass.isAssignableFrom(tag.getClass())) {
                    list.add(fromListTag(tag));
                } else if(stringTagClass.isAssignableFrom(tag.getClass())) {
                    list.add(MethodUtils.invokeMethod(tag, "a_"));
                } else {
                    throw new IllegalStateException("Unexpected Value: " + tag);
                }
            }
        }

        return list;
    }

    private <T> Optional<T> findSafe(@NotNull String key, @NotNull Class<T> type) {
        return Optional.ofNullable(this.elements.get(key))
                .filter((element) -> type.isAssignableFrom(element.getClass()))
                .map((element) -> (T) element);
    }

    private NBTStorage prepareKey(@NotNull String[] key) {
        NBTStorage current = this;

        for(int i = 0; i < key.length - 1; i++) {
            assert current != null;

            // Replace values with tag compound if it's not a compound
            if(current.getCompound(key[i]) == null) {
                current.setCompound(key[i], new NBTStorage());
            }

            current = (NBTStorage) current.getCompound(key[i]);
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
        List<T> list = (List<T>) findSafe(key, List.class)
                .orElse(def)
                .stream()
                .filter((element) -> element.getClass().isAssignableFrom(type))
                .collect(Collectors.toList());

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
     * Saves this storage into a mojang NBTTagCompound
     *
     * @return the nbt tag compound
     */
    public Object save() {
        try {
            Object compoundTag = ConstructorUtils.invokeConstructor(compoundTagClass);

            for(Map.Entry<String, Object> entry : this.elements.entrySet()) {
                if(entry.getValue() instanceof Byte) {
                    MethodUtils.invokeMethod(compoundTag, "set", entry.getKey(), ConstructorUtils.invokeConstructor(byteTagClass, entry.getValue()));
                } else if(entry.getValue() instanceof Short) {
                    MethodUtils.invokeMethod(compoundTag, "set", entry.getKey(), ConstructorUtils.invokeConstructor(shortTagClass, entry.getValue()));
                } else if(entry.getValue() instanceof Integer) {
                    MethodUtils.invokeMethod(compoundTag, "set", entry.getKey(), ConstructorUtils.invokeConstructor(intTagClass, entry.getValue()));
                } else if(entry.getValue() instanceof Long) {
                    MethodUtils.invokeMethod(compoundTag, "set", entry.getKey(), ConstructorUtils.invokeConstructor(longTagClass, entry.getValue()));
                } else if(entry.getValue() instanceof Float) {
                    MethodUtils.invokeMethod(compoundTag, "set", entry.getKey(), ConstructorUtils.invokeConstructor(floatTagClass, entry.getValue()));
                } else if(entry.getValue() instanceof Double) {
                    MethodUtils.invokeMethod(compoundTag, "set", entry.getKey(), ConstructorUtils.invokeConstructor(doubleTagClass, entry.getValue()));
                } else if(entry.getValue() instanceof String) {
                    MethodUtils.invokeMethod(compoundTag, "set", entry.getKey(),
                            ((String) entry.getValue()).isEmpty() ?
                                    ConstructorUtils.invokeConstructor(stringTagClass, entry.getValue()) :
                                    ConstructorUtils.invokeConstructor(stringTagClass)
                    );
                } else if(entry.getValue() instanceof byte[]) {
                    MethodUtils.invokeMethod(compoundTag, "set", entry.getKey(), ConstructorUtils.invokeConstructor(byteArrayTagClass, entry.getValue()));
                } else if(entry.getValue() instanceof int[]) {
                    MethodUtils.invokeMethod(compoundTag, "set", entry.getKey(), ConstructorUtils.invokeConstructor(intArrayTagClass, entry.getValue()));
                } else if(entry.getValue() instanceof List<?>) {
                    MethodUtils.invokeMethod(compoundTag, "set", entry.getKey(), toListTag((List<?>) entry.getValue()));
                } else if(entry.getValue() instanceof NBTStorage) {
                    MethodUtils.invokeMethod(compoundTag, "set", entry.getKey(), ((NBTStorage) entry.getValue()).save());
                } else if(entry.getValue() != null) {
                    throw new IllegalStateException("Unexpected value: " + entry.getValue());
                }
            }

            return compoundTag;
        } catch (ReflectiveOperationException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Unable to save NBTStorage into NBTTagCompound.", e);
        }

        return null;
    }

    /**
     * Saves this NBTTagCompound into a file and compress it with GZip
     *
     * @param file the file where you want to save this storage
     * @throws IOException If an IOError occurs
     */
    public void saveCompressed(@NotNull File file) throws IOException {
        try {
            MethodUtils.invokeStaticMethod(nbtIoClass, "a", save(), Files.newOutputStream(file.toPath()));
        } catch (ReflectiveOperationException e) {
            throw new IOException("Unable to save NBTTagCompound into File", e);
        }
    }
}
