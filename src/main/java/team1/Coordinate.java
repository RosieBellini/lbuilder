package team1;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a position in 2D space.
 */
public class Coordinate implements Comparable<Coordinate> {
    public final int x;
    public final int y;

    /**
     * Coordinate constructor.
     *
     * @param   x     The X position of this Coordinate
     * @param   y     The Y position of this Coordinate
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructor for a Coordinate clone.
     *
     * @param   coordToCopy       The Coordinate to clone
     */
    public Coordinate(Coordinate coordToCopy) {
        this.x = coordToCopy.x;
        this.y = coordToCopy.y;
    }

    /**
     * Returns a String representation of this Coordinate.
     *
     * @return      LEFT, RIGHT, UP or DOWN if the Coordinate corresponds to a
     *              unitary motion in one of these directions, otherwise
     *              the X and Y positions of the Coordinate separated by a
     *              comma.
     */
    public String toString() {
        if (x == 1 && y == 0) {
            return "RIGHT";
        } else if (x == -1 && y == 0) {
            return "LEFT";
        } else if (x == 0 && y == 1) {
            return "DOWN";
        } else if (x == 0 && y == -1) {
            return "UP";
        } else {
            return (x + "," + y);
        }
    }

    /**
     * Adds another Coordinate to this one.
     *
     * @param   coord       The Coordinate to add
     *
     * @return              The result, as a new Coordinate
     */
    public Coordinate add(Coordinate coord) {
        int x = this.x + coord.x;
        int y = this.y + coord.y;
        return new Coordinate(x, y);
    }

    /**
     * Subtracts another Coordinate from this one.
     *
     * @param   coord       The Coordinate to subtract
     *
     * @return              The result, as a new Coordinate
     */
    public Coordinate subtract(Coordinate coord) {
        int x = this.x - coord.x;
        int y = this.y - coord.y;
        return new Coordinate(x, y);
    }

    /**
     * Multiplies both components of this Coordinate by a given factor.
     *
     * @param   multiplier  The factor by which to multiply
     *
     * @return              The result, as a new Coordinate
     */
    public Coordinate mult(int multiplier) {
        int x = this.x * multiplier;
        int y = this.y * multiplier;
        return new Coordinate(x, y);
    }

    /**
     * Multiplies both components of this Coordinate by -1.
     *
     * @return      The result, as a new Coordinate
     */
    public Coordinate reverse() {
        return this.mult(-1);
    }

    /**
     * Returns an ArrayList of all valid Coordinates for a map of the given
     * size. The Coordinates are added in order of increasing X, then increasing
     * Y. Allows the positive quadrant only.
     *
     * @param   xSize       The X dimension of the map
     * @param   ySize       The Y dimension of the map
     *
     * @return              An ArrayList of all the Coordinates in the map
     *                      bound by the parameters.
     */
    public static ArrayList<Coordinate> allValidCoordinates(
                                            int xSize, int ySize) {
        ArrayList<Coordinate> allValidCoordinates = new ArrayList<Coordinate>();

        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                allValidCoordinates.add(new Coordinate(x, y));
            }
        }

        return allValidCoordinates;
    }

    /**
     * Tests if this Coordinate falls within the given range.
     *
     * @param   xLowerBound     The lower bound of the X range
     * @param   xUpperBound     The upper bound of the X range
     * @param   yLowerBound     The lower bound of the Y range
     * @param   yUpperBound     The upper bound of the Y range
     *
     * @return                  True if the Coordinate is in the given range,
     *                          false otherwise
     */
    public boolean inRange(int xLowerBound, int yLowerBound,
                            int xUpperBound, int yUpperBound) {
        return this.x >= xLowerBound && this.x <= xUpperBound
            && this.y >= yLowerBound && this.y <= yUpperBound;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Coordinate)) {
            return false;
        }
        Coordinate pos = (Coordinate) obj;
        if (this.x == pos.x && this.y == pos.y) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @see java.lang.Object#hashCode
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] { new Integer(x), new Integer(y) });
    }

    /**
     * Compares this Coordinate to another one. The difference in the X
     * dimension takes precedence.
     *
     * @see java.lang.Comparable#compareTo
     */
    @Override
    public int compareTo(Coordinate coord) {
        return (x - coord.x) * 20 + (y - coord.y);
    }
}
