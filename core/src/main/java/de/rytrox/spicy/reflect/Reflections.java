package de.rytrox.spicy.reflect;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.reflect.FieldUtils;
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
    public static Field getField(@NotNull Class<?> clazz, @NotNull String... names) {
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
    public static Field getField(@NotNull Class<?> clazz, @NotNull Class<?> type, @NotNull String... names) {
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
     * This method reads a value from a declared field in a class.
     * If the field cannot be found it returns null.
     *
     * @param value The holder of the field
     * @param name the name of the field
     * @param type the type of the field
     * @param <T> the type of the field
     * @return the found field or null if the field does not exist
     */
    @Nullable
    public static <T> T getSafeValueFromDeclaredField(@NotNull Object value, @NotNull String name, @NotNull Class<T> type) {
        Field field = getField(value.getClass(), type, name);

        if(field != null) {
            field.setAccessible(true);

            try {
                return (T) field.get(value);
            } catch (IllegalAccessException e) {
                Logger.getGlobal().log(Level.WARNING, e, () -> "Could not access field " + name);
            }
        }

        return null;
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
}
