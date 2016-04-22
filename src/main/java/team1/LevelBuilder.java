package team1;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintWriter;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
<<<<<<< HEAD
=======
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
>>>>>>> 6e96d986461f207ce7ccefa69223fb1fb5b5457c

/**
 * LevelBuilder class. Used to create and display the LevelBuilder GUI.
 * All of the fields & methods are static.
 *
 */

public class LevelBuilder extends JPanel{

    private static final long serialVersionUID = 1L;
<<<<<<< HEAD
    private TilePalette tilePalette;
    private String fileName;
    private SpriteMap spriteMap;
    private PrintWriter txtFile;
    public static SokobanObject state = SokobanObject.SPACE;

    public LevelBuilder(SpriteMap spriteMap) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(Color.RED);
        add(spriteMap);
        tilePalette = new TilePalette(spriteMap);
        add(new TilePalette(spriteMap));
=======
    private static JFrame frame;
    private static JPanel mainPanel;
    private static TilePalette tilePalette;
    private static JMenuBar menuBar;
    private static JMenu file, edit;
    private static JMenuItem newMap, open, save, compile, exit, undo, redo, help;
    private static String fileName;
    private static SpriteMap spriteMap;
    private static PrintWriter txtFile;
    protected static SokobanObject state = SokobanObject.SPACE;

    public static void main(String args[]) {
        activate();
    }

    public static void activate()
    {
        // Setup ActionListeners:
        class SaveAction implements ActionListener{

            @Override
            public void actionPerformed(ActionEvent arg0) {
                boolean finished = false;
                while(!finished){
                    fileName = JOptionPane.showInputDialog("Please input a name for your map. \nIf file name already exists, it will be overwritten.");
                    try{
                        saveMap(fileName);
                        finished=true;
                    }
                    catch(FileNotFoundException e)
                    {
                        System.out.print("FileNotFoundException found");
                    }
                    catch(IllegalArgumentException e)
                    {
                        JOptionPane.showMessageDialog(null,e.getMessage(),  "Warning: Save Error",JOptionPane.WARNING_MESSAGE);
                    }
                    catch(NullPointerException e)
                    {
                        // A NullPointerException is thrown if the user click cancel:
                        finished = true;
                    }
                }
            }

        }

        // Initialise GUI:
        frame = new JFrame("Map Editor");
        // frame.setSize(new Dimension(700, 500));
        // frame.setResizable(false);

        // Set up new WindowListener
        frame.addWindowListener(new WindowListen());

        // Setup menu bar:
        // Setup Menu Bar:
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        // Setup Top Menu Items:
        file = new JMenu("File");
        edit = new JMenu("Edit");
        menuBar.add(file);
        menuBar.add(edit);

        /*
         * Setup Sub-Menu Items:
         */

        final int SHORTCUT_MASK =
                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        // File Sub-Menu Items:
        newMap = new JMenuItem("New", KeyEvent.VK_N);
        newMap.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_MASK));
        newMap.setToolTipText("Start a new map design");
		newMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                mainPanel.removeAll();
                SokobanMap map = new SokobanMap(20, 20, 100);
                spriteMap = new SpriteMap(map, false, BoxTerm.getTileSetNo() % 3);
                tilePalette = new TilePalette(spriteMap);
                mainPanel.add(spriteMap);
                mainPanel.add(tilePalette);
                frame.setSize(frame.getPreferredSize());
            }
		});

        open = new JMenuItem("Open", KeyEvent.VK_O);
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_MASK));
        open.setToolTipText("Open a saved map design");


        save = new JMenuItem("Save", KeyEvent.VK_S);
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_MASK));
        save.setToolTipText("Save current map design to file");
        save.addActionListener(new SaveAction());

        compile = new JMenuItem("Compile Map", KeyEvent.VK_C);
        compile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, SHORTCUT_MASK));
        compile.setToolTipText("Compile your map");
        
        help = new JMenuItem("Help", KeyEvent.VK_H);
        help.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, SHORTCUT_MASK));
        help.setToolTipText("Help information");


        exit = new JMenuItem("Exit", KeyEvent.VK_Q);
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
        exit.setToolTipText("Exit Level Builder");

        // Edit Sub-Menu Items:
        undo = new JMenuItem("Undo", KeyEvent.VK_Z);
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, SHORTCUT_MASK));
		undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                SokobanMap map = spriteMap.getMap();
                map.undo();
                int boxCount = map.getMyState().getBoxPositions().size();
                int pressureCount = map.getMyState().getGoalPositions().size();
                TilePalette.updateCounters(boxCount, pressureCount);
                spriteMap.placeSprites();
            }
		});
        undo.setToolTipText("Undo block placements");

        redo = new JMenuItem("Redo", KeyEvent.VK_Y);
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, SHORTCUT_MASK));
		redo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                SokobanMap map = spriteMap.getMap();
                map.redo();
                int boxCount = map.getMyState().getBoxPositions().size();
                int pressureCount = map.getMyState().getGoalPositions().size();
                TilePalette.updateCounters(boxCount, pressureCount);
                spriteMap.placeSprites();
            }
		});
        redo.setToolTipText("Redo block placements");

        file.add(newMap);
        file.add(open);
        file.add(save);
        file.add(compile);
        file.add(help);
        file.add(exit);
        edit.add(undo);
        edit.add(redo);

        help.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, " This map editor can be used to design your own Sokoban levels (maximum 20x20). \n\n1) Use the palette on the right to select either walls, pressure pad, player starting position or boxes. \n\n2) Click on the map once you have selected something on the palette to begin designing your level. \n\n3) You can save your map design at any point using File>Save. \n\n4) You must use File>Compile Map if you want to run your map in-game.", "Map Editor Help", JOptionPane.PLAIN_MESSAGE, spriteMap.getBoxSprite());				
			}});

        

        // Setup Main Panel:
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setBackground(Color.RED);

        // Setup GridMap
        SokobanMap map = new SokobanMap(BoxTerm.getSpriteMap().getMap(), 100);
        spriteMap = new SpriteMap(map, false, BoxTerm.getTileSetNo() % 3);
        mainPanel.add(spriteMap);
//		spriteMap.placeSprites();

        // Setup TilePalette
        tilePalette = new TilePalette(spriteMap);
        mainPanel.add(new TilePalette(spriteMap));


        frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);

    }
    
       

    /**
     * Used to parse a map from the 2d Cell Array into a txt file. Throws FileNotFoundException.
     *
     * @param tile
     * @param rows the number of rows in the 2d Array.
     * @param cols the number of cols in the 2d Array.
     * @param fileName the user's chosen filename for the txt file.
     * @throws FileNotFoundException
     */
    public static void saveMap(String fileName) throws FileNotFoundException
    {
        // First, check if fileName is empty and throw an exception:
        if(fileName.isEmpty())
        {
            throw new IllegalArgumentException("Error: File name must not be empty.");
        }

        // Next, use a regular expression to ensure the fileName has no special characters and is not longer than 30 characters.
        String pattern = "\\w+{1,30}";
        if(!fileName.matches(pattern))
        {
            throw new IllegalArgumentException("Error: File name must only contain letters, numbers and underscores and be no more than 30 characters in length.");
        }

        // If nothing is thrown, begin parsing the map into a text file with the user's chosen filename:
        File fileDir = new File(fileName+".txt");
        System.out.println(fileDir.getAbsolutePath());
        txtFile = new PrintWriter(fileDir);
        txtFile.println(spriteMap.getMap().toString());

        // }
        txtFile.flush();
        txtFile.close();
    }

}

// Setup Window Listener

class WindowListen implements WindowListener {

    @Override
    public void windowClosed(WindowEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void windowClosing(WindowEvent e) {
        final JOptionPane optionPane = new JOptionPane(
                "Are you sure you want to quit level builder?\n",
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION);
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
>>>>>>> 6e96d986461f207ce7ccefa69223fb1fb5b5457c
    }

    public static void setState(SokobanObject object) {
        state = object;
    }
}
