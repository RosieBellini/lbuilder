package team1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SingleThreadSolver {
    private SokobanMap map;
    private List<SaveState> seenStates;
    private List<Integer> stateOrigins;
    private List<Coordinate[]> donePushes;

    public SingleThreadSolver(SokobanMap map){
        this.map=map;
        seenStates = new ArrayList<SaveState>();
        seenStates.add(map.getState());
        stateOrigins = new ArrayList<Integer>();
        stateOrigins.add(-1);
        donePushes = new ArrayList<Coordinate[]>();	
        Coordinate[] emptyPush = {new Coordinate(0,0),new Coordinate(0,0)};
        donePushes.add(emptyPush);
    }

    private List<Coordinate[]> validPushes(int stateIndex){
        map.loadSimpleState(seenStates.get(stateIndex));
        SaveState state = seenStates.get(stateIndex);		
        Set<Coordinate> accessibleSpaces = map.accessibleSpaces(state.getWPos(), false);
        List<Coordinate[]> validPushes = new ArrayList<Coordinate[]>();
        for (Coordinate box : state.getBoxPositions()){
            for (Coordinate spaceNextToBox : map.neighbors(box))
                if (accessibleSpaces.contains(spaceNextToBox)&&
                        (map.get(box.add(box.add(spaceNextToBox.reverse()))).name().equals("SPACE") ||
                                map.get(box.add(box.add(spaceNextToBox.reverse()))).name().equals("GOAL"))){
                    Coordinate[] aPush = {spaceNextToBox,box.add(spaceNextToBox.reverse())};
                    validPushes.add(aPush);
                }
        }
        return validPushes;
    }

    private boolean tryPush(int stateIndex, Coordinate[] aPush){
        map.put(SokobanObject.PLAYER, aPush[0]);
        map.move(aPush[1]);
        int triedPushes = seenStates.size();
        boolean isDone = map.isDone();
        SaveState possibleNewState = map.getState();
        if (!seenStates.contains(possibleNewState)){
            seenStates.add(possibleNewState);
            stateOrigins.add(stateIndex);
            donePushes.add(aPush);
        }
        map.undo();
        if (triedPushes%1000==0){
        System.out.println(seenStates.size());
        }
        return isDone;
    }

    public boolean solveLevel(){
        int currentStateIndex = 0;
        boolean solving = true;
        while (solving && currentStateIndex<seenStates.size()){
            map.loadSimpleState(seenStates.get(currentStateIndex));
            List<Coordinate[]> validPushes = validPushes(currentStateIndex);
            for (int i=0; i<validPushes.size();i++){
                boolean pushed = tryPush(currentStateIndex,validPushes.get(i));
                if(pushed){
                    solving=false;
                    break;
                }
                if(!solving){
                    break;
                }
            }
            currentStateIndex++;
        }
        return true;
    }

    public String validPushesTestString(){
        List<Coordinate[]> validPushes = validPushes(0);
        String allPushesString = "";
        for (int i=0;i<validPushes.size();i++){
            allPushesString+=(validPushes.get(i)[0]+"	 Direction: "+validPushes.get(i)[1]+"\n");
        }
        return allPushesString;
    }

    public String levelSolution(){
        LinkedList<Coordinate[]> pushesToSolve = new LinkedList<Coordinate[]>();
        String solution="Solution: \n";
        if(solveLevel()){    
            int currentStateIndex=seenStates.size()-1;
            while (currentStateIndex>0){
                pushesToSolve.addFirst(donePushes.get(currentStateIndex));
                currentStateIndex=stateOrigins.get(currentStateIndex);
            }
        }        
        for (int i=0;i<pushesToSolve.size();i++){
        solution+=(pushesToSolve.get(i)[0]+"   Direction: "+pushesToSolve.get(i)[1]+"\n");
        }
        return solution;
    }

}
