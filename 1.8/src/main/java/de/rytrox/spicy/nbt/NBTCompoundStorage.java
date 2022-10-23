package de.rytrox.spicy.nbt;

import de.rytrox.spicy.reflect.Reflections;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class NBTCompoundStorage implements NBTStorage<String> {

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

    public NBTCompoundStorage(@NotNull Object nbttagcompound) {
        try {
            Set<String> keys = (Set<String>) MethodUtils.invokeMethod(nbttagcompound, "c");

            for(String key : keys) {
                Object value = MethodUtils.invokeMethod(nbttagcompound, "get", key);

                if(value.getClass().isAssignableFrom(compoundTagClass)) {
                    elements.put(key, new NBTCompoundStorage(value));
                } else if(value.getClass().isAssignableFrom(byteTagClass)) {
                    elements.put(key, MethodUtils.invokeMethod(value, "f"));
                } else if(value.getClass().isAssignableFrom(shortTagClass)) {
                    elements.put(key, MethodUtils.invokeMethod(value, "e"));
                } else if(value.getClass().isAssignableFrom(intTagClass)) {
                    elements.put(key, MethodUtils.invokeMethod(value, "d"));
                } else if(value.getClass().isAssignableFrom(longTagClass)) {
                    elements.put(key, MethodUtils.invokeMethod(value, "c"));
                } else if(value.getClass().isAssignableFrom(floatTagClass)) {
                    elements.put(key, MethodUtils.invokeMethod(value, "h"));
                } else if(value.getClass().isAssignableFrom(doubleTagClass)) {
                    elements.put(key, MethodUtils.invokeMethod(value, "g"));
                } else if(value.getClass().isAssignableFrom(byteArrayTagClass)) {
                    elements.put(key, MethodUtils.invokeMethod(value, "c"));
                } else if(value.getClass().isAssignableFrom(intArrayTagClass)) {
                    elements.put(key, MethodUtils.invokeMethod(value, "c"));
                } else if(value.getClass().isAssignableFrom(listTagClass)) {
                    elements.put(key, fromListTag(value));
                } else if(value.getClass().isAssignableFrom(stringTagClass)) {
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

    public NBTCompoundStorage() {
        // Empty Default Constructor
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
            } else if(element instanceof NBTCompoundStorage) {
                MethodUtils.invokeMethod(tag, "add", ((NBTCompoundStorage) element).save());
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
                if(tag.getClass().isAssignableFrom(compoundTagClass)) {
                    list.add(new NBTCompoundStorage(tag));
                } else if(tag.getClass().isAssignableFrom(byteTagClass)) {
                    list.add(MethodUtils.invokeMethod(tag, "f"));
                } else if(tag.getClass().isAssignableFrom(shortTagClass)) {
                    list.add(MethodUtils.invokeMethod(tag, "e"));
                } else if(tag.getClass().isAssignableFrom(intTagClass)) {
                    list.add(MethodUtils.invokeMethod(tag, "d"));
                } else if(tag.getClass().isAssignableFrom(longTagClass)) {
                    list.add(MethodUtils.invokeMethod(tag, "c"));
                } else if(tag.getClass().isAssignableFrom(floatTagClass)) {
                    list.add(MethodUtils.invokeMethod(tag, "h"));
                } else if(tag.getClass().isAssignableFrom(doubleTagClass)) {
                    list.add(MethodUtils.invokeMethod(tag, "g"));
                } else if(tag.getClass().isAssignableFrom(byteArrayTagClass)) {
                    list.add(MethodUtils.invokeMethod(tag, "c"));
                } else if(tag.getClass().isAssignableFrom(intArrayTagClass)) {
                    list.add(MethodUtils.invokeMethod(tag, "c"));
                } else if(tag.getClass().isAssignableFrom(listTagClass)) {
                    list.add(fromListTag(tag));
                } else if(tag.getClass().isAssignableFrom(stringTagClass)) {
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
                .filter((element) -> type.equals(element.getClass()))
                .map((element) -> (T) element);
    }

    private NBTCompoundStorage prepareKey(@NotNull String[] key) {
        NBTCompoundStorage current = this;

        for(int i = 0; i < key.length - 1; i++) {
            assert current != null;

            // Replace values with tag compound if it's not a compound
            if(current.getCompound(key[i]) == null) {
                current.setCompound(key[i], new NBTCompoundStorage());
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
            NBTCompoundStorage storage = prepareKey(split);

            storage.elements.put(split[split.length - 1], value);
        }
    }

    @Override
    public void remove(@NotNull String key) {
        String[] split = key.split("\\.");
        NBTCompoundStorage storage = prepareKey(split);

        storage.elements.remove(split[split.length - 1]);
    }

    @Override
    public @Nullable <T> List<T> getList(@NotNull String key, @NotNull Class<T> type, @Nullable List<T> def) {
        List<T> list = (List<T>) findSafe(key, List.class)
                .orElse(def)
                .stream()
                .filter((element) -> element.getClass().isAssignableFrom(type))
                .collect(Collectors.toList());

        return list.isEmpty() ? def : list;
    }

    @Override
    public void setList(@NotNull String key, @Nullable List<?> value) {
        set(key, value);
    }

    @Override
    public @Nullable Byte getByte(@NotNull String key, @Nullable Byte def) {
        return findSafe(key, Byte.class)
                .orElse(def);
    }

    @Override
    public void setByte(@NotNull String key, @Nullable Byte value) {
        set(key, value);
    }

    @Override
    public @Nullable Short getShort(@NotNull String key, @Nullable Short def) {
        return findSafe(key, Short.class)
                .orElse(def);
    }

    @Override
    public void setShort(@NotNull String key, @Nullable Short value) {
        set(key, value);
    }

    @Override
    public @Nullable Integer getInt(@NotNull String key, @Nullable Integer def) {
        return findSafe(key, Integer.class)
                .orElse(def);
    }

    @Override
    public void setInt(@NotNull String key, @Nullable Integer value) {
        set(key, value);
    }

    @Override
    public @Nullable Long getLong(@NotNull String key, @Nullable Long def) {
        return findSafe(key, Long.class)
                .orElse(def);
    }

    @Override
    public void setLong(@NotNull String key, @Nullable Long value) {
        set(key, value);
    }

    @Override
    public @Nullable Float getFloat(@NotNull String key, @Nullable Float def) {
        return findSafe(key, Float.class)
                .orElse(def);
    }

    @Override
    public void setFloat(@NotNull String key, @Nullable Float value) {
        set(key, value);
    }

    @Override
    public @Nullable Double getDouble(@NotNull String key, @Nullable Double def) {
        return findSafe(key, Double.class)
                .orElse(def);
    }

    @Override
    public void setDouble(@NotNull String key, @Nullable Double value) {
        set(key, value);
    }

    @Override
    public byte @Nullable [] getByteArray(@NotNull String key, byte @Nullable [] def) {
        return findSafe(key, byte[].class)
                .orElse(def);
    }

    @Override
    public void setByteArray(@NotNull String key, byte @Nullable [] value) {
        set(key, value);
    }

    @Override
    public @Nullable String getString(@NotNull String key, @Nullable String def) {
        return findSafe(key, String.class)
                .orElse(def);
    }

    @Override
    public void setString(@NotNull String key, @Nullable String value) {
        set(key, value);
    }

    @Override
    public @Nullable NBTCompoundStorage getCompound(@NotNull String key, @Nullable NBTCompoundStorage def) {
        return findSafe(key, NBTCompoundStorage.class)
                .orElse(def);
    }

    @Override
    public void setCompound(@NotNull String key, @Nullable NBTCompoundStorage value) {
        set(key, value);
    }

    @Override
    public int @Nullable [] getIntArray(@NotNull String key, int @Nullable [] def) {
        return findSafe(key, int[].class)
                .orElse(def);
    }

    @Override
    public void setIntArray(@NotNull String key, int @Nullable [] value) {
        set(key, value);
    }

    @Override
    public long @Nullable [] getLongArray(@NotNull String key, long @Nullable [] def) {
        return findSafe(key, long[].class)
                .orElse(def);
    }

    @Override
    public void setLongArray(@NotNull String key, long @Nullable [] value) {
        set(key, value);
    }

    @Override
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
                } else if(entry.getValue() instanceof NBTCompoundStorage) {
                    MethodUtils.invokeMethod(compoundTag, "set", entry.getKey(), ((NBTCompoundStorage) entry.getValue()).save());
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

    @Override
    public void saveCompressed(@NotNull File file) throws IOException {
        try {
            MethodUtils.invokeStaticMethod(nbtIoClass, "a", save(), Files.newOutputStream(file.toPath()));
        } catch (ReflectiveOperationException e) {
            throw new IOException("Unable to save NBTTagCompound into File", e);
        }
    }
}
