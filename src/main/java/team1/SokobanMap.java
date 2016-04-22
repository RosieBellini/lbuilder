package team1;

import java.awt.Toolkit;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

/**
 * Represents a Sokoban level. "Static" objects (spaces, walls, goals) are
 * represented in an array of SokobanObjects whereas "dynamic" objects (boxes
 * and the player) are represented in a FixedSizeStack of SaveState objects.
 * This separation reduces the amount of information we need to store for the
 * undo command and solver.
 *
 * TODO: make the array of static objects immutable
 */
public class SokobanMap {
    private FixedSizeStack<SaveState> history;
    private Stack<SaveState> redoStack;
    private int prevRedoStackSize;
    private int xSize;
    private int ySize;
    private int maxUndos;
    private SaveState initialState;

    /**
     * Initialises a MapContainer of the given size filled with spaces
     */
    public SokobanMap(int xSize, int ySize, int maxUndos) {
        this.maxUndos = maxUndos;
        this.xSize = xSize;
        this.ySize = ySize;
        history = new FixedSizeStack<SaveState>(maxUndos);
        history.push(new SaveState(new Coordinate(-1, -1), new HashSet<Coordinate>(), new HashSet<Coordinate>(), new HashSet<Coordinate>()));
        initialState = new SaveState(new Coordinate(-1, -1), new HashSet<Coordinate>(), new HashSet<Coordinate>(), new HashSet<Coordinate>());
        redoStack = new Stack<SaveState>();
        prevRedoStackSize = 0;
    }

    public SokobanMap(SokobanMap mapToCopy) {
        this.maxUndos = mapToCopy.getMaxUndos();
        this.xSize = mapToCopy.getXSize();
        this.ySize = mapToCopy.getYSize();
        history = new FixedSizeStack<SaveState>(maxUndos);
        SaveState state = mapToCopy.getInitialState();
        history.push(new SaveState(state));
        initialState = new SaveState(state);
        redoStack = new Stack<SaveState>();
        prevRedoStackSize = 0;
    }

    public int getYSize() {
        return ySize;
    }

    public int getXSize() {
        return xSize;
    }

    public int getMaxUndos() {
        return maxUndos;
    }

    public SaveState getInitialState() {
        return initialState;
    }

    public void storeState() {
        history.push(new SaveState(getMyState()));
    }

    /**
     * Returns a SaveState object that contains the upper leftmost accessible square and the box positions.
     * This will be used to determine a state of the map independent of the players exact position.
     * @return a SaveState which represents a state of the game for the solving algorithm to use.
     */
    public SaveState getState(){
        Set<Coordinate> accessibleSpaces = accessibleSpaces(getMyState().getWPos(),true);
        Coordinate potentialTopLeftSpace = new Coordinate(xSize,ySize);
        for (Coordinate potential : Coordinate.allValidCoordinates(xSize, ySize)) {
            potentialTopLeftSpace = potential;
            if (accessibleSpaces.contains(potentialTopLeftSpace)){
                break;
            }
        }
        //		System.out.println(potentialTopLeftSpace.getX()+""+potentialTopLeftSpace.getY());
        return new SaveState(potentialTopLeftSpace, getMyState().getBoxPositions());
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
    public void undo() {
        SaveState state = history.pop();
        if (historyLength() == 0) {
            history.push(state);
        } else {
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

    public void reset() {
        history.reset(initialState);
        clearRedoStack();
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
     * @return  true if all boxes are on a goal, false otherwise
     */
    public boolean isDone() {
        for (Coordinate coord : getMyState().getGoalPositions()) {
            if (get(coord) != SokobanObject.BOX_ON_GOAL) {
                return false;
            }
        }
        return true;
    }

    public SaveState getMyState() {
        return history.peek();
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
        return getMyState().put(object, coord);
    }

    /**
     * Removes a "layer" from the given coordinate. If there is a BOX or PLAYER
     * on a SPACE or GOAL, remove it; otherwise, make the coordinate a SPACE.
     *
     * @param coord     the coordinate to remove a layer from
     */
    public void removeLayer(Coordinate coord) {
        getMyState().removeLayer(coord);
    }

    /**
     * Get the type of SokobanObject at the given coordinate. Again, see the
     * SokobanObject documentation for an explanation of how objects are stored
     * and returned.
     */
    public SokobanObject get(Coordinate coord) {
        return getMyState().get(coord);
    }

    public String toString() {
        String mapLine = "";
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                Coordinate coord = new Coordinate(x, y);
                mapLine = mapLine + get(coord).toString();
            }
            mapLine = mapLine + "\n";

        }
        return mapLine;
    }

    public Set<Coordinate> getChanges() {
        Set<Coordinate> changedPlaces = new HashSet<Coordinate>();
        SaveState[] stateArray = new SaveState[maxUndos];
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

        return getMyState().compareStates(lastState);
    }

    /**
     * Takes a coordinate and returns the set of coordinates which are accessible from there.
     * Accessible meaning not blocked by a WALL.
     * @param origin The coordinate space from which to start the search for accessible spaces.
     * @param doBoxesBlock true if you want to ignore boxes in the search.
     */
    public Set<Coordinate> accessibleSpaces(Coordinate origin,boolean ignoreBoxes) {
        Set<Coordinate> edges = new HashSet<Coordinate>();
        Set<Coordinate> accessible = new HashSet<Coordinate>();
        Set<Coordinate> newEdges = new HashSet<Coordinate>();
        edges.add(origin);
        while (edges.size() != 0) {
            for (Coordinate edge: edges) {
                accessible.add(edge);
                for (Coordinate potentialEdge : neighbors(edge)) {
                    SokobanObject objectInPotentialEdge=get(potentialEdge);
                    if (ignoreBoxes){
                        if (objectInPotentialEdge != SokobanObject.WALL) {
                            newEdges.add(potentialEdge);
                        }
                    }
                    if(!ignoreBoxes){
                        if (objectInPotentialEdge != SokobanObject.WALL && objectInPotentialEdge != SokobanObject.BOX) {
                        }
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
        Set<Coordinate> potentialNeighbors = new HashSet<Coordinate>();
        Set<Coordinate> neighbors = new HashSet<Coordinate>();
        potentialNeighbors.add(origin.add(new Coordinate(1,0)));
        potentialNeighbors.add(origin.add(new Coordinate(-1,0)));
        potentialNeighbors.add(origin.add(new Coordinate(0,1)));
        potentialNeighbors.add(origin.add(new Coordinate(0,-1)));
        for(Coordinate potentialNeighbor : potentialNeighbors){
            if (potentialNeighbor.inRange(0, 0, xSize, ySize)) {
                neighbors.add(potentialNeighbor);
            }
        }
        return neighbors;
    }

    public Set<Coordinate> growGrass() {
        ArrayList<Coordinate> potentialGrass = Coordinate.allValidCoordinates(xSize, ySize);
        Set<Coordinate> grassPositions = new HashSet<Coordinate>();
        potentialGrass.removeAll(accessibleSpaces(getMyState().getWPos(),true));
        for(Coordinate potentialGrassSpace : potentialGrass){
            if (get(potentialGrassSpace) == SokobanObject.SPACE) {
                grassPositions.add(potentialGrassSpace);
            }
        }
        return grassPositions;
    }

	/**
	 * Interprets the contents of the "level" file and stores it as a SokobanMap
	 */
	public static SokobanMap importLevel(InputStream levelFile) {
		// if (levelFile == null) { //If Getfile is cancelled.
		// 	return;
		// }
		int x = 0;
		int y = 0;
		int xSize = 0;
		int ySize = 0;
		/*
		 * First, get the raw data as an array of strings and use this to
		 * determine the size of the level
		 */
		ArrayList<String> levelLines = new ArrayList<String>();
		Scanner level = new Scanner(levelFile);
		while (level.hasNextLine()) {
			String line = level.nextLine();
			if (line.length() > xSize) {
				xSize = line.length();
			}
			levelLines.add(line);
		}
		ySize = levelLines.size();
		SokobanMap map = new SokobanMap(xSize, ySize, 20);

		/*
		 * Then convert the raw data into a SokobanMap using the static
		 * method charToSokobanObject from the SokobanObject class
		 */
		for (String line: levelLines) {
			for (char ch: line.toCharArray()) {
				Coordinate coord = new Coordinate(x, y);
				SokobanObject object = SokobanObject.charToSokobanObject(ch);
				map.put(object, coord);
				x++;
			}
			x = 0;
			y++;
		}
		level.close();
		map.initialState = map.getMyState();
        return map;
	}

    /**
     * Moves the object at a given position in the direction specified,
     * respecting the rules of the game.
     *
     * @param iCoord        position of the object to move
     * @param direction     direction in which to move it
     * @return  true if the object was moved, false if this would violate the
     *          rules of the game
     */
    public boolean teleport(Coordinate iCoord, Coordinate direction) {
        Coordinate fCoord = iCoord.add(direction);
        SokobanObject source = get(iCoord);
        if (source != SokobanObject.WALL && source != SokobanObject.GOAL) {
            if (put(SokobanObject.getTopLayer(source), fCoord)) {
                if (source != SokobanObject.PLAYER_ON_GOAL) {
                    removeLayer(iCoord);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Moves the player in the given direction, respecting the rules of the
     * game and pushing boxes when necessary.
     *
     * TODO:    make sure this actually works when the magnitude of the direction
     *          is more than 1
     *
     * @param direction     the direction in which to move the player
     * @return true if the move is permitted, false otherwise
     */
    public boolean move(Coordinate direction) {
        Coordinate wCoord = getMyState().getWPos();
        Coordinate nCoord = wCoord.add(direction);
        storeState();

        /*
         * Try to move the player in the specified direction; if this fails,
         * try to move the object in front of the player in the specified
         * direction; if this succeeds, the player can be moved to the
         * coordinates that the user specified.
         */
        if (!teleport(wCoord, direction)) {
            if (teleport(nCoord, direction)) {
                teleport(wCoord, direction);
            } else {
                /*
                 * otherwise, undo the last move without sticking it in the redo stack
                 * to avoid the undo stack getting filled up with identical states
                 */
                history.pop();
                Toolkit.getDefaultToolkit().beep();
                return false;
            }
        }

        /*
         * don't give the player the possibility of jumping to inaccessible
         * states by reloading past states
         */
        clearRedoStack();
        return true;
    }
}
