package team1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class GamePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static JLabel statusBar;
    private static MapPanel mapPanel;
    private static KeyListener listener;
    private static GamePanel instance;
    private static boolean playable = true;
    private static JList<ImageIcon> list;
    private static final ImageIcon[] tiles = new ImageIcon[4];
    private static JPanel tilePalette;
    private static JPanel statusBarContainer;
    private static Label boxLabel;
    private static Label goalLabel;

    /**
     * A constructor to initialise the key listener which allows methods to be
     * run when key presses are detected
     */
    private GamePanel(MapPanel mapPanel) {
        listener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                moveWorker(e);
            }
        };
        addKeyListener(listener);
        setFocusable(true);

        GamePanel.mapPanel = mapPanel;

        list = new JList<ImageIcon>(tiles);
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        importPaletteIcons();
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

        tilePalette = new JPanel();
        tilePalette.setLayout(new BoxLayout(tilePalette, BoxLayout.X_AXIS));
        tilePalette.setBackground(Color.WHITE);
        tilePalette.add(counters, BorderLayout.WEST);
        tilePalette.add(list, BorderLayout.CENTER);
        tilePalette.add(padding, BorderLayout.EAST);
        tilePalette.setVisible(false);
        tilePalette.setPreferredSize(new Dimension(tilePalette.getSize().width, 48));
        selectTile();

        statusBarContainer = new JPanel();
        statusBar = new JLabel();
        statusBar.setFont(new Font("Helvetica",Font.PLAIN , 24));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        statusBarContainer.add(statusBar);
        statusBarContainer.setPreferredSize(new Dimension(statusBarContainer.getSize().width, 48));
        add(mapPanel);
        add(statusBarContainer);
        add(tilePalette);
        redraw();
        setVisible(true);
    }

    class ListListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                selectTile();
            }
        }
    }

    public static GamePanel getInstance(MapPanel mapPanel) {
        if (instance == null) {
            instance = new GamePanel(mapPanel);
        }
        return instance;
    }

    public static void toggleMode() {
        SokobanMap map = mapPanel.getSokobanMap();
        if (!playable) {
            map.setInitialState(map.getState());
        }
        map.reset();
        playable = !playable;
        tilePalette.setVisible(!playable);
        statusBarContainer.setVisible(playable);
        mapPanel.resetSolver();
        mapPanel.toggleMode();
        redraw();
    }

    public static MapPanel getSpriteMap() {
        return mapPanel;
    }

    public static SokobanMap getSokobanMap() {
        return mapPanel.getSokobanMap();
    }

    /**
     * Runs player movement methods when keypresses are detected, then checks
     * to see if the level has been completed. If it has, displays "YOU WON!",
     * else redraws the level.
     *
     * TODO:    Only check if win conditions have been met when a box is placed
     *          on a goal rather than every time the player moves
     */
    private static void moveWorker(KeyEvent e) {
        if (!playable) {
            return;
        }

        SokobanMap map = getSokobanMap();

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:     map.move(new Coordinate(0, -1));
                                    break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:     map.move(new Coordinate(0, 1));
                                    break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:     map.move(new Coordinate(-1, 0));
                                    break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:     map.move(new Coordinate(1, 0));
                                    break;
            default:                return;
        }
        redraw();
    }

    /**
     * Updates the contents of the game window
     */
    public static void redraw() {
        int boxCount = getSokobanMap().getState().getBoxPositions().size();
        int goalCount = getSokobanMap().getState().getGoalPositions().size();
        boxLabel.setText("Boxes: " + boxCount);
        goalLabel.setText("Goals: " + goalCount);

        statusBar.setText(Integer.toString(getSokobanMap().historyLength() -1));

        mapPanel.placeSprites();

        if (playable && getSokobanMap().isDone()) {
            SokobanPanel.winDialog();
        }
    }

    public static void importPaletteIcons() {
        tiles[0] = mapPanel.getUnscaledIconMap().get("WALL");
        tiles[1] = mapPanel.getUnscaledIconMap().get("BOX");
        tiles[2] = mapPanel.getUnscaledIconMap().get("GOAL");
        tiles[3] = mapPanel.getUnscaledIconMap().get("PLAYER");
        list.setFixedCellHeight(mapPanel.getIconSize() + 4);
        list.setFixedCellWidth(mapPanel.getIconSize() + 4);
        list.repaint();
    }

    private static void selectTile() {

        int selection = list.getSelectedIndex();
        SokobanObject paletteState = SokobanObject.WALL;

        switch(selection) {
            case 0: paletteState = SokobanObject.WALL;
                    break;
            case 1: paletteState = SokobanObject.BOX;
                    break;
            case 2: paletteState = SokobanObject.GOAL;
                    break;
            case 3: paletteState = SokobanObject.PLAYER;
                    break;
        }

        SpriteLabel.setPaletteState(paletteState);
    }
}
