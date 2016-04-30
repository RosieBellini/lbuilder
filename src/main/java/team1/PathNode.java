package team1;

public class PathNode implements Comparable<PathNode> {
    private Coordinate position;
    private PathNode parent;
    private Coordinate target;
    private int hCost;
    private int gCost;

    public PathNode(Coordinate position, PathNode parent, Coordinate target) {
        this.position = position;
        this.parent = parent;
        this.target = target;
        updateCosts();
    }

    public PathNode(Coordinate position, Coordinate target) {
        this.position = position;
        this.parent = null;
        this.hCost = manhattanDistance(position, target);
        this.gCost = 0;
    }

    public void updateCosts() {
        hCost = manhattanDistance(position, target);
        gCost = manhattanDistance(position, parent.getPosition());
    }

    public void changeParent(PathNode parent) {
        this.parent = parent;
    }

    public static int manhattanDistance(Coordinate source, Coordinate target) {
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

    public PathNode getParent() {
        return parent;
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

    @Override
    public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

        if (!(obj instanceof PathNode)) {
            return false;
        }

        int x = ((PathNode) obj).getPosition().x;
        int y = ((PathNode) obj).getPosition().y;

		if (x == position.x && y == position.y) {
			return true;
		} else {
			return false;
		}
    }

	@Override
	public int hashCode()
	{
		return this.getPosition().hashCode();
	}
}
