package team1;

import java.util.ArrayList;
import java.util.Arrays;

public class Coordinate implements Comparable<Coordinate>{
    public final int x;
    public final int y;

	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Coordinate(Coordinate coordToCopy) {
		this.x = coordToCopy.x;
		this.y = coordToCopy.y;
	}

	public String toString() {
		if (x == 1 && y == 0) {
			return "RIGHT";
		} else if(x == -1 && y == 0) {
			return "LEFT";
		} else if(x == 0 && y == 1) {
			return "DOWN";
		} else if(x == 0 && y == -1) {
			return "UP";
		} else {
            return(x + "," + y);
        }
	}

	public Coordinate add(Coordinate coord) {
		int x = this.x + coord.x;
		int y = this.y + coord.y;
		return new Coordinate(x, y);
	}

    public Coordinate subtract(Coordinate coord) {
        int x = this.x - coord.x;
        int y = this.y - coord.y;
		return new Coordinate(x, y);
    }

	public Coordinate mult(int multiplier) {
		int x = this.x * multiplier;
		int y = this.y * multiplier;
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

        Coordinate position;
        if (obj instanceof PathNode) {
            position = ((PathNode) obj).getPosition();
        } else if (obj instanceof Coordinate) {
            position = (Coordinate) obj;
        } else {
            return false;
        }

        int x = this.x;
        int y = this.y;


		if (x == position.x && y == position.y) {
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
