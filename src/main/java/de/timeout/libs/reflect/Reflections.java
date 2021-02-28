package de.timeout.libs.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Reflections {

    private Reflections() { }

    /**
     * Searches through a bundle of field until it founds the correct field
     * @param clazz the class you want to search
     * @param names a bundle of names. NMS-Fieldnames may changes in different versions
     * @return the searched field or null if the field cannot be found.
     */
    @Nullable
    public static Field getField(Class<?> clazz, String... names) {
        Validate.notEmpty(names, "Names cannot be empty");

        return Arrays.stream(names)
                .map(name -> FieldUtils.getField(clazz, name, true))
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);
    }

    /**
     * Searches for a field which has different names in different versions.
     * @param clazz the class which stores this field. Cannot be null
     * @param type the type of data of the field. Cannot be null
     * @param names the different names of the field
     * @return the field or null if the field could not be found
     */
    @Nullable
    public static Field getField(Class<?> clazz, Class<?> type, String... names) {
        // Validate
        Validate.notEmpty(names, "Names cannot be empty");

        // recursive search if field could not be found. Otherwise return field
        return Arrays.stream(names)
                .map(name -> FieldUtils.getField(clazz, name, true))
                .filter(Objects::nonNull)
                .filter(field -> field.getType().equals(type))
                .findAny()
                .orElse(null);
    }

    /**
     * This method returns the value of the Field in your obj
     * @param field the field which you want to read
     * @param obj the object you want to read
     * @return the value, which you are looking for. null if there were an error
     */
    public static @NotNull Object getValue(Field field, Object obj) throws IllegalAccessException {
        return FieldUtils.readField(field, obj, true);
    }

    /**
     * This method gets a SubClass in a class with a certain name
     * @param overclass the class which contains the class you are searching for
     * @param classname the name of the class you are searching for
     * @return the class you are searching for. Null if the class does not exist
     */
    @Nullable
    public static Class<?> getSubClass(@NotNull Class<?> overclass, @NotNull String classname) {
        return Arrays.stream(overclass.getClasses())
                .filter(underclass -> underclass.getName().equalsIgnoreCase(overclass.getName() + "$" + classname))
                .findAny()
                .orElse(null);
    }

    /**
     * This method returns a class-object from its name
     * @param classpath the name of the class
     * @return the class itself
     */
    public static @NotNull Class<?> getClass(String classpath) throws ClassNotFoundException {
        return ClassUtils.getClass(classpath);
    }

    /**
     * This Method set a value into a Field in an Object
     * @param field the Field
     * @param obj the Object you want to modify
     * @param value the new value of the field
     */
    public static void setValue(@NotNull Field field, @NotNull Object obj, @Nullable Object value) throws IllegalAccessException {
        FieldUtils.writeField(field, obj, value, true);
    }

    /**
     * Returns the method of a certain object due reflections
     * @param clazz the class which has the method. Cannot be null
     * @param name the name of the method. Can neither be null nor empty
     * @param params the parameters of the method
     * @return the method or null if the method could not be found
     */
    public static @NotNull Method getMethod(@NotNull Class<?> clazz, @NotNull String name, Class<?>... params) throws NoSuchMethodException {
        Method method;

        try {
            method = clazz.getMethod(name, params);
        } catch(NoSuchMethodException e) {
            method = clazz.getDeclaredMethod(name, params);
        }

        method.setAccessible(true);
        return method;
    }


    /**
     * This method return an NMS-Class, which has a certain name
     * @param nmsClass the name of the NMS-Class
     * @return the CLass itself. Null if the class cannot be found.
     */
    public static Class<?> getNMSClass(String nmsClass) {
        return loadClass("net.minecraft.server.%s.%s", nmsClass);
    }

    /**
     * This method returns the Array type of an NMS-Class.
     * For example it will return PacketPlayOutNamedEntitySpawn[].class instead of PacketPlayOutNamedEntitySpawn.class
     *
     * For single types, please use {@link Reflections#getNMSClass(String)}
     *
     * @param nmsClass the name of the NMS-Class
     * @return the Array Type of the NMS-Class. Null if the class cannot be found
     */
    public static Class<?> getNMSArrayTypeClass(String nmsClass) {
        return loadClass("[Lnet.minecraft.server.%s.%s;", nmsClass);
    }

    /**
     * This method returns a CraftBukkit-Class
     * @param clazz the Craftbukkit-Class
     * @return the CraftBukkit-Class. Null if the class cannot be found
     */
    public static Class<?> getCraftBukkitClass(String clazz) {
        return loadClass("org.bukkit.craftbukkit.%s.%s", clazz);
    }

    /**
     * This method returns the array type of a certain CraftBukkit-Class
     * For example: it will returns CraftPlayer[].class instead of CraftPlayer.class
     *
     * For single types please use {@link Reflections#getCraftBukkitClass(String)}
     *
     * @param clazz the path of the class after the version package. Split them with an '.'
     * @return the array type of the CraftBukkit-Class. Null if the class cannot be found
     */
    public static Class<?> getCraftBukkitArrayTypeClass(String clazz) {
        return loadClass("[Lorg.bukkit.craftbukkit.%s.%s;", clazz);
    }

    private static Class<?> loadClass(String subpackage, String clazz) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        String name = String.format(subpackage, version, clazz);
        try {
            return ClassUtils.getClass(name);
        } catch (ClassNotFoundException e) {
            Logger.getGlobal().log(Level.WARNING, e, () ->"Could not find Class " + name);
        }
        return null;
    }
}
