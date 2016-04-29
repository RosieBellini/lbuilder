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

/**
 * The SpriteMap class provides a graphical representation of a SokobanMap as
 * as JPanel. It provides methods for importing tilesets and toggling between a
 * playable game mode and editor mode.
 */

public class SpriteMap extends JPanel {
    private static final long serialVersionUID = 1L;
    private SokobanMap map;
    private int xSize;
    private int ySize;
    private Map<Coordinate, JLabel> panelHolder;
    private Map<String, ImageIcon> iconMap;
    private Map<String, Integer> iconCountMap;
    private Map<String, ImageIcon> unscaledIconMap;
    private boolean playable;
    private float scale;
    private int tilesetNum;
    private Map<SaveState, Coordinate[]> solution;
    private Random random;
    private final int tilesetCount = 3;

    /**
     * SpriteMap constructor.
     *
     * @param   map         The initial SokobanMap to display
     * @param   tilesetNum   The initial tileset to use
     */
    public SpriteMap(SokobanMap map, int tilesetNum) {
        panelHolder = new HashMap<Coordinate, JLabel>();
        iconMap = new HashMap<String, ImageIcon>();
        iconCountMap = new HashMap<String, Integer>();
        solution = new HashMap<SaveState, Coordinate[]>();
        playable = true;
        scale = 1;
        this.tilesetNum = tilesetNum;
        loadSprites();
        this.updateMap(map);
        setVisible(true);
    }

    /**
     * Replaces the displayed SokobanMap with a different one.
     *
     * @param   map         The new SokobanMap to display
     */
    public void updateMap(SokobanMap map) {
        this.map = SokobanMap.shallowCopy(map, map.getMaxUndos());
        resetSolver();
        xSize = map.getXSize();
        ySize = map.getYSize();
        setLayout(new GridLayout(ySize, xSize));
        panelHolder.clear();
        this.removeAll();

        ArrayList<Coordinate> gridPositions
                = Coordinate.allValidCoordinates(xSize, ySize);
        for (Coordinate position : gridPositions) {
            panelHolder.put(position, new Cell(position, this));
            add(panelHolder.get(position));
        }

        placeSprites(true);
    }

    /**
     * Updates cells according to the status of the SokobanMap.
     */
    public void placeSprites(boolean redraw) {
        Set<Coordinate> grassPositions = map.inaccessibleSpaces();
        ArrayList<Coordinate> toDraw = new ArrayList<Coordinate>();

        if (redraw) {
            toDraw = Coordinate.allValidCoordinates(xSize, ySize);
            random = new Random(map.hashCode());
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
                icon = randomIcon("GRASS");
            } else {
                icon = randomIcon(object.name());
            }

            panelHolder.get(position).setIcon(icon);
        }

        if (solution.containsKey(map.getSimpleState())) {
            SaveState mapState = map.getSimpleState();
            Coordinate direction = solution.get(mapState)[1];
            Coordinate position = solution.get(mapState)[0].add(direction);
            ImageIcon icon = iconMap.get(map.get(position).name()
                                            + "_" + direction.toString());

            panelHolder.get(position).setIcon(icon);
        }

        repaint();
    }

    /**
     * {@code redraw} defaults to false.
     *
     * @see placeSprites(boolean)
     */
    public void placeSprites() {
        placeSprites(false);
    }

    /**
     * Imports the icons of the active tileset and stores them in iconMap and
     * unscaledIconMap for scaled and unscaled variants respectively.
     */
    public void loadSprites() {
        String tilesetPath = "/tileset0" + tilesetNum + "/";
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
    }

    /**
     * Returns a randomly selected variant of the given icon.
     *
     * @param   iconName        The icon type to use
     */
    private ImageIcon randomIcon(String iconName) {
        int iconCount = iconCountMap.get(iconName);

        if (iconCount == 1 || !playable) {
            return iconMap.get(iconName);
        }

        String randomNumber = Integer.toString(random.nextInt(iconCount) + 1);
        if (randomNumber.equals("1")) {
            randomNumber = "";
        }

        return iconMap.get(iconName + randomNumber);
    }

    /**
     * Resizes the icons in iconMap according to the scale field.
     */
    private void resizeSprites() {
        float iconDimension = scale * getIconSize();
        int newIconDimension = (int) iconDimension;
        for (String iconName : iconMap.keySet()) {
            Image iconImage = iconMap.get(iconName).getImage();
            Image resizedImage = iconImage.getScaledInstance(newIconDimension,
                                        newIconDimension, Image.SCALE_DEFAULT);
            iconMap.put(iconName, new ImageIcon(resizedImage));
        }
    }

    /**
     * Returns the SokobanMap and this SpriteMap to their initial states.
     */
    public void reset() {
        resetSolver();
        map.reset();
        placeSprites(true);
    }

    /**
     * Removes the solution if one has been calculated.
     */
    public void resetSolver() {
        solution.clear();
    }

    /**
     * Returns the mode of the SpriteMap.
     *
     * @return      True if the SpriteMap is in game mode, false if in editor
     *              mode
     */
    public boolean getPlayable() {
        return playable;
    }

    /**
     * Toggles the mode of the SpriteMap.
     */
    public void toggleMode() {
        playable = !playable;
        placeSprites(true);
    }

    /**
     * Returns the size of the SokobanMap's X dimension.
     *
     * @return      The value of xSize
     */
    public int getXSize() {
        return xSize;
    }

    /**
     * Returns the size of the SokobanMap's Y dimension.
     *
     * @return      The value of ySize
     */
    public int getYSize() {
        return ySize;
    }

    /**
     * Returns the SokobanMap associated with this SpriteMap.
     *
     * @return      The value of map
     */
    public SokobanMap getSokobanMap() {
        return map;
    }

    /**
     * Returns the map of icon names to icons.
     *
     * @return      The value of iconMap
     */
    public Map<String, ImageIcon> getIconMap() {
        return iconMap;
    }

    /**
     * Returns the map of icon names to icons (unscaled variants).
     *
     * @return      The value of unscaledIconMap
     */
    public Map<String, ImageIcon> getUnscaledIconMap() {
        return unscaledIconMap;
    }

    /**
     * Advances the value of tilesetNum to the next valid one. Loops back to
     * zero.
     */
    public void nextTileset() {
        tilesetNum = (tilesetNum + 1) % tilesetCount;
    }

    /**
     * Returns the current icon scale used by this SpriteMap.
     *
     * @return      The value of scale
     */
    public float getScale() {
        return scale;
    }

    /**
     * Changes the scale used by icons in this SpriteMap.
     *
     * @param   scale       The new icon scale to use (1 is native)
     */
    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * Returns the native height of icons in the active tileset. This assumes
     * that the icons are square and that they all have the same dimensions.
     *
     * @return      The native size of active icons (in pixels)
     */
    public int getIconSize() {
        return unscaledIconMap.get("BOX").getIconHeight();
    }

    /**
     * Adds a solution for the active SokobanMap.
     *
     * @param   solution        The HashMap that links the "correct" states of
     *                          the solution to the player's position and
     *                          required push direction at each state
     */
    public void setSolution(HashMap<SaveState, Coordinate[]> solution) {
        this.solution = solution;
    }
}
