package team1;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/** Box Terminator main method. This class handles importing the level, drawing
 * the game screen and interpreting key presses.
 */

@SuppressWarnings("serial")
public class BoxTerm extends JPanel {
    private static SokobanMap map;
    private static SpriteMap gameMap;
    private static SpriteMap editorMap;
    private static JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
    private static JLabel statusBar;
    private static SpriteMap spriteMap;
    private static int tileSetNo = 1;
    private static SokobanGame game;
    public static LevelBuilder builder;
    private static boolean editMode = false;
    private static JFrame frame;
    private static BoxTerm boxTerm;
    private static JMenuBar menubar;
    private static JMenu editMenu;
    private static JMenu gameMenu;

    public BoxTerm() {
    }
    /**
     * A constructor to initialise the key listener which allows methods to be
     * run when key presses are detected
     */
    public static int getTileSetNo() {
        return tileSetNo;
    }

    public static SpriteMap getSpriteMap() {
        return getMySpriteMap();
    }

    private static void makeMenuBar(JFrame frame) {
        final int SHORTCUT_MASK =
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        menubar = new JMenuBar();
        frame.setJMenuBar(menubar);

        // create the File manu
        JMenu fileMenu = new JMenu("File");
        menubar.add(fileMenu, 0);

        editMenu = new JMenu("Edit");
        // menubar.add(editMenu);

        gameMenu = new JMenu("Game");
        menubar.add(gameMenu, 1);

        JMenu viewMenu = new JMenu("View");
        menubar.add(viewMenu, 2);

        JMenu helpMenu = new JMenu("Help");
        menubar.add(helpMenu, 3);

        // File menu

        JMenuItem newMap = new JMenuItem("New", KeyEvent.VK_N);
        newMap.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_MASK));
        newMap.setToolTipText("Start a new map design");
		newMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                // mainPanel.removeAll();
                // SokobanMap map = new SokobanMap(20, 20, 100);
                // getMySpriteMap() = new SpriteMap(map, false, BoxTerm.getTileSetNo() % 3);
                // tilePalette = new TilePalette(getMySpriteMap());
                // mainPanel.add(getMySpriteMap());
                // mainPanel.add(tilePalette);
                // frame.setSize(frame.getPreferredSize());
            }
		});
        fileMenu.add(newMap);

        JMenuItem openItem = new JMenuItem("Open");
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_MASK));
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // GOT TO SORT THIS OUT.  Possibly put this and repeated code in main method in importLevel().
                try {
                    map = SokobanMap.importLevel(getFile());
                } catch (FileNotFoundException e1) {
                    // TODO Sort out some verification here.
                    System.out.println("BAD LEVEL");
                    e1.printStackTrace();
                }

                if (!editMode) {
                    boxTerm.remove(game);
                    gameMap = new SpriteMap(map, true, 1);
                    game = new SokobanGame(gameMap);
                    boxTerm.add(game, BorderLayout.CENTER);
                } else {
                    boxTerm.remove(builder);
                    editorMap = new SpriteMap(map, false, 1);
                    builder = new LevelBuilder(editorMap);
                    boxTerm.add(builder, BorderLayout.SOUTH);
                }

                frame.pack();
            }
        });
        fileMenu.add(openItem);

        JMenuItem save = new JMenuItem("Save", KeyEvent.VK_S);
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_MASK));
        save.setToolTipText("Save current map design to file");
        // save.addActionListener(new SaveAction());
        fileMenu.add(save);

        JMenuItem levelBuilderItem = new JMenuItem("Toggle mode");
        levelBuilderItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleMode();
            }
        });
        fileMenu.add(levelBuilderItem);

        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
        quitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(quitItem);


        // Edit menu

        JMenuItem undo = new JMenuItem("Undo", KeyEvent.VK_Z);
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, SHORTCUT_MASK));
		undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                SokobanMap map = getMySpriteMap().getMap();
                map.undo();
                TilePalette.updateCounters();
                getMySpriteMap().placeSprites();
            }
		});
        editMenu.add(undo);
        gameMenu.add(undo);

        JMenuItem redo = new JMenuItem("Redo", KeyEvent.VK_Y);
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, SHORTCUT_MASK));
		redo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                SokobanMap map = getMySpriteMap().getMap();
                map.redo();
                TilePalette.updateCounters();
                getMySpriteMap().placeSprites();
            }
		});
        editMenu.add(redo);
        gameMenu.add(redo);



        // Game menu

        JMenuItem resetItem = new JMenuItem("Reset level");
        resetItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, SHORTCUT_MASK));
        resetItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getMySpriteMap().reset();
                SokobanGame.redraw();
            }
        });
        gameMenu.add(resetItem);



        // View menu

        JMenuItem tileItem = new JMenuItem("Change Tileset");
        tileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, SHORTCUT_MASK));
        tileItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tileSetNo++;
                getMySpriteMap().loadSprites(tileSetNo % 3);
            }
        });
        viewMenu.add(tileItem);

        JMenuItem magnifyItem = new JMenuItem("Increase Magnification");
        magnifyItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getMySpriteMap().changeMagnification(true);
                getMySpriteMap().loadSprites(tileSetNo % 3);
                frame.pack();
            }
        });
        viewMenu.add(magnifyItem);

        JMenuItem deMagnifyItem = new JMenuItem("Decrease Magnification");
        deMagnifyItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getMySpriteMap().changeMagnification(false);
                getMySpriteMap().placeSprites();
                frame.pack();
            }
        });
        viewMenu.add(deMagnifyItem);



        // Help menu

        JMenuItem assistItem = new JMenuItem("Print solution");
        assistItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SokobanMap mapToSolve = new SokobanMap(map);
                SingleThreadSolver solver = new SingleThreadSolver(mapToSolve);
                System.out.println(solver.levelSolution());
            }
        });
        helpMenu.add(assistItem);

        JMenuItem builderHelpItem = new JMenuItem("Builder help");
        builderHelpItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, " This map editor can be used to"
                   + " design your own Sokoban levels (maximum 20x20). \n\n1) Use the"
                   + " palette on the right to select either walls, pressure pad, player"
                   + " starting position or boxes. \n\n2) Click on the map once you have"
                   + " selected something on the palette to begin designing your level."
                   + " \n\n3) You can save your map design at any point using File>Save."
                   + " \n\n4) You must use File>Compile Map if you want to run your map"
                   + " in-game.", "Map Editor Help", JOptionPane.PLAIN_MESSAGE,
                    getMySpriteMap().getBoxSprite());
            }
        });
        helpMenu.add(builderHelpItem);


        JMenuItem printItem = new JMenuItem("Print topleft Accessible Area");
        printItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println(map.getState().getWPos());
            }
        });
        helpMenu.add(printItem);

        JMenuItem aboutItem = new JMenuItem("About Box Terminator");
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(getMySpriteMap(), "A Sokoban clone.", "Box Terminator",JOptionPane.PLAIN_MESSAGE,getMySpriteMap().getBoxSprite());
            }
        });
        helpMenu.add(aboutItem);
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

    public static void toggleMode() {
        if (editMode) {
            editMode = false;
            menubar.remove(editMenu);
            menubar.add(gameMenu, 1);
            boxTerm.remove(builder);
            gameMap = new SpriteMap(SokobanMap.shallowCopy(editorMap.getMap(), 20), true, tileSetNo);
            game = new SokobanGame(gameMap);
            boxTerm.add(game);
            game.requestFocusInWindow();
        } else {
            editMode = true;
            menubar.remove(gameMenu);
            menubar.add(editMenu, 1);
            game.removeKeyListener(SokobanGame.listener);
            boxTerm.remove(game);
            editorMap = new SpriteMap(new SokobanMap(gameMap.getMap(), 100), false, tileSetNo);
            builder = new LevelBuilder(editorMap);
            boxTerm.add(builder);
        }
        frame.setSize(frame.getPreferredSize());
    }

    public static SpriteMap getMySpriteMap() {
        if (!editMode) {
            return gameMap;
        } else {
            return editorMap;
        }
    }

    public static void main(String[] args) {
        InputStream level = BoxTerm.class.getClassLoader().getResourceAsStream("level");
        map = SokobanMap.importLevel(level);
        gameMap = new SpriteMap(map, true, 1);
        editorMap = new SpriteMap(map, false, 1);
        game = new SokobanGame(gameMap);
        builder = new LevelBuilder(editorMap);

        boxTerm = new BoxTerm();
        boxTerm.setLayout(new BoxLayout(boxTerm, BoxLayout.X_AXIS));

        frame = new JFrame("Box Terminator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        boxTerm.add(game, BorderLayout.CENTER);
        makeMenuBar(frame);
        frame.add(boxTerm);
        frame.pack();
        frame.setVisible(true);
    }
}
