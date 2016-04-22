package team1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Label;
import java.awt.TextField;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class TilePalette extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final ImageIcon[] tiles = new ImageIcon[4];
    private static final JList<ImageIcon> list = new JList<ImageIcon>(tiles);
    protected static TextField boxCounter = new TextField(10);
    protected static TextField pressureCounter = new TextField(10);
    protected static int boxCount;
    protected static int pressureCount;
    protected static int playerCount;
    protected static SpriteMap spriteMap;

    public TilePalette(SpriteMap spriteMap)
    {
        super();
        TilePalette.spriteMap = spriteMap;
        importImages();
        add(new JLabel("Palette"));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(100, 80));
        formatList();
        setCounters();
        updateCounters();
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
        tiles[0] = spriteMap.getIconMap().get("WALL");
        tiles[1] = spriteMap.getIconMap().get("BOX");
        tiles[2] = spriteMap.getIconMap().get("GOAL");
        tiles[3] = spriteMap.getIconMap().get("PLAYER");
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
