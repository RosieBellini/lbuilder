package team1;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
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
    private Map<String, Integer> iconCountMap;
    private Map<String, ImageIcon> unscaledIconMap;
    private boolean mapDrawn;
    private boolean playable;
    private boolean initialised;
    private float scale;
    private int tileSetNo;
    private Map<SaveState, Coordinate[]> solution;
    private Random random;

    public SpriteMap(SokobanMap map, int tileSetNo) {
        panelHolder = new HashMap<Coordinate, JLabel>();
        iconMap = new HashMap<String, ImageIcon>();
        iconCountMap = new HashMap<String, Integer>();
        solution = new HashMap<SaveState, Coordinate[]>();
        playable = true;
        scale = 1;
        this.tileSetNo = tileSetNo;
        initialised = false;
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
            } else if (grassPositions.contains(position)) {
                icon = randomIcon("GRASS", iconCountMap.get("GRASS"));
            } else {
                icon = randomIcon(object.name(), iconCountMap.get(object.name()));
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

    public void loadSprites() {
        String tilesetPath = "/tileset0" + tileSetNo + "/";
        ArrayList<String> iconNames
                = new ArrayList<String>(Arrays.asList("SPACE", "GOAL", "BOX",
                        "BOX_ON_GOAL", "PLAYER", "PLAYER_ON_GOAL", "GRASS",
                        "WALL", "DEFAULT", "DEFAULT_HOVER", "BOX_UP",
                        "BOX_DOWN", "BOX_LEFT", "BOX_RIGHT","BOX_ON_GOAL_UP",
                        "BOX_ON_GOAL_RIGHT","BOX_ON_GOAL_DOWN",
                        "BOX_ON_GOAL_LEFT"));
        iconMap.clear();
        iconCountMap.clear();
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        for (String iconName : iconNames) {
            String iconPath = tilesetPath + iconName;
            URL iconURL = getClass().getResource(iconPath + ".png");
            Image icon = toolkit.getImage(iconURL);
            iconMap.put(iconName, new ImageIcon(icon));

            int i = 2;
            iconURL = getClass().getResource(iconPath + i + ".png");
            while (iconURL != null) {
                icon = toolkit.getImage(iconURL);
                iconMap.put(iconName + i, new ImageIcon(icon));
                i++;
                iconURL = getClass().getResource(iconPath + i + ".png");
            }
            iconCountMap.put(iconName, i - 1);
        }

        unscaledIconMap = new HashMap<String, ImageIcon>(iconMap);

        resizeSprites();

        mapDrawn = false;
        resetRandom();
        placeSprites();
    }

    private ImageIcon randomIcon(String iconName, int iconCount) {
        if (iconCount == 1 || !playable) {
            return iconMap.get(iconName);
        }

        String randomNumber = Integer.toString(random.nextInt(iconCount) + 1);
        if (randomNumber.equals("1")) {
            randomNumber = "";
        }

        return iconMap.get(iconName + randomNumber);
    }

    private void resizeSprites() {
        float iconDimension = scale * getIconSize();
        int newIconDimension = (int) iconDimension;
        for(String iconName : iconMap.keySet()) {
            Image iconImage = iconMap.get(iconName).getImage();
            Image resizedImage = iconImage.getScaledInstance(newIconDimension, newIconDimension, Image.SCALE_DEFAULT);
            iconMap.put(iconName, new ImageIcon(resizedImage));
        }
    }

    public void reset() {
        resetSolver();
        resetRandom();
        map.reset();
        mapDrawn = false;
    }

    public void resetSolver() {
        solution.clear();
    }

    private void resetRandom() {
        random = new Random(map.hashCode());
    }

    public void forceRedraw() {
        mapDrawn = false;
        resetRandom();
        placeSprites();
    }

    public boolean getPlayable() {
        return playable;
    }

    public void toggleMode() {
        playable = !playable;
        forceRedraw();
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }

    public SokobanMap getSokobanMap() {
        return map;
    }

    public void setMap(SokobanMap map) {
        this.map = map;
    }

    public Map<String, ImageIcon> getIconMap() {
        return iconMap;
    }

    public Map<String, ImageIcon> getUnscaledIconMap() {
        return unscaledIconMap;
    }

    public int getTileSetNo() {
        return tileSetNo;
    }

    public void setTileSetNo(int tileSetNo) {
        this.tileSetNo = tileSetNo;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public int getIconSize() {
        return unscaledIconMap.get("BOX").getIconHeight();
    }

    public void setSolution(HashMap<SaveState, Coordinate[]> solution) {
        this.solution = solution;
    }
}
