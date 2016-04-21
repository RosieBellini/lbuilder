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

    public Cell(Coordinate position, SpriteMap spriteMap, boolean playable) {
        super();
        this.position = position;
        this.spriteMap = spriteMap;
        tileType = SokobanObject.SPACE;
        setIcon(new ImageIcon(getClass().getResource("/tileset03/SPACE.png"), "Default"));

        // Mouse listener:
        if (!playable) {
            addMouseListener(new MouseAdapter(){


                public void mousePressed(MouseEvent me)
                {
                    spriteMap.getMap().storeState();
                    // If left mouse button is clicked
                    if (me.getButton() == MouseEvent.BUTTON1) {
                        // setIcon(SpriteMap.getIconMap().get(LevelBuilder.state.name()));
                        spriteMap.getMap().put(LevelBuilder.state, position);
                        tileType = LevelBuilder.state;
                    }


                    // If right mouse is clicked, undo
                    // If right mouse is clicked, undo
                    if (me.getButton() == MouseEvent.BUTTON3) {

                        if (LevelBuilder.state == SokobanObject.BOX && TilePalette.boxCount != 0) {
                            TilePalette.boxCount -= 1;
                            TilePalette.boxCounter.setText("" + TilePalette.boxCount); // decrease box count
                        }
                        if (LevelBuilder.state == SokobanObject.GOAL && TilePalette.pressureCount != 0) {
                            TilePalette.pressureCount -= 1;
                            TilePalette.pressureCounter.setText("" + TilePalette.pressureCount); // decrease pressure pad count
                        }

                        setIcon(new ImageIcon(getClass().getResource("/tileset03/SPACE_HOVER.png"), "Default"));
                        tileType = SokobanObject.SPACE;
                    }
                    spriteMap.placeSprites();
                }

                //	Mouse hover

                public void mouseEntered(MouseEvent me) {
                    if (tileType.equals(SokobanObject.SPACE)) {
                        setIcon(new ImageIcon(getClass().getResource("/tileset03/SPACE_HOVER.png")));
                    }
                }

                public void mouseExited(MouseEvent me) {
                    if (tileType.equals(SokobanObject.SPACE)) {
                        setIcon(SpriteMap.getIconMap().get("SPACE"));
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
