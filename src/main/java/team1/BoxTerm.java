package team1;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

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
	 * Runs player movement methods when keypresses are detected, then checks
	 * to see if the level has been completed. If it has, displays "YOU WON!",
	 * else redraws the level.
	 *
	 * TODO:    Only check if win conditions have been met when a box is placed
	 *          on a goal rather than every time the player moves
	 */
	private static void moveWorker(KeyEvent e) {
		switch(e.getKeyCode()) {
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

		JMenu viewMenu = new JMenu("View");
		menubar.add(viewMenu);

		JMenu helpMenu = new JMenu("Help");
		menubar.add(helpMenu);

		JMenuItem openItem = new JMenuItem("Open");
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_MASK));
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// GOT TO SORT THIS OUT.  Possibly put this and repeated code in main method in importLevel().
				try {
					SokobanMap.importLevel(getFile());
				} catch (FileNotFoundException e1) {
					// TODO Sort out some verification here.
					System.out.println("BAD LEVEL");
					e1.printStackTrace();
				}
				frame.remove(spriteMap);
				spriteMap = new SpriteMap(map, true, 1);
				redraw();
				frame.add(spriteMap);
				frame.pack();
			}
		});
		fileMenu.add(openItem);

		JMenuItem levelBuilderItem = new JMenuItem("Start level builder");
		levelBuilderItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LevelBuilder.activate();
			}
		});
		fileMenu.add(levelBuilderItem);

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
		viewMenu.add(tileItem);

		JMenuItem magnifyItem = new JMenuItem("Increase Magnification");
		magnifyItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				spriteMap.changeMagnification(true);
				spriteMap.loadSprites(tileSetNo%3);
				frame.pack(); }
		});
		viewMenu.add(magnifyItem);

		JMenuItem deMagnifyItem = new JMenuItem("Decrease Magnification");
		deMagnifyItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				spriteMap.changeMagnification(false);
				spriteMap.placeSprites();
				frame.pack(); }
		});
		viewMenu.add(deMagnifyItem);

		JMenuItem aboutItem = new JMenuItem("About Box Terminator");
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(spriteMap, "A Sokoban clone.", "Box Terminator",JOptionPane.PLAIN_MESSAGE,spriteMap.getBoxSprite());
			}
		});
		helpMenu.add(aboutItem);
	}

	//TODO The getFile method should check for a valid Sokoban level file and force the user to choose another level
	public static InputStream getFile() throws FileNotFoundException
	{
		int returnVal = fileChooser.showOpenDialog(null);
		if(returnVal != JFileChooser.APPROVE_OPTION) {
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
		textArea.setText(map.toString());
		textArea.append(Integer.toString(map.totalHistoryLength() - 1));
		// New line here to redraw the spriteMap.
		spriteMap.placeSprites();
	}

	public static void main(String[] args) {
		// importLevel(new File("src/main/resources/level"));
		InputStream level = BoxTerm.class.getClassLoader().getResourceAsStream("level");
		map = SokobanMap.importLevel(level);
		map.growGrass();
		BoxTerm boxterm = new BoxTerm();
		JFrame frame = new JFrame("Box Terminator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		textArea = new JTextArea(ySize + 1, xSize + 1);
		textArea.setFont(new Font("monospaced", Font.PLAIN, 24));
		textArea.setEditable(false);
		spriteMap = new SpriteMap(map, true, 1);
		redraw();
		frame.add(boxterm);
		frame.add(spriteMap);
		makeMenuBar(frame);
		frame.pack();
		frame.setVisible(true);
	}
}
