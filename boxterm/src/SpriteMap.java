import java.awt.GridLayout;
import javax.imageio.*;
import javax.swing.*;
import java.io.*;
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
    private Map<String, ImageIcon> iconMap;
    private boolean mapDrawn;

	public SpriteMap(SokobanMap map) {
        mapDrawn = false;
        xSize = map.getXSize();
        ySize = map.getYSize();
        panelHolder = new JLabel[ySize][xSize];
        setLayout(new GridLayout(ySize, xSize));
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                panelHolder[y][x] = new JLabel();
                add(panelHolder[y][x]);
            }
        }
		this.map = map;
        iconMap = new HashMap<String, ImageIcon>();
		loadSprites();
		setVisible(true);
		placeSprites();
	}

	public void placeSprites() {
        if (!mapDrawn) {
            for (int y = 0; y < ySize; y++) {
                for (int x = 0; x < xSize; x++) {
                    SokobanObject object = map.get(new Coordinate(x, y));
                    panelHolder[y][x].setIcon(iconMap.get(object.name()));
                }
            }
            mapDrawn = true;
        } else {
            for (Coordinate coord : map.getChanges()) {
                    SokobanObject object = map.get(coord);
                    panelHolder[coord.getY()][coord.getX()].setIcon(iconMap.get(object.name()));
            }
        }
		win();
		revalidate();
		repaint();
	}

	public void win() {
		if(map.isDone()) {
            removeAll();
			add (new JLabel("YOU WON!!"));
        }
	}

	public void loadSprites() {
		try {
            String[] iconNames = {"SPACE", "WALL", "GOAL", "BOX", "BOX_ON_GOAL", "PLAYER", "PLAYER_ON_GOAL"};
            for (String icon : iconNames) {
                iconMap.put(icon, new ImageIcon(ImageIO.read(new File("src/tileset01/" + icon + ".png"))));
            }
		} catch (IOException e) {
            e.printStackTrace();
        }
	}
}
