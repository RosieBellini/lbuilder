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

        // Mouse listener:
        if (!this.playable) {
            addMouseListener(new MouseAdapter(){


                public void mousePressed(MouseEvent me)
                {
                    // If left mouse button is clicked
                    if (me.getButton() == MouseEvent.BUTTON1) {
                        spriteMap.getMap().put(LevelBuilder.state, position);
                    }

                    if (me.getButton() == MouseEvent.BUTTON3) {
                        spriteMap.getMap().removeLayer(position);
                    }
                    spriteMap.placeSprites();
                    spriteMap.getMap().storeState();
                    spriteMap.getMap().clearRedoStack();

                    int boxCount = spriteMap.getMap().getMyState().getBoxPositions().size();
                    int pressureCount = spriteMap.getMap().getMyState().getGoalPositions().size();
                    TilePalette.updateCounters(boxCount, pressureCount);
                }

                //	Mouse hover

                public void mouseEntered(MouseEvent me) {
                    if (spriteMap.getMap().get(position) == SokobanObject.SPACE) {
                        setIcon(spriteMap.getIconMap().get("DEFAULT_HOVER"));
                    }
                }

                public void mouseExited(MouseEvent me) {
                    if(spriteMap.getMap().get(position) == SokobanObject.SPACE){
                        setIcon(spriteMap.getIconMap().get("DEFAULT"));
                    } else {
                        setIcon(spriteMap.getIconMap().get(spriteMap.getMap().get(position).name()));
                    }
                }
            });
        }
    }

    public Coordinate getPosition() {
        return position;
    }
}
