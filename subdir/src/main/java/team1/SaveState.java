package team1;

import java.util.*;

/**
 * Represents the positions of dynamic objects (the player and boxes)
 */
public final class SaveState {
    private final Coordinate wPos;
    private final Set<Coordinate> boxPositions;

    public SaveState(Coordinate wPos, Set<Coordinate> boxPositions) {
        this.wPos = new Coordinate(wPos.getX(), wPos.getY());
        this.boxPositions = new HashSet<Coordinate>(boxPositions);
    }

    public final Coordinate getWPos() {
        return wPos;
    }

    public final Set<Coordinate> getBoxPositions() {
        return boxPositions;
    }

}

