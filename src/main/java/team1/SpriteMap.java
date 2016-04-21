package team1;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
// import javax.imageio.*;
import javax.swing.*;
// import java.io.*;
import java.util.*;

/**
 * TODO: put the counter back
 */

public class SpriteMap extends JPanel {
    private static final long serialVersionUID = 1L;
    private SokobanMap map;
    private int xSize;
    private int ySize;
    private JLabel[][] panelHolder;
    private static Map<String, ImageIcon> iconMap;
    private boolean mapDrawn;
    private boolean playable;
    int noOfWalls;
    int noOfGrass;

    public SpriteMap(SokobanMap map, boolean playable, int tileSetNo) {
        mapDrawn = false;
        xSize = map.getXSize();
        ySize = map.getYSize();
        panelHolder = new JLabel[ySize][xSize];
        setLayout(new GridLayout(ySize, xSize));
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                panelHolder[y][x] = new Cell(new Coordinate(x, y), this, playable);
                add(panelHolder[y][x]);
            }
        }
        this.map = map;
        this.playable = playable;
        iconMap = new HashMap<String, ImageIcon>();
        loadSprites(tileSetNo);
        setVisible(true);
    }

    public void placeSprites() {
        if (!mapDrawn) {
            for (int y = 0; y < ySize; y++) {
                for (int x = 0; x < xSize; x++) {
                    SokobanObject object = map.get(new Coordinate(x, y));
                    if(object.name()=="WALL") {
                        panelHolder[y][x].setIcon(randomWall());
                    }
                    else if (object.name()=="GRASS") {
                        panelHolder[y][x].setIcon(randomGrass());
                    }
                    else {
                        panelHolder[y][x].setIcon(iconMap.get(object.name()));
                    }
                    // panelHolder[y][x].setIcon(iconMap.get(object.name()));
                }
            }
            mapDrawn = true;
        }
        else {
            for (Coordinate coord : map.getChanges()) {
                if (!coord.equals(new Coordinate(-1, -1))) {
                    SokobanObject object = map.get(coord);
                    panelHolder[coord.getY()][coord.getX()].setIcon(iconMap.get(object.name()));
                }
            }
        }
        if (playable) {
            win();
        }
        revalidate();
        repaint();
    }

    public ImageIcon randomWall() {
        if (noOfWalls == 1) {
            return iconMap.get("WALL");
        }
        Random r = new Random();
        String randomNumber = Integer.toString(r.nextInt(noOfWalls) + 1);
        if (randomNumber.equals("1")) {
            randomNumber="";
        }
        return iconMap.get("WALL" + randomNumber);
    }

    public ImageIcon randomGrass() {
        if (noOfGrass==1) {
            return iconMap.get("GRASS");
        }
        Random r = new Random();
        String randomNumber = Integer.toString(r.nextInt(noOfGrass) + 1);
        if (randomNumber.equals("1")) {
            randomNumber="";
        }
        return iconMap.get("GRASS" + randomNumber);
    }

    public void win() {
        if(map.isDone()) {
            removeAll();
            this.setBackground(Color.green);
            JLabel winIcon = new JLabel("YOU WON!!");
            winIcon.setFont(new Font("Courier New", Font.ITALIC, 50));
            winIcon.setForeground(Color.WHITE);
            add (winIcon);
        }
    }

    public void loadSprites(int tileSetNo) {
        String tilesetpath = "/tileset0" + tileSetNo + "/";
        ArrayList<String> iconNames = new ArrayList<String>(Arrays.asList("SPACE", "GOAL", "BOX", "BOX_ON_GOAL", "PLAYER", "PLAYER_ON_GOAL", "GRASS", "WALL"));
        noOfWalls=1;
        noOfGrass=1;
        while (getClass().getResource(tilesetpath + "WALL" + (noOfWalls + 1) + ".png") != null) {
        	iconNames.add("WALL" + (noOfWalls + 1));
        	noOfWalls++;
        }
        while (getClass().getResource(tilesetpath+"GRASS"+(noOfGrass+1)+".png") != null) {
        	iconNames.add("GRASS" + (noOfGrass + 1));
        	noOfGrass++;
        }
        // try {
        for (String icon : iconNames) {
            iconMap.put(icon, new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(tilesetpath + icon + ".png"))));
        }
        // } catch (IOException e) {
        // 	e.printStackTrace();
        // }
        mapDrawn=false;
        placeSprites();
    }

    public ImageIcon getPlayerSprite(){
    	return iconMap.get("PLAYER");
    }

    public SokobanMap getMap() {
        return map;
    }

    public static Map<String, ImageIcon> getIconMap() {
        return iconMap;
    }
}
