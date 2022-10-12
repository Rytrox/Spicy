package de.rytrox.spicy.config;

import net.minecraft.server.v1_8_R3.*;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class NBTConfig extends MemoryConfiguration {

    public NBTConfig(@NotNull NBTTagCompound compound) {
        Map<String, Object> readData = readTagCompound(compound);

        this.convertMapsToSections(readData, Objects.requireNonNull(this.getRoot()));
    }

    public NBTConfig() {
    }

    @Contract("_ -> new")
    public static @NotNull NBTConfig fromCompressedFile(@NotNull File compressedFile) throws IOException {
        try(InputStream in = Files.newInputStream(compressedFile.toPath())) {
            return new NBTConfig(Objects.requireNonNull(NBTCompressedStreamTools.a(in)));
        }
    }

    @Contract("_ -> new")
    public static @NotNull NBTConfig fromUncompressedFile(@NotNull File uncompressedFile) throws IOException {
        try(InputStream in = Files.newInputStream(uncompressedFile.toPath())) {
            return new NBTConfig(Objects.requireNonNull(NBTCompressedStreamTools.a(in)));
        }
    }

    @NotNull
    private static Map<String, Object> readTagCompound(@NotNull NBTTagCompound section) {
        Map<String, Object> map = new HashMap<>();

        for (String key : section.c()) {
            NBTBase base = section.get(key);

            if(base instanceof NBTTagByte) {
                map.put(key, ((NBTTagByte) base).f());
            } else if(base instanceof NBTTagByteArray) {
                map.put(key, ((NBTTagByteArray) base).c());
            } else if(base instanceof NBTTagCompound) {
                map.put(key, readTagCompound((NBTTagCompound) base));
            } else if(base instanceof NBTTagDouble) {
                map.put(key, ((NBTTagDouble) base).g());
            } else if(base instanceof NBTTagFloat) {
                map.put(key, ((NBTTagFloat) base).h());
            } else if(base instanceof NBTTagInt) {
                map.put(key, ((NBTTagInt) base).d());
            } else if(base instanceof NBTTagIntArray) {
                map.put(key, ((NBTTagIntArray) base).c());
            } else if(base instanceof NBTTagList) {
                map.put(key, readList((NBTTagList) base));
            } else if(base instanceof NBTTagLong) {
                map.put(key, ((NBTTagLong) base).c());
            } else if(base instanceof NBTTagShort) {
                map.put(key, ((NBTTagShort) base).e());
            } else if(base instanceof NBTTagString) {
                map.put(key, ((NBTTagString) base).a_());
            } else {
                throw new IllegalStateException("Unexpected value: " + section.get(key));
            }
        }

        return map;
    }

    @NotNull
    private static Object readList(NBTTagList list) {
        // Convert TagList to List with elements
        List<Object> elements = new LinkedList<>();

        for(int i = 0; i < list.size(); i++) {
            NBTBase base = list.g(i);

            if(base instanceof NBTTagByte) {
                elements.add(((NBTTagByte) base).f());
            } else if(base instanceof NBTTagByteArray) {
                elements.add(((NBTTagByteArray) base).c());
            } else if(base instanceof NBTTagCompound) {
                elements.add(readTagCompound((NBTTagCompound) base));
            } else if(base instanceof NBTTagDouble) {
                elements.add(((NBTTagDouble) base).g());
            } else if(base instanceof NBTTagFloat) {
                elements.add(((NBTTagFloat) base).h());
            } else if(base instanceof NBTTagInt) {
                elements.add(((NBTTagInt) base).d());
            } else if(base instanceof NBTTagIntArray) {
                elements.add(((NBTTagIntArray) base).c());
            } else if(base instanceof NBTTagList) {
                elements.add(readList((NBTTagList) base));
            } else if(base instanceof NBTTagLong) {
                elements.add(((NBTTagLong) base).c());
            } else if(base instanceof NBTTagShort) {
                elements.add(((NBTTagShort) base).e());
            } else if(base instanceof NBTTagString) {
                elements.add(((NBTTagString) base).a_());
            } else {
                throw new IllegalStateException("Unexpected value: " + elements);
            }
        }

        return elements;
    }

    @NotNull
    private static NBTTagCompound convertMapToCompound(@NotNull Map<?, ?> map) {
        NBTTagCompound compound = new NBTTagCompound();

        map.forEach((key, value) -> {
            if(key instanceof String && value != null) {
                if(value instanceof Byte) {
                    compound.setByte((String) key, (Byte) value);
                } else if(value instanceof Short) {
                    compound.setShort((String) key, (Short) value);
                } else if(value instanceof Integer) {
                    compound.setInt((String) key, (Integer) value);
                } else if(value instanceof Long) {
                    compound.setLong((String) key, (Long) value);
                } else if(value instanceof Float) {
                    compound.setFloat((String) key, (Float) value);
                } else if(value instanceof Double) {
                    compound.setDouble((String) key, (Double) value);
                } else if(value instanceof String) {
                    compound.setString((String) key, (String) value);
                } else if(value instanceof Boolean) {
                    compound.setBoolean((String) key, (Boolean) value);
                } else if(value instanceof Map) {
                    compound.set((String) key, convertMapToCompound((Map<?, ?>) value));
                } else if(value instanceof List) {
                    compound.set((String) key, convertListToNBTList((List<?>) value));
                } else if(value instanceof byte[]) {
                    compound.setByteArray((String) key, (byte[]) value);
                } else if(value instanceof int[]) {
                    compound.setIntArray((String) key, (int[]) value);
                } else {
                    Bukkit.getLogger().log(Level.WARNING, "Unable to convert type " + value.getClass());
                }
            }
        });

        return compound;
    }

    @NotNull
    private static NBTTagList convertListToNBTList(@NotNull List<?> list) {
        NBTTagList nbtList = new NBTTagList();

        list.forEach(value -> {
            if(value instanceof Byte) {
                nbtList.add(new NBTTagByte((Byte) value));
            } else if(value instanceof Short) {
                nbtList.add(new NBTTagShort((Short) value));
            } else if(value instanceof Integer) {
                nbtList.add(new NBTTagInt((Integer) value));
            } else if(value instanceof Long) {
                nbtList.add(new NBTTagLong((Long) value));
            } else if(value instanceof Float) {
                nbtList.add(new NBTTagFloat((Float) value));
            } else if(value instanceof Double) {
                nbtList.add(new NBTTagDouble((Double) value));
            } else if(value instanceof String) {
                nbtList.add(new NBTTagString((String) value));
            } else if(value instanceof Boolean) {
                nbtList.add(new NBTTagByte((byte) ((Boolean) value).compareTo(false)));
            } else if(value instanceof Map) {
                nbtList.add(convertMapToCompound((Map<?, ?>) value));
            } else if(value instanceof List) {
                nbtList.add(convertListToNBTList((List<?>) value));
            } else if(value instanceof byte[]) {
                nbtList.add(new NBTTagByteArray((byte[]) value));
            } else if(value instanceof int[]) {
                nbtList.add(new NBTTagIntArray((int[]) value));
            } else {
                Bukkit.getLogger().log(Level.WARNING, "Unable to convert type " + value.getClass());
            }
        });

        return nbtList;
    }

    private void convertMapsToSections(@NotNull Map<String, Object> input, @NotNull ConfigurationSection section) {
        input.forEach((key, value) -> {
            if (value instanceof Map) {
                this.convertMapsToSections((Map<String, Object>) value, section.createSection(key));
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
     * Compress the TagCompound and save into a file
     *
     * @param file the file you want to save the nbt-data
     * @throws IOException if the file cannot be written
     */
    public void saveCompressed(@NotNull File file) throws IOException {
        try(OutputStream out = Files.newOutputStream(file.toPath())) {
            NBTCompressedStreamTools.a(save(), out);
        }
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

        return val instanceof Number && (((Number)val).byteValue() == 1 || ((Number)val).byteValue() == 0);
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
                .map((element) -> element == 1)
                .collect(Collectors.toList());
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
}
