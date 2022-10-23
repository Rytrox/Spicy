package de.rytrox.spicy.nbt;

import net.minecraft.nbt.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class NBTCompoundStorage implements NBTStorage<String> {

    private final Map<String, Object> elements = new HashMap<>();

    public NBTCompoundStorage(@NotNull CompoundTag nbttagcompound) {
        nbttagcompound.getAllKeys().forEach((key) -> {
            Tag value = nbttagcompound.get(key);

            switch (value) {
                case CompoundTag tag -> elements.put(key, new NBTCompoundStorage(tag));
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

    public NBTCompoundStorage() {
        // Empty Default Constructor
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
                case NBTCompoundStorage compound -> tag.add(compound.save());
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
                case CompoundTag compound -> list.add(new NBTCompoundStorage(compound));
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
        List<T> list = findSafe(key, List.class)
                .orElse(def)
                .stream()
                .filter((element) -> element.getClass().isAssignableFrom(type))
                .toList();

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
                case NBTCompoundStorage compound -> tag.put(key, compound.save());
                default -> throw new IllegalStateException("Unexpected value: " + value);
            }
        });

        return tag;
    }
}
