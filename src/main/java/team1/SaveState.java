package team1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the positions of dynamic objects (the player and boxes)
 */
public final class SaveState {
    private Coordinate wPos;
    private Set<Coordinate> boxPositions;
    private Set<Coordinate> wallPositions;
    private Set<Coordinate> goalPositions;
    private final boolean simpleState;

    public SaveState(
                        Coordinate wPos,
                        Set<Coordinate> boxPositions,
                        Set<Coordinate> wallPositions,
                        Set<Coordinate> goalPositions
                    ) {
        this.wPos = new Coordinate(wPos.x, wPos.y);
        this.boxPositions = new HashSet<Coordinate>(boxPositions);
        this.wallPositions = new HashSet<Coordinate>(wallPositions);
        this.goalPositions = new HashSet<Coordinate>(goalPositions);

        simpleState = false;
    }

    public SaveState(SaveState stateToCopy) {
        this.wPos = new Coordinate(stateToCopy.getWPos());
        this.boxPositions = new HashSet<Coordinate>();
        this.wallPositions = new HashSet<Coordinate>();
        this.goalPositions = new HashSet<Coordinate>();

        this.boxPositions.addAll(stateToCopy.getBoxPositions());

        if (stateToCopy.isSimple()) {
            simpleState = true;
        } else {
            this.wallPositions.addAll(stateToCopy.getWallPositions());
            this.goalPositions.addAll(stateToCopy.getGoalPositions());
            simpleState = false;
        }
    }

    public SaveState(Coordinate wPos, Set<Coordinate> boxPositions) {
        this.wPos = new Coordinate(wPos);
        this.boxPositions = new HashSet<Coordinate>(boxPositions);
        this.wallPositions = new HashSet<Coordinate>();
        this.goalPositions = new HashSet<Coordinate>();

        simpleState = true;
    }

    public SaveState() {
        this.wPos = new Coordinate(-1, -1);
        this.boxPositions = new HashSet<Coordinate>();
        this.wallPositions = new HashSet<Coordinate>();
        this.goalPositions = new HashSet<Coordinate>();

        simpleState = false;
    }

    public SokobanObject get(Coordinate position) {
        SokobanObject object;

        if (wallPositions.contains(position)) {
            object = SokobanObject.WALL;
        } else if (goalPositions.contains(position)) {
            if (wPos.equals(position)) {
                object = SokobanObject.PLAYER_ON_GOAL;
            } else if (boxPositions.contains(position)) {
                object = SokobanObject.BOX_ON_GOAL;
            } else {
                object = SokobanObject.GOAL;
            }
        } else if (boxPositions.contains(position)) {
            object = SokobanObject.BOX;
        } else if (wPos.equals(position)) {
            object = SokobanObject.PLAYER;
        } else {
            object = SokobanObject.SPACE;
        }

        return object;
    }

    public boolean put(SokobanObject object, Coordinate coord) {
        SokobanObject target = get(coord);
        boolean success = false;

        switch (object) {
        case PLAYER:
            if (target == SokobanObject.SPACE || target == SokobanObject.GOAL) {
                wPos = coord;
                success = true;
            }
            break;

        case BOX:
            if (target == SokobanObject.SPACE || target == SokobanObject.GOAL) {
                success = boxPositions.add(coord);
            }
            break;
        case WALL:
            if (target == SokobanObject.SPACE) {
                success = wallPositions.add(coord);
            }
            break;

        case GOAL:
            if (!wallPositions.contains(coord)) {
                success = goalPositions.add(coord);
            }
            break;

        case PLAYER_ON_GOAL:
        case BOX_ON_GOAL:
            if (put(SokobanObject.GOAL, coord)) {
                success = put(SokobanObject.getTopLayer(object), coord);
            }
            break;

        case SPACE:
            makeEmpty(coord);
            success = true;
            break;

        default:
            success = false;
        }

        return success;
    }

    public void removeLayer(Coordinate coord) {
        if (wPos.equals(coord)) {
            wPos = new Coordinate(-1, -1);
        } else if (get(coord).equals(SokobanObject.BOX_ON_GOAL)) {
            boxPositions.remove(coord);
        } else if (!get(coord).equals(SokobanObject.PLAYER_ON_GOAL)) {
            makeEmpty(coord);
        }
    }

    public final Coordinate getWPos() {
        return wPos;
    }

    private void makeEmpty(Coordinate position) {
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (!(obj instanceof SaveState)) {
            return false;
        } else {
            return this.hashCode() == obj.hashCode();
        }
    }

    @Override
    public int hashCode() {
            ArrayList<Coordinate> coordArrayList = new ArrayList<Coordinate>();
            coordArrayList.addAll(boxPositions);
            if (!simpleState){
                coordArrayList.addAll(wallPositions);
                coordArrayList.addAll(goalPositions);
            }
            Collections.sort(coordArrayList);
            coordArrayList.add(wPos.mult(-1));
            int[] coordListXY = new int[coordArrayList.size() * 2];
            int p = 0;
            for (int i = 0; i < coordArrayList.size(); i++) {
                coordListXY[p] = coordArrayList.get(i).x;
                p++;
                coordListXY[p] = coordArrayList.get(i).y;
                p++;
            }
            return Arrays.hashCode(coordListXY);
    }

    public boolean isSimple() {
        return simpleState;
    }
}
