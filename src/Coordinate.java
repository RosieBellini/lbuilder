import java.util.*;

public class Coordinate {
    private final int x;
    private final int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Coordinate add(Coordinate coord) {
        int x = this.x + coord.getX();
        int y = this.y + coord.getY();
        return new Coordinate(x, y);
    }

    public Coordinate mult(int multiplier) {
        int x = this.x * 2;
        int y = this.y * 2;
        return new Coordinate(x, y);
    }

    public Coordinate reverse() {
        int x = this.x * -1;
        int y = this.y * -1;
        return new Coordinate(x, y);
    }

    public static Set<Coordinate> allValidCoordinates(int xSize, int ySize) {
        Set<Coordinate> allValidCoordinates = new HashSet<Coordinate>();
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                allValidCoordinates.add(new Coordinate(x, y));
            }
        }
        return allValidCoordinates;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Coordinate)) {
            return false;
        }
        Coordinate pos = (Coordinate) obj;
        if (this.x == pos.getX() && this.y == pos.getY()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(new Object[]{new Integer(x), new Integer(y)});
    }
}
