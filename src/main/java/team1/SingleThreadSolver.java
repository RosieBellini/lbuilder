package team1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SingleThreadSolver implements Runnable {
    private SokobanMap map;
    private List<SaveState> seenStates;
    private Set<Integer> seenStatesValues;
    private Set<String> seenStateStrings;
    private List<Integer> stateOrigins;
    private List<Coordinate[]> donePushes;
    private boolean solving;
    private int triedPushes;

    public SingleThreadSolver(SokobanMap map){
        this.map=new SokobanMap(map);
        seenStates = new ArrayList<SaveState>();
        seenStates.add(map.getSimpleState());
        seenStatesValues = new HashSet<Integer>();
        seenStatesValues.add(map.getSimpleState().hashCode());
        seenStateStrings = new HashSet<String>();
        seenStateStrings.add(map.toString());
        stateOrigins = new ArrayList<Integer>();
        stateOrigins.add(-1);
        donePushes = new ArrayList<Coordinate[]>();
        Coordinate[] emptyPush = {new Coordinate(0,0),new Coordinate(0,0)};
        donePushes.add(emptyPush);
        triedPushes=0;
    }

    public void run(){
        System.out.println(levelSolution());
    }

    public void stopSolving(){
        solving = false;
    }

    public boolean isSolving() {
        return solving;
    }

    private List<Coordinate[]> validPushes(int stateIndex){
        map.loadSimpleState(seenStates.get(stateIndex));
        SaveState state = seenStates.get(stateIndex);
        Set<Coordinate> accessibleSpaces = map.accessibleSpaces(state.getWPos(), false);
        List<Coordinate[]> validPushes = new ArrayList<Coordinate[]>();
        for (Coordinate box : state.getBoxPositions()){
            for (Coordinate spaceNextToBox : map.neighbors(box)){
                SokobanObject thingOppositeBox = map.get(box.add(box.add(spaceNextToBox.reverse())));
                if (accessibleSpaces.contains(spaceNextToBox)&&
                        (thingOppositeBox==SokobanObject.SPACE ||
                        thingOppositeBox==SokobanObject.PLAYER ||
                        thingOppositeBox==SokobanObject.GOAL ||
                        thingOppositeBox==SokobanObject.PLAYER_ON_GOAL)){
                    Coordinate[] aPush = {spaceNextToBox,box.add(spaceNextToBox.reverse())};
                    if(isSafePush(aPush)){
                        validPushes.add(aPush);
                    }
                }
            }
        }
        return validPushes;
    }

    private boolean isSafePush(Coordinate[] aPush){
        Coordinate spaceBehindPush = (aPush[0].add(aPush[1].mult(3)));
        SokobanObject objectBehindPush = map.get(spaceBehindPush);
        Coordinate spaceToPushInto = (aPush[0].add(aPush[1]));
        SokobanObject objectToPushInto = map.get(spaceToPushInto);
        if (objectToPushInto==SokobanObject.GOAL || objectBehindPush!=SokobanObject.WALL){
            return true;
        }
        Coordinate spaceLeftOfPush = new Coordinate(-1,-1);
        Coordinate spaceRightOfPush= new Coordinate (-1,-1);
        if(aPush[1].x!=0){
            spaceLeftOfPush = (aPush[0].add(aPush[1].mult(2))).add(new Coordinate(0,1));
            spaceRightOfPush = (aPush[0].add(aPush[1].mult(2))).add(new Coordinate(0,-1));
        }
        else if (aPush[1].y!=0){
            spaceLeftOfPush = (aPush[0].add(aPush[1].mult(2))).add(new Coordinate(1,0));
            spaceRightOfPush = (aPush[0].add(aPush[1].mult(2))).add(new Coordinate(-1,0));
        }
        if (map.get(spaceRightOfPush)==SokobanObject.WALL||map.get(spaceLeftOfPush)==SokobanObject.WALL){
            return false;
        }
        else return true;
    }

    private boolean tryPush(int stateIndex, Coordinate[] aPush){
        map.put(SokobanObject.PLAYER, aPush[0]);
        map.move(aPush[1]);
        boolean isDone = map.isDone();
        SaveState possibleNewState = new SaveState(map.getSimpleState());
        int newStateHashCode = possibleNewState.hashCode();
//        		String newStateString = map.toString();
        if (!(seenStatesValues.contains(newStateHashCode)
//                				&&seenStates.contains(possibleNewState)
                )){
            seenStates.add(possibleNewState);
            seenStatesValues.add(newStateHashCode);
            //			seenStateStrings.add(newStateString);
            stateOrigins.add(stateIndex);
            donePushes.add(aPush);
        }
        map.undo();
        triedPushes++;
        if (triedPushes%1000==0){
            System.out.println(triedPushes);
        }
        return isDone;
    }

    public boolean solveLevel(){
        int currentStateIndex = 0;
        solving = true;
        boolean solved = false;
        while (solving && currentStateIndex<seenStates.size()){
            map.loadSimpleState(seenStates.get(currentStateIndex));
            List<Coordinate[]> validPushes = validPushes(currentStateIndex);
            for (int i=0; i<validPushes.size();i++){
                boolean pushed = tryPush(currentStateIndex,validPushes.get(i));
                if(pushed){
                    solving=false;
                    solved=true;
                    break;
                }
                if(!solving){
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
        for (int i=0;i<validPushes.size();i++){
            allPushesString+=(validPushes.get(i)[0]+"    Direction: "+validPushes.get(i)[1]+"\n");
        }
        return allPushesString;
    }

    public HashMap<SaveState, Coordinate[]> levelSolution(){
        LinkedList<Coordinate[]> pushesToSolve = new LinkedList<Coordinate[]>();
        LinkedList<SaveState> statesToSolve = new LinkedList<SaveState>();
        HashMap<SaveState, Coordinate[]> solution = new HashMap<SaveState, Coordinate[]>();
        String solutionString = "NO_SOLUTION";

        if (solveLevel()) {
            int currentStateIndex = seenStates.size() - 1;
            solutionString="Solution: \n";
            while (currentStateIndex > 0){
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
        return solution;
    }

}
