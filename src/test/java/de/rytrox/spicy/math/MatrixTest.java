package de.rytrox.spicy.math;

import org.bukkit.util.Vector;
import org.junit.Test;

import static org.junit.Assert.*;

public class MatrixTest {

    private final Matrix m1 = new Matrix(new Vector(3, 2, 1), new Vector(4, 5, 2), new Vector(-1, -2, 3));
    private final Matrix E = new Matrix();
    private final Matrix m2 = new Matrix(new Vector(1, 2, 3), new Vector(1, 2, 3), new Vector(1, 2, 3));

    @Test
    public void shouldCopyCorrectly() {
        assertEquals(m1, new Matrix(m1));
        assertEquals(E, new Matrix());
        assertEquals(m2, new Matrix(m2));
    }

    @Test
    public void shouldCalcDet() {
        assertEquals(0, m2.det(), 0);
        assertEquals(1, E.det(), 0);
        assertEquals(26, m1.det(), 0);
    }

    @Test
    public void shouldMultiplyVector() {
        assertEquals(new Vector(1, 2, 3), E.multiply(new Vector(1, 2 ,3)));
        assertEquals(new Vector(3, 6, 9), m2.multiply(new Vector(1, 1, 1)));
    }

    @Test
    public void shouldMultiplyMatrix() {
        assertEquals(new Matrix(
                new Vector(6, 12, 18),
                new Vector(11, 22, 33),
                new Vector(0, 0, 0)
        ), m2.multiply(m1));
        assertEquals(m2, m2.multiply(E));
        assertEquals(new Matrix(
                new Vector(8, 6, 14),
                new Vector(8, 6, 14),
                new Vector(8, 6, 14)), m1.multiply(m2));
    }

    @Test
    public void shouldTransform() {
        assertEquals(new Matrix(), E.transform());
        assertEquals(new Matrix(new Vector(1, 1, 1), new Vector(2, 2, 2), new Vector(3, 3, 3)), m2.transform());
        assertEquals(new Matrix(new Vector(3, 4, -1), new Vector(2, 5, -2), new Vector(1, 2, 3)), m1.transform());
    }

    @Test
    public void shouldMultiplyConstant() {
        assertEquals(new Matrix(new Vector(2, 0, 0), new Vector(0, 2, 0), new Vector(0, 0, 2)), E.multiply(2));
    }

    @Test
    public void shouldInvert() {
        assertEquals(E.invert(), new Matrix());
        assertEquals(new Matrix(
                new Vector(19 / 26D, -4 / 13D, -1 / 26D),
                new Vector(-7 / 13D, 5 / 13D, -1 / 13D),
                new Vector(-3 / 26D, 2 / 13D, 7 / 26D)), m1.invert());

        assertThrows(ArithmeticException.class, m2::invert);
    }
}
