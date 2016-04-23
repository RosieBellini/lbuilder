package team1;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
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
    private static JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
    private static int tileSetNo = 1;
    private static float magnification = 1;
    private static SokobanGame game;
    private static LevelBuilder builder;
    private static boolean editMode = false;
    private static JFrame frame;
    private static BoxTerm boxTerm;
    private static JMenuBar menubar;
    private static JMenu gameMenu;
    private static Set<JMenuItem> editMenuItems = new HashSet<JMenuItem>();
    private static Set<JMenuItem> gameMenuItems = new HashSet<JMenuItem>();

    public static int getTileSetNo() {
        return tileSetNo;
    }

    public static float getMagnification() {
        return magnification;
    }

    public static JFrame getFrame() {
        return frame;
    }

    private static void makeMenuBar(JFrame frame) {
        final int SHORTCUT_MASK =
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        menubar = new JMenuBar();
        frame.setJMenuBar(menubar);

        // create the File manu
        JMenu fileMenu = new JMenu("File");
        menubar.add(fileMenu);

        gameMenu = new JMenu("Game");
        menubar.add(gameMenu);

        JMenu viewMenu = new JMenu("View");
        menubar.add(viewMenu);

        JMenu helpMenu = new JMenu("Help");
        menubar.add(helpMenu);



        // File menu

        JMenuItem newMapItem = new JMenuItem("New", KeyEvent.VK_N);
        newMapItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_MASK));
        newMapItem.setToolTipText("Start a new map design");
		newMapItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                SokobanMap map = new SokobanMap(20, 20, 100);
                map.put(SokobanObject.PLAYER, new Coordinate(5, 5));
                LevelBuilder.getSpriteMap().updateMap(map);
                LevelBuilder.updateCounters();
                frame.setSize(frame.getPreferredSize());
            }
		});
        editMenuItems.add(newMapItem);
        fileMenu.add(newMapItem);

        JMenuItem openItem = new JMenuItem("Open");
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_MASK));
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    SokobanMap map = SokobanMap.importLevel(getFile());
                    if (!editMode) {
                        SokobanGame.getSpriteMap().updateMap(map);
                    } else {
                        LevelBuilder.getSpriteMap().updateMap(map);
                        LevelBuilder.updateCounters();
                    }
                } catch (FileNotFoundException e1) {
                    // TODO Sort out some verification here.
                    System.out.println("BAD LEVEL");
                    e1.printStackTrace();
                }

                frame.pack();
            }
        });
        fileMenu.add(openItem);

        JMenuItem saveItem = new JMenuItem("Save", KeyEvent.VK_S);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_MASK));
        saveItem.setToolTipText("Save current map design to file");
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser() {
                    @Override
                    public void approveSelection(){
                        File file = getSelectedFile();
                        if( file.exists() && getDialogType() == SAVE_DIALOG){
                            int result = JOptionPane.showConfirmDialog(this, file + " already exists. Overwrite it?", "Overwrite file", JOptionPane.YES_NO_OPTION);
                            switch(result){
                                case JOptionPane.YES_OPTION:
                                    super.approveSelection();
                                    return;
                                case JOptionPane.NO_OPTION:
                                    return;
                                case JOptionPane.CLOSED_OPTION:
                                    return;
                            }
                        }
                        super.approveSelection();
                    }
                };

                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    if (file != null) {
                        Path newFile = Paths.get(file.getPath());
                        List<String> contents = Arrays.asList(LevelBuilder.getSpriteMap().getMap().toString().split("\\n"));
                        try {
                            Files.write(newFile, contents);
                        } catch (IOException io) {
                            System.out.println("Couldn't save");
                        }
                    }
                }
            }
        });
        editMenuItems.add(saveItem);
        fileMenu.add(saveItem);

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
                if (editMode) {
                    LevelBuilder.updateCounters();
                } else {
                    SokobanGame.redraw();
                }
                getMySpriteMap().placeSprites();
            }
        });
        gameMenu.add(undo);

        JMenuItem redo = new JMenuItem("Redo", KeyEvent.VK_Y);
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, SHORTCUT_MASK));
        redo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SokobanMap map = getMySpriteMap().getMap();
                map.redo();
                LevelBuilder.updateCounters();
                getMySpriteMap().placeSprites();
            }
        });
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
        gameMenuItems.add(resetItem);
        gameMenu.add(resetItem);



        // View menu

        JMenuItem tileItem = new JMenuItem("Change Tileset");
        tileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, SHORTCUT_MASK));
        tileItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tileSetNo = (tileSetNo + 1) % 3;
                SokobanGame.getSpriteMap().loadSprites(tileSetNo);
                LevelBuilder.getSpriteMap().loadSprites(tileSetNo);
                LevelBuilder.importImages();
            }
        });
        viewMenu.add(tileItem);

        JMenuItem magnifyItem = new JMenuItem("Increase Magnification");
        magnifyItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeMagnification(true);
                frame.pack();
            }
        });
        viewMenu.add(magnifyItem);

        JMenuItem deMagnifyItem = new JMenuItem("Decrease Magnification");
        deMagnifyItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeMagnification(false);
                frame.pack();
            }
        });
        viewMenu.add(deMagnifyItem);



        // Help menu

        JMenuItem assistItem = new JMenuItem("Print solution");
        assistItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SokobanMap mapToSolve = new SokobanMap(SokobanGame.getSpriteMap().getMap());
                SingleThreadSolver solver = new SingleThreadSolver(mapToSolve);
                System.out.println(solver.levelSolution());
            }
        });
        gameMenuItems.add(assistItem);
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
        editMenuItems.add(builderHelpItem);
        helpMenu.add(builderHelpItem);

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

    public static void changeMagnification(boolean getBigger) {
        if (getBigger) {
            if (magnification > 1) {
                magnification++;
            } else {
                magnification = magnification * 2;
            }
        } else {
            if (magnification > 1) {
                magnification--;
            } else {
                magnification = magnification / 2;
            }
        }
        getMySpriteMap().update();
        if (editMode) {
            LevelBuilder.importImages();
        }
    }

    public static void toggleMode() {
        editMode = !editMode;

        if (!editMode) {
            gameMenu.setText("Game");
            builder.setVisible(false);
            SokobanGame.getSpriteMap().updateMap(SokobanMap.shallowCopy(LevelBuilder.getSpriteMap().getMap(), 20));
            game.setVisible(true);
            game.requestFocusInWindow();
            SokobanGame.redraw();
        } else {
            gameMenu.setText("Edit");
            game.setVisible(false);
            LevelBuilder.getSpriteMap().updateMap(new SokobanMap(SokobanGame.getSpriteMap().getMap(), 100));
            builder.setVisible(true);
        }

        updateContextMenu();
        frame.setSize(frame.getPreferredSize());
    }

    public static void updateContextMenu() {
        for (JMenuItem item : gameMenuItems) {
            item.setVisible(!editMode);
        }

        for (JMenuItem item : editMenuItems) {
            item.setVisible(editMode);
        }
    }

    public static SpriteMap getMySpriteMap() {
        if (!editMode) {
            return SokobanGame.getSpriteMap();
        } else {
            return LevelBuilder.getSpriteMap();
        }
    }

    public static void main(String[] args) {
        frame = new JFrame("Box Terminator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        InputStream level = BoxTerm.class.getClassLoader().getResourceAsStream("level");
        SokobanMap map = SokobanMap.importLevel(level);
        game = new SokobanGame(new SpriteMap(map, true, 1));
        builder = new LevelBuilder(new SpriteMap(map, false, 1));

        boxTerm = new BoxTerm();
        boxTerm.setLayout(new BoxLayout(boxTerm, BoxLayout.X_AXIS));
        boxTerm.add(game, BorderLayout.CENTER);
        boxTerm.add(builder, BorderLayout.SOUTH);
        builder.setVisible(false);

        makeMenuBar(frame);
        updateContextMenu();
        frame.setResizable(false);
        frame.add(boxTerm);
        frame.pack();
        frame.setVisible(true);
    }
}
