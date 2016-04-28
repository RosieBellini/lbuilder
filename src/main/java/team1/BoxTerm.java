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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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
    private static SokobanGame game;
    private static JFrame frame;
    private static BoxTerm boxTerm;
    private static JMenuBar menubar;
    private static JMenu gameMenu;
    private static JMenuItem toggleItem;
    private static Set<JMenuItem> editMenuItems = new HashSet<JMenuItem>();
    private static Set<JMenuItem> gameMenuItems = new HashSet<JMenuItem>();
    private static SingleThreadSolver solver;
    private static boolean solving;
    private static ArrayList<String> levels = new ArrayList<String>();
    private static int currentLevelIndex = 0;
    private static SokobanMap lastOpenedMap;

    private static void startSolver() {
        solving = true;
        final JDialog dialog = new JDialog();
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        class solverWorker extends SwingWorker<Void, Object> {
            HashMap<SaveState, Coordinate[]> solution;

            @Override
            protected void done() {
                if (solution.size() != 0) {
                    solving = false;
                    SokobanGame.getSpriteMap().reset();
                    SokobanGame.getSpriteMap().setSolution(solution);
                    SokobanGame.redraw();
                }

                dialog.dispose();
            }

            @Override
            protected Void doInBackground() throws Exception {
                solving = true;
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
                if (!hasChanged("make a new one?")) {
                    SokobanMap map = new SokobanMap(20, 20, 100);
                    SokobanGame.getSpriteMap().updateMap(map);
                    SokobanGame.getSokobanMap().put(SokobanObject.PLAYER, new Coordinate(5, 5));
                    SokobanGame.getSpriteMap().forceRedraw();
                    SokobanGame.redraw();
                    frame.setSize(frame.getPreferredSize());
                }
            }
        });
        editMenuItems.add(newMapItem);
        fileMenu.add(newMapItem);

        JMenuItem openItem = new JMenuItem("Open");
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_MASK));
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!solving || confirmCancelSolver("open a new level")){
                    if (!hasChanged("open a different one?")) {
                        openDialog();
                    }
                }
            }
        });
        fileMenu.add(openItem);

        JMenuItem saveItem = new JMenuItem("Save", KeyEvent.VK_S);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_MASK));
        saveItem.setToolTipText("Save current map design to file");
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SokobanMap map = SokobanGame.getSokobanMap();

                if (!map.validate()) {
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
                        List<String> contents = Arrays.asList(map.toString().split("\\n"));
                        try {
                            Files.write(newFile, contents);
                            map.setInitialState(map.getState());
                            map.reset();
                            lastOpenedMap = new SokobanMap(map);
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
                if(!solving || confirmCancelSolver("edit the current level")){
                    toggleMode();
                }
            }
        });
        fileMenu.add(toggleItem);

        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
        quitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!hasChanged("exit?")) {
                    System.exit(0);
                }
            }
        });
        fileMenu.add(quitItem);



        // Edit menu

        JMenuItem undo = new JMenuItem("Undo", KeyEvent.VK_Z);
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, SHORTCUT_MASK));
        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SokobanGame.getSokobanMap().undo();
                SokobanGame.redraw();
            }
        });
        gameMenu.add(undo);

        JMenuItem redo = new JMenuItem("Redo", KeyEvent.VK_Y);
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, SHORTCUT_MASK));
        redo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SokobanGame.getSokobanMap().redo();
                SokobanGame.redraw();
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
                SokobanGame.getSpriteMap().updateMap(SokobanMap.crop(SokobanGame.getSokobanMap()));
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
                SokobanGame.getSpriteMap().reset();
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
                SpriteMap spriteMap = SokobanGame.getSpriteMap();
                int tileSetNo = spriteMap.getTileSetNo();
                spriteMap.setTileSetNo((tileSetNo + 1) % 3);
                spriteMap.loadSprites();
                SokobanGame.importPaletteIcons();
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
                        + " palette at the bottom to select either walls, goals, player"
                        + " starting position or boxes. \n\n2) Click on the map once you have"
                        + " selected something on the palette to begin designing your level."
                        + " \n\n3) You can save your map design at any point using File>Save.", "Map Editor Help", JOptionPane.PLAIN_MESSAGE,
                        SokobanGame.getSpriteMap().getBoxSprite());
            }
        });
        editMenuItems.add(editorHelpItem);
        helpMenu.add(editorHelpItem);

        JMenuItem aboutItem = new JMenuItem("About Box Terminator");
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(SokobanGame.getSpriteMap(), "A Sokoban clone.", "Box Terminator",JOptionPane.PLAIN_MESSAGE,SokobanGame.getSpriteMap().getBoxSprite());
            }
        });
        helpMenu.add(aboutItem);
    }

    private static void changeMagnification(boolean getBigger) {
        SpriteMap spriteMap = SokobanGame.getSpriteMap();
        float scale = spriteMap.getScale();

        if (getBigger) {
            if (scale > 1) {
                scale++;
            } else {
                scale = scale * 2;
            }
        } else {
            if (scale > 1) {
                scale--;
            } else {
                scale = scale / 2;
            }
        }

        spriteMap.setScale(scale);
        spriteMap.loadSprites();
    }

    private static void toggleMode() {
        boolean playable = SokobanGame.getSpriteMap().getPlayable();

        if (!playable) {
            if (SokobanGame.getSokobanMap().validate()) {
                gameMenu.setText("Game");
                toggleItem.setText("Start editor");
                game.requestFocusInWindow();
            } else {
                JOptionPane.showMessageDialog(frame, "This level cannot be won"
                        + ".\nMake sure that there are at least as many boxes"
                        + " as goals\nand that there is at least one uncovered goal,\n"
                        + "and that all goals are accessible to the player.",
                        "Incomplete level", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } else {
            gameMenu.setText("Edit");
            toggleItem.setText("Start game");
        }

        SokobanGame.toggleMode();
        updateContextMenu();
    }

    private static void updateContextMenu() {
        boolean playable = SokobanGame.getSpriteMap().getPlayable();

        for (JMenuItem item : gameMenuItems) {
            item.setVisible(playable);
        }

        for (JMenuItem item : editMenuItems) {
            item.setVisible(!playable);
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
        solver.stopSolving();
        solving = false;
        JButton button1 = new JButton("Next level");
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.getWindowAncestor(button1).dispose();
                currentLevelIndex++;
                InputStream level = BoxTerm.class.getResourceAsStream("/levels/" + levels.get(currentLevelIndex));
                SokobanMap map = SokobanMap.importLevel(level);
                lastOpenedMap = new SokobanMap(map);
                SokobanGame.getSpriteMap().updateMap(map);
                SokobanGame.redraw();
                frame.pack();
            }
        });

        JButton button2 = new JButton("Change level");
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
        SokobanMap map;

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
            map = SokobanMap.importLevel(level);
            SokobanGame.getSpriteMap().updateMap(map);
            lastOpenedMap = new SokobanMap(map);
            SokobanGame.redraw();
            frame.pack();
        } else if (selectReturnVal == 1) {
            try {
                int returnVal = fileChooser.showOpenDialog(null);
                if (returnVal != JFileChooser.APPROVE_OPTION) {
                    return;  // cancelled
                }
                File selectedFile = fileChooser.getSelectedFile();
                InputStream stream = new FileInputStream(selectedFile);
                map = SokobanMap.importLevel(stream);
                if (!map.validate()) {
                    JOptionPane.showMessageDialog(frame,
                            "This level cannot be beaten.\n You may want "
                            + "to load it in the level editor and correct "
                            + "it.", "Invalid level",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                SokobanGame.getSpriteMap().updateMap(map);
                lastOpenedMap = new SokobanMap(map);
                SokobanGame.redraw();
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

    private static boolean hasChanged(String reason) {
        SokobanMap map = SokobanGame.getSokobanMap();
        boolean changed;

        if (SokobanGame.getSpriteMap().getPlayable()) {
            changed = !map.getInitialState().equals(lastOpenedMap.getInitialState());
        } else {
            changed = !map.getState().equals(lastOpenedMap.getInitialState());
        }

        if (changed) {
            int result = JOptionPane.showConfirmDialog(frame, "Your level has unsaved changes.\n"
                    + "Are you sure you want to " + reason, "Unsaved changes",
                    JOptionPane.YES_NO_OPTION);
            if (result != 0) {
                return true;
            }
        }

        return false;
    }

    private static boolean confirmCancelSolver(String reason) {
        int result = JOptionPane.showConfirmDialog(frame, "The assistant is "
                + "trying to find a solution for this level.\nWould you like "
                + "to cancel it in order to " + reason + "?",
                "Cancel solving level", JOptionPane.YES_NO_OPTION);
        if (result == 0) {
            solver.stopSolving();
            solving = false;
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
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

        ArrayList<String> boxWords = new ArrayList<String>();
        ArrayList<String> pushWords = new ArrayList<String>();
        ArrayList<String> activeDictionary = boxWords;
        InputStream wordList = BoxTerm.class.getResourceAsStream("/WORDS");
        Scanner wordScanner = new Scanner(wordList);
        while (wordScanner.hasNextLine()) {
            String line = wordScanner.nextLine();
            if (line.equals("BOX") || line.toCharArray()[0] == '#') {
                continue;
            } else if (line.equals("PUSH")) {
                activeDictionary = pushWords;
            } else {
                activeDictionary.add(line);
            }
        }
        wordScanner.close();
        Random randomGenerator = new Random();
        int index1 = randomGenerator.nextInt(boxWords.size());
        int index2 = randomGenerator.nextInt(pushWords.size());
        String programName = boxWords.get(index1) + " " + pushWords.get(index2);

        frame = new JFrame(programName);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        getBuiltinLevels();
        SokobanMap map = SokobanMap.importLevel(BoxTerm.class.getResourceAsStream("/levels/" + levels.get(currentLevelIndex)));
        lastOpenedMap = new SokobanMap(map);
        game = SokobanGame.getInstance(new SpriteMap(map, 1));
        solver = new SingleThreadSolver(map);

        boxTerm = new BoxTerm();
        boxTerm.setLayout(new BoxLayout(boxTerm, BoxLayout.X_AXIS));
        boxTerm.add(game, BorderLayout.CENTER);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!hasChanged("exit?")) {
                    System.exit(0);
                }
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
