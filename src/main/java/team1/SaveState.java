package team1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the positions of SokobanObjects and provides methods for examining
 * a given position, placing objects while abiding the rules of Sokoban, and
 * getting the positions of all objects of a given type. SaveStates may be of
 * "full" type, in which the positions of the player, boxes, walls and goals are
 * stored, or "simple", in which only the player and box positions are stored.
 * A player position of (-1, -1) is equivalent to "no player".
 */
public final class SaveState {
    private Coordinate playerPos;
    private Set<Coordinate> boxPositions;
    private Set<Coordinate> wallPositions;
    private Set<Coordinate> goalPositions;
    private final boolean simpleState;

    /**
     * Full SaveState constructor.
     *
     * @param   playerPos       The Coordinate of the player
     * @param   boxPositions    The Set of Coordinates of box positions
     * @param   wallPositions   The Set of Coordinates of wall positions
     * @param   goalPositions   The Set of Coordinates of goal positions
     */
    public SaveState(
                        Coordinate playerPos,
                        Set<Coordinate> boxPositions,
                        Set<Coordinate> wallPositions,
                        Set<Coordinate> goalPositions
                    ) {
        this.playerPos = new Coordinate(playerPos.x, playerPos.y);
        this.boxPositions = new HashSet<Coordinate>(boxPositions);
        this.wallPositions = new HashSet<Coordinate>(wallPositions);
        this.goalPositions = new HashSet<Coordinate>(goalPositions);

        simpleState = false;
    }

    /**
     * Simple SaveState constructor.
     *
     * @param   playerPos       The Coordinate of the player
     * @param   boxPositions    The Set of Coordinates of box positions
     */
    public SaveState(Coordinate playerPos, Set<Coordinate> boxPositions) {
        this.playerPos = new Coordinate(playerPos);
        this.boxPositions = new HashSet<Coordinate>(boxPositions);
        this.wallPositions = new HashSet<Coordinate>();
        this.goalPositions = new HashSet<Coordinate>();

        simpleState = true;
    }

    /**
     * Empty SaveState constructor.
     */
    public SaveState() {
        this.playerPos = new Coordinate(-1, -1);
        this.boxPositions = new HashSet<Coordinate>();
        this.wallPositions = new HashSet<Coordinate>();
        this.goalPositions = new HashSet<Coordinate>();

        simpleState = false;
    }

    /**
     * Constructor for a SaveState clone. Respects simple and full types.
     */
    public SaveState(SaveState stateToCopy) {
        this.playerPos = new Coordinate(stateToCopy.getWPos());
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

    /**
     * Returns the type of the SaveState.
     *
     * @return      True if the state is simple, false otherwise
     */
    public boolean isSimple() {
        return simpleState;
    }

    /**
     * Returns the positions of all boxes in this SaveState.
     *
     * @return      The Set of Coordinates of box positions
     */
    public final Set<Coordinate> getBoxPositions() {
        return boxPositions;
    }

    /**
     * Returns the positions of all walls in this SaveState.
     *
     * @return      The Set of Coordinates of wall positions
     */
    public final Set<Coordinate> getWallPositions() {
        return wallPositions;
    }

    /**
     * Returns the positions of all boxes in this SaveState.
     *
     * @return      The Set of Coordinates of box positions
     */
    public final Set<Coordinate> getGoalPositions() {
        return goalPositions;
    }

    /**
     * Returns the player's position.
     *
     * @return      The Coordinate of the player
     */
    public final Coordinate getWPos() {
        return playerPos;
    }

    /**
     * Removes all objects from a given position.
     *
     * @param   position        The Coordinate to wipe clean
     *
     * TODO: this doesn't remove the player. Should it?
     */
    private void makeEmpty(Coordinate position) {
        boxPositions.remove(position);
        wallPositions.remove(position);
        goalPositions.remove(position);
    }

    /**
     * Returns the SokobanObject at the given position. This includes objects
     * not explicitly stored by the SaveState class, e.g. if both a box and
     * a goal are on the given space the method will return
     * SokobanObject.BOX_ON_GOAL and so forth.
     *
     * @param   position        The Coordinate to examine
     */
    public SokobanObject get(Coordinate position) {
        SokobanObject object;

        if (wallPositions.contains(position)) {
            object = SokobanObject.WALL;

        } else if (goalPositions.contains(position)) {
            if (playerPos.equals(position)) {
                object = SokobanObject.PLAYER_ON_GOAL;
            } else if (boxPositions.contains(position)) {
                object = SokobanObject.BOX_ON_GOAL;
            } else {
                object = SokobanObject.GOAL;
            }

        } else if (boxPositions.contains(position)) {
            object = SokobanObject.BOX;

        } else if (playerPos.equals(position)) {
            object = SokobanObject.PLAYER;

        } else {
            object = SokobanObject.SPACE;
        }

        return object;
    }

    /**
     * Places the given SokobanObject at the given position, respecting the
     * rules of the game. For example, will not allow a wall to be placed on top
     * of a goal, nor a box on a player etc.
     *
     * @param   object      The SokobanObject to place
     * @param   position    The Coordinate at which to place the SokobanObject
     *
     * @return              True if the operation was allowed, false otherwise
     */
    public boolean put(SokobanObject object, Coordinate position) {
        SokobanObject target = get(position);
        boolean success = false;

        switch (object) {
        case PLAYER:
            if (target == SokobanObject.SPACE || target == SokobanObject.GOAL) {
                playerPos = position;
                success = true;
            }
            break;

        case BOX:
            if (target == SokobanObject.SPACE || target == SokobanObject.GOAL) {
                success = boxPositions.add(position);
            }
            break;
        case WALL:
            if (target == SokobanObject.SPACE) {
                success = wallPositions.add(position);
            }
            break;

        case GOAL:
            if (!wallPositions.contains(position)) {
                success = goalPositions.add(position);
            }
            break;

        case PLAYER_ON_GOAL:
        case BOX_ON_GOAL:
            if (put(SokobanObject.GOAL, position)) {
                success = put(SokobanObject.getTopLayer(object), position);
            }
            break;

        case SPACE:
            makeEmpty(position);
            success = true;
            break;

        default:
            success = false;
        }

        return success;
    }

    /**
     * Removes the uppermost object from the given position. For example, a
     * BOX will become a SPACE, but a BOX_ON_GOAL will become a BOX.
     *
     * @param   position        The position from which to remove an object
     */
    public void removeLayer(Coordinate position) {
        if (playerPos.equals(position)) {
            playerPos = new Coordinate(-1, -1);

        } else if (get(position).equals(SokobanObject.BOX_ON_GOAL)) {
            boxPositions.remove(position);

        } else if (!get(position).equals(SokobanObject.PLAYER_ON_GOAL)) {
            makeEmpty(position);
        }
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (!(obj instanceof SaveState)) {
            return false;
        } else if (this == obj) {
            return true;
        } else {
            return this.hashCode() == obj.hashCode();
        }
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
            ArrayList<Coordinate> coordArrayList = new ArrayList<Coordinate>();
            coordArrayList.addAll(boxPositions);

            if (!simpleState){
                coordArrayList.addAll(wallPositions);
                coordArrayList.addAll(goalPositions);
            }
            Collections.sort(coordArrayList);
            coordArrayList.add(playerPos.mult(-1));
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
}
