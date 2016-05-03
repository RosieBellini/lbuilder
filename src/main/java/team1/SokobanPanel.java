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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
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

/**
 * Sokoban main class. Handles creating the main frame and menubar plus its
 * associated functions.
 */
@SuppressWarnings("serial")
public class SokobanPanel extends JPanel {
    private static JFileChooser fileChooser
                            = new JFileChooser(System.getProperty("user.dir"));
    private static Toolkit toolkit = Toolkit.getDefaultToolkit();
    private static GamePanel game;
    private static JFrame frame;
    private static SokobanPanel sokobanPanel;
    private static JMenu gameMenu;
    private static JMenuItem toggleItem;
    private static JMenuItem resetAssistItem;
    private static Set<JMenuItem> editMenuItems = new HashSet<JMenuItem>();
    private static Set<JMenuItem> gameMenuItems = new HashSet<JMenuItem>();
    private static SingleThreadSolver solver;
    private static boolean solving;
    private static ArrayList<String> levels = new ArrayList<String>();
    private static int currentLevelIndex = 0;
    private static SokobanMap lastOpenedMap;
    private static boolean autoScale = false;
    private static float autoScaleFactor = 1;
    private static SokobanMap.SolutionRunner runner;
    private static final int FRAME_MIN_WIDTH = 220;
    private static final int FRAME_BORDER_PAD = 64;

    /**
     * Returns the SolutionRunner used by the assistant.
     *
     * @return      The SokobanMap.SolutionRunner object used in this frame
     */
    public static SokobanMap.SolutionRunner getRunner() {
        return runner;
    }

    /**
     * Starts running the solver on the open level and displays a dialogue
     * allowing the user to cancel the operation or see the level played out,
     * and to warn them if the level is impossible.
     */
    private static void startSolver() {
        solving = true;
        final JDialog dialog = new JDialog();
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        URL loadingBoxURL
                        = SokobanPanel.class.getResource("/tileset01/cube.gif");
        ImageIcon loadingBox = new ImageIcon(loadingBoxURL);
        JLabel loadingBoxLabel = new JLabel(loadingBox);

        URL impossibleBoxURL
                = SokobanPanel.class.getResource("/tileset01/IMPOSSIBLE.png");
        ImageIcon impossibleBox = new ImageIcon(impossibleBoxURL);
        JLabel impossibleBoxLabel = new JLabel(impossibleBox);

        JLabel msgLabel = new JLabel("Calculating...");
        JButton b1 = new JButton("Cancel");

        ActionListener listen = new ActionListener() {
            public void actionPerformed(ActionEvent e2) {
                solver.stopSolving();
                dialog.dispose();
            }
        };

        b1.addActionListener(listen);

        panel.add(msgLabel, BorderLayout.PAGE_START);
        panel.add(loadingBoxLabel, BorderLayout.CENTER);
        panel.add(b1, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        dialog.getContentPane().add(panel);
        dialog.setTitle("Assistant");
        dialog.setResizable(false);
        dialog.pack();
        dialog.setSize(200, dialog.getHeight());
        dialog.setLocationRelativeTo(frame);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);

        class solverWorker extends SwingWorker<Void, Object> {
            Entry<HashMap<SaveState, Coordinate[]>,
                LinkedList<Coordinate[]>> solution;
            SokobanMap map = GamePanel.getSokobanMap();

            @Override
            protected void done() {
                solving = false;

                if (solution != null) {
                    if (solution.getKey().size() > 0) {
                        GamePanel.getMapPanel().reset();
                        GamePanel.getMapPanel().setSolution(solution.getKey());
                        GamePanel.redraw();
                        resetAssistItem.setVisible(true);
                        dialog.dispose();

                        int result = JOptionPane.showConfirmDialog(frame,
                                "A solution was found!\nWould you like the "
                                + "assistant to play it for you?",
                                "Solution found", JOptionPane.YES_NO_OPTION);

                        switch (result) {
                            case JOptionPane.NO_OPTION:
                            case JOptionPane.CLOSED_OPTION:
                                return;
                        }

                        map = GamePanel.getSokobanMap();
                        runner = map.new SolutionRunner(solution.getValue());
                        runner.start();
                    } else {
                        msgLabel.setText("This level is impossible!");
                        panel.remove(loadingBoxLabel);
                        panel.add(impossibleBoxLabel);
                        panel.repaint();
                        panel.setSize(panel.getPreferredSize());
                    }
                } else {
                    dialog.dispose();
                }
            }

            @Override
            protected Void doInBackground() throws Exception {
                solving = true;
                solver = new SingleThreadSolver(map);
                solution = solver.levelSolution();
                return null;
            }
        }

        (new solverWorker()).execute();
    }

    /**
     * Creates the Sokoban menubar and populates it with MenuItems.
     */
    private static JMenuBar makeMenuBar() {
        final int SHORTCUT_MASK = toolkit.getMenuShortcutKeyMask();

        JMenuBar menubar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        menubar.add(fileMenu);

        gameMenu = new JMenu("Game");
        menubar.add(gameMenu);

        JMenu viewMenu = new JMenu("View");
        menubar.add(viewMenu);

        JMenu helpMenu = new JMenu("Help");
        menubar.add(helpMenu);



        // =====================================================================
        // File menu

        // New map
        JMenuItem newMapItem = new JMenuItem("New", KeyEvent.VK_N);
        newMapItem.setAccelerator(
                        KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_MASK));
        newMapItem.setToolTipText("Start a new map design");

        newMapItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!hasChanged("make a new one?")) {
                    SokobanMap map = new SokobanMap(20, 20);
                    MapPanel mapPanel = GamePanel.getMapPanel();
                    map.put(SokobanObject.PLAYER, new Coordinate(5, 5));

                    mapPanel.updateMap(map);
                    changeMagnification(0);
                    mapPanel.placeSprites(true);
                    GamePanel.redraw();
                    currentLevelIndex = -1;
                    frame.pack();
                }
            }
        });

        editMenuItems.add(newMapItem);
        fileMenu.add(newMapItem);


        // Open map
        JMenuItem openItem = new JMenuItem("Open");
        openItem.setAccelerator(
                        KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_MASK));

        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!solving || confirmCancelSolver("open a new level")) {
                    if (!hasChanged("open a different one?")) {
                        openDialog();
                    }
                }
            }
        });

        fileMenu.add(openItem);


        // Save map
        JMenuItem saveItem = new JMenuItem("Save", KeyEvent.VK_S);
        saveItem.setAccelerator(
                        KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_MASK));
        saveItem.setToolTipText("Save current map design to file");

        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveDialog();
            }
        });

        editMenuItems.add(saveItem);
        fileMenu.add(saveItem);


        // Toggle mode
        toggleItem = new JMenuItem("Start editor");
        toggleItem.setAccelerator(
                        KeyStroke.getKeyStroke(KeyEvent.VK_E, SHORTCUT_MASK));

        toggleItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!solving || confirmCancelSolver("edit the current level")) {
                    toggleMode();
                }
            }
        });

        fileMenu.add(toggleItem);


        // Quit
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.setAccelerator(
                        KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));

        quitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!hasChanged("exit?")) {
                    System.exit(0);
                }
            }
        });

        fileMenu.add(quitItem);



        // =====================================================================
        // Edit menu

        // Undo
        JMenuItem undo = new JMenuItem("Undo", KeyEvent.VK_Z);
        undo.setAccelerator(
                        KeyStroke.getKeyStroke(KeyEvent.VK_Z, SHORTCUT_MASK));

        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GamePanel.getSokobanMap().undo();
                GamePanel.redraw();
            }
        });

        gameMenu.add(undo);


        // Redo
        JMenuItem redo = new JMenuItem("Redo", KeyEvent.VK_Y);
        redo.setAccelerator(
                        KeyStroke.getKeyStroke(KeyEvent.VK_Y, SHORTCUT_MASK));

        redo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GamePanel.getSokobanMap().redo();
                GamePanel.redraw();
            }
        });

        gameMenu.add(redo);


        // Crop
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

                SokobanMap map = GamePanel.getSokobanMap();
                MapPanel mapPanel = GamePanel.getMapPanel();

                mapPanel.updateMap(SokobanMap.crop(map));
                changeMagnification(0);
                frame.pack();
            }
        });

        editMenuItems.add(cropItem);
        gameMenu.add(cropItem);



        // =====================================================================
        // Game menu

        // Reset
        JMenuItem resetItem = new JMenuItem("Reset level");
        resetItem.setAccelerator(KeyStroke.getKeyStroke(
                                                KeyEvent.VK_R, SHORTCUT_MASK));

        resetItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GamePanel.getMapPanel().reset();
                GamePanel.redraw();
            }
        });

        gameMenuItems.add(resetItem);
        gameMenu.add(resetItem);



        // =====================================================================
        // View menu

        // Change tileset
        JMenuItem tileItem = new JMenuItem("Change Tileset");
        tileItem.setAccelerator(
                        KeyStroke.getKeyStroke(KeyEvent.VK_T, SHORTCUT_MASK));

        tileItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MapPanel mapPanel = GamePanel.getMapPanel();
                mapPanel.nextTileset();
                mapPanel.loadSprites();
                mapPanel.placeSprites(true);
                GamePanel.importPaletteIcons();
            }
        });

        viewMenu.add(tileItem);


        // Increase magnification
        JMenuItem magnifyItem = new JMenuItem("Increase Magnification");
        magnifyItem.setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, SHORTCUT_MASK));

        magnifyItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeMagnification(1);
                frame.pack();
            }
        });

        viewMenu.add(magnifyItem);


        // Decrease magnification
        JMenuItem deMagnifyItem = new JMenuItem("Decrease Magnification");
        deMagnifyItem.setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, SHORTCUT_MASK));

        deMagnifyItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeMagnification(-1);
                frame.pack();
            }
        });

        viewMenu.add(deMagnifyItem);


        // Enable autoscale
        JCheckBoxMenuItem autoScaleItem
                            = new JCheckBoxMenuItem("Enable autoscale", false);
        autoScaleItem.setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, SHORTCUT_MASK));

        autoScaleItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (autoScaleItem.getState()) {
                    autoScale = true;
                    changeMagnification(0);
                    frame.pack();
                } else {
                    autoScale = false;
                }
            }
        });

        viewMenu.add(autoScaleItem);



        // =====================================================================
        // Help menu

        // Assistant
        JMenuItem assistItem = new JMenuItem("Assistant");
        assistItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startSolver();
            }
        });

        gameMenuItems.add(assistItem);
        helpMenu.add(assistItem);


        // Reset assistant
        resetAssistItem = new JMenuItem("Reset Assistant");
        resetAssistItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GamePanel.getMapPanel().resetSolver();
                GamePanel.getMapPanel().placeSprites(true);
                resetAssistItem.setVisible(false);
            }
        });

        helpMenu.add(resetAssistItem);
        resetAssistItem.setVisible(false);


        // Editor help
        JMenuItem editorHelpItem = new JMenuItem("Editor help");
        editorHelpItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, " This map editor can be "
                        + "used to design your own Sokoban levels (maximum "
                        + "20x20).\n\n1) Use the palette at the bottom to "
                        + "select walls, goals, player starting position or "
                        + "boxes.\n\n2) Click or drag on the map to begin "
                        + "designing your level.\n\n3) Save your map by "
                        + "selecting File > Save, or play it by selecting File "
                        + "> Start Game.", "Map Editor Help",
                        JOptionPane.PLAIN_MESSAGE,
                        GamePanel.getMapPanel().getBoxIcon());
            }
        });

        editMenuItems.add(editorHelpItem);
        helpMenu.add(editorHelpItem);


        // About
        JMenuItem aboutItem = new JMenuItem("About Wonderful Sokoban");
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(GamePanel.getMapPanel(),
                        "A Sokoban clone.\n\nRosie Bellini\nJosh Gant\nDoris"
                        + " Hao\nHaiza Hazali\nLoki Li\nTom Picton\nMarcus "
                        + "Redgrave-Close\nJohn Zhuang\n\nLevels designed by "
                        + "Lee J. Haywood", "Wonderful Sokoban",
                        JOptionPane.PLAIN_MESSAGE,
                        GamePanel.getMapPanel().getBoxIcon());
            }
        });

        helpMenu.add(aboutItem);

        return menubar;
    }

    /**
     * Changes the magnification level of the MapPanel. Also handles autoscale,
     * which attempts to keep the game window to fill 75% of the screen's
     * vertical resolution. Limits the horizontal width of the main frame to
     * FRAME_MIN_WIDTH.
     *
     * @param   scaleDirection      1 to increase the magnification,
     *                              -1 to decrease the magnification,
     *                              0 to update the minimum allowed
     *                              magnification for the current map
     */
    private static void changeMagnification(int scaleDirection) {
        if (Math.abs(scaleDirection) != 1 && scaleDirection != 0) {
            throw new IllegalArgumentException("changeMagnification parameter "
                    + "must be -1, 0 or 1");
        }

        MapPanel mapPanel = GamePanel.getMapPanel();
        int iconSize = mapPanel.getIconSize();
        int xSize = mapPanel.getXSize();
        int ySize = mapPanel.getYSize();

        if (!autoScale) {
            float scale = mapPanel.getScale();
            scale = perfectPixelScaler(scale, scaleDirection);
            int gameWidth = (int) (iconSize * xSize * scale);

            if (gameWidth > FRAME_MIN_WIDTH) {
                mapPanel.setScale(scale);
                mapPanel.loadSprites();
                mapPanel.placeSprites(true);

            } else if (scaleDirection == 0) {
                scale = FRAME_MIN_WIDTH / ((float) (iconSize * xSize));
                scale = (float) Math.ceil(scale);
                mapPanel.setScale(scale);
                mapPanel.loadSprites();
                mapPanel.placeSprites(true);
            }

        } else {
            float scale = autoScaleFactor;
            double screenHeight = toolkit.getScreenSize().getHeight();
            double preferredHeight = 0.75 * autoScaleFactor * screenHeight;
            int unscaledGameHeight = iconSize * ySize;
            float gameHeight = unscaledGameHeight * scale + FRAME_BORDER_PAD;

            boolean getBigger = false;

            while (gameHeight < preferredHeight) {
                getBigger = true;
                scale = perfectPixelScaler(scale, 1);
                gameHeight = unscaledGameHeight * scale + FRAME_BORDER_PAD;
            }

            if (!getBigger) {
                while (gameHeight > preferredHeight) {
                    scale = perfectPixelScaler(scale, -1);
                    gameHeight = unscaledGameHeight * scale + FRAME_BORDER_PAD;
                }
            }

            if (getBigger) {
                scale = perfectPixelScaler(scale, -1);
            }

            mapPanel.setScale(scale);
            mapPanel.loadSprites();
            mapPanel.placeSprites(true);
        }
    }

    /**
     * Returns multiples of a value that permit nearest-neighbour scaling
     * without artifacts. Results are either a whole integer if above 1, or
     * a power of 0.5 if below.
     *
     * @param   input               The number to adjust
     * @param   scaleDirection      1 to increase the scale of the input, -1
     *                              to decrease it
     *
     * @return                      The scaled input
     */
    private static float perfectPixelScaler(float input, int scaleDirection) {
        if (scaleDirection > 0) {
            input = input > 1 ? input + 1 : input * 2;
        } else if (scaleDirection < 0) {
            input = input > 1 ? input - 1 : input / 2;
        }

        return input;
    }

    /**
     * Switches the frame between game and editor modes. Changes context
     * sensitive menubar items and prompts the user if they have unsaved changes
     * in the open map.
     */
    private static void toggleMode() {
        boolean playable = GamePanel.getMapPanel().getPlayable();

        if (!playable) {
            if (GamePanel.getSokobanMap().validate()) {
                gameMenu.setText("Game");
                toggleItem.setText("Start editor");
                game.requestFocusInWindow();
            } else {
                JOptionPane.showMessageDialog(frame, "This level cannot be won."
                        + "\nMake sure that there are at least as many boxes "
                        + "as\ngoals, that there is at least one uncovered "
                        + "goal,\nand that all goals are accessible to the "
                        + "player.", "Incomplete level",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } else {
            gameMenu.setText("Edit");
            toggleItem.setText("Start game");
            resetAssistItem.setVisible(false);
        }

        GamePanel.toggleMode();
        updateContextMenu();
    }

    /**
     * Sets the MenuItems relevant to the active mode visible and hides those
     * that aren't.
     */
    private static void updateContextMenu() {
        boolean playable = GamePanel.getMapPanel().getPlayable();

        for (JMenuItem item : gameMenuItems) {
            item.setVisible(playable);
        }

        for (JMenuItem item : editMenuItems) {
            item.setVisible(!playable);
        }
    }

    /**
     * Initialises the levels ArrayList by loading the text files listed in the
     * LEVELS_INDEX file.
     */
    private static void getBuiltinLevels() {
        InputStream levelIndex
            = SokobanPanel.class.getResourceAsStream("/levels/LEVEL_INDEX");
        Scanner levelIndexScanner = new Scanner(levelIndex);

        while (levelIndexScanner.hasNextLine()) {
            String levelName = levelIndexScanner.nextLine();
            levels.add(levelName);
        }

        levelIndexScanner.close();
    }

    /**
     * Displays a dialogue from which the user may advance to the next level in
     * the set of built in levels or start the "Open" dialogue.
     */
    public static void winDialog() {
        solver.stopSolving();
        solving = false;
        JButton button1 = new JButton("Next level");
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.getWindowAncestor(button1).dispose();
                currentLevelIndex++;
                String levelURL = "/levels/" + levels.get(currentLevelIndex);
                InputStream level
                        = SokobanPanel.class.getResourceAsStream(levelURL);
                SokobanMap map = SokobanMap.importLevel(level);

                lastOpenedMap = new SokobanMap(map);
                GamePanel.getMapPanel().updateMap(map);
                changeMagnification(0);
                resetAssistItem.setVisible(false);
                GamePanel.redraw();
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

        JOptionPane.showOptionDialog(frame, "You beat the level!\nWhat next?",
                "Congratulations!", JOptionPane.PLAIN_MESSAGE,
                JOptionPane.PLAIN_MESSAGE, null,
                new JButton[] { button1, button2 }, button1);
    }

    /**
     * Displays a dialogue prompting the user to select one of the built in
     * levels or to import their own from the filesystem.
     */
    private static void openDialog() {
        SokobanMap map;

        String[] buttons = { "Open", "Import", "Cancel" };
        String[] items = levels.toArray(new String[levels.size()]);
        JList<String> list = new JList<String>(items);
        list.setSelectedIndex(0);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(200, 256));

        int selectReturnVal = JOptionPane.showOptionDialog(frame, scrollPane,
                "Select or import a level", JOptionPane.PLAIN_MESSAGE,
                JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);

        if (selectReturnVal == 0) {
            currentLevelIndex = list.getSelectedIndex();
            String levelURL = "/levels/" + levels.get(currentLevelIndex);
            InputStream level
                            = SokobanPanel.class.getResourceAsStream(levelURL);
            map = SokobanMap.importLevel(level);

            GamePanel.getMapPanel().updateMap(map);
            lastOpenedMap = new SokobanMap(map);
            resetAssistItem.setVisible(false);
            changeMagnification(0);
            GamePanel.redraw();
            frame.pack();

        } else if (selectReturnVal == 1) {
            try {
                int returnVal = fileChooser.showOpenDialog(null);

                if (returnVal != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                File selectedFile = fileChooser.getSelectedFile();
                InputStream stream = new FileInputStream(selectedFile);
                map = SokobanMap.importLevel(stream);
                if (!map.validate()) {
                    JOptionPane.showMessageDialog(frame,
                            "This level cannot be beaten.\n You may want to "
                            + "load it in the level editor and correct it.",
                            "Invalid level", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                GamePanel.getMapPanel().updateMap(map);
                lastOpenedMap = new SokobanMap(map);
                changeMagnification(0);
                resetAssistItem.setVisible(false);
                GamePanel.redraw();
                currentLevelIndex = -1;
                frame.pack();

            } catch (FileNotFoundException e1) {
                System.out.println("BAD LEVEL");
                e1.printStackTrace();

            } catch (IllegalArgumentException e2) {
                JOptionPane.showMessageDialog(frame, "Invalid level.\nMake sure"
                        + " that the file you have selected uses the standard "
                        + "format.", "Invalid level",
                        JOptionPane.WARNING_MESSAGE);
            }
        }

        frame.pack();
    }

    /**
     * Displays a dialogue prompting the user to choose a file in which to save
     * the open level. Warns the user if this file already exists.
     */
    private static void saveDialog() {
        SokobanMap map = GamePanel.getSokobanMap();

        if (!map.validate()) {
            int result = JOptionPane.showConfirmDialog(frame, "This level is "
                    + "incomplete.\nYou may save it and resume editing later,\n"
                    + "but it won't be playable.\nContinue?", "Save level",
                    JOptionPane.YES_NO_OPTION);

            switch (result) {
                case JOptionPane.NO_OPTION:
                case JOptionPane.CLOSED_OPTION:
                    return;
            }
        }

        JFileChooser fileChooser = new JFileChooser() {
            @Override
            public void approveSelection() {
                File file = getSelectedFile();

                if (file.exists() && getDialogType() == SAVE_DIALOG) {
                    int result = JOptionPane.showConfirmDialog(this, file
                            + " already exists. Overwrite it?",
                            "Overwrite file", JOptionPane.YES_NO_OPTION);

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
                String mapString = map.toString();
                List<String> contents = Arrays.asList(mapString.split("\\n"));

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

    /**
     * Displays a dialogue warning the user if modifications to the open map
     * have not been saved.
     *
     * @param   reason      An explanation that will be appended to "Are you
     *                      sure you want to..." in the dialogue
     *
     * @return              True if the user wishes to proceed, false otherwise
     */
    private static boolean hasChanged(String reason) {
        SokobanMap map = GamePanel.getSokobanMap();
        SaveState lastInitialState = lastOpenedMap.getInitialState();
        boolean changed;

        if (GamePanel.getMapPanel().getPlayable()) {
            changed = !map.getInitialState().equals(lastInitialState);
        } else {
            changed = !map.getState().equals(lastInitialState);
        }

        if (changed) {
            int result = JOptionPane.showConfirmDialog(frame, "Your level has "
                    + "unsaved changes.\nAre you sure you want to " + reason,
                    "Unsaved changes", JOptionPane.YES_NO_OPTION);

            if (result != 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * Displays a dialogue warning the user that the operation they are
     * attempting will cancel the assistant if it's running.
     *
     * @param   reason      An explanation that will be appended to "Would you
     *                      like to cancel [the assistant] in order to..." in
     *                      the dialogue
     *
     * @return              True if the user wishes to proceed, false otherwise
     */
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

    /**
     * Randomly generates a title for the game. The candidates words are stored
     * in the resource WORDS. The available first words are delimited by "BOX"
     * and the available second words by "PUSH".
     *
     * @return      A randomly generated game title of the form
     *              [storage-related noun] [profession]
     */
    private static String generateTitle() {
        ArrayList<String> boxWords = new ArrayList<String>();
        ArrayList<String> pushWords = new ArrayList<String>();
        ArrayList<String> activeDictionary = boxWords;
        InputStream wordList = SokobanPanel.class.getResourceAsStream("/WORDS");
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
        return boxWords.get(index1) + " " + pushWords.get(index2);
    }

    /**
     * Main method. Creates the main frame and loads the first level.
     */
    public static void main(String[] args) {
        try {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        frame = new JFrame(generateTitle());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        getBuiltinLevels();
        String levelURL = "/levels/" + levels.get(currentLevelIndex);
        InputStream level = SokobanPanel.class.getResourceAsStream(levelURL);
        SokobanMap map = SokobanMap.importLevel(level);

        lastOpenedMap = new SokobanMap(map);
        game = GamePanel.getInstance(new MapPanel(map, 1));
        solver = new SingleThreadSolver(map);

        sokobanPanel = new SokobanPanel();
        sokobanPanel.setLayout(new BoxLayout(sokobanPanel, BoxLayout.X_AXIS));
        sokobanPanel.add(game, BorderLayout.CENTER);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!hasChanged("exit?")) {
                    System.exit(0);
                }
            }
        });

        frame.setJMenuBar(makeMenuBar());
        updateContextMenu();
        frame.setResizable(false);
        frame.add(sokobanPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
