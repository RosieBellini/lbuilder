package team1;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class SingleThreadSolver implements Runnable {
    private SokobanMap map;
    private List<SaveState> seenStates;
    private Set<Integer> seenStatesValues;
    private List<Integer> stateOrigins;
    private List<Coordinate[]> donePushes;
    private boolean solving;
    private boolean stopped;
    private int triedPushes;

    public SingleThreadSolver(SokobanMap map) {
        this.map = new SokobanMap(map);
        seenStates = new ArrayList<SaveState>();
        seenStates.add(map.getSimpleState());
        seenStatesValues = new HashSet<Integer>();
        seenStatesValues.add(map.getSimpleState().hashCode());
        stateOrigins = new ArrayList<Integer>();
        stateOrigins.add(-1);
        donePushes = new ArrayList<Coordinate[]>();
        Coordinate[] emptyPush = {new Coordinate(0,0),new Coordinate(0,0)};
        donePushes.add(emptyPush);
        triedPushes = 0;
    }

    public void run() {
        System.out.println(levelSolution());
    }

    public void stopSolving() {
        stopped = true;
    }

    public boolean isSolving() {
        return solving;
    }

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

    private boolean isSafePush(Coordinate[] aPush) {
        Coordinate spaceBehindPush = (aPush[0].add(aPush[1].mult(3)));
        SokobanObject objectBehindPush = map.get(spaceBehindPush);
        Coordinate spaceToPushInto = (aPush[0].add(aPush[1].mult(2)));
        SokobanObject objectToPushInto = map.get(spaceToPushInto);
        if (objectToPushInto == SokobanObject.GOAL ||
                objectBehindPush!=SokobanObject.WALL) {
            return true;
        }
        Coordinate spaceLeftOfPush = new Coordinate(-1, -1);
        Coordinate spaceRightOfPush= new Coordinate (-1, -1);
        if (aPush[1].y == 0) {
            spaceLeftOfPush = (aPush[0].add(aPush[1].mult(2))).add(new Coordinate(0, 1));
            spaceRightOfPush = (aPush[0].add(aPush[1].mult(2))).add(new Coordinate(0, -1));
        } else if (aPush[1].x == 0) {
            spaceLeftOfPush = (aPush[0].add(aPush[1].mult(2))).add(new Coordinate(1, 0));
            spaceRightOfPush = (aPush[0].add(aPush[1].mult(2))).add(new Coordinate(-1, 0));
        }

        if (map.get(spaceRightOfPush) == SokobanObject.WALL ||
                map.get(spaceLeftOfPush) == SokobanObject.WALL) {
            return false;
        } else {
            return true;
        }
    }

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

        triedPushes++;
        if (triedPushes%10000 == 0) {
            System.out.println(triedPushes);
        }

        return isDone;
    }

    public boolean solveLevel(){
        int currentStateIndex = 0;
        solving = true;
        boolean solved = false;
        while (!stopped && solving && currentStateIndex<seenStates.size()) {
            map.loadSimpleState(seenStates.get(currentStateIndex));
            List<Coordinate[]> validPushes = validPushes(currentStateIndex);
            for (int i=0; i<validPushes.size();i++) {
                boolean pushed = tryPush(currentStateIndex,validPushes.get(i));
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

    public String validPushesTestString(){
        List<Coordinate[]> validPushes = validPushes(0);
        String allPushesString = "";
        for (int i = 0; i < validPushes.size(); i++) {
            allPushesString += (validPushes.get(i)[0] + "    Direction: " + validPushes.get(i)[1] + "\n");
        }
        return allPushesString;
    }

    public Entry<HashMap<SaveState, Coordinate[]>, LinkedList<Coordinate[]>> levelSolution() {
        LinkedList<Coordinate[]> pushesToSolve = new LinkedList<Coordinate[]>();
        LinkedList<SaveState> statesToSolve = new LinkedList<SaveState>();
        HashMap<SaveState, Coordinate[]> solution = new HashMap<SaveState, Coordinate[]>();
        String solutionString = "NO_SOLUTION";

        if (solveLevel()) {
            int currentStateIndex = seenStates.size() - 1;
            solutionString="Solution: \n";
            while (currentStateIndex > 0) {
                pushesToSolve.addFirst(donePushes.get(currentStateIndex));
                currentStateIndex = stateOrigins.get(currentStateIndex);
                statesToSolve.addFirst(seenStates.get(currentStateIndex));
            }
        }
        for (int i = 0; i < pushesToSolve.size(); i++) {
            solutionString += (pushesToSolve.get(i)[0] + "   Direction: " + pushesToSolve.get(i)[1] + "\n");
            solution.put(statesToSolve.get(i), new Coordinate[]{ pushesToSolve.get(i)[0], pushesToSolve.get(i)[1] });
        }
        System.out.println(solutionString);

        if (stopped) {
            return null;
        }

        return new SimpleEntry<HashMap<SaveState, Coordinate[]>, LinkedList<Coordinate[]>>(solution, pushesToSolve);
    }
}
