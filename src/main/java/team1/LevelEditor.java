package team1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Label;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * LevelEditor class. Used to create and display the LevelEditor GUI.
 * All of the fields & methods are static.
 *
 */

public class LevelEditor extends JPanel{

    private static final long serialVersionUID = 1L;
    private static SpriteMap spriteMap;
    private static final ImageIcon[] tiles = new ImageIcon[4];
    private static JList<ImageIcon> list;
    private static SokobanObject state = SokobanObject.SPACE;
    private static Label boxLabel;
    private static Label goalLabel;
    private static LevelEditor instance;
    private static JPanel tilePalette;

    private LevelEditor(SpriteMap spriteMap) {
        LevelEditor.spriteMap = spriteMap;

        tilePalette = new JPanel();
        tilePalette.setLayout(new BoxLayout(tilePalette, BoxLayout.X_AXIS));
        tilePalette.setBackground(Color.WHITE);

        list = new JList<ImageIcon>(tiles);
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        importImages();
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setVisibleRowCount(1);
        list.setBackground(Color.WHITE);
        list.setForeground(Color.BLACK);
        list.setSelectedIndex(0);
        list.addListSelectionListener(new ListListener());

        JPanel counters = new JPanel();
        JPanel padding = new JPanel();
        counters.setLayout(new BoxLayout(counters, BoxLayout.Y_AXIS));
        boxLabel = new Label();
        goalLabel = new Label();

        counters.add(boxLabel);
        counters.add(goalLabel);
        tilePalette.add(counters, BorderLayout.WEST);
        tilePalette.add(list, BorderLayout.CENTER);
        tilePalette.add(padding, BorderLayout.EAST);
        // tilePalette.setSize(tilePalette.getPreferredSize());
        selectTile();
        updateCounters();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(spriteMap);
        add(tilePalette);
    }

    public static LevelEditor getInstance(SpriteMap spriteMap) {
        if (instance == null) {
            instance = new LevelEditor(spriteMap);
        }
        return instance;
    }

    public static void importImages() {
        tiles[0] = spriteMap.getUnscaledIconMap().get("WALL");
        tiles[1] = spriteMap.getUnscaledIconMap().get("BOX");
        tiles[2] = spriteMap.getUnscaledIconMap().get("GOAL");
        tiles[3] = spriteMap.getUnscaledIconMap().get("PLAYER");
        list.setFixedCellHeight(36);
        list.setFixedCellWidth(36);
        list.repaint();
    }

    public static void selectTile() {

        int selection = list.getSelectedIndex();
        switch(selection) {
            case 0: state = SokobanObject.WALL;
                    break;
            case 1: state = SokobanObject.BOX;
                    break;
            case 2: state = SokobanObject.GOAL;
                    break;
            case 3: state = SokobanObject.PLAYER;
                    break;
        }
    }

    public static SpriteMap getSpriteMap() {
        return spriteMap;
    }

    public static SokobanMap getSokobanMap() {
        return spriteMap.getSokobanMap();
    }

    public static SokobanObject getState() {
        return state;
    }

    public static double getTilePaletteHeight() {
        return tilePalette.getPreferredSize().getHeight();
    }

    public static void updateCounters() {
        int boxCount = getSokobanMap().getMyState().getBoxPositions().size();
        int goalCount = getSokobanMap().getMyState().getGoalPositions().size();
        boxLabel.setText("Boxes: " + boxCount);
        goalLabel.setText("Goals: " + goalCount);
    }

    class ListListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                selectTile();
            }
        }
    }
}
