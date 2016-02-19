public enum SokobanObject {
    SPACE, WALL, GOAL, BOX, BOX_ON_GOAL, PLAYER, PLAYER_ON_GOAL;

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
