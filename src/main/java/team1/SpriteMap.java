package team1;
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

public class SpriteMap extends JPanel {
    private static final long serialVersionUID = 1L;
    private SokobanMap map;
    private int xSize;
    private int ySize;
    private Map<Coordinate, JLabel> panelHolder;
    private Map<String, ImageIcon> iconMap;
    private Map<String, ImageIcon> unscaledIconMap;
    private boolean mapDrawn;
    private boolean playable;
    private boolean initialised = false;
    private float scale;
    private int tileSetNo;
    private int noOfWalls;
    private int noOfGrass;
    private HashMap<SaveState, Coordinate[]> solution;
    private int iconSize;
    private Random random;

    public SpriteMap(SokobanMap map, int tileSetNo) {
        panelHolder = new HashMap<Coordinate, JLabel>();
        iconMap = new HashMap<String, ImageIcon>();
        solution = new HashMap<SaveState, Coordinate[]>();
        playable = true;
        scale = 1;
        this.tileSetNo = tileSetNo;
        this.updateMap(map);
        loadSprites();
        setVisible(true);
        initialised = true;
    }

    public void updateMap(SokobanMap map) {
        mapDrawn = false;
        resetSolver();
        this.map = SokobanMap.shallowCopy(map, map.getMaxUndos());
        random = new Random(this.map.hashCode());
        xSize = map.getXSize();
        ySize = map.getYSize();
        setLayout(new GridLayout(ySize, xSize));
        panelHolder.clear();
        this.removeAll();
        for (Coordinate position : Coordinate.allValidCoordinates(xSize, ySize)) {
            panelHolder.put(position, new Cell(position, this, this.playable));
            add(panelHolder.get(position));
        }
        if (initialised) {
            placeSprites();
        }
    }

    public void toggleMode() {
        playable = !playable;
        forceRedraw();
    }

    public boolean getPlayable() {
        return playable;
    }

    public int getIconSize() {
        return iconSize;
    }

    public void setMap(SokobanMap map) {
        this.map = map;
    }

    public SokobanMap getSokobanMap() {
        return map;
    }

    public void placeSprites() {
        Set<Coordinate> grassPositions = map.inaccessibleSpaces();
        ArrayList<Coordinate> toDraw = new ArrayList<Coordinate>();

        if (!mapDrawn) {
            toDraw = Coordinate.allValidCoordinates(xSize, ySize);
            mapDrawn = true;
        } else {
            toDraw.addAll(map.tilesToRedraw(playable));
        }

        toDraw.remove(new Coordinate(-1, -1));

        for (Coordinate position : toDraw) {
            SokobanObject object = map.get(position);
            ImageIcon icon;
            if (!playable && object == SokobanObject.SPACE) {
                icon = iconMap.get("DEFAULT");
            } else if (object == SokobanObject.WALL) {
                icon = randomIcon("WALL", noOfWalls);
            } else if (grassPositions.contains(position)) {
                icon = randomIcon("GRASS", noOfGrass);
            } else {
                icon = iconMap.get(object.name());
            }
            panelHolder.get(position).setIcon(icon);
        }

        if (solution.containsKey(map.getSimpleState())) {
            Coordinate direction = solution.get(map.getSimpleState())[1];
            Coordinate position = solution.get(map.getSimpleState())[0].add(direction);
            ImageIcon icon = iconMap.get(map.get(position).name() + "_" + direction.toString());
            panelHolder.get(position).setIcon(icon);
        }

        repaint();
    }

    public void resetSolver() {
        solution.clear();
    }

    public void reset() {
        resetSolver();
        random = new Random(map.hashCode());
        map.reset();
        mapDrawn = false;
    }

    public void forceRedraw() {
        mapDrawn = false;
        random = new Random(map.hashCode());
        placeSprites();
    }

    private ImageIcon randomIcon(String iconName, int iconCount) {
        if (iconCount == 1 || !playable) {
            return iconMap.get(iconName);
        }

        String randomNumber = Integer.toString(random.nextInt(iconCount) + 1);
        if (randomNumber.equals("1")) {
            randomNumber="";
        }
        return iconMap.get(iconName + randomNumber);
    }

    public void loadSprites() {
        String tilesetpath = "/tileset0" + tileSetNo + "/";
        ArrayList<String> iconNames = new ArrayList<String>(Arrays.asList("SPACE", "GOAL", "BOX", "BOX_ON_GOAL", "PLAYER", "PLAYER_ON_GOAL", "GRASS", "WALL", "DEFAULT", "DEFAULT_HOVER", "BOX_UP", "BOX_DOWN", "BOX_LEFT", "BOX_RIGHT","BOX_ON_GOAL_UP","BOX_ON_GOAL_RIGHT","BOX_ON_GOAL_DOWN","BOX_ON_GOAL_LEFT"));
        iconMap.clear();
        noOfWalls = 1;
        noOfGrass = 1;

        while (getClass().getResource(tilesetpath + "WALL" + (noOfWalls + 1) + ".png") != null) {
            iconNames.add("WALL" + (noOfWalls + 1));
            noOfWalls++;
        }

        while (getClass().getResource(tilesetpath + "GRASS" + (noOfGrass + 1) + ".png") != null) {
            iconNames.add("GRASS" + (noOfGrass + 1));
            noOfGrass++;
        }

        for (String icon : iconNames) {
            iconMap.put(icon, new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(tilesetpath + icon + ".png"))));
        }

        unscaledIconMap = new HashMap<String, ImageIcon>(iconMap);

        iconSize = iconMap.get("BOX").getIconHeight();
        resizeSprites();

        mapDrawn = false;
        placeSprites();
    }

    public ImageIcon getBoxSprite(){
        return iconMap.get("BOX");
    }

    public Map<String, ImageIcon> getIconMap() {
        return iconMap;
    }

    public int getTileSetNo() {
        return tileSetNo;
    }

    public void setTileSetNo(int tileSetNo) {
        this.tileSetNo = tileSetNo;
    }

    public Map<String, ImageIcon> getUnscaledIconMap() {
        return unscaledIconMap;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return scale;
    }

    public void setSolution(HashMap<SaveState, Coordinate[]> solution) {
        this.solution = solution;
    }

    private void resizeSprites() {
        float iconDimension = scale * iconSize;
        int newIconDimension = (int) iconDimension;
        for(String iconName : iconMap.keySet()) {
            Image iconImage = iconMap.get(iconName).getImage();
            Image resizedImage = iconImage.getScaledInstance(newIconDimension, newIconDimension, Image.SCALE_DEFAULT);
            iconMap.put(iconName, new ImageIcon(resizedImage));
        }
    }
}
