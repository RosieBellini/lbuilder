import java.util.*;

public class MapContainer {
    private FixedSizeStack<SaveState> history;
    private Stack<SaveState> redoStack;
    private SokobanObject[][] map;

    public MapContainer(int xSize, int ySize) {
        map = new SokobanObject[ySize][xSize];
        for (SokobanObject[] row : map) {
            Arrays.fill(row, SokobanObject.SPACE);
        }
        history = new FixedSizeStack<SaveState>(20);
        history.push(new SaveState(new Coordinate(-1, -1), new HashSet<Coordinate>()));
        redoStack = new Stack<SaveState>();
    }

    public int getYSize() {
        return map.length;
    }

    public int getXSize() {
        return map[0].length;
    }

    public void storeState() {
        history.push(new SaveState(getWPos(), getBoxPositions()));
    }

    public void undo(boolean sendToRedoStack) {
        SaveState state = history.pop();
        if (historyLength() == 0) {
            history.push(state);
        } else if (sendToRedoStack) {
            redoStack.push(state);
        }
    }

    public void redo() {
        if (redoStack.size() != 0) {
            history.push(redoStack.pop());
        }
    }

    public void clearRedoStack() {
        redoStack.clear();
    }

    public int historyLength() {
        return history.size();
    }

    public int totalHistoryLength() {
        return history.getTotalSize();
    }

    public boolean isDone() {
        for (Coordinate coord : getBoxPositions()) {
            if (get(coord) != SokobanObject.BOX_ON_GOAL) {
                return false;
            }
        }
        return true;
    }

    public Coordinate getWPos() {
        return history.peek().getWPos();
    }

    public Set<Coordinate> getBoxPositions() {
        return history.peek().getBoxPositions();
    }

    public boolean put(SokobanObject object, Coordinate coord) {
        Set<Coordinate> boxPositions = getBoxPositions();
        Coordinate wPos = getWPos();
        SokobanObject target = get(coord);
        int x = coord.getX();
        int y = coord.getY();
        if (object == SokobanObject.PLAYER || object == SokobanObject.PLAYER_ON_GOAL) {
            switch(target) {
                case SPACE:
                case GOAL:      wPos = coord;
                                break;
                default:        return false;
            }
            if (object == SokobanObject.PLAYER_ON_GOAL) {
                map[y][x] = SokobanObject.GOAL;
            }
        } else if (object == SokobanObject.BOX || object == SokobanObject.BOX_ON_GOAL) {
            switch(target) {
                case SPACE:
                case GOAL:  if (!boxHere(coord)) {
                                boxPositions.add(coord);
                            }
                            break;
                default:    return false;
            }
            if (object == SokobanObject.BOX_ON_GOAL) {
                map[y][x] = SokobanObject.GOAL;
            }
        } else if (object == SokobanObject.WALL) {
            if (wPos.equals(coord) || boxHere(coord)) {
                return false;
            }
            map[y][x] = SokobanObject.WALL;
        } else {
            map[y][x] = object;
        }
        history.pop();
        history.push(new SaveState(wPos, boxPositions));
        return true;
    }

    public boolean boxHere(Coordinate coord) {
        return getBoxPositions().contains(coord);
    }

    public void removeLayer(Coordinate coord) {
        Set<Coordinate> boxPositions = getBoxPositions();
        Coordinate wPos = getWPos();
        if (wPos.equals(coord)) {
            wPos = new Coordinate(-1, -1);
        } else if (boxHere(coord)) {
            boxPositions.remove(coord);
        } else {
            put(SokobanObject.SPACE, coord);
        }
        history.pop();
        history.push(new SaveState(wPos, boxPositions));
    }

    public SokobanObject get(Coordinate coord) {
        int x = coord.getX();
        int y = coord.getY();
        if (x > getXSize() - 1 || y > getYSize() - 1 || x < 0 || y < 0) {
            return SokobanObject.WALL;
        } else if (boxHere(coord)) {
            if (map[y][x] == SokobanObject.GOAL) {
                return SokobanObject.BOX_ON_GOAL;
            }
            return SokobanObject.BOX;
        } else if (getWPos().equals(coord)) {
            if (map[y][x] == SokobanObject.GOAL) {
                return SokobanObject.PLAYER_ON_GOAL;
            }
            return SokobanObject.PLAYER;
        }
        return map[y][x];
    }

    public String toString() {
        String mapLine = "";
        for (int y = 0; y < getYSize(); y++) {
            for (int x = 0; x < getXSize(); x++) {
                Coordinate coord = new Coordinate(x, y);
                mapLine = mapLine + get(coord).toString();
            }
            mapLine = mapLine + "\n";

        }
        return mapLine;
    }
}
