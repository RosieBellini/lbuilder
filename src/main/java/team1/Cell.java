package team1;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

// import levelBuilder.TilePalette.ListListener;

public class Cell extends JLabel{

    private static final long serialVersionUID = 1L;
    private SokobanObject tileType;
    private SpriteMap spriteMap;
    // Coordinate values: Named i and j so they don't affect LayoutManager.
    private Coordinate position;
    private boolean playable;

    public Cell(Coordinate position, SpriteMap spriteMap, boolean playable) {
        super();
        this.playable=playable;
        this.position = position;
        this.spriteMap = spriteMap;
        tileType = SokobanObject.SPACE;
//        setIcon(new ImageIcon(getClass().getResource("/tileset01/DEFAULT.png"), "Default"));

        // Mouse listener:
        if (!this.playable) {
            addMouseListener(new MouseAdapter(){


                public void mousePressed(MouseEvent me)
                {
                    // If left mouse button is clicked
                    if (me.getButton() == MouseEvent.BUTTON1) {
                        spriteMap.getMap().put(LevelBuilder.state, position);
                        tileType = LevelBuilder.state;
                    }

                    if (me.getButton() == MouseEvent.BUTTON3) {
//                        setIcon(new ImageIcon(getClass().getResource("/tileset01/DEFAULT_HOVER.png"), "Default"));
                        spriteMap.getMap().removeLayer(position);
                        tileType = SokobanObject.SPACE;
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
                    if (tileType.equals(SokobanObject.SPACE)) {
                        setIcon(spriteMap.getIconMap().get("DEFAULT_HOVER"));
                    }
                }

                public void mouseExited(MouseEvent me) {
                	if(spriteMap.getMap().get(position).name().equals("SPACE")){
                		setIcon(spriteMap.getIconMap().get("DEFAULT"));
                	}
                	else{
                    setIcon(spriteMap.getIconMap().get(spriteMap.getMap().get(position).name()));
                	}
                }
            });
        }
    }

    public SokobanObject getTileType()
    {
        return tileType;
    }

    public Coordinate getPosition() {
        return position;
    }
}
