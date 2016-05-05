package team1;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Attempts to find a push-optimised solution for a given SokobanMap.
 */
public class SingleThreadSolver implements Runnable {
    private SokobanMap map;
    /**
     * A list of all the permutations of boxes and player positions
     * that the Solver encounters whilst trying to find a solution.
     */
    private List<SaveState> seenStates;
    /**
     * A set of all the hashcodes representing the different states
     * of the map that the Solver has seen.
     * Used to check whether a push progresses the level somehow.
     */
    private Set<Integer> seenStatesValues;
    /**
     * A list of integers which represent the index location of the
     * state from which the state at the current index in seenStates originated.
     * Used to iterate backwards through seenStates and donePushes
     * to return a solution once one has been found.
     */
    private List<Integer> stateOrigins;
    /**
     * A list of all the pushes performed to get to a solution,
     * a push is represented by an array of Coordinates.
     * One coordinate represents the location from which the push is performed.
     * The second coordinate is the direction which the push moves a box.
     */
    private List<Coordinate[]> donePushes;
    private boolean solving;
    private boolean stopped;

    public SingleThreadSolver(SokobanMap map) {
        this.map = new SokobanMap(map);
        seenStates = new ArrayList<SaveState>();
        seenStates.add(this.map.getSimpleState());
        seenStatesValues = new HashSet<Integer>();
        seenStatesValues.add(this.map.getSimpleState().hashCode());
        stateOrigins = new ArrayList<Integer>();
        stateOrigins.add(-1);
        donePushes = new ArrayList<Coordinate[]>();
        Coordinate[] emptyPush = { new Coordinate(0, 0), new Coordinate(0, 0) };
        donePushes.add(emptyPush);
    }

    public void run() {
        System.out.println(levelSolution());
    }

    /**
     * Cancels the current solving operation if the solver is working.
     */
    public void stopSolving() {
        stopped = true;
    }

    /**
     * Returns whether or not this SingleThreadSolver is working.
     *
     * @return      True if the solver is in the process of calculating a
     *              solution, false otherwise
     */
    public boolean isSolving() {
        return solving;
    }

    /**
     * For a given index, looks in the collection of all seen game states in the
     * seenStates list and returns all the pushes that the player can perform from
     * the area that he can currently access.
     * @param stateIndex The number of the state for which you are finding all the possible pushes.
     * @return A list of Coordinate[] representing the location and direction of each push.
     */
    private List<Coordinate[]> validPushes(int stateIndex) {
        map.loadSimpleState(seenStates.get(stateIndex));
        SaveState state = seenStates.get(stateIndex);
        Set<Coordinate> accessibleSpaces = map.accessibleSpaces(state.getPlayerPos(), false);
        List<Coordinate[]> validPushes = new ArrayList<Coordinate[]>();

        for (Coordinate box : state.getBoxPositions()) {
            for (Coordinate spaceNextToBox : map.neighbours(box)) {
                SokobanObject thingOppositeBox = map.get(box.add(box.add(spaceNextToBox.reverse())));

                if (accessibleSpaces.contains(spaceNextToBox)
                        && (thingOppositeBox == SokobanObject.SPACE
                            || thingOppositeBox == SokobanObject.PLAYER
                            || thingOppositeBox == SokobanObject.GOAL
                            || thingOppositeBox == SokobanObject.PLAYER_ON_GOAL)) {
                    Coordinate[] aPush = { spaceNextToBox, box.add(spaceNextToBox.reverse()) };

                    if (isSafePush(aPush)) {
                        validPushes.add(aPush);
                    }
                }
            }
        }

        return validPushes;
    }

    /**
     * For a given push (a coordinate and a direction to push towards)
     * checks if that push would put the level into a deadlocked state
     * that would prevent the level from then being completed.
     * @param aPush The push to perform.
     * @return A boolean, true if the push is safe and would not put a box in a corner.
     */
    private boolean isSafePush(Coordinate[] aPush) {
        Coordinate spaceBehindPush = (aPush[0].add(aPush[1].mult(3)));
        SokobanObject objectBehindPush = map.get(spaceBehindPush);
        Coordinate spaceToPushInto = (aPush[0].add(aPush[1].mult(2)));
        SokobanObject objectToPushInto = map.get(spaceToPushInto);
        if (objectToPushInto == SokobanObject.GOAL || objectToPushInto == SokobanObject.PLAYER_ON_GOAL
                || objectBehindPush != SokobanObject.WALL) {
            return true;
                }
        Coordinate spaceLeftOfPush = new Coordinate(-1, -1);
        Coordinate spaceRightOfPush = new Coordinate(-1, -1);
        if (aPush[1].y == 0) {
            spaceLeftOfPush = (aPush[0].add(aPush[1].mult(2))).add(new Coordinate(0, 1));
            spaceRightOfPush = (aPush[0].add(aPush[1].mult(2))).add(new Coordinate(0, -1));
        } else if (aPush[1].x == 0) {
            spaceLeftOfPush = (aPush[0].add(aPush[1].mult(2))).add(new Coordinate(1, 0));
            spaceRightOfPush = (aPush[0].add(aPush[1].mult(2))).add(new Coordinate(-1, 0));
        }

        if (map.get(spaceRightOfPush) == SokobanObject.WALL || map.get(spaceLeftOfPush) == SokobanObject.WALL) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Takes a given level state (containing the position of all boxes and the player)
     * and performs a single push from a given coordinate.
     * If that push was successful and generated a new unseen state of the level:
     * it is added to the list of all seenStates.
     * If that push also solved the level then the method returns true.
     * @param stateIndex The position of the state to push from in the seenStates list.
     * @param aPush The coordinate to push from and direction to push.
     * @return True if the push completed the level.
     */
    private boolean tryPush(int stateIndex, Coordinate[] aPush) {
        map.put(SokobanObject.PLAYER, aPush[0]);
        map.move(aPush[1]);
        boolean isDone = map.isDone();
        SaveState possibleNewState = new SaveState(map.getSimpleState());
        int newStateHashCode = possibleNewState.hashCode();
        if (!seenStatesValues.contains(newStateHashCode)) {
            seenStates.add(possibleNewState);
            seenStatesValues.add(newStateHashCode);
            stateOrigins.add(stateIndex);
            donePushes.add(aPush);
        }
        map.undo();

        return isDone;
    }

    /**
     * Repeatedly performs the validPushes() method on map and performs all the returned pushes
     * if no solution is found it moves to the next state in seenStates and again pushes every box it can.
     * The method continues until either a solution is found or it has seen every possible
     * state of the map, in which case it returns false.
     * @return True if the level has been solved.  False if the level is found impossible.
     */
    public boolean solveLevel() {
        int currentStateIndex = 0;
        solving = true;
        boolean solved = false;
        while (!stopped && solving && currentStateIndex < seenStates.size()) {
            map.loadSimpleState(seenStates.get(currentStateIndex));
            List<Coordinate[]> validPushes = validPushes(currentStateIndex);
            for (int i = 0; i < validPushes.size(); i++) {
                boolean pushed = tryPush(currentStateIndex, validPushes.get(i));
                if (pushed) {
                    solving = false;
                    solved = true;
                    break;
                }
                if (!solving || stopped) {
                    break;
                }
            }
            currentStateIndex++;
        }
        return solved;
    }

    /**
     * Runs the solveLevel() method to completion.
     * If solveLevel() successfully completes the level then
     * a List containing all the pushes that the player needs to perform
     * to complete the level, and all the various states that the level
     * will be in during the performance of the returned solution.
     * @return A collection representing the solution for the level.
     */
    public Entry<HashMap<SaveState, Coordinate[]>, LinkedList<Coordinate[]>> levelSolution() {
        LinkedList<Coordinate[]> pushesToSolve = new LinkedList<Coordinate[]>();
        LinkedList<SaveState> statesToSolve = new LinkedList<SaveState>();
        HashMap<SaveState, Coordinate[]> solution = new HashMap<SaveState, Coordinate[]>();
        String solutionString = "NO_SOLUTION";

        if (solveLevel()) {
            int currentStateIndex = seenStates.size() - 1;
            solutionString = "Solution: \n";
            while (currentStateIndex > 0) {
                pushesToSolve.addFirst(donePushes.get(currentStateIndex));
                currentStateIndex = stateOrigins.get(currentStateIndex);
                statesToSolve.addFirst(seenStates.get(currentStateIndex));
            }
        }
        for (int i = 0; i < pushesToSolve.size(); i++) {
            solutionString += (pushesToSolve.get(i)[0] + "   Direction: "
                                + pushesToSolve.get(i)[1] + "\n");
            solution.put(statesToSolve.get(i), new Coordinate[] { pushesToSolve.get(i)[0], pushesToSolve.get(i)[1] });
        }
        System.out.println(solutionString);

        if (stopped) {
            return null;
        }

        return new SimpleEntry<HashMap<SaveState, Coordinate[]>, LinkedList<Coordinate[]>>(solution, pushesToSolve);
    }
}
