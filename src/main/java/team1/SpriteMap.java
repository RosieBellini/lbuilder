package team1;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

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
    private Set<Coordinate> mistakePlace;
    private boolean lastMoveUndo;
    private Stack<ArrayList<Coordinate>> lastCorrectPush;

    public SpriteMap(SokobanMap map, int tileSetNo) {
        panelHolder = new HashMap<Coordinate, JLabel>();
        lastCorrectPush = new Stack<ArrayList<Coordinate>>();
        iconMap = new HashMap<String, ImageIcon>();
        solution = new LinkedList<Coordinate[]>();
        playable = true;
        lastMoveUndo = false;
        scale = 1;
        boxToSolve = new Coordinate(-2, -2);
        mistakePlace = new HashSet<Coordinate>();
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

    public void placeSprites() {
        Set<Coordinate> grassPositions = map.inaccessibleSpaces();
        boolean needNextArrow = false;
        ArrayList<Coordinate> toDraw = new ArrayList<Coordinate>();

        if (!mapDrawn) {
            toDraw = Coordinate.allValidCoordinates(xSize, ySize);
            resizeSprites();
            mapDrawn = true;
        } else {
            toDraw.addAll(map.getChanges());
            if (solution.size() != 0) {
                Set<Coordinate> changedPlaces = new HashSet<Coordinate>(toDraw);
                Coordinate playerPos = new Coordinate(-1, -1);
                Coordinate boxPos = new Coordinate(-1, -1);
                for (Coordinate position : changedPlaces) {
                    SokobanObject object = map.get(position);
                    if (object == SokobanObject.PLAYER || object == SokobanObject.PLAYER_ON_GOAL) {
                        playerPos = position;
                    } else if (object == SokobanObject.BOX|| object == SokobanObject.BOX_ON_GOAL) {
                        boxPos = position;
                    }
                }

                if (!lastMoveUndo && !playerPos.equals(new Coordinate(-1, -1)) && !boxPos.equals(new Coordinate(-1, -1))) {
                    if (!(playerPos.equals(boxToSolve) && boxPos.equals(boxToSolve.add(solution.get(stageInSolution)[1])))) {
                        mistakePlace = changedPlaces;
                        System.out.println("mistake made");
                        toDraw.add(boxToSolve);
                    }
                }

                if (lastMoveUndo && mistakePlace.size() == 0 && lastCorrectPush.size() > 0 && changedPlaces.containsAll(lastCorrectPush.peek())) {
                    System.out.println("undid correct push");
                    toDraw.add(boxToSolve);
                    lastCorrectPush.pop();
                    stageInSolution--;
                    lastMoveUndo = false;
                }
            }
        }

        if (lastMoveUndo && map.getChanges().equals(mistakePlace)) {
            lastMoveUndo = false;
            mistakePlace.clear();
        }

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
            } else if (solution.size() != 0 && position.equals(boxToSolve) && (object == SokobanObject.PLAYER || object == SokobanObject.PLAYER_ON_GOAL)) {
                needNextArrow = true;
                icon = iconMap.get(object.name());
            } else {
                icon = iconMap.get(object.name());
            }
            panelHolder.get(position).setIcon(icon);
        }

        if (needNextArrow && mistakePlace.size() == 0) {
            lastCorrectPush.push(toDraw);
            stageInSolution++;
            if (stageInSolution >= solution.size()) {
                resetSolver();
            }
        }

        if (solution.size() != 0 && mistakePlace.size() == 0) {
            Coordinate position = solution.get(stageInSolution)[0].add(solution.get(stageInSolution)[1]);
            boxToSolve = position;
            ImageIcon icon = iconMap.get("BOX_" + solution.get(stageInSolution)[1].toString());
            panelHolder.get(position).setIcon(icon);
        }

        revalidate();
        repaint();

    }

    public void resetSolver() {
        stageInSolution = 0;
        boxToSolve = new Coordinate(-2, -2);
        lastCorrectPush.clear();
        solution.clear();
    }

    public void reset() {
        resetSolver();
        map.reset();
        mapDrawn = false;
        mistakePlace.clear();
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

    public void loadSprites() {
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

    public void setTileSetNo(int tileSetNo) {
        this.tileSetNo = tileSetNo;
    }

    public Map<String, ImageIcon> getUnscaledIconMap() {
        return unscaledIconMap;
    }

    public void update() {
        mapDrawn = false;
        loadSprites();
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

    public void lastSolutionStep() {
        // if (map.getChanges().equals(mistakePlace)) {
        //     System.out.println("undid mistake");
        //     if (solution.size() > 0) {
        //         lastMoveUndo = true;
        //     }
        // }
        if (solution.size() > 0) {
            lastMoveUndo = true;
        }
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
