import java.util.*;
import java.io.*;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	// SpriteMap class to display the tileset.
	private static SpriteMap spriteMap;
	private static JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
	private static int tileSetNo=1;

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
	private static void importLevel(File levelFile) {
		if (levelFile == null) { //If Getfile is cancelled.
            return;
        }
		int x = 0;
		int y = 0;
		xSize = 0;
		ySize = 0;
		/*
		 * First, get the raw data as an array of strings and use this to
		 * determine the size of the level
		 */
		ArrayList<String> levelLines = new ArrayList<String>();
		try {
			Scanner level = new Scanner(levelFile);
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
			map.growGrass();
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
            case KeyEvent.VK_H:     map.getChanges();
            break;
            default:                return;
		}
		redraw();
	}

	private static void makeMenuBar(JFrame frame)
	{
		final int SHORTCUT_MASK =
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		JMenuBar menubar = new JMenuBar();
		frame.setJMenuBar(menubar);

		// create the File manu
		JMenu fileMenu = new JMenu("File");
		menubar.add(fileMenu);

		JMenuItem openItem = new JMenuItem("Open");
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_MASK));
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			// GOT TO SORT THIS OUT.  Possibly put this and repeated code in main method in importLevel().
				importLevel(getFile());
				frame.remove(spriteMap);
				spriteMap = new SpriteMap(map);
				redraw();
				frame.add(spriteMap);
				frame.pack();}
		});
		fileMenu.add(openItem);

		JMenuItem quitItem = new JMenuItem("Quit");
		quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
		quitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { System.exit(0);; }
		});
		fileMenu.add(quitItem);

		JMenuItem tileItem = new JMenuItem("Change Tileset");
		tileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, SHORTCUT_MASK));
		tileItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tileSetNo++;
				spriteMap.loadSprites(tileSetNo%3);; }
		});
		fileMenu.add(tileItem);
	}

	public static File getFile()
	{
		int returnVal = fileChooser.showOpenDialog(null);
		if(returnVal != JFileChooser.APPROVE_OPTION) {
			return null;  // cancelled
		}
		File selectedFile = fileChooser.getSelectedFile();
		return selectedFile;
	}

	/**
	 * Updates the contents of the game window
	 */
	public static void redraw() {
		textArea.setText(map.toString());
		textArea.append(Integer.toString(map.totalHistoryLength() - 1));
		// New line here to redraw the spriteMap.
		spriteMap.placeSprites();
	}

	public static void main(String[] args) {
		importLevel(new File("src/level"));
		BoxTerm boxterm = new BoxTerm();
		JFrame frame = new JFrame("Box Terminator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		textArea = new JTextArea(ySize + 1, xSize + 1);
		textArea.setFont(new Font("monospaced", Font.PLAIN, 24));
		textArea.setEditable(false);
		// Added by Josh 16.02.29.
		spriteMap = new SpriteMap(map);
		redraw();
		frame.add(boxterm);
		//        frame.add(new JScrollPane(textArea));
		// Following two lines added by Josh. 16.02.29.
		//		frame.setSize((xSize)*32,(ySize+1)*32);
		frame.add(spriteMap);
		makeMenuBar(frame);
		frame.pack();
		frame.setVisible(true);
        System.
	}
}
