package de.timeout.libs.config;

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

    public NBTConfig(@NotNull NBTTagCompound compound) {
        Map<String, Object> readData = readTagCompound(compound);

        this.convertMapsToSections(readData, Objects.requireNonNull(this.getRoot()));
    }

    public NBTConfig() {
    }

    @Contract("_ -> new")
    public static @NotNull NBTConfig fromCompressedFile(@NotNull File compressedFile) throws IOException {
        return new NBTConfig(Objects.requireNonNull(NBTCompressedStreamTools.a(compressedFile)));
    }

    @Contract("_ -> new")
    public static @NotNull NBTConfig fromUncompressedFile(@NotNull File uncompressedFile) throws IOException {
        return new NBTConfig(Objects.requireNonNull(NBTCompressedStreamTools.b(uncompressedFile)));
    }

    @NotNull
    private static Map<String, Object> readTagCompound(@NotNull NBTTagCompound section) {
        Map<String, Object> map = new HashMap<>();

        for (String key : section.d()) {
            switch(Objects.requireNonNull(section.c(key))) {
                case NBTTagCompound s -> map.put(key, readTagCompound(s));
                case NBTTagIntArray i -> {
                    if(i.size() == 4) {
                        map.put(key, Bukkit.getOfflinePlayer(GameProfileSerializer.a(i)));
                    } else map.put(key, readList(i));
                }
                case NBTList<? extends NBTBase> l -> map.put(key, readList(l));
                case NBTNumber number -> map.put(key, number.k());
                case NBTTagString string -> map.put(key, string.e_());
                default -> throw new IllegalStateException("Unexpected value: " + section.c(key));
            }
        }

        return map;
    }

    @NotNull
    private static List<?> readList(@NotNull NBTList<? extends NBTBase> list) {
        // Convert TagList to List with elements
        List<Object> elements = new LinkedList<>();

        switch (list) {
            case NBTTagByteArray byteArray -> elements.addAll(Bytes.asList(byteArray.d()));
            case NBTTagIntArray i -> {
                if(i.size() == 4) {
                    // Try convert to OfflinePlayer here!
                    elements.add(Bukkit.getOfflinePlayer(GameProfileSerializer.a(i)));
                } else elements.addAll(Arrays.stream(i.f()).boxed().toList());
            }
            case NBTTagLongArray longArray -> elements.addAll(Arrays.stream(longArray.f()).boxed().toList());
            default ->
                list.forEach(element -> {
                    switch(element) {
                        case NBTTagCompound s -> elements.add(readTagCompound(s));
                        case NBTList<? extends NBTBase> l -> elements.add(readList(l));
                        case NBTTagString s -> elements.add(s.e_());
                        case NBTNumber n -> elements.add(n.k());
                        default -> throw new IllegalStateException("Unexpected value in List found " + element);
                    }
                });
        }

        return elements;
    }

    @NotNull
    private static NBTTagCompound convertMapToCompound(@NotNull Map<?, ?> map) {
        NBTTagCompound compound = new NBTTagCompound();

        map.forEach((a, value) -> {
            if(a instanceof String key && value != null) {
                switch(value) {
                    case Long l -> compound.a(key, l);
                    case Double d -> compound.a(key, d);
                    case Integer i -> compound.a(key, i);
                    case Float f -> compound.a(key, f);
                    case Short s -> compound.a(key, s);
                    case Byte b -> compound.a(key, b);
                    case Boolean b -> compound.a(key, b);
                    case Character c -> compound.a(key, c);
                    case String s -> compound.a(key, s);
                    case UUID uuid -> compound.a(key, uuid);
                    case OfflinePlayer p -> compound.a(key, p.getUniqueId());
                    case Map m -> compound.a(key, convertMapToCompound(m));
                    case List l -> compound.a(key, convertListToNBTList(l));
                    default -> Bukkit.getLogger().log(Level.WARNING, "Unable to convert type " + value.getClass());
                }
            }
        });

        return compound;
    }

    @NotNull
    private static NBTList<? extends NBTBase> convertListToNBTList(@NotNull List<?> list) {
        NBTTagList nbtList = new NBTTagList();

        list.forEach(element -> {
            switch (element) {
                case Long l -> nbtList.add(NBTTagLong.a(l));
                case Double d -> nbtList.add(NBTTagDouble.a(d));
                case Integer i -> nbtList.add(NBTTagInt.a(i));
                case Float f -> nbtList.add(NBTTagFloat.a(f));
                case Short s -> nbtList.add(NBTTagShort.a(s));
                case Byte b -> nbtList.add(NBTTagByte.a(b));
                case Boolean b -> nbtList.add(NBTTagByte.a(b));
                case Character c -> nbtList.add(NBTTagString.a(String.valueOf(c)));
                case String s -> nbtList.add(NBTTagString.a(s));
                case OfflinePlayer p -> nbtList.add(GameProfileSerializer.a(p.getUniqueId()));
                case UUID uuid -> nbtList.add(GameProfileSerializer.a(uuid));
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
    public NBTTagCompound save() {
        Map<String, Object> map = Optional.ofNullable(this.getRoot())
                .map((root) -> root.getValues(true))
                .orElse(new HashMap<>());

        return convertMapToCompound(map);
    }

    /**
     * Saves the config into a file
     *
     * @param file the file you want to save the nbt-data
     * @throws IOException if the file cannot be written
     */
    public void save(@NotNull File file) throws IOException {
        NBTCompressedStreamTools.b(save(), file);
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
