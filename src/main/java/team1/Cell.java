package team1;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

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
            spriteMap.getMap().put(LevelBuilder.state, position);
        } else if (me.getButton() == MouseEvent.BUTTON3) {
            spriteMap.getMap().removeLayer(position);
        }
        spriteMap.placeSprites();
        spriteMap.getMap().clearRedoStack();

        int boxCount = spriteMap.getMap().getMyState().getBoxPositions().size();
        int pressureCount = spriteMap.getMap().getMyState().getGoalPositions().size();
        TilePalette.updateCounters(boxCount, pressureCount);
    }
}
