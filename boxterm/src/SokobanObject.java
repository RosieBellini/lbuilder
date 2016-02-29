/**
 * Represents the various objects in a Sokoban level. Note that the PLAYER,
 * PLAYER_ON_GOAL and BOX_ON_GOAL objects can be thought of as "meta-objects".
 * They aren't actually stored in this form when placed in a level; the
 * PLAYER_ON_GOAL and BOX_ON_GOAL objects represent that a given coordinate has
 * a GOAL object in the array of static objects and either a PLAYER or BOX in
 * the current SaveState, and likewise for the PLAYER object with a SPACE and
 * the worker's position. For example, placing a BOX_ON_GOAL object will add a
 * BOX object to the current savestate and a GOAL to the static object array.
 *
 */

public enum SokobanObject {
    SPACE, WALL, GOAL, BOX, BOX_ON_GOAL, PLAYER, PLAYER_ON_GOAL;

    /**
     * Converts a SokobanObject to a String
     *
     * @return  string representation of this SokobanObject
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
     * Converts a char to a SokobanObject
     *
     * @param ch    the character to be converted
     * @return      the resulting SokobanObject
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
        }
        return object;
    }
}
