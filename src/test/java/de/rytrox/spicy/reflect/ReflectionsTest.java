package de.rytrox.spicy.reflect;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ReflectionsTest {

    @Test
    public void shouldCheckAllReflectionNames() {
        Field field = Reflections.getField(Developer.class, "Names", "xyt", "name");
        assertEquals(FieldUtils.getField(Developer.class, "name", true), field);
    }

    @Test
    public void shouldReturnNullWhenFieldCannotBeFound() {
        Field notFound = Reflections.getField(Developer.class, "dsadwa", "sda");
        assertNull(notFound);
    }

    @Test
    public void shouldCheckAllReflectionNamesWithType() {
        Field field = Reflections.getField(Developer.class, String.class, "Names", "xym", "name");
        assertEquals(FieldUtils.getField(Developer.class, "name", true), field);
    }

    @Test
    public void shouldReturnNullWhenFieldCannotBeFoundAndTypeIsCorrect() {
        Field notFound = Reflections.getField(Developer.class, String.class, "sadawer", "sfasdf");
        assertNull(notFound);
    }

    @Test
    public void shouldReturnNullWhenFieldNameIsCorrectButInvalidType() {
        Field field = Reflections.getField(Developer.class, int.class, "name");
        assertNull(field);
    }

    @Test
    public void shouldReadValueInDeclaredField() throws IllegalAccessException {
        Developer developer = new Developer();
        FieldUtils.writeField(developer, "name", "Timeout", true);

        assertEquals("Timeout", Reflections.getSafeValueFromDeclaredField(developer, "name", String.class));
    }

    @Test
    public void shouldReturnNullWhenFieldCannotBeFoundWhileReading() throws IllegalAccessException {
        Developer developer = new Developer();
        FieldUtils.writeField(developer, "name", "Timeout", true);

        assertNull(Reflections.getSafeValueFromDeclaredField(developer, "name123", String.class));
    }

    @Test
    public void shouldReturnNullWhenFieldCannotBeRead() throws IllegalAccessException {
        try(MockedStatic<Reflections> mockedStatic = Mockito.mockStatic(Reflections.class, Mockito.CALLS_REAL_METHODS)) {
            Field mock = Mockito.mock(Field.class);

            Mockito.doNothing().when(mock).setAccessible(Mockito.eq(true));
            Mockito.doThrow(IllegalAccessException.class).when(mock).get(Mockito.any());
            mockedStatic.when(() -> Reflections.getField(Mockito.eq(Developer.class), Mockito.eq(String.class), Mockito.eq("name")))
                    .thenReturn(mock);

            Developer developer = new Developer();
            FieldUtils.writeField(developer, "name", "Timeout", true);

            assertNull(Reflections.getSafeValueFromDeclaredField(developer, "name", String.class));
        }
    }
}
