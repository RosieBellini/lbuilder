package team1;

import java.util.*;

/**
 * Represents the positions of dynamic objects (the player and boxes)
 */
public final class SaveState {
    private Coordinate wPos;
    private Set<Coordinate> boxPositions;
    private Set<Coordinate> wallPositions;
    private Set<Coordinate> goalPositions;

    public SaveState(Coordinate wPos, Set<Coordinate> boxPositions, Set<Coordinate> wallPositions, Set<Coordinate> goalPositions) {
        this.wPos = new Coordinate(wPos.getX(), wPos.getY());
        this.boxPositions = new HashSet<Coordinate>(boxPositions);
        this.wallPositions = new HashSet<Coordinate>(wallPositions);
        this.goalPositions = new HashSet<Coordinate>(goalPositions);
    }

    public SaveState(SaveState stateToCopy) {
        this.wPos = new Coordinate(stateToCopy.getWPos());
        this.boxPositions = new HashSet<Coordinate>(stateToCopy.getBoxPositions());
        this.wallPositions = new HashSet<Coordinate>(stateToCopy.getWallPositions());
        this.goalPositions = new HashSet<Coordinate>(stateToCopy.getGoalPositions());
    }

    public SaveState(Coordinate wPos, SaveState stateToCopy) {
        this.wPos = new Coordinate(wPos);
        this.boxPositions = new HashSet<Coordinate>(stateToCopy.getBoxPositions());
        this.wallPositions = new HashSet<Coordinate>(stateToCopy.getWallPositions());
        this.goalPositions = new HashSet<Coordinate>(stateToCopy.getGoalPositions());
    }

    public SokobanObject get(Coordinate position) {
        if (wallPositions.contains(position)) {
            return SokobanObject.WALL;
        } else if (goalPositions.contains(position)) {
            if (wPos.equals(position)) {
                return SokobanObject.PLAYER_ON_GOAL;
            } else if (boxPositions.contains(position)) {
                return SokobanObject.BOX_ON_GOAL;
            }
            return SokobanObject.GOAL;
        } else if (boxPositions.contains(position)) {
            return SokobanObject.BOX;
        } else if (wPos.equals(position)) {
            return SokobanObject.PLAYER;
        } else {
            return SokobanObject.SPACE;
        }
    }

    public boolean put(SokobanObject object, Coordinate coord) {
        SokobanObject target = get(coord);

        if (object == SokobanObject.PLAYER || object == SokobanObject.PLAYER_ON_GOAL) {
            switch(target) {
                case SPACE:
                case GOAL:      wPos = coord;
                                break;
                default:        return false;
            }
            if (object == SokobanObject.PLAYER_ON_GOAL) {
                goalPositions.add(coord);
            }

        } else if (object == SokobanObject.BOX || object == SokobanObject.BOX_ON_GOAL) {
            switch(target) {
                case SPACE:
                case GOAL:  if (!boxPositions.contains(coord)) {
                                boxPositions.add(coord);
                }
                break;
                default:    return false;
            }
            if (object == SokobanObject.BOX_ON_GOAL) {
                goalPositions.add(coord);
            }

        } else if (object == SokobanObject.WALL) {
            if (wPos.equals(coord) || boxPositions.contains(coord)) {
                return false;
            }
            wallPositions.add(coord);
        } else if (object == SokobanObject.GOAL) {
            if (wallPositions.contains(coord)) {
                return false;
            }
            goalPositions.add(coord);
        } else if (object == SokobanObject.SPACE) {
            boxPositions.remove(coord);
            wallPositions.remove(coord);
            goalPositions.remove(coord);
        }
        return true;
    }

    public void removeLayer(Coordinate coord) {
        Coordinate wPos = getWPos();
        if (wPos.equals(coord)) {
            wPos = new Coordinate(-1, -1);
        } else if (get(coord).equals(SokobanObject.BOX_ON_GOAL)) {
            boxPositions.remove(coord);
        } else if (get(coord).equals(SokobanObject.BOX)) {
            boxPositions.remove(coord);
        } else if (get(coord).equals(SokobanObject.WALL)) {
            wallPositions.remove(coord);
        } else if (get(coord).equals(SokobanObject.GOAL)) {
            goalPositions.remove(coord);
        }
    }

    public final Coordinate getWPos() {
        return wPos;
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

    public boolean equals(SaveState someState) {
        return wPos.equals(someState.getWPos())
            && boxPositions.equals(someState.getBoxPositions())
            && wallPositions.equals(someState.getWallPositions())
            && goalPositions.equals(someState.getGoalPositions());
    }

    public int hashCode(){
        return Arrays.hashCode(new Object[]{wPos.hashCode(), boxPositions.hashCode(), wallPositions.hashCode(), goalPositions.hashCode()});
    }
}

