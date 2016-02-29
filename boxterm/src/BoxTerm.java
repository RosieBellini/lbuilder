import java.util.*;
import java.io.*;
import java.awt.Font;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/** Box Terminator main method. This class handles importing the level, drawing
 * the game screen and interpreting key presses.
 *
 * TODO:    -Fix freeze when clicking on the level
 *          -Draw the level with something more appropriate - JavaFX or Graphics
 *          2d?
 */

@SuppressWarnings("serial")
public class BoxTerm extends JPanel {
    private static SokobanMap map;
    private static int xSize;
    private static int ySize;
    private static JTextArea textArea;

    /**
     * A constructor to initialise the key listener which allows methods to be
     * run when key presses are detected
     */
    public BoxTerm() {
        addKeyListener(new KeyListener() {
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
        });
        setFocusable(true);
    }

    /**
     * Interprets the contents of the "level" file and stores it as a SokobanMap
     */
    private static void importLevel() {
        int x = 0;
        int y = 0;

        /*
         * First, get the raw data as an array of strings and use this to
         * determine the size of the level
         */
        ArrayList<String> levelLines = new ArrayList<String>();
        try {
            Scanner level = new Scanner(new FileReader("src/level"));
            while (level.hasNextLine()) {
                String line = level.nextLine();
                if (line.length() > xSize) {
                    xSize = line.length();
                }
                levelLines.add(line);
            }
            ySize = levelLines.size();

            /*
             * Then convert the raw data into a SokobanMap using the static
             * method charToSokobanObject from the SokobanObject class
             */
            map = new SokobanMap(xSize, ySize);
            for (String line: levelLines) {
                for (char ch: line.toCharArray()) {
                    Coordinate coord = new Coordinate(x, y);
                    SokobanObject object = SokobanObject.charToSokobanObject(ch);
                    map.put(object, coord);
                    x++;
                }
                x = 0;
                y++;
            }
            level.close();
        } catch (FileNotFoundException e) {
            System.out.println("Level file not found");
        }
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
        switch(e.getKeyCode()) {
            case KeyEvent.VK_W:     map.move(new Coordinate(0, -1));
                                    break;
            case KeyEvent.VK_S:     map.move(new Coordinate(0, 1));
                                    break;
            case KeyEvent.VK_A:     map.move(new Coordinate(-1, 0));
                                    break;
            case KeyEvent.VK_D:     map.move(new Coordinate(1, 0));
                                    break;
            case KeyEvent.VK_U:     map.undo(true);
                                    break;
            case KeyEvent.VK_R:     map.redo();
                                    break;
            default:                return;
        }
        if (map.isDone()) {
            textArea.setText("YOU WON!");
        } else {
            redraw();
        }
    }

    /**
     * Updates the contents of the game window
     */
    public static void redraw() {
        textArea.setText(map.toString());
        textArea.append(Integer.toString(map.totalHistoryLength() - 1));
    }

    public static void main(String[] args) {
        importLevel();
        BoxTerm boxterm = new BoxTerm();
        JFrame frame = new JFrame("Box Terminator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        textArea = new JTextArea(ySize + 1, xSize + 1);
        textArea.setFont(new Font("monospaced", Font.PLAIN, 24));
        textArea.setEditable(false);
        redraw();
        frame.add(boxterm);
        frame.add(new JScrollPane(textArea));
        frame.pack();
        frame.setVisible(true);
    }
}
