package team1;

import java.awt.Toolkit;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Provides methods to move objects around in a MapContainer
 */
public class SokobanMap extends MapContainer {

	/**
	 * Interprets the contents of the "level" file and stores it as a SokobanMap
	 */
	public static SokobanMap importLevel(InputStream levelFile) {
		// if (levelFile == null) { //If Getfile is cancelled.
		// 	return;
		// }
		int x = 0;
		int y = 0;
		int xSize = 0;
		int ySize = 0;
		/*
		 * First, get the raw data as an array of strings and use this to
		 * determine the size of the level
		 */
		ArrayList<String> levelLines = new ArrayList<String>();
		Scanner level = new Scanner(levelFile);
		while (level.hasNextLine()) {
			String line = level.nextLine();
			if (line.length() > xSize) {
				xSize = line.length();
			}
			levelLines.add(line);
		}
		ySize = levelLines.size();

		SokobanMap map = new SokobanMap(xSize, ySize, 20);
		/*
		 * Then convert the raw data into a SokobanMap using the static
		 * method charToSokobanObject from the SokobanObject class
		 */
		for (String line: levelLines) {
			for (char ch: line.toCharArray()) {
				Coordinate coord = new Coordinate(x, y);
				SokobanObject object = SokobanObject.charToSokobanObject(ch);
				map.put(object, coord);
				x++;
			}
			x = 0;
			y++;
		}
		level.close();
        return map;
	}

    public SokobanMap(int xSize, int ySize, int maxUndos) {
        super(xSize, ySize, maxUndos);
    }

    /**
     * Evaluates if it's possible to move a BOX or PLAYER to the given
     * coordinate.
     *
     * TODO:    Currently doesn't check to see if there are walls or boxes
     *          blocking the player from reaching this spot. Need to implement
     *          algorithm to calculate this for the solver
     *
     * @return  true if a BOX or PLAYER can be moved here, false otherwise
     */
    public boolean canMoveHere(Coordinate coord) {
        SokobanObject target = super.get(coord);
        if (target == SokobanObject.SPACE || target == SokobanObject.GOAL) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Moves the object at a given position in the direction specified,
     * respecting the rules of the game.
     *
     * @param iCoord        position of the object to move
     * @param direction     direction in which to move it
     * @return  true if the object was moved, false if this would violate the
     *          rules of the game
     */
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

    /**
     * Moves the player in the given direction, respecting the rules of the
     * game and pushing boxes when necessary.
     *
     * TODO:    make sure this actually works when the magnitude of the direction
     *          is more than 1
     *
     * @param direction     the direction in which to move the player
     * @return true if the move is permitted, false otherwise
     */
    public boolean move(Coordinate direction) {
        Coordinate wCoord = super.getMyState().getWPos();
        Coordinate nCoord = wCoord.add(direction);
        super.storeState();

        /*
         * this slightly confusing chunk of code makes use of the fact that
         * methods are completely executed when an equality check is performed.
         * It tries to move the player in the specified direction; if this
         * fails, it tries to move the object in front of the player in the
         * specified direction; if this succeeds, the player can be moved to the
         * coordinates that the user specified.
         */
        if (teleport(wCoord, direction)) {
        } else if (teleport(nCoord, direction)) {
            teleport(wCoord, direction);

        /*
         * otherwise, if we couldn't perform the move within the boundaries of
         * the game's rules, we have to undo the last move without sticking it
         * in the redo stack to avoid the undo stack getting filled up with
         * identical states
         */
        } else {
            super.undo(false);
            Toolkit.getDefaultToolkit().beep();
            return false;
        }

        /*
         * don't give the player the possibility of jumping to inaccessible
         * states by reloading past states
         */
        super.clearRedoStack();
        return true;
    }
}
