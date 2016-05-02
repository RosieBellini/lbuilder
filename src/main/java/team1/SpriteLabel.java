package team1;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

/**
 * Represents a single position in the MapPanel and provides a mouse listener
 * to enable player interaction with it.
 */
public class SpriteLabel extends JLabel {
    private static final long serialVersionUID = 1L;
    private MapPanel mapPanel;
    private Coordinate position;
    private static SokobanObject paletteState = SokobanObject.WALL;
    private static SokobanMap.Mover mover;
    private static boolean mouseDown = false;
    private static boolean rightMouseDown = false;

    /**
     * SpriteLabel constructor.
     *
     * @param   position        The position of this SpriteLabel in the game map
     * @param   mapPanel        The MapPanel that this SpriteLabel belongs to
     */
    public SpriteLabel(Coordinate position, MapPanel mapPanel) {
        super();
        this.position = position;
        this.mapPanel = mapPanel;

        addMouseListener(new MouseAdapter() {

            // Sets mouse status fields to held and either moves the player or
            // modifies the object at this position depending on mode
            public void mousePressed(MouseEvent me) {
                if (!mapPanel.getPlayable()) {
                    if (me.getButton() == MouseEvent.BUTTON1) {
                        mouseDown = true;
                    }
                    if (me.getButton() == MouseEvent.BUTTON3) {
                        rightMouseDown = true;
                    }

                    modifyCell(me);

                } else {
                    move();
                }
            }

            // Sets mouse status fields to released
            public void mouseReleased(MouseEvent me) {
                if (!mapPanel.getPlayable()) {
                    if (me.getButton() == MouseEvent.BUTTON1) {
                        mouseDown = false;
                    }
                    if (me.getButton() == MouseEvent.BUTTON3) {
                        rightMouseDown = false;
                    }
                }
            }

            //Shows DEFAULT_HOVER icon in editor mode
            public void mouseEntered(MouseEvent me) {
                SokobanObject object = mapPanel.getSokobanMap().get(position);

                if (!mapPanel.getPlayable()) {
                    if (mouseDown || rightMouseDown) {
                        modifyCell(me);
                    }

                    if (me.getButton() == MouseEvent.NOBUTTON) {
                        if (object == SokobanObject.SPACE) {
                            setIcon(mapPanel.getIconMap().get("DEFAULT_HOVER"));
                        }

                    } else {
                        modifyCell(me);
                    }
                }
            }

            //Removes DEFAULT_HOVER icon in editor mode
            public void mouseExited(MouseEvent me) {
                SokobanObject object = mapPanel.getSokobanMap().get(position);

                if (!mapPanel.getPlayable()) {
                    if (object == SokobanObject.SPACE) {
                        setIcon(mapPanel.getIconMap().get("DEFAULT"));
                    } else {
                        setIcon(mapPanel.getIconMap().get(object.name()));
                    }
                }
            }
        });
    }

    /**
     * Changes the SokobanObject at the position of this SpriteLabel to the same
     * type as that selected in the tile palette.
     *
     * @param   me      The mouse event to trigger this action on
     */
    private void modifyCell(MouseEvent me) {
        SokobanMap map = mapPanel.getSokobanMap();
        map.storeState();

        if (mouseDown) {
            map.put(paletteState, position);
        } else if (rightMouseDown) {
            SokobanObject object = map.get(position);
            if (object != SokobanObject.PLAYER
                    || object != SokobanObject.PLAYER_ON_GOAL) {
                map.removeLayer(position);
            }
        }

        GamePanel.redraw();
        map.clearRedoStack();
    }

    /**
     * Sets the type of SokobanObject to place in editor mode.
     *
     * @param   paletteState        The type of SokobanObject selected in the
     *                              tile palette
     */
    public static void setPaletteState(SokobanObject paletteState) {
        SpriteLabel.paletteState = paletteState;
    }

    /**
     * Returns the SokobanMap.Mover used by this class for click movement.
     *
     * @return      The value of the static field mover
     */
    public static SokobanMap.Mover getMover() {
        return mover;
    }

    /**
     * Moves the player to the position of this SpriteLabel, interrupting
     * moves in execution if necessary, and pushing boxes if allowed.
     */
    private void move() {
        SokobanMap map = mapPanel.getSokobanMap();
        Coordinate playerPosition = map.getState().getPlayerPos();

        if (map.neighbours(position).contains(playerPosition)) {
            map.move(position.add(playerPosition.reverse()));
            GamePanel.redraw();

        } else {
            if (!(map.getIsCurrentlyMoving() || map.getIsDoingSolution())) {
                mover = map.new Mover(position, 75);
                mover.start();

            } else if (map.getIsCurrentlyMoving()
                    && !map.getIsDoingSolution()) {
                mover.interrupt();

                while (!mover.isInterrupted()) {
                    mover.interrupt();
                }

                mover = map.new Mover(position, 75);
                mover.start();
            }
        }
    }

    /**
     * Forces the status of the mouse buttons to "up" until they are pressed
     * again.
     */
    public static void disableButtons() {
        mouseDown = false;
        rightMouseDown = false;
    }
}
