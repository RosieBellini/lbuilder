package team1;

import java.util.*;

/**
 * Represents the positions of dynamic objects (the player and boxes)
 */
public final class SaveState {
    private final Coordinate wPos;
    private final Set<Coordinate> boxPositions;
    private final Set<Coordinate> wallPositions;
    private final Set<Coordinate> goalPositions;

    public SaveState(Coordinate wPos, Set<Coordinate> boxPositions, Set<Coordinate> wallPositions, Set<Coordinate> goalPositions) {
        this.wPos = new Coordinate(wPos.getX(), wPos.getY());
        this.boxPositions = new HashSet<Coordinate>(boxPositions);
        this.wallPositions = new HashSet<Coordinate>(wallPositions);
        this.goalPositions = new HashSet<Coordinate>(goalPositions);
    }

    public final Coordinate getWPos() {
        return wPos;
    }

    public boolean isEmpty(Coordinate position) {
        return boxPositions.contains(position) || goalPositions.contains(position) || wallPositions.contains(position);
    }

    public void makeEmpty(Coordinate position) {
        boxPositions.remove(position);
        wallPositions.remove(position);
        goalPositions.remove(position);
    }

    public final Set<Coordinate> getBoxPositions() {
        return boxPositions;
    }

    public final Set<Coordinate> getWallPositions() {
        return wallPositions;
    }

    public final Set<Coordinate> getGoalPositions() {
        return goalPositions;
    }

    public static Set<Coordinate> symmetricDiff(Set<Coordinate> set1, Set<Coordinate> set2) {
        Set<Coordinate> set1Copy = new HashSet<Coordinate>(set1);
        Set<Coordinate> set2Copy = new HashSet<Coordinate>(set2);
        set1Copy.removeAll(set2);
        set2Copy.removeAll(set1);
        Set<Coordinate> differences = new HashSet<Coordinate>();
        differences.addAll(set1Copy);
        differences.addAll(set2Copy);
        return differences;
    }

    public Set<Coordinate> compareStates(SaveState someState) {
        Set<Coordinate> changedPlaces = new HashSet<Coordinate>();
        if (!wPos.equals(someState.getWPos())) {
            changedPlaces.add(wPos);
            changedPlaces.add(someState.getWPos());
        }
        changedPlaces.addAll(symmetricDiff(boxPositions, someState.getBoxPositions()));
        changedPlaces.addAll(symmetricDiff(wallPositions, someState.getWallPositions()));
        changedPlaces.addAll(symmetricDiff(goalPositions, someState.getGoalPositions()));
        return changedPlaces;
    }
}

