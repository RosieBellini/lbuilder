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
		List<Coordinate[]> validPushes = new ArrayList<Coordinate[]>();
		for (Coordinate box : state.getBoxPositions()){
			for (Coordinate spaceNextToBox : map.neighbors(box))
				if (accessibleSpaces.contains(spaceNextToBox)){
					Coordinate[] aPush = {spaceNextToBox,spaceNextToBox.add(box.reverse())};
					validPushes.add(aPush);
				}
		}
		return validPushes;
	}

}
