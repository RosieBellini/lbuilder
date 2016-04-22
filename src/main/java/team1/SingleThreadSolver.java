package team1;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SingleThreadSolver {
	private SokobanMap map;
	private List<SaveState> seenStates;
	private List<Integer> stateOrigins;
	private List<Coordinate[]> pushes;

	public SingleThreadSolver(SokobanMap map){
		this.map=map;
		seenStates = new ArrayList<SaveState>();
		stateOrigins = new ArrayList<Integer>();
		pushes = new ArrayList<Coordinate[]>();		
	}

	private List<Coordinate[]> validPushes(SaveState state){
		Set<Coordinate> accessibleSpaces = map.accessibleSpaces(state.getWPos(), false);
		Set<Coordinate> allAccessibleSpaces = map.accessibleSpaces(state.getWPos(), true);
		List<Coordinate[]> validPushes = new ArrayList<Coordinate[]>();
		for (Coordinate box : state.getBoxPositions()){
			for (Coordinate spaceNextToBox : map.neighbors(box))
				if (accessibleSpaces.contains(spaceNextToBox)&&
						map.get(box.add(box.add(spaceNextToBox.reverse()))).name().equals("SPACE") ||
						map.get(box.add(box.add(spaceNextToBox.reverse()))).name().equals("GOAL")){
					Coordinate[] aPush = {spaceNextToBox,box.add(spaceNextToBox.reverse())};
					validPushes.add(aPush);
				}
		}
		return validPushes;
	}
	
	public String validPushesTestString(){
		List<Coordinate[]> validPushes = validPushes(map.getInitialState());
		String allPushesString = "";
		for (int i=0;i<validPushes.size();i++){
		 allPushesString+=(validPushes.get(i)[0]+"	 Direction: "+validPushes.get(i)[1]+"\n");
		}
		return allPushesString;
	}

}
