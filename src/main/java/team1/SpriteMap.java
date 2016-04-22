package team1;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * TODO: put the counter back
 */

public class SpriteMap extends JPanel {
    private static final long serialVersionUID = 1L;
    private SokobanMap map;
    private int xSize;
    private int ySize;
    private Map<Coordinate, JLabel> panelHolder;
    private Map<String, ImageIcon> iconMap;
    private boolean mapDrawn;
    private boolean playable;
    int noOfWalls;
    int noOfGrass;
    float magnification;

    public SpriteMap(SokobanMap map, boolean playable, int tileSetNo) {
        mapDrawn = false;
        xSize = map.getXSize();
        ySize = map.getYSize();
        this.playable = playable;
        panelHolder = new HashMap<Coordinate, JLabel>();
        setLayout(new GridLayout(ySize, xSize));
        for (Coordinate position : Coordinate.allValidCoordinates(xSize, ySize)) {
            panelHolder.put(position, new Cell(position, this, playable));
            add(panelHolder.get(position));
        }
        this.map = map;
        magnification = 1;
        iconMap = new HashMap<String, ImageIcon>();
        loadSprites(tileSetNo);
        setVisible(true);
    }

    public void setMap(SokobanMap map) {
        this.map = map;
    }

    public void placeSprites() {
        if (!mapDrawn) {
            resizeSprites();
            Set<Coordinate> grassPositions = map.inaccessibleSpaces();
            for (Coordinate position : Coordinate.allValidCoordinates(xSize, ySize)) {
                SokobanObject object = map.get(position);
                ImageIcon icon;
                if (!playable && object.name().equals("SPACE")){
                    icon = iconMap.get("DEFAULT");
                } else if (object.name().equals("WALL")) {
                    icon = randomIcon("WALL", noOfWalls);
                } else if (playable && grassPositions.contains(position)) {
                    icon = randomIcon("GRASS", noOfGrass);
                } else {
                    icon = iconMap.get(object.name());
                }
                panelHolder.get(position).setIcon(icon);
            }
            mapDrawn = true;
        } else {
            for (Coordinate position : map.getChanges()) {
                if (!position.equals(new Coordinate(-1, -1))) {
                    SokobanObject object = map.get(position);
                    ImageIcon icon;
                    if(!playable && object.name().equals("SPACE")){
                        icon = iconMap.get("DEFAULT");
                    } else {
                        icon=iconMap.get(object.name());
                    }
                    panelHolder.get(position).setIcon(icon);
                }
            }
        }
        if (playable) {
            win();
        }
        revalidate();
        repaint();
    }

    public void reset() {
        map.reset();
        mapDrawn = false;
    }

    public ImageIcon randomIcon(String iconName, int iconCount) {
        if (noOfGrass==1) {
            return iconMap.get(iconName);
        }
        Random r = new Random();
        String randomNumber = Integer.toString(r.nextInt(iconCount) + 1);
        if (randomNumber.equals("1")) {
            randomNumber="";
        }
        return iconMap.get(iconName + randomNumber);
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
        ArrayList<String> iconNames = new ArrayList<String>(Arrays.asList("SPACE", "GOAL", "BOX", "BOX_ON_GOAL", "PLAYER", "PLAYER_ON_GOAL", "GRASS", "WALL","DEFAULT","DEFAULT_HOVER"));
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

    public ImageIcon getBoxSprite(){
        return iconMap.get("BOX");
    }

    public SokobanMap getMap() {
        return map;
    }

    public Map<String, ImageIcon> getIconMap() {
        return iconMap;
    }

    public void changeMagnification(boolean getBigger) {
        if (getBigger) {
            if (magnification > 1) {
                magnification++;
            } else {
                magnification = magnification * 2;
            }
        } else {
            if (magnification > 1) {
                magnification--;
            } else {
                magnification = magnification / 2;
            }
        }
        mapDrawn = false;
    }

    public void resizeSprites(){
        float iconDimension = magnification*32;
        int newIconDimension= (int)iconDimension;
        for(String iconName:iconMap.keySet()){
            Image iconImage = iconMap.get(iconName).getImage();
            Image resizedImage = iconImage.getScaledInstance(newIconDimension,newIconDimension,Image.SCALE_DEFAULT);
            iconMap.put(iconName, new ImageIcon(resizedImage));
        }
    }

}
