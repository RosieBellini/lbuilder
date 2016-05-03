package team1;

/**
 * Represents the various objects in a Sokoban level. The SPACE,
 * PLAYER_ON_GOAL and BOX_ON_GOAL objects can be thought of as "meta-objects".
 * They aren't actually stored in this form when placed in a level; the
 * PLAYER_ON_GOAL and BOX_ON_GOAL objects represent that a given Coordinate has
 * both a GOAL and either a PLAYER or BOX in the current SaveState, and the
 * SPACE object represents that no other object is in that Coordinate. For
 * example, placing a BOX_ON_GOAL object at a given position will add that
 * position to the SaveState's boxPositions and goalPositions Sets.
 *
 */

public enum SokobanObject {
    SPACE, WALL, GOAL, BOX, BOX_ON_GOAL, PLAYER, PLAYER_ON_GOAL;

    /**
     * Converts a SokobanObject to its String representation in the standard
     * Sokoban map format.
     *
     * @return      A String representation of this SokobanObject
     */
    public String toString() {
        switch(this) {
            case SPACE:             return " ";
            case WALL:              return "#";
            case GOAL:              return ".";
            case BOX:               return "$";
            case BOX_ON_GOAL:       return "*";
            case PLAYER:            return "@";
            case PLAYER_ON_GOAL:    return "+";
            default:                return "!";
        }
    }

    /**
     * Converts a char to a SokobanObject based on the standard Sokoban map
     * format.
     *
     * @param   ch      The character to be converted to a SokobanObject
     *
     * @return          The resulting SokobanObject
     */
    public static SokobanObject charToSokobanObject(char ch) {
        SokobanObject object = null;

        switch(ch) {
            case '@':   object = SokobanObject.PLAYER;
                        break;
            case '$':   object = SokobanObject.BOX;
                        break;
            case '.':   object = SokobanObject.GOAL;
                        break;
            case '#':   object = SokobanObject.WALL;
                        break;
            case '+':   object = SokobanObject.PLAYER_ON_GOAL;
                        break;
            case '*':   object = SokobanObject.BOX_ON_GOAL;
                        break;
            case ' ':   object = SokobanObject.SPACE;
                        break;
            default:    throw new IllegalArgumentException("Invalid character");
        }

        return object;
    }

    /**
     * Returns the topmost object if the given SokobanObject represents two
     * objects at the same position, or the input object otherwise.
     *
     * @param   object      The SokobanObject from which to get the top layer
     *
     * @return              SokobanObject.PLAYER if the input is PLAYER_ON_GOAL,
     *                      SokobanObject.BOX if the input is BOX_ON_GOAL,
     *                      the input object otherwise
     */
    public static SokobanObject getTopLayer(SokobanObject object) {
        if (object == SokobanObject.PLAYER_ON_GOAL) {
            object = SokobanObject.PLAYER;
        } else if (object == SokobanObject.BOX_ON_GOAL) {
            object = SokobanObject.BOX;
        }

        return object;
    }
}
