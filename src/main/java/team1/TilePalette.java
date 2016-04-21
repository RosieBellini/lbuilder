package team1;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Dimension;

public class TilePalette extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final ImageIcon[] tiles = new ImageIcon[4];
    private static final JList<ImageIcon> list = new JList<ImageIcon>(tiles);
    protected static TextField boxCounter = new TextField(10);
    protected static TextField pressureCounter = new TextField(10);
    protected static int boxCount;
    protected static int pressureCount;
    protected static int playerCount;

    public TilePalette()
    {
        super();
        importImages();
        add(new JLabel("Palette"));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(100, 80));
        formatList();
        setCounters();
        list.addListSelectionListener(new ListListener());
    }

    public void formatList() {
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL_WRAP);
        list.setFixedCellHeight(36);
        list.setFixedCellWidth(34); // rather than 32 to allow border
        list.setVisibleRowCount(4);
        list.setBackground(Color.GRAY);
        list.setForeground(Color.BLACK);
        add(list);
        list.setSelectedIndex(0);
        selectTile();
    }

    public void importImages() {

        ImageIcon wall = new ImageIcon(getClass().getResource("/tileset01/WALL.png"), "Wall");
        ImageIcon box = new ImageIcon(getClass().getResource("/tileset01/BOX.png"), "Box");
        ImageIcon pressure = new ImageIcon(getClass().getResource("/tileset01/GOAL.png"), "Pressure Pad");
        ImageIcon player = new ImageIcon(getClass().getResource("/tileset01/PLAYER.png"), "Player");

        tiles[0] = wall;
        tiles[1] = box;
        tiles[2] = pressure;
        tiles[3] = player;
    }

    public void setCounters() {
        add(new Label("Boxes:"));
        boxCounter.setText("" + boxCount);
        add(boxCounter);
        add(new Label("Pressure Pads:"));
        pressureCounter.setText("" + pressureCount);
        add(pressureCounter);
        boxCounter.setEditable(false);
        pressureCounter.setEditable(false);
    }

    public void selectTile() {

        int selection = list.getSelectedIndex();
        switch(selection) {
            case 0: LevelBuilder.state = SokobanObject.WALL;
                    break;
            case 1: LevelBuilder.state = SokobanObject.BOX;
                    break;
            case 2: LevelBuilder.state = SokobanObject.GOAL;
                    break;
            case 3: LevelBuilder.state = SokobanObject.PLAYER;
                    break;
        }
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
