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

public class SokobanMap {
    private Stack<SaveState> history;
    private Stack<SaveState> redoStack;
    private int prevRedoStackSize;
    private int xSize;
    private int ySize;
    private SaveState initialState;
    private boolean isCurrentlyMoving;
    private boolean isDoingSolution;

    public SokobanMap(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        history = new Stack<SaveState>();
        history.push(new SaveState());
        initialState = new SaveState();
        redoStack = new Stack<SaveState>();
        prevRedoStackSize = 0;
    }

    public SokobanMap(SokobanMap mapToCopy) {
        this(mapToCopy.getXSize(), mapToCopy.getYSize());
        this.initialState = new SaveState(mapToCopy.getInitialState());
        this.reset();
    }

    public static SokobanMap shallowCopy(SokobanMap mapToCopy) {
        SokobanMap newMap = new SokobanMap(mapToCopy);
        newMap.setInitialState(mapToCopy.getState());
        newMap.reset();
        return newMap;
    }

    /*
     * Is this right to do? For a given state I want to set the map to that
     * position so I can use accessibleSpaces() to work out what boxes you can
     * push from a given SaveState.
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

    public int getYSize() {
        return ySize;
    }

    public int getXSize() {
        return xSize;
    }

    public SaveState getInitialState() {
        return initialState;
    }

    public void setInitialState(SaveState initialState) {
        this.initialState = initialState;
    }

    public void storeState() {
        history.push(new SaveState(getState()));
    }

    /**
     * Returns a SaveState object that contains the upper leftmost accessible
     * square and the box positions. This will be used to determine a state of
     * the map independent of the players exact position.
     *
     * @return a SaveState which represents a state of the game for the solving
     *         algorithm to use.
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
     * Pops a SaveState out of the history stack. If the stack is then empty,
     * the SaveState is put back in place as we require that there is always one
     * state to represent the positions of dynamic objects.
     *
     * @param sendToRedoStack
     *            whether or not to place the popped SaveState into the redo
     *            stack e.g. when the user inputs a command
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
        history.clear();
        history.push(initialState);
        clearRedoStack();
    }

    public int historyLength() {
        return history.size();
    }

    /**
     * Checks the positions of all boxes to see if they've been placed on a goal
     *
     * @return true if all boxes are on a goal, false otherwise
     */
    public boolean isDone() {
        for (Coordinate coord : getState().getGoalPositions()) {
            if (get(coord) != SokobanObject.BOX_ON_GOAL) {
                return false;
            }
        }

        return true;
    }

    public SaveState getState() {
        return history.peek();
    }

    /**
     * Put a SokobanObject in the given position. See SokobanObject
     * documentation for an explanation of how objects are stored. Respects the
     * rules of the game e.g. can't place a PLAYER on top of a WALL.
     *
     * @param object
     *            the object to be stored
     * @param coord
     *            the coordinate at which to place it
     * @return true if successful, false otherwise
     */
    public boolean put(SokobanObject object, Coordinate position) {
        if (position.inRange(0, 0, xSize, ySize)) {
            return getState().put(object, position);
        } else {
            return false;
        }
    }

    /**
     * Removes a "layer" from the given coordinate. If there is a BOX or PLAYER
     * on a SPACE or GOAL, remove it; otherwise, make the coordinate a SPACE.
     *
     * @param coord
     *            the coordinate to remove a layer from
     */
    public void removeLayer(Coordinate coord) {
        getState().removeLayer(coord);
    }

    /**
     * Get the type of SokobanObject at the given coordinate. Again, see the
     * SokobanObject documentation for an explanation of how objects are stored
     * and returned.
     */
    public SokobanObject get(Coordinate position) {
        if (position.inRange(0, 0, xSize, ySize)) {
            return getState().get(position);
        } else {
            return SokobanObject.WALL;
        }
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
     * Takes a coordinate and returns the set of coordinates which are
     * accessible from there. Accessible meaning not blocked by a WALL.
     *
     * @param origin
     *            The coordinate space from which to start the search for
     *            accessible spaces.
     * @param ignoreBoxes
     *            true if you want to ignore boxes in the search.
     */
    public Set<Coordinate> accessibleSpaces(Coordinate origin, boolean ignoreBoxes) {
        Set<Coordinate> edges = new HashSet<Coordinate>();
        Set<Coordinate> accessible = new HashSet<Coordinate>();
        Set<Coordinate> newEdges = new HashSet<Coordinate>();

        edges.add(origin);
        while (edges.size() > 0) {
            for (Coordinate edge : edges) {
                accessible.add(edge);
                for (Coordinate potentialEdge : neighbors(edge)) {
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
     * Takes a coordinate and returns its orthogonal neighbors (avoiding out of
     * bounds areas).
     */
    public Set<Coordinate> neighbors(Coordinate origin) {
        Set<Coordinate> potentialNeighbors = new HashSet<Coordinate>();
        Set<Coordinate> neighbors = new HashSet<Coordinate>();

        potentialNeighbors.add(origin.add(new Coordinate(1, 0)));
        potentialNeighbors.add(origin.add(new Coordinate(-1, 0)));
        potentialNeighbors.add(origin.add(new Coordinate(0, 1)));
        potentialNeighbors.add(origin.add(new Coordinate(0, -1)));

        for (Coordinate potentialNeighbor : potentialNeighbors) {
            if (potentialNeighbor.inRange(0, 0, xSize, ySize)) {
                neighbors.add(potentialNeighbor);
            }
        }

        return neighbors;
    }

    /**
     *
     * @return a Set of all the inaccessible coordinates from the given
     *         position.
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
     * Interprets the contents of the "level" file and stores it as a SokobanMap
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

    class Mover extends Thread {
        private Coordinate target;
        private int delay;

        public Mover(Coordinate target, int delay) {
            clearRedoStack();
            this.target = target;
            this.delay = delay;
        }

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

    class SolutionRunner extends Thread {
        LinkedList<Coordinate[]> solution;

        public SolutionRunner(LinkedList<Coordinate[]> solution) {
            this.solution = solution;
        }

        public void run() {
            isDoingSolution = true;

            try {
                sleep(500);
            } catch (InterruptedException e) {
                isDoingSolution = false;
                return;
            }

            for (Coordinate[] instruction : solution) {
                try {
                    Mover mover = new Mover(instruction[0], 200);
                    mover.start();
                    mover.join();
                    move(instruction[1]);
                    GamePanel.redraw();
                    sleep(200);
                } catch (InterruptedException e) {
                    System.out.println("SolutionRunner interrupted");
                    break;
                }
            }

            isDoingSolution = false;
        }
    }

    public boolean getIsDoingSolution() {
        return isDoingSolution;
    }

    public boolean getIsCurrentlyMoving() {
        return isCurrentlyMoving;
    }

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

            for (Coordinate neighbour : neighbors(activeNode.getPosition())) {
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
     * @param direction
     *            the direction in which to move the player
     * @return true if the move is permitted, false otherwise
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
