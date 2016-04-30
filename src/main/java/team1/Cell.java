package team1;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

/**
 * Cell Class. Used to represent an individual 'cell' in the grid for the Map Editor.
 * Each cell has a mouse listener so that users can click and change the state of each cell.
 *
 * @version 22/04/2016
 */

public class Cell extends JLabel{

    private static final long serialVersionUID = 1L;
    private SpriteMap spriteMap;
    private Coordinate position;
    private static SokobanObject paletteState = SokobanObject.WALL;

    public Cell(Coordinate position, SpriteMap spriteMap) {
        super();
        this.position = position;
        this.spriteMap = spriteMap;

        addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent me)
            {
                if (!spriteMap.getPlayable()) {
                    modifyCell(me);
                }
                else {
                    // spriteMap.getSokobanMap().findPath(position);
                    try {
                        spriteMap.getSokobanMap().moveTo(position);
                    } catch (InterruptedException e) {

                    }
                }
            }

            public void mouseEntered(MouseEvent me) {
                if (!spriteMap.getPlayable()) {
                    if (me.getButton() == MouseEvent.NOBUTTON) {
                        if (spriteMap.getSokobanMap().get(position) == SokobanObject.SPACE) {
                            setIcon(spriteMap.getIconMap().get("DEFAULT_HOVER"));
                        }
                    } else {
                        modifyCell(me);
                    }
                }
            }

            public void mouseExited(MouseEvent me) {
                if (!spriteMap.getPlayable()) {
                    if (spriteMap.getSokobanMap().get(position) == SokobanObject.SPACE) {
                        setIcon(spriteMap.getIconMap().get("DEFAULT"));
                    } else {
                        setIcon(spriteMap.getIconMap().get(spriteMap.getSokobanMap().get(position).name()));
                    }
                }
            }
        });
    }

    private void modifyCell(MouseEvent me) {
        spriteMap.getSokobanMap().storeState();
        if (me.getButton() == MouseEvent.BUTTON1) {
            spriteMap.getSokobanMap().put(paletteState, position);
        } else if (me.getButton() == MouseEvent.BUTTON3) {
            if (spriteMap.getSokobanMap().get(position) != SokobanObject.PLAYER && spriteMap.getSokobanMap().get(position) != SokobanObject.PLAYER_ON_GOAL) {
                spriteMap.getSokobanMap().removeLayer(position);
            }
        }

        SokobanGame.redraw();
        spriteMap.getSokobanMap().clearRedoStack();
    }

    public static void setPaletteState(SokobanObject paletteState) {
        Cell.paletteState = paletteState;
    }
}
