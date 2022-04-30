package de.rytrox.spicy.skin.mineskin;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GenerateOptionsTest {

    @Test
    public void shouldHaveDefaultValues() {
        GenerateOptions options = new GenerateOptions();

        assertEquals("", options.getName());
        assertEquals(GenerateOptions.Visibility.PRIVATE.getType(), options.getVisibility());
        assertEquals(GenerateOptions.Variant.CLASSIC.getType(), options.getVariant());
    }

    @Test
    public void shouldSetVariant() {
        GenerateOptions options = new GenerateOptions();

        options.setVariant(GenerateOptions.Variant.SLIM);

        assertEquals(GenerateOptions.Variant.SLIM.getType(), options.getVariant());
    }

    @Test
    public void shouldSetVisibility() {
        GenerateOptions options = new GenerateOptions();

        options.setVisibility(GenerateOptions.Visibility.PUBLIC);

        assertEquals(GenerateOptions.Visibility.PUBLIC.getType(), options.getVisibility());
    }

    @Test
    public void shouldSetName() {
        GenerateOptions options = new GenerateOptions();

        options.setName("New Name");

        assertEquals("New Name", options.getName());
    }
}
