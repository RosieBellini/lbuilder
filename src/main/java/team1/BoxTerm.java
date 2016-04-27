package team1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

/** Box Terminator main method. This class handles importing the level, drawing
 * the game screen and interpreting key presses.
 */

@SuppressWarnings("serial")
public class BoxTerm extends JPanel {
    private static JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
    private static int tileSetNo = 1;
    private static float magnification = 1;
    private static SokobanGame game;
    private static LevelEditor editor;
    private static boolean editMode = false;
    private static JFrame frame;
    private static BoxTerm boxTerm;
    private static JMenuBar menubar;
    private static JMenu gameMenu;
    private static JMenuItem toggleItem;
    private static Set<JMenuItem> editMenuItems = new HashSet<JMenuItem>();
    private static Set<JMenuItem> gameMenuItems = new HashSet<JMenuItem>();
    private static SingleThreadSolver solver;
    private static ArrayList<String> levels = new ArrayList<String>();
    private static int currentLevelIndex = 0;

    public static int getTileSetNo() {
        return tileSetNo;
    }

    public static float getMagnification() {
        return magnification;
    }

    public static JFrame getFrame() {
        return frame;
    }

    public static void startSolver() {
        final JDialog dialog = new JDialog();
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JButton b2 = new JButton("Close");

        ActionListener listen2 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        };

        b2.addActionListener(listen2);

        class solverWorker extends SwingWorker<Void, Object> {
            String solution;

            @Override
            protected void done() {
                if (solution == "NO_SOLUTION") {
                    dialog.dispose();
                } else {
                    panel.removeAll();
                    panel.add(new JLabel("<html>" + solution.replaceAll("\n", "<br>") + "</html>"), BorderLayout.PAGE_START);
                    panel.add(b2, BorderLayout.SOUTH);
                    dialog.pack();
                }
            }

            @Override
            protected Void doInBackground() throws Exception {
                SokobanMap mapToSolve = new SokobanMap(SokobanGame.getSokobanMap());
                solver = new SingleThreadSolver(mapToSolve);
                solution = solver.levelSolution();
                return null;
            }
        };

        ImageIcon spinnyCube = new ImageIcon(Toolkit.getDefaultToolkit().getImage(BoxTerm.class.getResource("/tileset01/cube.gif")));
        JLabel loadingCube = new JLabel(spinnyCube);
        JLabel msgLabel = new JLabel("Calculating...");
        JButton b1 = new JButton("Cancel");

        ActionListener listen = new ActionListener() {
            public void actionPerformed(ActionEvent e2) {
                solver.stopSolving();
            }
        };

        b1.addActionListener(listen);

        panel.add(msgLabel, BorderLayout.PAGE_START);
        panel.add(loadingCube, BorderLayout.CENTER);
        panel.add(b1, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        dialog.getContentPane().add(panel);
        dialog.setTitle("Solver");
        dialog.setResizable(false);
        dialog.pack();
        dialog.setSize(200, dialog.getHeight());
        dialog.setLocationRelativeTo(frame);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);

        (new solverWorker()).execute();
    }

    private static void makeMenuBar(JFrame frame) {
        final int SHORTCUT_MASK =
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        menubar = new JMenuBar();
        frame.setJMenuBar(menubar);

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
                LevelEditor.getSpriteMap().updateMap(map);
                LevelEditor.getSokobanMap().put(SokobanObject.PLAYER, new Coordinate(5, 5));
                LevelEditor.getSpriteMap().forceRedraw();
                LevelEditor.updateCounters();
                frame.setSize(frame.getPreferredSize());
            }
		});
        editMenuItems.add(newMapItem);
        fileMenu.add(newMapItem);

        JMenuItem openItem = new JMenuItem("Open");
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_MASK));
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openDialog();
            }
        });
        fileMenu.add(openItem);

        JMenuItem saveItem = new JMenuItem("Save", KeyEvent.VK_S);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_MASK));
        saveItem.setToolTipText("Save current map design to file");
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!LevelEditor.getSokobanMap().validate()) {
                    int result = JOptionPane.showConfirmDialog(frame, "This "
                            + "level is incomplete.\nYou may save it and "
                            + "resume editing later,\nbut it won't be "
                            + "playable.\nContinue?", "Save level",
                            JOptionPane.YES_NO_OPTION);
                    switch (result) {
                        case JOptionPane.NO_OPTION:
                        case JOptionPane.CLOSED_OPTION:
                            return;
                    }
                }

                JFileChooser fileChooser = new JFileChooser() {
                    @Override
                    public void approveSelection(){
                        File file = getSelectedFile();
                        if (file.exists() && getDialogType() == SAVE_DIALOG) {
                            int result = JOptionPane.showConfirmDialog(this, file + " already exists. Overwrite it?", "Overwrite file", JOptionPane.YES_NO_OPTION);
                            switch (result) {
                                case JOptionPane.YES_OPTION:
                                    super.approveSelection();
                                    return;
                                case JOptionPane.NO_OPTION:
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
                        List<String> contents = Arrays.asList(LevelEditor.getSokobanMap().toString().split("\\n"));
                        try {
                            Files.write(newFile, contents);
                            LevelEditor.getSpriteMap().updateMap(SokobanMap.shallowCopy(LevelEditor.getSokobanMap(), 100));
                        } catch (IOException io) {
                            System.out.println("Couldn't save");
                        }
                    }
                }
            }
        });
        editMenuItems.add(saveItem);
        fileMenu.add(saveItem);

        toggleItem = new JMenuItem("Start editor");
        toggleItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, SHORTCUT_MASK));
        toggleItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleMode();
            }
        });
        fileMenu.add(toggleItem);

        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
        quitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeRoutine();
            }
        });
        fileMenu.add(quitItem);



        // Edit menu

        JMenuItem undo = new JMenuItem("Undo", KeyEvent.VK_Z);
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, SHORTCUT_MASK));
        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SokobanMap map = getMySpriteMap().getSokobanMap();
                map.undo();
                if (editMode) {
                    LevelEditor.updateCounters();
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
                SokobanMap map = getMySpriteMap().getSokobanMap();
                map.redo();
                LevelEditor.updateCounters();
                getMySpriteMap().placeSprites();
            }
        });
        gameMenu.add(redo);

        JMenuItem cropItem = new JMenuItem("Crop");
        cropItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(frame, "This is a "
                        + "potentially destructive\noperation.\n\nAre you sure"
                        + " you want to crop?", "Crop",
                        JOptionPane.YES_NO_OPTION);
                switch (result) {
                    case JOptionPane.NO_OPTION:
                    case JOptionPane.CLOSED_OPTION:
                        return;
                }
                LevelEditor.getSpriteMap().updateMap(SokobanMap.crop(LevelEditor.getSokobanMap()));
                frame.setSize(frame.getPreferredSize());
            }
        });
        editMenuItems.add(cropItem);
        gameMenu.add(cropItem);



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
                LevelEditor.getSpriteMap().loadSprites(tileSetNo);
                LevelEditor.importImages();
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

        JMenuItem assistItem = new JMenuItem("Solver");
        assistItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startSolver();
            }
        });
        gameMenuItems.add(assistItem);
        helpMenu.add(assistItem);

        JMenuItem editorHelpItem = new JMenuItem("Builder help");
        editorHelpItem.addActionListener(new ActionListener() {
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
        editMenuItems.add(editorHelpItem);
        helpMenu.add(editorHelpItem);

        JMenuItem aboutItem = new JMenuItem("About Box Terminator");
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(getMySpriteMap(), "A Sokoban clone.", "Box Terminator",JOptionPane.PLAIN_MESSAGE,getMySpriteMap().getBoxSprite());
            }
        });
        helpMenu.add(aboutItem);
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
        LevelEditor.getSpriteMap().update();
        SokobanGame.getSpriteMap().update();
        LevelEditor.importImages();
    }

    public static void toggleMode() {
        editMode = !editMode;

        if (!editMode) {
            if (LevelEditor.getSokobanMap().validate()) {
                gameMenu.setText("Game");
                toggleItem.setText("Start editor");
                editor.setVisible(false);
                SokobanGame.getSpriteMap().updateMap(SokobanMap.shallowCopy(LevelEditor.getSokobanMap(), 20));
                game.setVisible(true);
                game.requestFocusInWindow();
                SokobanGame.redraw();
            } else {
                editMode = true;
                JOptionPane.showMessageDialog(frame, "This level cannot be won"
                        + ".\nMake sure that there are at least as many boxes"
                        + " as goals\nand that there is at least one uncovered goal,\n"
                        + "and that all goals are accessible to the player.",
                        "Incomplete level", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            gameMenu.setText("Edit");
            toggleItem.setText("Start game");
            game.setVisible(false);
            LevelEditor.getSpriteMap().updateMap(new SokobanMap(SokobanGame.getSokobanMap(), 100));
            editor.setVisible(true);
        }

        updateContextMenu();
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
            return LevelEditor.getSpriteMap();
        }
    }
    private static void getBuiltinLevels() {
        InputStream levelIndex = BoxTerm.class.getResourceAsStream("/levels/LEVEL_INDEX");
        Scanner levelIndexScanner = new Scanner(levelIndex);
        while (levelIndexScanner.hasNextLine()) {
            String levelName = levelIndexScanner.nextLine();
            levels.add(levelName);
        }
        levelIndexScanner.close();
    }

    public static void winDialog() {
        JButton button1 = new JButton("Next level");
        button1.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.getWindowAncestor(button1).dispose();
                        currentLevelIndex++;
                        InputStream level = BoxTerm.class.getResourceAsStream("/levels/" + levels.get(currentLevelIndex));
                        SokobanMap map = SokobanMap.importLevel(level);
                        SokobanGame.getSpriteMap().updateMap(map);
                        // LevelEditor.getSpriteMap().updateMap(map);
                        SokobanGame.redraw();
                        frame.pack();
                    }
                });

        JButton button2 = new JButton("Change level");
        button2.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        SwingUtilities.getWindowAncestor(button2).dispose();
                        openDialog();
                    }
                });

        if (currentLevelIndex == levels.size() - 1 || currentLevelIndex == -1) {
            button1.setEnabled(false);
        }

        JOptionPane.showOptionDialog(frame, "You beat the level!\nWhat next?", "Congratulations!",
                JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, new JButton[] { button1, button2 }, button1);
    }

    private static void openDialog() {
        String[] buttons = { "Open", "Import", "Cancel"};
        String[] items = levels.toArray(new String[levels.size()]);
        JList<String> list = new JList<String>(items);
        list.setSelectedIndex(0);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(200, 256));
        int selectReturnVal = JOptionPane.showOptionDialog(frame, scrollPane, "Select or import a level",
                JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);
        if (selectReturnVal == 0) {
            currentLevelIndex = list.getSelectedIndex();
            InputStream level = BoxTerm.class.getResourceAsStream("/levels/" + levels.get(currentLevelIndex));
            SokobanMap map = SokobanMap.importLevel(level);
            if (!editMode) {
                SokobanGame.getSpriteMap().updateMap(map);
                SokobanGame.redraw();
            } else {
                LevelEditor.getSpriteMap().updateMap(map);
                LevelEditor.updateCounters();
            }
            frame.pack();
        } else if (selectReturnVal == 1) {
            try {
                int returnVal = fileChooser.showOpenDialog(null);
                if (returnVal != JFileChooser.APPROVE_OPTION) {
                    return;  // cancelled
                }
                File selectedFile = fileChooser.getSelectedFile();
                InputStream stream = new FileInputStream(selectedFile);
                SokobanMap map = SokobanMap.importLevel(stream);
                if (!map.validate()) {
                    JOptionPane.showMessageDialog(frame,
                            "This level cannot be beaten.\n You may want "
                            + "to load it in the level editor and correct "
                            + "it.", "Invalid level",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!editMode) {
                    SokobanGame.getSpriteMap().updateMap(map);
                    SokobanGame.redraw();
                } else {
                    LevelEditor.getSpriteMap().updateMap(map);
                    LevelEditor.updateCounters();
                }
                currentLevelIndex = -1;
                frame.pack();
            } catch (FileNotFoundException e1) {
                System.out.println("BAD LEVEL");
                e1.printStackTrace();
            } catch (IllegalArgumentException e2) {
                JOptionPane.showMessageDialog(frame, "Invalid level."
                        + "\nMake sure that the file you have selected "
                        + "uses the standard format.", "Invalid level",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
        frame.pack();
    }

    private static boolean checkChanges() {
        SokobanMap map = LevelEditor.getSokobanMap();
        return map.getInitialState().equals(map.getMyState());
    }

    private static void closeRoutine() {
        if (!checkChanges()) {
            int result = JOptionPane.showConfirmDialog(frame, "Your level has unsaved changes.\n"
                    + "Are you sure you want to exit?", "Unsaved changes",
                    JOptionPane.YES_NO_OPTION);
            if (result == 0) {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        if (args[0].equals("osx")) {
            try {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Test");
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch(ClassNotFoundException e) {
                System.out.println("ClassNotFoundException: " + e.getMessage());
            }
            catch(InstantiationException e) {
                System.out.println("InstantiationException: " + e.getMessage());
            }
            catch(IllegalAccessException e) {
                System.out.println("IllegalAccessException: " + e.getMessage());
            }
            catch(UnsupportedLookAndFeelException e) {
                System.out.println("UnsupportedLookAndFeelException: " + e.getMessage());
            }
        }

        frame = new JFrame("Box Terminator");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        getBuiltinLevels();
        // InputStream level = BoxTerm.class.getClassLoader().getResourceAsStream("level");
        SokobanMap map = SokobanMap.importLevel(BoxTerm.class.getResourceAsStream("/levels/" + levels.get(currentLevelIndex)));
        editor = LevelEditor.getInstance(new SpriteMap(map, false, 1));
        game = SokobanGame.getInstance(new SpriteMap(map, true, 1));
        solver = new SingleThreadSolver(map);

        boxTerm = new BoxTerm();
        boxTerm.setLayout(new BoxLayout(boxTerm, BoxLayout.X_AXIS));
        boxTerm.add(game, BorderLayout.CENTER);
        boxTerm.add(editor, BorderLayout.SOUTH);
        editor.setVisible(false);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeRoutine();
            }
        });

        makeMenuBar(frame);
        updateContextMenu();
        frame.setResizable(false);
        frame.add(boxTerm);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
