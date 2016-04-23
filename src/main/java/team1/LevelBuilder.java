package team1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Label;
import java.awt.TextField;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * LevelBuilder class. Used to create and display the LevelBuilder GUI.
 * All of the fields & methods are static.
 *
 */

public class LevelBuilder extends JPanel{

    private static final long serialVersionUID = 1L;
    private static SpriteMap spriteMap;
    private static final ImageIcon[] tiles = new ImageIcon[4];
    private static final JList<ImageIcon> list = new JList<ImageIcon>(tiles);
    protected static TextField boxCounter = new TextField(10);
    protected static TextField pressureCounter = new TextField(10);
    public static SokobanObject state = SokobanObject.SPACE;
    protected static int boxCount;
    protected static int pressureCount;

    public LevelBuilder(SpriteMap spriteMap) {
        LevelBuilder.spriteMap = spriteMap;

        JPanel tilePalette = new JPanel();
        importImages();
        tilePalette.add(new JLabel("Palette"));
        tilePalette.setBackground(Color.WHITE);
        tilePalette.setPreferredSize(new Dimension(100, 80));
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL_WRAP);
        list.setVisibleRowCount(4);
        list.setBackground(Color.GRAY);
        list.setForeground(Color.BLACK);
        tilePalette.add(list);
        list.setSelectedIndex(0);
        selectTile();
        boxCounter.setText("" + boxCount);
        pressureCounter.setText("" + pressureCount);
        boxCounter.setEditable(false);
        pressureCounter.setEditable(false);
        tilePalette.add(new Label("Boxes:"));
        tilePalette.add(boxCounter);
        tilePalette.add(new Label("Pressure Pads:"));
        tilePalette.add(pressureCounter);
        updateCounters();
        list.addListSelectionListener(new ListListener());

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(spriteMap);
        add(tilePalette);
    }

    public static void importImages() {
        tiles[0] = spriteMap.getIconMap().get("WALL");
        tiles[1] = spriteMap.getIconMap().get("BOX");
        tiles[2] = spriteMap.getIconMap().get("GOAL");
        tiles[3] = spriteMap.getIconMap().get("PLAYER");
        list.setFixedCellHeight((int) BoxTerm.getMagnification() * 36);
        list.setFixedCellWidth((int) BoxTerm.getMagnification() * 34); // rather than 32 to allow border
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

    public static void updateCounters() {
        int boxCount = spriteMap.getMap().getMyState().getBoxPositions().size();
        int pressureCount = spriteMap.getMap().getMyState().getGoalPositions().size();
        boxCounter.setText("" + boxCount);
        pressureCounter.setText("" + pressureCount);
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
