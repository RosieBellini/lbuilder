package team1;

import java.awt.Toolkit;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

/**
 * Represents a Sokoban map and the SaveStates that it has been in. Includes
 * methods for player movement, undo/redo, and polling information about the
 * map's state.
 */
public class SokobanMap {
    private Stack<SaveState> history;
    private Stack<SaveState> redoStack;
    private int prevRedoStackSize;
    private int xSize;
    private int ySize;
    private SaveState initialState;
    private boolean isCurrentlyMoving;
    private boolean isDoingSolution;
    private static final int ASSISTANT_DELAY = 200;

    /**
     * SokobanMap constructor.
     *
     * @param   xSize       The size of this SokobanMap's X dimension
     * @param   ySize       The size of this SokobanMap's Y dimension
     */
    public SokobanMap(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        history = new Stack<SaveState>();
        history.push(new SaveState());
        initialState = new SaveState();
        redoStack = new Stack<SaveState>();
        prevRedoStackSize = 0;
    }

    /**
     * Constructor for a SokobanMap clone.
     *
     * @param   mapToCopy       The SokobanMap to clone
     */
    public SokobanMap(SokobanMap mapToCopy) {
        this(mapToCopy.getXSize(), mapToCopy.getYSize());
        this.initialState = new SaveState(mapToCopy.getInitialState());
        this.reset();
    }

    /**
     * Constructor for a SokobanMap clone, using the old map's current state
     * as the initial state for the new one.
     *
     * @param   mapToCopy       The SokobanMap to copy
     *
     * @return                  A new SokobanMap with an initial state equal to
     *                          the current state of the mapToCopy parameter
     */
    public static SokobanMap shallowCopy(SokobanMap mapToCopy) {
        SokobanMap newMap = new SokobanMap(mapToCopy);
        newMap.setInitialState(mapToCopy.getState());
        newMap.reset();
        return newMap;
    }

    /**
     * Sets the map's current state to that of the given simple state. Used
     * in conjunction with accessibleSpaces() etc.
     *
     * @param   state       The simple SaveState used to set the map's new state
     */
    public void loadSimpleState(SaveState state) {
        if (!state.isSimple()) {
            throw new IllegalArgumentException();
        }

        Coordinate playerPosition = state.getPlayerPos();
        Set<Coordinate> boxPositions = state.getBoxPositions();
        Set<Coordinate> wallPositions = getState().getWallPositions();
        Set<Coordinate> goalPositions = getState().getGoalPositions();

        SaveState stateToLoad = new SaveState(playerPosition, boxPositions,
                                                wallPositions, goalPositions);
        history.push(stateToLoad);
    }

    /**
     * Returns the size of this map's X dimension.
     *
     * @return      The value of xSize
     */
    public int getXSize() {
        return xSize;
    }

    /**
     * Returns the size of this map's Y dimension.
     *
     * @return      The value of ySize
     */
    public int getYSize() {
        return ySize;
    }

    /**
     * Returns the initial state of this SokobanMap.
     *
     * @return      The value of initialState
     */
    public SaveState getInitialState() {
        return initialState;
    }

    /**
     * Sets the initial state of this SokobanMap to a different one.
     *
     * @param   initialState        The new SaveState to use as this map's
     *                              initialState
     */
    public void setInitialState(SaveState initialState) {
        this.initialState = initialState;
    }

    /**
     * Makes a new copy of this map's current state and adds it to the map's
     * history stack.
     */
    public void storeState() {
        history.push(new SaveState(getState()));
    }

    /**
     * Returns a SaveState object that contains the upper leftmost accessible
     * square and the box positions. This is used to determine a state of the
     * map independent of the player's exact position.
     *
     * @return      A simple SaveState that uses the map's current box positions
     *              and the upper leftmost square accessible by the player
     */
    public SaveState getSimpleState() {
        Coordinate playerPosition = getState().getPlayerPos();
        Set<Coordinate> boxPositions = getState().getBoxPositions();

        Set<Coordinate> accessibleSpaces
                = accessibleSpaces(playerPosition, false);
        ArrayList<Coordinate> allSpaces
                = Coordinate.allValidCoordinates(xSize, ySize);

        Coordinate potentialTopLeftSpace = new Coordinate(xSize, ySize);
        for (Coordinate potential : allSpaces) {
            potentialTopLeftSpace = potential;
            if (accessibleSpaces.contains(potentialTopLeftSpace)) {
                break;
            }
        }

        return new SaveState(potentialTopLeftSpace, boxPositions);
    }

    /**
     * Pops a SaveState out of the history stack. Does not allow the stack's
     * size to drop below 1.
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
     * Returns an undone state to the history stack.
     */
    public void redo() {
        if (redoStack.size() > 0) {
            prevRedoStackSize = redoStack.size();
            history.push(redoStack.pop());
        }
    }

    /**
     * Clears the redo stack. Used to discard undone states when the player
     * moves.
     */
    public void clearRedoStack() {
        redoStack.clear();
    }

    /**
     * Returns this map to its state at construction.
     */
    public void reset() {
        history.clear();
        history.push(initialState);
        clearRedoStack();
    }

    /**
     * Returns the size of the history stack.
     *
     * @return      The value of history.size()
     */
    public int historyLength() {
        return history.size();
    }

    /**
     * Determines whether or not the map has been completed.
     *
     * @return      True if all goals have a box on them, false otherwise
     */
    public boolean isDone() {
        for (Coordinate coord : getState().getGoalPositions()) {
            if (get(coord) != SokobanObject.BOX_ON_GOAL) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the current state of this SokobanMap.
     *
     * @return      The top SaveState in the history stack
     */
    public SaveState getState() {
        return history.peek();
    }

    /**
     * Performs SaveState.put on the current state.
     *
     * @see team1.SaveState#put
     */
    public boolean put(SokobanObject object, Coordinate position) {
        if (position.inRange(0, 0, xSize - 1, ySize - 1)) {
            return getState().put(object, position);
        } else {
            return false;
        }
    }

    /**
     * Performs SaveState.removeLayer on the current state.
     *
     * @see team1.SaveState#removeLayer
     */
    public void removeLayer(Coordinate coord) {
        getState().removeLayer(coord);
    }

    /**
     * Performs SaveState.get on the current state.
     *
     * @see team1.SaveState#get
     */
    public SokobanObject get(Coordinate position) {
        if (position.inRange(0, 0, xSize - 1, ySize - 1)) {
            return getState().get(position);
        } else {
            return SokobanObject.WALL;
        }
    }

    /**
     * Returns a String representation of this SokobanMap's current state.
     *
     * @return      A String containing this map's state in the standard
     *              SokobanMap format
     */
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

    /**
     * Returns which positions should be considered for redrawing since the
     * last state of the map. Note that this may return positions which have not
     * changed; for large maps it is computationally cheaper to be slightly
     * excessive in redraws than to calculate precisely what has changed.
     *
     * @param   playable        True to ignore walls and goals, false otherwise
     *
     * @return                  A Set of Coordinates which may have changed
     *                          since the last state
     */
    public Set<Coordinate> tilesToRedraw(boolean playable) {
        Set<Coordinate> changedPlaces = new HashSet<Coordinate>();
        SaveState[] stateArray = new SaveState[history.size()];
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

        changedPlaces.add(getState().getPlayerPos());
        changedPlaces.add(lastState.getPlayerPos());

        changedPlaces.addAll(getState().getBoxPositions());
        changedPlaces.addAll(lastState.getBoxPositions());

        if (!playable) {
            changedPlaces.addAll(getState().getWallPositions());
            changedPlaces.addAll(lastState.getWallPositions());

            changedPlaces.addAll(getState().getGoalPositions());
            changedPlaces.addAll(lastState.getGoalPositions());
        }

        return changedPlaces;
    }

    /**
     * Returns the Coordinates that are currently accessible from a given
     * Coordinate on this map.
     *
     * @param   origin      The first Coordinate contained by the accessible
     *                      area
     * @param ignoreBoxes   True to ignore boxes, false otherwise
     *
     * @return              The Coordinates currently accessible from the origin
     *                      Coordinate
     */
    public Set<Coordinate> accessibleSpaces(Coordinate origin,
                                            boolean ignoreBoxes) {
        Set<Coordinate> edges = new HashSet<Coordinate>();
        Set<Coordinate> accessible = new HashSet<Coordinate>();
        Set<Coordinate> newEdges = new HashSet<Coordinate>();

        edges.add(origin);
        while (edges.size() > 0) {
            for (Coordinate edge : edges) {
                accessible.add(edge);
                for (Coordinate potentialEdge : neighbours(edge)) {
                    SokobanObject object = get(potentialEdge);

                    if (ignoreBoxes && object != SokobanObject.WALL) {
                        newEdges.add(potentialEdge);
                    } else if (!ignoreBoxes
                            && object != SokobanObject.WALL
                            && object != SokobanObject.BOX
                            && object != SokobanObject.BOX_ON_GOAL) {
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
     * Returns the orthogonal neighbours of a given Coordinate.
     *
     * @param   origin      The Coordinate to search for neighbours of
     *
     * @return              The orthogonal neighbours of the given Coordinate
     */
    public Set<Coordinate> neighbours(Coordinate origin) {
        Set<Coordinate> potentialNeighbours = new HashSet<Coordinate>();
        Set<Coordinate> neighbours = new HashSet<Coordinate>();

        potentialNeighbours.add(origin.add(new Coordinate(1, 0)));
        potentialNeighbours.add(origin.add(new Coordinate(-1, 0)));
        potentialNeighbours.add(origin.add(new Coordinate(0, 1)));
        potentialNeighbours.add(origin.add(new Coordinate(0, -1)));

        for (Coordinate potentialNeighbor : potentialNeighbours) {
            if (potentialNeighbor.inRange(0, 0, xSize - 1, ySize - 1)) {
                neighbours.add(potentialNeighbor);
            }
        }

        return neighbours;
    }

    /**
     * Returns the Coordinates which are not accessible to the player, ignoring
     * boxes.
     *
     * @return      The Set of Coordinate which are inaccessible from the
     *              player's position.
     */
    public Set<Coordinate> inaccessibleSpaces() {
        ArrayList<Coordinate> potentialGrass =
                                Coordinate.allValidCoordinates(xSize, ySize);
        Coordinate playerPosition = getState().getPlayerPos();
        Set<Coordinate> inaccessibleSpaces = new HashSet<Coordinate>();
        potentialGrass.removeAll(accessibleSpaces(playerPosition, true));

        for (Coordinate potentialGrassSpace : potentialGrass) {
            if (get(potentialGrassSpace) == SokobanObject.SPACE
                    || get(potentialGrassSpace) == SokobanObject.GOAL) {
                inaccessibleSpaces.add(potentialGrassSpace);
            }
        }

        return inaccessibleSpaces;
    }

    /**
     * Returns a new SokobanMap that uses a standard format Sokoban text file
     * to determine its initial state.
     *
     * @param   levelFile       The InputStream containing the characters from
     *                          which to derive the new map's state
     *
     * @return                  A new SokobanMap that matches the input file
     */
    public static SokobanMap importLevel(InputStream levelFile) {
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
            xSize = line.length() > xSize ? line.length() : xSize;
            levelLines.add(line);
        }

        ySize = levelLines.size();
        SokobanMap map = new SokobanMap(xSize, ySize);

        /*
         * Then convert the raw data into a SokobanMap using the static method
         * charToSokobanObject from the SokobanObject class
         */
        for (String line : levelLines) {
            for (char ch : line.toCharArray()) {
                Coordinate coord = new Coordinate(x, y);
                SokobanObject object = SokobanObject.charToSokobanObject(ch);
                map.put(object, coord);
                x++;
            }

            x = 0;
            y++;
        }

        level.close();
        map.initialState = map.getState();
        return map;
    }

    /**
     * Returns whether or not the map's current state can be beaten. This
     * represents the minimum criteria for playability; a valid map may still
     * be impossible.
     *
     * @return      True if the map is playable, false otherwise
     */
    public boolean validate() {
        int boxCount = 0;
        int goalCount = 0;

        for (Coordinate position : inaccessibleSpaces()) {
            if (get(position) == SokobanObject.GOAL) {
                return false;
            }
        }

        Coordinate playerPosition = getState().getPlayerPos();
        for (Coordinate position : accessibleSpaces(playerPosition, true)) {
            if (get(position) == SokobanObject.BOX) {
                boxCount++;
            } else if (get(position) == SokobanObject.GOAL) {
                goalCount++;
            }
        }

        return boxCount >= goalCount && !isDone() && goalCount > 0;
    }

    /**
     * Crops the given SokobanMap to its outer walls.
     *
     * @param   mapToCrop       The SokobanMap to crop
     *
     * @return                  A new SokobanMap with an initial state matching
     *                          the input map's current state, with inaccessible
     *                          spaces outside the outer walls cropped away
     */
    public static SokobanMap crop(SokobanMap mapToCrop) {
        int xStart = mapToCrop.getXSize() - 1;
        int xEnd = 0;
        int yStart = mapToCrop.getYSize() - 1;
        int yEnd = 0;

        for (Coordinate position : mapToCrop.getState().getWallPositions()) {
            int x = position.x;
            int y = position.y;

            xStart = xStart > x ? x : xStart;
            xEnd = xEnd < x ? x : xEnd;
            yStart = yStart > y ? y : yStart;
            yEnd = xEnd < y ? y : yEnd;
        }

        int newXSize = xEnd - xStart + 1;
        int newYSize = yEnd - yStart + 1;
        SokobanMap croppedMap = new SokobanMap(newXSize, newYSize);

        for (int y = yStart; y <= yEnd; y++) {
            for (int x = xStart; x <= xEnd; x++) {
                SokobanObject sourceObj = mapToCrop.get(new Coordinate(x, y));
                Coordinate newPosition = new Coordinate(x - xStart, y - yStart);
                croppedMap.put(sourceObj, newPosition);
            }
        }

        return croppedMap;
    }

    /**
     * Subclass for moving the player to a position with a time delay after
     * each movement.
     */
    class Mover extends Thread {
        private Coordinate target;
        private int delay;

        /**
         * Mover constructor.
         *
         * @param   target      The position to which the player will move
         * @param   delay       The delay between movements
         */
        public Mover(Coordinate target, int delay) {
            clearRedoStack();
            this.target = target;
            this.delay = delay;
        }

        /**
         * Moves the player to target using SokobanMap.findPath with the given
         * delay between each movement.
         */
        public void run() {
            isCurrentlyMoving = true;

            ArrayList<Coordinate> path = findPath(target);
            if (path != null) {
                for (Coordinate position : path) {
                    try {
                        storeState();
                        put(SokobanObject.PLAYER, position);
                        GamePanel.redraw();
                        sleep(delay);
                    } catch (InterruptedException e) {
                        System.out.println("Mover interrupted");
                        break;
                    }
                }
            }

            isCurrentlyMoving = false;
        }
    }

    /**
     * Subclass for executing a given solution with a time delay after each
     * movement.
     */
    class SolutionRunner extends Thread {
        LinkedList<Coordinate[]> solution;
        Mover mover;

        /**
         * SolutionRunner constructor.
         *
         * @param   solution        A LinkedList of Coordinate arrays of length
         *                          two, where the first entry is the correct
         *                          position for the player at that step in the
         *                          solution and the second entry is the
         *                          direction in which to push
         */
        public SolutionRunner(LinkedList<Coordinate[]> solution) {
            this.solution = solution;
        }

        /**
         * Exeutes the solution with a delay of ASSISTANT_DELAY between each
         * step, waiting for three times the ASSISTANT_DELAY before starting.
         */
        public void run() {
            isDoingSolution = true;

            try {
                sleep(ASSISTANT_DELAY * 3);
            } catch (InterruptedException e) {
                isDoingSolution = false;
                return;
            }

            for (Coordinate[] instruction : solution) {
                try {
                    mover = new Mover(instruction[0], ASSISTANT_DELAY);
                    mover.start();
                    mover.join();
                    move(instruction[1]);
                    GamePanel.redraw();
                    sleep(ASSISTANT_DELAY);
                } catch (InterruptedException e) {
                    System.out.println("SolutionRunner interrupted");

                    if (getIsCurrentlyMoving()) {
                        mover.interrupt();

                        while (!mover.isInterrupted()) {
                            mover.interrupt();
                        }
                    }

                    break;
                }
            }

            isDoingSolution = false;
        }
    }

    /**
     * Returns whether or not a SolutionRunner is in execution.
     *
     * @return      True if a SolutionRunner is working, false otherwise
     */
    public boolean getIsDoingSolution() {
        return isDoingSolution;
    }

    /**
     * Returns whether or not a Mover is in execution.
     *
     * @return      True if a Mover is working, false otherwise
     */
    public boolean getIsCurrentlyMoving() {
        return isCurrentlyMoving;
    }

    /**
     * Uses the A* algorithm to calculate a path from the player to the given
     * position.
     *
     * @param   target      The target Coordinate
     *
     * @return              An ArrayList of Coordinates which the player should
     *                      visit to reach the target Coordinate
     */
    public ArrayList<Coordinate> findPath(Coordinate target) {
        Coordinate playerPos = getState().getPlayerPos();

        if (!accessibleSpaces(playerPos, false).contains(target)
                || get(target) == SokobanObject.PLAYER) {
            return null;
        }

        ArrayList<PathNode> closed = new ArrayList<PathNode>();
        ArrayList<PathNode> open = new ArrayList<PathNode>();

        PathNode activeNode = new PathNode(playerPos, target);
        open.add(activeNode);

        while (open.size() > 0) {
            activeNode = open.get(0);

            for (Coordinate neighbour : neighbours(activeNode.getPosition())) {
                PathNode nodeDummy = new PathNode(neighbour, neighbour);

                if ((getState().get(neighbour) == SokobanObject.SPACE
                        || getState().get(neighbour) == SokobanObject.GOAL)
                        && !closed.contains(nodeDummy)) {

                    if (!open.contains(nodeDummy)) {
                        int gCost = activeNode.getGCost() + 10;
                        PathNode thisNode = new PathNode(neighbour, activeNode,
                                                                target, gCost);
                        open.add(thisNode);
                    }

                    if (neighbour.equals(target)) {
                        ArrayList<Coordinate> directions
                                    = new ArrayList<Coordinate>();
                        directions.add(target);
                        int nodeIndex = open.indexOf(nodeDummy);
                        PathNode parent = open.get(nodeIndex).getParent();

                        while (parent != null) {
                            directions.add(parent.getPosition());
                            parent = parent.getParent();
                        }

                        directions.remove(playerPos);
                        Collections.reverse(directions);

                        return directions;
                    }
                }
            }

            Collections.sort(open);
            open.remove(activeNode);
            closed.add(activeNode);
        }

        System.out.println("No path found");
        return new ArrayList<Coordinate>();
    }

    /**
     * Moves the player in the given direction, respecting the rules of the game
     * and pushing boxes when necessary.
     *
     * @param   direction       The direction in which to move the player
     *
     * @return                  True if the move is permitted, false otherwise
     */
    public boolean move(Coordinate direction) {
        int directionMagnitude = Math.abs(direction.x) + Math.abs(direction.y);
        if (directionMagnitude != 1) {
            throw new IllegalArgumentException(
                                    "Move direction must have magnitude 1");
        }

        boolean validMove = false;
        Coordinate playerPos = getState().getPlayerPos();
        Coordinate target = playerPos.add(direction);
        storeState();

        if (!put(SokobanObject.PLAYER, target)) {
            if (get(target) == SokobanObject.BOX
                    || get(target) == SokobanObject.BOX_ON_GOAL) {
                if (put(SokobanObject.BOX, target.add(direction))) {
                    removeLayer(target);
                    put(SokobanObject.PLAYER, target);
                    validMove = true;
                }
            }
        } else {
            validMove = true;
        }

        if (validMove) {
            clearRedoStack();
            return true;
        } else {
            history.pop();
            Toolkit.getDefaultToolkit().beep();
            return false;
        }
    }
}
