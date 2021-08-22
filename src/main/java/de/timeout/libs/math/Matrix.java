package de.timeout.libs.math;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Mathematical implementation of a 3x3-Matrix that solves the equation: A * x = b where x, b are {@link Vector}. <br>
 * <br>
 * Sometimes it's more performant to solve linear equations than calculating angles. <br>
 * For the usage of Matrices see Linear Algebra II.
 */
public class Matrix {

    private final double[][] A = new double[3][3];

    /**
     * Creates a new 3x3-Matrix based on the given axes.
     *
     * @param x the x-axis
     * @param y the y-axis
     * @param z the z-axis
     */
    public Matrix(@NotNull Vector x, @NotNull Vector y, @NotNull Vector z) {
        A[0] = new double[] { x.getX(), x.getY(), x.getZ() };
        A[1] = new double[] { y.getX(), y.getY(), y.getZ() };
        A[2] = new double[] { z.getX(), z.getY(), z.getZ() };
    }

    /**
     * Creates a new elementary 3x3-Matrix.
     */
    public Matrix() {
        A[0] = new double[] { 1D, 0D, 0D };
        A[1] = new double[] { 0D, 1D, 0D };
        A[2] = new double[] { 0D, 0D, 1D };
    }

    /**
     * Copy constructor for matrices
     *
     * @param copy the copy matrix
     */
    public Matrix(@NotNull Matrix copy) {
        for (int x = 0; x < 3; x++) {
            System.arraycopy(copy.A[x], 0, A[x], 0, 3);
        }
    }

    /**
     * Uses the Rule of Sarrus to calculate the determinate of the matrix.
     *
     * @return the determinate of the matrix
     */
    public double det() {
        return (A[0][0] * A[1][1] * A[2][2]) + (A[1][0] * A[2][1] * A[0][2]) + (A[2][0] * A[0][1] * A[1][2]) -
                (A[2][0] * A[1][1] * A[0][2]) - (A[0][0] * A[2][1] * A[1][2]) - (A[1][0] * A[0][1] * A[2][2]);
    }

    /**
     * Solves the linear equation A * x = b. <br>
     * Returns a new Vector called b
     *
     * @param x the Vector x
     * @return the new Vector b
     */
    public Vector multiply(@NotNull Vector x) {
        return new Vector(
                A[0][0] * x.getX() + A[1][0] * x.getY() + A[2][0] * x.getZ(),
                A[0][1] * x.getX() + A[1][1] * x.getY() + A[2][1] * x.getZ(),
                A[0][2] * x.getX() + A[1][2] * x.getY() + A[2][2] * x.getZ()
        );
    }

    /**
     * Chaining two matrices together and creates a new matrix.
     *
     * @param B the other matrix
     * @return the new chained matrix C
     */
    public Matrix multiply(@NotNull Matrix B) {
        Matrix matrix = new Matrix();

        for(int x = 0; x < A.length; x++) {
            for(int y = 0; y < A[0].length; y++) {
                double c = 0;
                for(int k = 0; k < A[0].length; k++) {
                    c += A[k][x] * B.A[y][k];
                }

                matrix.A[x][y] = c;
            }
        }

        return matrix;
    }

    /**
     * Solves the equation A * x = b, where x and b are Locations
     *
     * @param x the Location x
     * @return the new Location b
     */
    public Location multiply(@NotNull Location x) {
        return this.multiply(x.toVector())
                .toLocation(Objects.requireNonNull(x.getWorld()));
    }

    /**
     * Multiply a matrix with a constant and create a new matrix
     *
     * @param c the constant
     * @return the new matrix matching the equation: c * A = B
     */
    public Matrix multiply(double c) {
        Matrix matrix = new Matrix(this);

        for (int x = 0; x < A.length; x++) {
            for (int y = 0; y < A[x].length; y++) {
                matrix.A[x][y] *= c;
            }
        }

        return matrix;
    }
}
