package team1;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple representation of the state of the current map.
 * Will be used in the solver.
 *
 */
public class GameState {
    private Coordinate wPos;
    private Set<Coordinate> boxPositions;
    
    public GameState(Coordinate wPos, Set<Coordinate> boxPositions){
    this.wPos = new Coordinate(wPos.getX(), wPos.getY());
    this.boxPositions = new HashSet<Coordinate>(boxPositions);
    }
    
    public final Coordinate getWPos() {
        return wPos;
    }
    
    public final Set<Coordinate> getBoxPositions() {
        return boxPositions;
    }
    
    public boolean equals(GameState otherGameState){
    return(wPos.equals(otherGameState.getWPos()) && boxPositions.equals(otherGameState.getBoxPositions()));
    }
    
    public int hashCode(){
    	return Arrays.hashCode(new Object[]{new Integer(wPos.getX()), new Integer(wPos.getY()),boxPositions.hashCode()});
    }
}
