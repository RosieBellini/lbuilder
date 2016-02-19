import java.awt.Toolkit;

public class SokobanMap extends MapContainer {

    public SokobanMap(int xSize, int ySize) {
        super(xSize, ySize);
    }

    public boolean canMoveHere(Coordinate coord) {
        SokobanObject target = super.get(coord);
        if (target == SokobanObject.SPACE || target == SokobanObject.GOAL) {
            return true;
        } else {
            return false;
        }
    }

    public boolean teleport(Coordinate iCoord, Coordinate direction) {
        Coordinate fCoord = iCoord.add(direction);
        if (canMoveHere(fCoord)) {
            SokobanObject source = super.get(iCoord);
            if (source != SokobanObject.WALL && source != SokobanObject.GOAL) {
                super.removeLayer(iCoord);
                if (source == SokobanObject.PLAYER_ON_GOAL) {
                    source = SokobanObject.PLAYER;
                } else if (source == SokobanObject.BOX_ON_GOAL) {
                    source = SokobanObject.BOX;
                }
                super.put(source, fCoord);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean move(Coordinate direction) {
        Coordinate wCoord = super.getWPos();
        Coordinate nCoord = wCoord.add(direction);
        super.storeState();
        if (teleport(wCoord, direction)) {
        } else if (teleport(nCoord, direction)) {
            teleport(wCoord, direction);
        } else {
            super.undo(false);
            Toolkit.getDefaultToolkit().beep();
            return false;
        }
        super.clearRedoStack();
        return true;
    }
}
