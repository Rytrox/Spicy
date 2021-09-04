package de.timeout.libs.math;

import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.builder.EqualsBuilder;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.builder.HashCodeBuilder;
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
        A[0] = new double[] { x.getX(), y.getX(), z.getX() };
        A[1] = new double[] { x.getY(), y.getY(), z.getY() };
        A[2] = new double[] { x.getZ(), y.getZ(), z.getZ() };
    }

    /**
     * Creates a new unitary 3x3-Matrix.
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Matrix matrix = (Matrix) o;

        return new EqualsBuilder().append(A, matrix.A).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(A)
                .toHashCode();
    }

    @Override
    public String toString() {
        return String.format("%n[ %f, %f, %f ]%n[ %f, %f, %f ]%n[ %f, %f, %f ]",
                A[0][0], A[0][1], A[0][2],
                A[1][0], A[1][1], A[1][2],
                A[2][0], A[2][1], A[2][2]);
    }

    /**
     * Uses the Rule of Sarrus to calculate the determinant of the matrix.
     *
     * @return the determinant of the matrix
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
    @NotNull
    public Vector multiply(@NotNull Vector x) {
        return new Vector(
                A[0][0] * x.getX() + A[0][1] * x.getY() + A[0][2] * x.getZ(),
                A[1][0] * x.getX() + A[1][1] * x.getY() + A[1][2] * x.getZ(),
                A[2][0] * x.getX() + A[2][1] * x.getY() + A[2][2] * x.getZ()
        );
    }

    /**
     * Chaining two matrices together and creates a new matrix.
     *
     * @param B the other matrix
     * @return the new chained matrix C
     */
    @NotNull
    public Matrix multiply(@NotNull Matrix B) {
        Matrix matrix = new Matrix();

        for(int x = 0; x < A.length; x++) {
            for(int y = 0; y < A[0].length; y++) {
                double c = 0;
                for(int k = 0; k < A[0].length; k++) {
                    c += A[x][k] * B.A[k][y];
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
    @NotNull
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
    @NotNull
    public Matrix multiply(double c) {
        Matrix matrix = new Matrix(this);

        for (int x = 0; x < A.length; x++) {
            for (int y = 0; y < A[x].length; y++) {
                matrix.A[x][y] *= c;
            }
        }

        return matrix;
    }

    /**
     * Add two matrices together and returns a new matrix that combines two matrices
     *
     * @param other the other matrix. Cannot be null
     * @return the new combined matrix
     */
    @NotNull
    public Matrix add(@NotNull Matrix other) {
        Matrix copy = new Matrix(this);

        for(int x = 0; x < A.length; x++) {
            for(int y = 0; y < A[x].length; y++) {
                copy.A[x][y] += other.A[x][y];
            }
        }

        return copy;
    }

    /**
     * Subtract two matrices and returns a new matrix that combines two matrices
     *
     * @param other the other matrix. Cannot be null
     * @return the new combined matrix
     */
    @NotNull
    public Matrix subtract(@NotNull Matrix other) {
        Matrix copy = new Matrix(this);

        for(int x = 0; x < A.length; x++) {
            for(int y = 0; y < A[x].length; y++) {
                copy.A[x][y] -= other.A[x][y];
            }
        }

        return copy;
    }

    /**
     * Transform (rotates) a matrix and returns a new matrix
     *
     * @return the new transformed matrix
     */
    @NotNull
    public Matrix transform() {
        Matrix copy = new Matrix(this);

        // rotate matrix (ignore diagonal axis)
        for(int x = 0; x < A.length; x++) {
            // increase speed to O(n * log(n))
            for(int y = A[x].length - 1; y > x; y--) {
                // cache value
                double cache = copy.A[x][y];

                copy.A[x][y] = copy.A[y][x];
                copy.A[y][x] = cache;
            }

        }

        return copy;
    }

    /**
     * Checks if the matrix is orthogonal
     *
     * @return true if this matrix is orthogonal, false otherwise
     */
    public boolean isOrthogonal() {
        // E = A * A^T = A * A^(-1)
        return this.multiply(this.transform()).equals(new Matrix());
    }

    /**
     * Inverts a Matrix.
     *
     * @return the inverted matrix
     */
    public Matrix invert() {
        // transform when Matrix is orthogonal
        if(isOrthogonal())
            return transform();

        double det = det();
        if(det == 0)
            throw new ArithmeticException("Matrix does not have an inverse matrix, determinante is 0");

        Matrix matrix = new Matrix();

        for(int x = 0; x < 3; x++) {
            for(int y = 0; y < 3; y++)
                matrix.A[x][y] = (((A[(y + 1) % 3][(x + 1) % 3] * A[(y + 2) % 3][(x + 2) % 3])
                        - (A[(y + 1) % 3][(x +2 ) % 3] * A[(y + 2) % 3][(x + 1) % 3])) / det);
        }

        return matrix;
    }
}
