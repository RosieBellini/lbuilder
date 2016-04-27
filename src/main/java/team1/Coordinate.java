package team1;

import java.util.ArrayList;
import java.util.Arrays;

public class Coordinate implements Comparable<Coordinate>{
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
    public final int x;
    public final int y;

	public Coordinate(Coordinate coordToCopy) {
		this.x = coordToCopy.getX();
		this.y = coordToCopy.getY();
	}

	public String toString() {
		if (x == 1 && y == 0) {
			return "Right";
		} else if(x == -1 && y == 0) {
			return "Left";
		} else if(x == 0 && y == 1) {
			return "Down";
		} else if(x == 0 && y == -1) {
			return "Up";
		} else {
            return(x + "," + y);
        }
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

	public static ArrayList<Coordinate> allValidCoordinates(int xSize, int ySize) {
		ArrayList<Coordinate> allValidCoordinates = new ArrayList<Coordinate>();
		for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				allValidCoordinates.add(new Coordinate(x, y));
			}
		}
		return allValidCoordinates;
	}

	public boolean inRange(int xLowerBound, int yLowerBound, int xUpperBound, int yUpperBound) {
		return this.x >= xLowerBound && this.x <= xUpperBound
				&& this.y >= yLowerBound && this.y <= yUpperBound;
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

	@Override
	public int compareTo(Coordinate coord) {
	    return (x - coord.x) * 20 + (y - coord.y);
	}
}
