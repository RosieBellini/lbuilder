package team1;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
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
    private LinkedList<Coordinate[]> solution;
    private int stageInSolution = 0;
    private Coordinate boxToSolve;

    public SpriteMap(SokobanMap map, int tileSetNo) {
        panelHolder = new HashMap<Coordinate, JLabel>();
        iconMap = new HashMap<String, ImageIcon>();
        solution = new LinkedList<Coordinate[]>();
        playable = true;
        scale = 1;
        boxToSolve = new Coordinate(-2, -2);
        this.tileSetNo = tileSetNo;
        this.updateMap(map);
        loadSprites(tileSetNo);
        setVisible(true);
        initialised = true;
    }

    public void updateMap(SokobanMap map) {
        mapDrawn = false;
        this.map = SokobanMap.shallowCopy(map, map.getMaxUndos());
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

    public void setMap(SokobanMap map) {
        this.map = map;
    }

    public SokobanMap getSokobanMap() {
        return map;
    }

    public void placeSprites(ArrayList<Coordinate> toDraw) {
        Set<Coordinate> grassPositions = map.inaccessibleSpaces();
        boolean arrowDrawn = false;
        boolean needNextArrow = false;


        toDraw.remove(new Coordinate(-1, -1));

        for (Coordinate position : toDraw) {
                SokobanObject object = map.get(position);
                ImageIcon icon;
                if (!playable && object == SokobanObject.SPACE) {
                    icon = iconMap.get("DEFAULT");
                } else if (object == SokobanObject.WALL) {
                    icon = randomIcon("WALL", noOfWalls);
                } else if (playable && grassPositions.contains(position)) {
                    icon = randomIcon("GRASS", noOfGrass);
                } else if (solution.size() != 0 && position.equals(solution.get(stageInSolution)[0].add(solution.get(stageInSolution)[1])) && (object == SokobanObject.BOX || object == SokobanObject.BOX_ON_GOAL)) {
                    icon = iconMap.get("BOX_" + solution.get(stageInSolution)[1].toString());
                    boxToSolve = position;
                    arrowDrawn = true;
                } else if (solution.size() != 0 && position.equals(boxToSolve) && (object == SokobanObject.PLAYER || object == SokobanObject.PLAYER_ON_GOAL)) {
                    icon = iconMap.get("PLAYER");
                    needNextArrow = true;
                } else {
                    icon = iconMap.get(object.name());
                }
                panelHolder.get(position).setIcon(icon);
        }

        revalidate();
        repaint();

        if (arrowDrawn) {
            stageInSolution++;
            if (stageInSolution >= solution.size()) {
                stageInSolution = 0;
                solution.clear();
            }
        }

        if (needNextArrow) {
            ArrayList<Coordinate> nextArrow = new ArrayList<Coordinate>();
            nextArrow.add(solution.get(stageInSolution)[0].add(solution.get(stageInSolution)[1]));
            placeSprites(nextArrow);
        }
    }

    public void placeSprites() {
        ArrayList<Coordinate> toDraw = new ArrayList<Coordinate>();

        if (!mapDrawn) {
            toDraw = Coordinate.allValidCoordinates(xSize, ySize);
            resizeSprites();
            mapDrawn = true;
        } else {
            toDraw.addAll(map.getChanges());
        }

        placeSprites(toDraw);
    }

    public void reset() {
        map.reset();
        mapDrawn = false;
    }

    public void forceRedraw() {
        mapDrawn = false;
        placeSprites();
    }

    private ImageIcon randomIcon(String iconName, int iconCount) {
        if (iconCount == 1 || !playable) {
            return iconMap.get(iconName);
        }

        Random r = new Random();
        String randomNumber = Integer.toString(r.nextInt(iconCount) + 1);
        if (randomNumber.equals("1")) {
            randomNumber="";
        }
        return iconMap.get(iconName + randomNumber);
    }

    public void loadSprites(int tileSetNo) {
        String tilesetpath = "/tileset0" + tileSetNo + "/";
        ArrayList<String> iconNames = new ArrayList<String>(Arrays.asList("SPACE", "GOAL", "BOX", "BOX_ON_GOAL", "PLAYER", "PLAYER_ON_GOAL", "GRASS", "WALL", "DEFAULT", "DEFAULT_HOVER", "BOX_UP", "BOX_DOWN", "BOX_LEFT", "BOX_RIGHT"));
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

    public Map<String, ImageIcon> getUnscaledIconMap() {
        return unscaledIconMap;
    }

    public void update() {
        mapDrawn = false;
        loadSprites(tileSetNo);
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return scale;
    }

    public void setSolution(LinkedList<Coordinate[]> solution) {
        this.solution = solution;
    }

    private void resizeSprites(){
        float iconDimension = scale * 32;
        int newIconDimension = (int) iconDimension;
        for(String iconName : iconMap.keySet()) {
            Image iconImage = iconMap.get(iconName).getImage();
            Image resizedImage = iconImage.getScaledInstance(newIconDimension, newIconDimension, Image.SCALE_DEFAULT);
            iconMap.put(iconName, new ImageIcon(resizedImage));
        }
    }

    @Override public Dimension getPreferredSize(){
    	float prefWidth = 32*scale*xSize;
    	float prefHeight = 32*scale*ySize;
    	return new Dimension((int) prefWidth,(int) prefHeight);
    }

}
