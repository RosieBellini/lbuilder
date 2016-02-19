import java.util.*;
import java.io.*;
import java.awt.Font;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@SuppressWarnings("serial")
public class BoxTerm extends JPanel {
    private static SokobanMap map;
    private static int xSize;
    private static int ySize;
    private static JTextArea textArea;

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


    private static void importLevel() {
        int x = 0;
        int y = 0;
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
