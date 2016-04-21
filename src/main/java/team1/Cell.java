package team1;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

// import levelBuilder.TilePalette.ListListener;

public class Cell extends JLabel{

    private static final long serialVersionUID = 1L;
    private boolean inUse;
    private String tileType;
    // Coordinate values: Named i and j so they don't affect LayoutManager.
    private int i; // X Coordinate
    private int j; // Y Coordinate

    public Cell(int i, int j, boolean playable) {
        super();
        this.i = i;
        this.j = j;
        inUse = false;
        tileType = "SPACE";
        setIcon(new ImageIcon(getClass().getResource("/tileset03/SPACE.png"), "Default"));

        // Mouse listener:
        if (!playable) {
            addMouseListener(new MouseAdapter(){


                public void mousePressed(MouseEvent me)
                {
                    // If left mouse button is clicked
                    if (me.getButton() == MouseEvent.BUTTON1) {
                        setIcon(SpriteMap.getIconMap().get(LevelBuilder.state));
                        tileType = LevelBuilder.state;
                        if (tileType == "BOX") {
                            TilePalette.boxCount = TilePalette.boxCount + 1;
                            TilePalette.boxCounter.setText("" + TilePalette.boxCount);	// increment box count
                        } else if (tileType == "GOAL") {
                            TilePalette.pressureCount = TilePalette.pressureCount + 1;
                            TilePalette.pressureCounter.setText("" + TilePalette.pressureCount); // increment pressure pad count
                        } else if (tileType == "PLAYER") {
                            TilePalette.playerCount = TilePalette.playerCount + 1; //	increment player count
                        }
                        inUse = true; // tile is in use if clicked
                    }


                    // If right mouse is clicked, undo
                    // If right mouse is clicked, undo
                    if (me.getButton() == MouseEvent.BUTTON3) {

                        if (LevelBuilder.state == "BOX" && TilePalette.boxCount != 0) {
                            TilePalette.boxCount -= 1;
                            TilePalette.boxCounter.setText("" + TilePalette.boxCount); // decrease box count
                        }
                        if (LevelBuilder.state == "GOAL" && TilePalette.pressureCount != 0) {
                            TilePalette.pressureCount -= 1;
                            TilePalette.pressureCounter.setText("" + TilePalette.pressureCount); // decrease pressure pad count
                        }

                        setIcon(new ImageIcon(getClass().getResource("/tileset03/SPACE_HOVER.png"), "Default"));
                        tileType = "SPACE";
                        inUse = false;
                    }

                }

                //	Mouse hover

                public void mouseEntered(MouseEvent me) {
                    if (!inUse) {
                        setIcon(new ImageIcon(getClass().getResource("/tileset03/SPACE_HOVER.png")));
                    }
                }

                public void mouseExited(MouseEvent me) {
                    if (!inUse) {
                        setIcon(new ImageIcon(getClass().getResource("/tileset03/SPACE.png")));
                    }
                }


            });
        }
    }


    // Accessor methods:
    public int getI()
    {
        return i;
    }

    public int getJ()
    {
        return j;
    }

    public boolean getInUse()
    {
        return inUse;
    }

    public String getTileType()
    {
        return tileType;
    }



}
