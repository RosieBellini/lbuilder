import java.util.*;

/**
 * Represents a Sokoban level. "Static" objects (spaces, walls, goals) are
 * represented in an array of SokobanObjects whereas "dynamic" objects (boxes
 * and the player) are represented in a FixedSizeStack of SaveState objects.
 * This separation reduces the amount of information we need to store for the
 * undo command and solver.
 *
 * TODO: make the array of static objects immutable
 */
public class MapContainer {
	private FixedSizeStack<SaveState> history;
	private Stack<SaveState> redoStack;
	private SokobanObject[][] map;
	private final int MAXUNDOS = 20;
    private int prevRedoStackSize;

	/**
	 * Initialises a MapContainer of the given size filled with spaces
	 */
	public MapContainer(int xSize, int ySize) {
		map = new SokobanObject[ySize][xSize];
		for (SokobanObject[] row : map) {
			Arrays.fill(row, SokobanObject.SPACE);
		}
		history = new FixedSizeStack<SaveState>(MAXUNDOS);
		history.push(new SaveState(new Coordinate(-1, -1), new HashSet<Coordinate>()));
		redoStack = new Stack<SaveState>();
        prevRedoStackSize = 0;
	}

	public int getYSize() {
		return map.length;
	}

	public int getXSize() {
		return map[0].length;
	}

	public void storeState() {
		history.push(new SaveState(getWPos(), getBoxPositions()));
	}

	/**
	 * Pops a SaveState out of the history stack. If the stack is then empty,
	 * the SaveState is put back in place as we require that there is always one
	 * state to represent the positions of dynamic objects.
	 *
	 * @param sendToRedoStack   whether or not to place the popped SaveState
	 *                          into the redo stack e.g. when the user inputs a
	 *                          command
	 */
	public void undo(boolean sendToRedoStack) {
		SaveState state = history.pop();
		if (historyLength() == 0) {
			history.push(state);
		} else if (sendToRedoStack) {
            prevRedoStackSize = redoStack.size();
			redoStack.push(state);
		}
	}

	/**
	 * Puts an undone state back
	 */
	public void redo() {
		if (redoStack.size() != 0) {
            prevRedoStackSize = redoStack.size();
			history.push(redoStack.pop());
		}
	}

	public void clearRedoStack() {
		redoStack.clear();
	}

	public int historyLength() {
		return history.size();
	}

	public int totalHistoryLength() {
		return history.getTotalSize();
	}

	/**
	 * Checks the positions of all boxes to see if they've been placed on a
	 * goal
	 * TODO: Fix this.  The player should win if all goals have boxes on them. (Not vice versa like now.)
	 * @return  true if all boxes are on a goal, false otherwise
	 */
	public boolean isDone() {
		for (Coordinate coord : getBoxPositions()) {
			if (get(coord) != SokobanObject.BOX_ON_GOAL) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Get the position of the worker
	 *
	 * @return  coordinates of the worker
	 */
	public Coordinate getWPos() {
		return history.peek().getWPos();
	}

	public Set<Coordinate> getBoxPositions() {
		return history.peek().getBoxPositions();
	}

	/**
	 * Put a SokobanObject in the given position. See SokobanObject
	 * documentation for an explanation of how objects are stored. Respects the
	 * rules of the game e.g. can't place a PLAYER on top of a WALL.
	 *
	 * @param object    the object to be stored
	 * @param coord     the coordinate at which to place it
	 * @return          true if successful, false otherwise
	 */
	public boolean put(SokobanObject object, Coordinate coord) {
		Set<Coordinate> boxPositions = getBoxPositions();
		Coordinate wPos = getWPos();
		SokobanObject target = get(coord);
		int x = coord.getX();
		int y = coord.getY();

		if (object == SokobanObject.PLAYER || object == SokobanObject.PLAYER_ON_GOAL) {
			switch(target) {
			case SPACE:
			case GOAL:      wPos = coord;
			break;
			default:        return false;
			}
			if (object == SokobanObject.PLAYER_ON_GOAL) {
				map[y][x] = SokobanObject.GOAL;
			}

		} else if (object == SokobanObject.BOX || object == SokobanObject.BOX_ON_GOAL) {
			switch(target) {
			case SPACE:
			case GOAL:  if (!boxHere(coord)) {
				boxPositions.add(coord);
			}
			break;
			default:    return false;
			}
			if (object == SokobanObject.BOX_ON_GOAL) {
				map[y][x] = SokobanObject.GOAL;
			}

		} else if (object == SokobanObject.WALL) {
			if (wPos.equals(coord) || boxHere(coord)) {
				return false;
			}
			map[y][x] = SokobanObject.WALL;

		} else {
			map[y][x] = object;
		}

		history.pop();
		history.push(new SaveState(wPos, boxPositions));
		return true;
	}

	public boolean boxHere(Coordinate coord) {
		return getBoxPositions().contains(coord);
	}

	/**
	 * Removes a "layer" from the given coordinate. If there is a BOX or PLAYER
	 * on a SPACE or GOAL, remove it; otherwise, make the coordinate a SPACE.
	 *
	 * @param coord     the coordinate to remove a layer from
	 */
	public void removeLayer(Coordinate coord) {
		Set<Coordinate> boxPositions = getBoxPositions();
		Coordinate wPos = getWPos();
		if (wPos.equals(coord)) {
			wPos = new Coordinate(-1, -1);
		} else if (boxHere(coord)) {
			boxPositions.remove(coord);
		} else {
			put(SokobanObject.SPACE, coord);
		}
		history.pop();
		history.push(new SaveState(wPos, boxPositions));
	}

	/**
	 * Get the type of SokobanObject at the given coordinate. Again, see the
	 * SokobanObject documentation for an explanation of how objects are stored
	 * and returned.
	 */
	public SokobanObject get(Coordinate coord) {
		int x = coord.getX();
		int y = coord.getY();
		if (x > getXSize() - 1 || y > getYSize() - 1 || x < 0 || y < 0) {
			return SokobanObject.WALL;
		} else if (boxHere(coord)) {
			if (map[y][x] == SokobanObject.GOAL) {
				return SokobanObject.BOX_ON_GOAL;
			}
			return SokobanObject.BOX;
		} else if (getWPos().equals(coord)) {
			if (map[y][x] == SokobanObject.GOAL) {
				return SokobanObject.PLAYER_ON_GOAL;
			}
			return SokobanObject.PLAYER;
		}
		return map[y][x];
	}

	public String toString() {
		String mapLine = "";
		for (int y = 0; y < getYSize(); y++) {
			for (int x = 0; x < getXSize(); x++) {
				Coordinate coord = new Coordinate(x, y);
				mapLine = mapLine + get(coord).toString();
			}
			mapLine = mapLine + "\n";

		}
		return mapLine;
	}

	public Set<Coordinate> getChanges() {
		Set<Coordinate> changedPlaces = new HashSet<Coordinate>();
		SaveState[] stateArray = new SaveState[MAXUNDOS];
		history.toArray(stateArray);
		if (history.size() < 2 && redoStack.size() == 0) {
			return changedPlaces;
		}

        SaveState lastState;
        if (prevRedoStackSize < redoStack.size()) {
            lastState = redoStack.peek();
        } else {
            lastState = stateArray[history.size() - 2];
        }

		if (!lastState.getWPos().equals(getWPos())) {
			changedPlaces.add(getWPos());
			changedPlaces.add(lastState.getWPos());
		}

		Set<Coordinate> currentBoxPositionsCopy = new HashSet<Coordinate>(getBoxPositions());
		Set<Coordinate> lastBoxPositionsCopy = new HashSet<Coordinate>(lastState.getBoxPositions());
		currentBoxPositionsCopy.removeAll(lastState.getBoxPositions());
		lastBoxPositionsCopy.removeAll(getBoxPositions());
		changedPlaces.addAll(currentBoxPositionsCopy);
		changedPlaces.addAll(lastBoxPositionsCopy);
		System.out.println(changedPlaces);
		return changedPlaces;
	}

	/**
	 * Takes a coordinate and returns the set of coordinates which are accessible from there.
	 * Accessible meaning not blocked by a WALL.
	 */
	public Set<Coordinate> accessibleFrom(Coordinate origin) {
		Set<Coordinate> edges = new HashSet<Coordinate>();
		Set<Coordinate> accessible = new HashSet<Coordinate>();
		Set<Coordinate> newEdges = new HashSet<Coordinate>();
		edges.add(origin);
		while (edges.size() != 0) {
			for (Coordinate edge: edges) {
				accessible.add(edge);
				for (Coordinate potentialEdge : neighbors(edge)) {
					if (get(potentialEdge) != SokobanObject.WALL) {
                        newEdges.add(potentialEdge);
                    }
				}
			}
			edges.addAll(newEdges);
			edges.removeAll(accessible);
		}
		return accessible;
	}

	/**
	 * Takes a coordinate and returns its orthogonal neighbors (avoiding out of bounds areas).
	 */
	public Set<Coordinate> neighbors(Coordinate origin){
		Set<Coordinate> neighbors = new HashSet<Coordinate>();
		neighbors.add(origin.add(new Coordinate(1,0)));
		neighbors.add(origin.add(new Coordinate(-1,0)));
		neighbors.add(origin.add(new Coordinate(0,1)));
		neighbors.add(origin.add(new Coordinate(0,-1)));
		for(Coordinate potentialNeighbor : neighbors){
			if (potentialNeighbor.getX()<0||potentialNeighbor.getX()>this.getXSize()||
					potentialNeighbor.getY()<0||potentialNeighbor.getY()>this.getYSize()){
				neighbors.remove(potentialNeighbor);
			}
		}
		return neighbors;
	}

	public void growGrass(){
		Set<Coordinate> potentialGrass = Coordinate.allValidCoordinates(getXSize(), getYSize());
		potentialGrass.removeAll(accessibleFrom(getWPos()));
		for(Coordinate potentialGrassSpace : potentialGrass){
			if (get(potentialGrassSpace) == SokobanObject.SPACE) {
                put(SokobanObject.GRASS, potentialGrassSpace);
            }
		}
	}
}
