package team1;

public class PathNode implements Comparable<PathNode> {
    private Coordinate position;
    private Coordinate parent;
    private int hCost;
    private int gCost;

    public PathNode(Coordinate position, Coordinate parent, Coordinate target) {
        this.position = position;
        this.parent = parent;
        this.hCost = manhattanDistance(position, target);
        this.gCost = manhattanDistance(position, parent);
    }

    private static int manhattanDistance(Coordinate source, Coordinate target) {
        Coordinate delta = source.subtract(target);

        return Math.abs(delta.x) + Math.abs(delta.y);
    }

    public int getHCost() {
        return hCost;
    }

    public int getGCost() {
        return gCost;
    }

    public int getFCost() {
        return gCost + hCost;
    }

    public Coordinate getPosition() {
        return position;
    }

    @Override
    public int compareTo(PathNode p) {
        if (this.getFCost() < p.getFCost()) {
            return 1;
        } else if (this.getFCost() > p.getFCost()) {
            return -1;
        } else {
            return 0;
        }
    }
}
