package team1;

/**
 * Represents a path finding node and its costs.
 */
public class PathNode implements Comparable<PathNode> {
    private Coordinate position;
    private PathNode parent;
    private int hCost;
    private int gCost;

    /**
     * PathNode constructor.
     *
     * @param   position        The position of this PathNode
     * @param   parent          This PathNode's parent node
     * @param   target          The position of the path finder's target
     * @param   gCost           The A* gCost for this PathNode
     */
    public PathNode(Coordinate position, PathNode parent,
                            Coordinate target, int gCost) {
        if (gCost < 0) {
            throw new IllegalArgumentException("gCost must be positive");
        }

        this.position = position;
        this.parent = parent;
        this.gCost = gCost;
        this.hCost = manhattanDistance(position, target);
    }

    /**
     * Constructor for the root PathNode.
     *
     * @param   position        The position of this PathNode
     * @param   target          The position of the path finder's target
     */
    public PathNode(Coordinate position, Coordinate target) {
        this.position = position;
        this.parent = null;
        this.hCost = manhattanDistance(position, target);
        this.gCost = 0;
    }

    /**
     * Returns the Manhattan distance between two positions.
     *
     * @param   position1       One of the two Coordinates bounding the distance
     * @param   position2       One of the two Coordinates bounding the distance
     *
     * @return                  The Manhattan distance between twe two
     *                          Coordinates
     *
     */
    public static int manhattanDistance(Coordinate position1,
                                        Coordinate position2) {
        Coordinate delta = position1.subtract(position2);
        return Math.abs(delta.x) + Math.abs(delta.y);
    }

    /**
     * Returns the A* hCost of this PathNode.
     *
     * @return      The value of hCost
     */
    public int getHCost() {
        return hCost;
    }

    /**
     * Returns the A* gCost of this PathNode.
     *
     * @return      The value of gCost
     */
    public int getGCost() {
        return gCost;
    }

    /**
     * Returns the A* fCost of this PathNode.
     *
     * @return      The value of fCost
     */
    public int getFCost() {
        return gCost + hCost;
    }

    /**
     * Returns this PathNode's position.
     *
     * @return      The position Coordinate of this PathNode
     */
    public Coordinate getPosition() {
        return position;
    }

    /**
     * Returns the parent node of this PathNode.
     *
     * @return      The parent PathNode of this PathNode
     */
    public PathNode getParent() {
        return parent;
    }

    /**
     * Compares this PathNode to another one. The smallest fCost takes
     * precedence.
     *
     * @see java.lang.Comparable#compareTo(T);
     */
    @Override
    public int compareTo(PathNode p) {
        if (this.getFCost() > p.getFCost()) {
            return 1;
        } else if (this.getFCost() < p.getFCost()) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * Equality check for PathNodes. PathNodes with the same position are
     * considered equal.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
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

    /**
     * @see java.lang.Object#hashCode()
     */
	@Override
	public int hashCode()
	{
		return this.getPosition().hashCode();
	}
}
