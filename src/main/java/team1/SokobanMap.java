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
 * Represents a Sokoban level. "Static" objects (spaces, walls, goals) are
 * represented in an array of SokobanObjects whereas "dynamic" objects (boxes
 * and the player) are represented in a FixedSizeStack of SaveState objects.
 * This separation reduces the amount of information we need to store for the
 * undo command and solver.
 *
 * TODO: make the array of static objects immutable
 */
public class SokobanMap {
    private Stack<SaveState> history;
    private Stack<SaveState> redoStack;
    private int prevRedoStackSize;
    private int xSize;
    private int ySize;
    private SaveState initialState;

    /**
     * Initialises a MapContainer of the given size filled with spaces
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
     *  Is this right to do?  For a given state I want to set the map to that position so I can
     *  use accessibleSpaces() to work out what boxes you can push from a given SaveState.
     */
    public void loadSimpleState(SaveState state){
        if (!state.isSimple()){
            throw new IllegalArgumentException();
        }
        SaveState stateToLoad = new SaveState(state.getPlayerPos(),
                state.getBoxPositions(),
                this.getState().getWallPositions(),
                this.getState().getGoalPositions());
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
     * Returns a SaveState object that contains the upper leftmost accessible square and the box positions.
     * This will be used to determine a state of the map independent of the players exact position.
     * @return a SaveState which represents a state of the game for the solving algorithm to use.
     */
    public SaveState getSimpleState() {
        Set<Coordinate> accessibleSpaces = accessibleSpaces(getState().getPlayerPos(), false);
        Coordinate potentialTopLeftSpace = new Coordinate(xSize,ySize);
        for (Coordinate potential : Coordinate.allValidCoordinates(xSize, ySize)) {
            potentialTopLeftSpace = potential;
            if (accessibleSpaces.contains(potentialTopLeftSpace)) {
                break;
            }
        }

        return new SaveState(potentialTopLeftSpace, getState().getBoxPositions());
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
        history.clear();
        history.push(initialState);
        clearRedoStack();
    }

    public int historyLength() {
        return history.size();
    }

    /**
     * Checks the positions of all boxes to see if they've been placed on a
     * goal
     * @return  true if all boxes are on a goal, false otherwise
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
     * @param object    the object to be stored
     * @param coord     the coordinate at which to place it
     * @return          true if successful, false otherwise
     */
    public boolean put(SokobanObject object, Coordinate coord) {
        if (coord.x >= 0 && coord.x < xSize && coord.y >= 0 && coord.y < ySize){
            return getState().put(object, coord);
        } else {
            return false;
        }
    }

    /**
     * Removes a "layer" from the given coordinate. If there is a BOX or PLAYER
     * on a SPACE or GOAL, remove it; otherwise, make the coordinate a SPACE.
     *
     * @param coord     the coordinate to remove a layer from
     */
    public void removeLayer(Coordinate coord) {
        getState().removeLayer(coord);
    }

    /**
     * Get the type of SokobanObject at the given coordinate. Again, see the
     * SokobanObject documentation for an explanation of how objects are stored
     * and returned.
     */
    public SokobanObject get(Coordinate coord) {
        if (coord.x >= 0 && coord.x < xSize && coord.y >= 0 && coord.y < ySize){
            return getState().get(coord);
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
     * Takes a coordinate and returns the set of coordinates which are accessible from there.
     * Accessible meaning not blocked by a WALL.
     * @param origin The coordinate space from which to start the search for accessible spaces.
     * @param ignoreBoxes true if you want to ignore boxes in the search.
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
                        if (objectInPotentialEdge != SokobanObject.WALL){
                            if(objectInPotentialEdge != SokobanObject.BOX){
                                if(objectInPotentialEdge != SokobanObject.BOX_ON_GOAL){
                                newEdges.add(potentialEdge);
                                }
                            }
                        }
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

    /**
     * Takes a coordinate of the map and returns all the spaces
     * which the player could access from that coordinate (ignoring boxes).
     * @return a Set of all the inaccessible coordinates from the given position.
     */
    public Set<Coordinate> inaccessibleSpaces() {
        ArrayList<Coordinate> potentialGrass = Coordinate.allValidCoordinates(xSize, ySize);
        Set<Coordinate> inaccessibleSpaces = new HashSet<Coordinate>();
        potentialGrass.removeAll(accessibleSpaces(getState().getPlayerPos(),true));
        for(Coordinate potentialGrassSpace : potentialGrass){
            if (get(potentialGrassSpace) == SokobanObject.SPACE || get(potentialGrassSpace) == SokobanObject.GOAL) {
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
            if (line.length() > xSize) {
                xSize = line.length();
            }
            levelLines.add(line);
        }
        ySize = levelLines.size();
        SokobanMap map = new SokobanMap(xSize, ySize);

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
        map.initialState = map.getState();
        return map;
    }

    public boolean validate() {
        int boxCount = 0;
        int goalCount = 0;
        boolean inaccessibleGoal = false;

        for (Coordinate position : inaccessibleSpaces()) {
            if (get(position) == SokobanObject.GOAL) {
                inaccessibleGoal = true;
                break;
            }
        }

        for (Coordinate position : accessibleSpaces(getState().getPlayerPos(), true)) {
            if (get(position) == SokobanObject.BOX) {
                boxCount++;
            }

            if (get(position) == SokobanObject.GOAL) {
                goalCount++;
            }
        }
        return boxCount >= goalCount && !isDone() && !inaccessibleGoal && goalCount > 0;
    }

    public static SokobanMap crop(SokobanMap mapToCrop) {
        int xStart = mapToCrop.getXSize() - 1;
        int xEnd = 0;
        int yStart = mapToCrop.getYSize() - 1;
        int yEnd = 0;

        for (Coordinate position : mapToCrop.getState().getWallPositions()) {
            int x = position.x;
            int y = position.y;

            if (xStart > x) {
                xStart = x;
            }

            if (xEnd < x) {
                xEnd = x;
            }

            if (yStart > y) {
                yStart = y;
            }

            if (yEnd < y) {
                yEnd = y;
            }
        }


        SokobanMap croppedMap = new SokobanMap(xEnd + 1 -xStart, yEnd + 1 - yStart);
        for (int y = yStart; y <= yEnd; y++) {
            for (int x = xStart; x <= xEnd; x++) {
                croppedMap.put(mapToCrop.get(new Coordinate(x, y)), new Coordinate(x - xStart, y - yStart));
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
            ArrayList<Coordinate> path = findPath(target);

            if (path != null) {
                for (Coordinate position : path) {
                    storeState();
                    put(SokobanObject.PLAYER, position);
                    GamePanel.redraw();
                    try {
                        sleep(delay);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void executeSolution(LinkedList<Coordinate[]> solution) {
        class SolutionRunner extends Thread {
            public void run() {
                try {
                    for (Coordinate[] instruction : solution) {
                        System.out.println(instruction[0]);
                        Mover mover = new Mover(instruction[0], 200);
                        mover.start();
                        mover.join();
                        move(instruction[1]);
                        GamePanel.redraw();
                        sleep(200);
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        SolutionRunner runner = new SolutionRunner();
        runner.start();
    }

    public ArrayList<Coordinate> findPath(Coordinate target) {
        Coordinate playerPos = getState().getPlayerPos();


        if (!accessibleSpaces(playerPos, false).contains(target) || get(target) == SokobanObject.PLAYER) {
            return null;
        }

        ArrayList<PathNode> closed = new ArrayList<PathNode>();
        ArrayList<PathNode> open = new ArrayList<PathNode>();

        PathNode activeNode = new PathNode(playerPos, target);
        open.add(activeNode);

        while (open.size() > 0) {
            activeNode = open.get(open.size() - 1);

            for (Coordinate neighbour : neighbors(activeNode.getPosition())) {
                PathNode nodeNeighbourDummy = new PathNode(neighbour, neighbour);
                if ((getState().get(neighbour) == SokobanObject.SPACE
                        || getState().get(neighbour) == SokobanObject.GOAL) && !closed.contains(nodeNeighbourDummy)) {

                    if (open.contains(nodeNeighbourDummy)) {
                        PathNode searchedNode = open.get(open.indexOf(nodeNeighbourDummy));
                        int maybeSmallerCost = activeNode.getGCost() + 10;

                        if (maybeSmallerCost < searchedNode.getGCost()) {
                            System.out.println("I found a cheaper route. Tell Tom!");
                            searchedNode.changeParent(activeNode);
                            searchedNode.setGCost(maybeSmallerCost);
                        }
                    } else {
                        open.add(new PathNode(neighbour, activeNode, target, activeNode.getGCost() + 10));
                    }

                    if (neighbour.equals(target)) {
                        System.out.println("Found target");
                        ArrayList<Coordinate> directions = new ArrayList<Coordinate>();
                        directions.add(target);
                        PathNode parent = open.get(open.indexOf(nodeNeighbourDummy)).getParent();

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
     * Moves the player in the given direction, respecting the rules of the
     * game and pushing boxes when necessary.
     *
     * @param direction     the direction in which to move the player
     * @return true if the move is permitted, false otherwise
     */
    public boolean move(Coordinate direction) {
        int directionMagnitude = Math.abs(direction.x) + Math.abs(direction.y);
        if (directionMagnitude != 1) {
            throw new IllegalArgumentException("Move direction must have magnitude 1");
        }

        Coordinate playerPos = getState().getPlayerPos();
        Coordinate target = playerPos.add(direction);
        storeState();

        if (!put(SokobanObject.PLAYER, target)) {
            if (get(target) == SokobanObject.BOX || get(target) == SokobanObject.BOX_ON_GOAL) {
                removeLayer(target);
                put(SokobanObject.BOX, target.add(direction));
                put(SokobanObject.PLAYER, target);
            } else {
                history.pop();
                Toolkit.getDefaultToolkit().beep();
                return false;
            }
        }

        clearRedoStack();
        return true;
    }
}
