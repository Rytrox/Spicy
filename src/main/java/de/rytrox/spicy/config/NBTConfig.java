package de.rytrox.spicy.config;

import com.google.common.primitives.Bytes;

import net.minecraft.nbt.*;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class NBTConfig extends MemoryConfiguration {

    public NBTConfig(@NotNull CompoundTag compound) {
        Map<String, Object> readData = readTagCompound(compound);

        this.convertMapsToSections(readData, Objects.requireNonNull(this.getRoot()));
    }

    public NBTConfig() {
    }

    @Contract("_ -> new")
    public static @NotNull NBTConfig fromCompressedFile(@NotNull File compressedFile) throws IOException {
        return new NBTConfig(Objects.requireNonNull(NbtIo.readCompressed(compressedFile)));
    }

    @Contract("_ -> new")
    public static @NotNull NBTConfig fromUncompressedFile(@NotNull File uncompressedFile) throws IOException {
        return new NBTConfig(Objects.requireNonNull(NbtIo.read(uncompressedFile)));
    }

    @NotNull
    private static Map<String, Object> readTagCompound(@NotNull CompoundTag section) {
        Map<String, Object> map = new HashMap<>();

        for (String key : section.getAllKeys()) {
            switch(Objects.requireNonNull(section.get(key))) {
                case CompoundTag s -> map.put(key, readTagCompound(s));
                case IntArrayTag i -> {
                    if(i.size() == 4) {
                        map.put(key, Bukkit.getOfflinePlayer(NbtUtils.loadUUID(i)));
                    } else map.put(key, readList(i));
                }
                case CollectionTag<? extends Tag> l -> map.put(key, readList(l));
                case NumericTag number -> map.put(key, number.getAsNumber());
                case StringTag string -> map.put(key, string.getAsString());
                default -> throw new IllegalStateException("Unexpected value: " + section.get(key));
            }
        }

        return map;
    }

    @NotNull
    private static Object readList(@NotNull CollectionTag<? extends Tag> list) {
        // Convert TagList to List with elements
        List<Object> elements = new LinkedList<>();

        switch (list) {
            case ByteArrayTag byteArray -> elements.addAll(Bytes.asList(byteArray.getAsByteArray()));
            case IntArrayTag i -> {
                if(i.size() == 4) {
                    // Try convert to OfflinePlayer here! That's why this method returns Object
                    return Bukkit.getOfflinePlayer(NbtUtils.loadUUID(i));
                } else elements.addAll(Arrays.stream(i.getAsIntArray()).boxed().toList());
            }
            case LongArrayTag longArray -> elements.addAll(Arrays.stream(longArray.getAsLongArray()).boxed().toList());
            default ->
                list.forEach(element -> {
                    switch(element) {
                        case CompoundTag s -> elements.add(readTagCompound(s));
                        case CollectionTag<? extends Tag> l -> elements.add(readList(l));
                        case StringTag s -> elements.add(s.getAsString());
                        case NumericTag n -> elements.add(n.getAsNumber());
                        default -> throw new IllegalStateException("Unexpected value in List found " + element);
                    }
                });
        }

        return elements;
    }

    @NotNull
    private static CompoundTag convertMapToCompound(@NotNull Map<?, ?> map) {
        CompoundTag compound = new CompoundTag();

        map.forEach((a, value) -> {
            if(a instanceof String key && value != null) {
                switch(value) {
                    case Long l -> compound.putLong(key, l);
                    case Double d -> compound.putDouble(key, d);
                    case Integer i -> compound.putInt(key, i);
                    case Float f -> compound.putFloat(key, f);
                    case Short s -> compound.putShort(key, s);
                    case Byte b -> compound.putByte(key, b);
                    case Boolean b -> compound.putBoolean(key, b);
                    case Character c -> compound.putInt(key, c);
                    case String s -> compound.putString(key, s);
                    case UUID uuid -> compound.putUUID(key, uuid);
                    case OfflinePlayer p -> compound.putUUID(key, p.getUniqueId());
                    case Map m -> compound.put(key, convertMapToCompound(m));
                    case List l -> compound.put(key, convertListToNBTList(l));
                    default -> Bukkit.getLogger().log(Level.WARNING, "Unable to convert type " + value.getClass());
                }
            }
        });

        return compound;
    }

    @NotNull
    private static CollectionTag<? extends Tag> convertListToNBTList(@NotNull List<?> list) {
        ListTag nbtList = new ListTag();

        list.forEach(element -> {
            switch (element) {
                case Long l -> nbtList.add(LongTag.valueOf(l));
                case Double d -> nbtList.add(DoubleTag.valueOf(d));
                case Integer i -> nbtList.add(IntTag.valueOf(i));
                case Float f -> nbtList.add(FloatTag.valueOf(f));
                case Short s -> nbtList.add(ShortTag.valueOf(s));
                case Byte b -> nbtList.add(ByteTag.valueOf(b));
                case Boolean b -> nbtList.add(ByteTag.valueOf(b));
                case Character c -> nbtList.add(IntTag.valueOf(c));
                case String s -> nbtList.add(StringTag.valueOf(s));
                case OfflinePlayer p -> nbtList.add(NbtUtils.createUUID(p.getUniqueId()));
                case UUID uuid -> nbtList.add(NbtUtils.createUUID(uuid));
                case Map m -> nbtList.add(convertMapToCompound(m));
                case List l -> nbtList.add(convertListToNBTList(l));
                default -> Bukkit.getLogger().log(Level.WARNING, "Unable to convert type " + element.getClass());
            }
        });

        return nbtList;
    }

    private void convertMapsToSections(@NotNull Map<String, Object> input, @NotNull ConfigurationSection section) {
        input.forEach((key, value) -> {
            if (value instanceof Map map) {
                this.convertMapsToSections(map, section.createSection(key));
            } else {
                section.set(key, value);
            }
        });
    }

    /**
     * Saves this config to an NBTTabCompound
     *
     * @return the NBTTagCompounds of this config
     */
    @NotNull
    public CompoundTag save() {
        Map<String, Object> map = Optional.ofNullable(this.getRoot())
                .map((root) -> root.getValues(true))
                .orElse(new HashMap<>());

        return convertMapToCompound(map);
    }

    /**
     * Compress the TagCompound and save into a file
     *
     * @param file the file you want to save the nbt-data
     * @throws IOException if the file cannot be written
     */
    public void saveCompressed(@NotNull File file) throws IOException {
        NbtIo.writeCompressed(save(), file);
    }

    /**
     * Save the TagCompound uncompressed in a file
     *
     * @param file the file you want to save the nbt-data
     * @throws IOException if the file cannot be written
     */
    public void saveUncompressed(@NotNull File file) throws IOException {
        NbtIo.write(save(), file);
    }

    /*
     * Adjustments for supported types
     */

    @Override
    public boolean getBoolean(@NotNull String path) {
        return getInt(path, 0) == 1;
    }

    @Override
    public boolean getBoolean(@NotNull String path, boolean def) {
        return getInt(path, 0) == 1 || def;
    }

    @Override
    public boolean isBoolean(@NotNull String path) {
        Object val = get(path);

        return val instanceof Number n && (n.byteValue() == 1 || n.byteValue() == 0);
    }

    public char getCharacter(@NotNull String path) {
        return (char) getInt(path);
    }

    public char getCharacter(@NotNull String path, char def) {
        return isCharacter(path) ? getCharacter(path) : def;
    }

    public boolean isCharacter(@NotNull String path) {
        if(isInt(path)) {
            int val = getInt(path);

            return val >= 0x000000 && val < 0x10FFFF;
        }

        return false;
    }

    @Override
    public @NotNull List<Boolean> getBooleanList(@NotNull String path) {
        return getList(path, new ArrayList<>()).stream()
                .filter((element) -> element instanceof Number)
                .map((element) -> ((Number) element).byteValue())
                .filter((element) -> element == 0 || element == 1)
                .map((element) -> element == 1).toList();
    }

    @Override
    public ItemStack getItemStack(@NotNull String path) {
        throw new UnsupportedOperationException("NBTTagCompounds can't hold ItemStacks");
    }

    @Override
    public ItemStack getItemStack(@NotNull String path, @Nullable ItemStack def) {
        throw new UnsupportedOperationException("NBTTagCompounds can't hold ItemStacks");
    }

    @Override
    public boolean isItemStack(@NotNull String path) {
        throw new UnsupportedOperationException("NBTTagCompounds can't hold ItemStacks");
    }

    @Override
    public @NotNull List<String> getInlineComments(@NotNull String path) {
        throw new UnsupportedOperationException("NBTTagCompounds does not support comments");
    }

    @Override
    public void setComments(@NotNull String path, @Nullable List<String> comments) {
        throw new UnsupportedOperationException("NBTTagCompounds does not support comments");
    }

    @Override
    public void setInlineComments(@NotNull String path, @Nullable List<String> comments) {
        throw new UnsupportedOperationException("NBTTagCompounds does not support comments");
    }
}
