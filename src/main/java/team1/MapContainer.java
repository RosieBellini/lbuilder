package team1;

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
    private int prevRedoStackSize;
    private int xSize;
    private int ySize;
    private int maxUndos;

    /**
     * Initialises a MapContainer of the given size filled with spaces
     */
    public MapContainer(int xSize, int ySize, int maxUndos) {
        history = new FixedSizeStack<SaveState>(maxUndos);
        history.push(new SaveState(new Coordinate(-1, -1), new HashSet<Coordinate>(), new HashSet<Coordinate>(), new HashSet<Coordinate>()));
        redoStack = new Stack<SaveState>();
        prevRedoStackSize = 0;
        this.maxUndos = maxUndos;
        this.xSize = xSize;
        this.ySize = ySize;
    }

    public int getYSize() {
        return ySize;
    }

    public int getXSize() {
        return xSize;
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
        Coordinate potentialTopLeftSpace = new Coordinate(getXSize(),getYSize());
        boolean done = false;
        for(int y=0;y<getYSize();y++){
            for(int x=0;x<getXSize();x++){
                potentialTopLeftSpace = new Coordinate(x,y);
                if (accessibleSpaces.contains(potentialTopLeftSpace)){
                    done=true;
                    break;
                }
            }
            if (done){
                break;
            }
        }
        //		System.out.println(potentialTopLeftSpace.getX()+""+potentialTopLeftSpace.getY());
        return new SaveState(potentialTopLeftSpace, getMyState());
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
            if (potentialNeighbor.getX()>=0&&potentialNeighbor.getX()<=this.getXSize()&&
                    potentialNeighbor.getY()>=0&&potentialNeighbor.getY()<=this.getYSize()){
                neighbors.add(potentialNeighbor);
                    }
        }
        return neighbors;
    }

    public Set<Coordinate> growGrass() {
        Set<Coordinate> potentialGrass = Coordinate.allValidCoordinates(getXSize(), getYSize());
        Set<Coordinate> grassPositions = new HashSet<Coordinate>();
        potentialGrass.removeAll(accessibleSpaces(getMyState().getWPos(),true));
        for(Coordinate potentialGrassSpace : potentialGrass){
            if (get(potentialGrassSpace) == SokobanObject.SPACE) {
                grassPositions.add(potentialGrassSpace);
            }
        }
        return grassPositions;
    }
}
