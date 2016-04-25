package team1;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Box Terminator main method. This class handles importing the level, drawing
 * the game screen and interpreting key presses.
 */

@SuppressWarnings("serial")
public class SokobanGame extends JPanel {
    // private static SokobanMap map;
    private static JLabel statusBar;
    private static SpriteMap spriteMap;
    private static JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
    private static int tileSetNo = 1;
    private static KeyListener listener;
    private static SokobanGame instance;

    /**
     * A constructor to initialise the key listener which allows methods to be
     * run when key presses are detected
     */
    private SokobanGame(SpriteMap spriteMap) {
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

        // setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        SokobanGame.spriteMap = spriteMap;

        JPanel statusBarContainer = new JPanel();
        statusBar = new JLabel();
        statusBar.setFont(new Font("Helvetica",Font.PLAIN , 24));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        statusBarContainer.add(statusBar);
        statusBarContainer.setPreferredSize(new Dimension(statusBarContainer.getSize().width, 48));
        add(spriteMap);
        add(statusBarContainer);
        redraw();
        setVisible(true);
    }

    public static SokobanGame getInstance(SpriteMap spriteMap) {
        if (instance == null) {
            instance = new SokobanGame(spriteMap);
        }
        return instance;
    }

    public static int getTileSetNo() {
        return tileSetNo;
    }

    public static SpriteMap getSpriteMap() {
        return spriteMap;
    }

    public static SokobanMap getSokobanMap() {
        return spriteMap.getSokobanMap();
    }

    /**
     * Runs player movement methods when keypresses are detected, then checks
     * to see if the level has been completed. If it has, displays "YOU WON!",
     * else redraws the level.
     *
     * TODO:    Only check if win conditions have been met when a box is placed
     *          on a goal rather than every time the player moves
     */
    public static void moveWorker(KeyEvent e) {
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

    //TODO The getFile method should check for a valid Sokoban level file and force the user to choose another level
    public static InputStream getFile() throws FileNotFoundException {
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return null;  // cancelled
        }
        File selectedFile = fileChooser.getSelectedFile();
        InputStream streamToReturn = new FileInputStream(selectedFile);
        return streamToReturn;
    }

    /**
     * Updates the contents of the game window
     */
    public static void redraw() {
        SokobanMap map = getSokobanMap();
        statusBar.setText(Integer.toString(map.totalHistoryLength() - 1));
        spriteMap.placeSprites();
    }

}
