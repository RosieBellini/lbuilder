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
    private boolean playable;

    
    public Cell(Coordinate position, SpriteMap spriteMap, boolean playable) {
        super();
        this.playable=playable;
        this.position = position;
        this.spriteMap = spriteMap;

        if (!this.playable) {
            addMouseListener(new MouseAdapter(){
                public void mousePressed(MouseEvent me)
                {
                    modifyCell(me);
                }

                public void mouseEntered(MouseEvent me) {
                    if (me.getButton() == MouseEvent.NOBUTTON) {
                        if (spriteMap.getMap().get(position) == SokobanObject.SPACE) {
                            setIcon(spriteMap.getIconMap().get("DEFAULT_HOVER"));
                        }
                    } else {
                        modifyCell(me);
                    }
                }

                public void mouseExited(MouseEvent me) {
                    if (spriteMap.getMap().get(position) == SokobanObject.SPACE) {
                        setIcon(spriteMap.getIconMap().get("DEFAULT"));
                    } else {
                        setIcon(spriteMap.getIconMap().get(spriteMap.getMap().get(position).name()));
                    }
                }
            });
        }
    }

    public void modifyCell(MouseEvent me) {
        spriteMap.getMap().storeState();
        if (me.getButton() == MouseEvent.BUTTON1) {
            spriteMap.getMap().put(BoxTerm.builder.state, position);
            // if (LevelBuilder.state == SokobanObject.PLAYER) {
            //     spriteMap.getMap().setInitialState(new SaveState(spriteMap.getMap().getMyState()));
            // }
        } else if (me.getButton() == MouseEvent.BUTTON3) {
            spriteMap.getMap().removeLayer(position);
        }
        spriteMap.placeSprites();
        spriteMap.getMap().clearRedoStack();

        TilePalette.updateCounters();
    }
}
