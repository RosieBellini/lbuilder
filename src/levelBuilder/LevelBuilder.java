package levelBuilder;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


public class LevelBuilder{

	
	public static void main(String[] args)
	{
		createGUI();
	}
	
	
	
	
	/*
	 * Used to render the GUI.
	 * 
	 */
	
	private static void createGUI()
	{
		/*
		 *  Declare all variables used below:
		 */
		
		JFrame frame;
		JMenuBar menuBar;
		JMenu file, edit, help;
		JMenuItem newMap, open, save, compile, exit, undo, redo;
		ImageIcon wallIcon, goalIcon, playerIcon, boxIcon;
		JLabel wall, goal, player, box;
		
		 
		// Setup Frame:
        frame = new JFrame("Level Builder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(750, 550);
        
        // Setup Menu Bar:
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        // Setup Top Menu Items:
        file = new JMenu("File");
        edit = new JMenu("Edit");
        help = new JMenu("Help");
        menuBar.add(file);
        menuBar.add(edit);
        menuBar.add(help);
        
        /*
         * Setup Sub-Menu Items:
         */
        
        // File Sub-Menu Items:
        newMap = new JMenuItem("New", KeyEvent.VK_N);
        newMap.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        newMap.setToolTipText("Start a new map design");

        
        open = new JMenuItem("Open", KeyEvent.VK_O);
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        open.setToolTipText("Open a saved map design");

        
        save = new JMenuItem("Save", KeyEvent.VK_S);
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        save.setToolTipText("Save current map design to file");
        
        compile = new JMenuItem("Compile Map", KeyEvent.VK_C);
        compile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        compile.setToolTipText("Compile your map");
        
        exit = new JMenuItem("Exit", KeyEvent.VK_Q);
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        exit.setToolTipText("Exit Level Builder");
        
        // Edit Sub-Menu Items:
        undo = new JMenuItem("Undo", KeyEvent.VK_Z);
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        undo.setToolTipText("Undo block placements");
        
        redo = new JMenuItem("Redo", KeyEvent.VK_Y);
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        redo.setToolTipText("Redo block placements");
        
        file.add(newMap);
        file.add(open);
        file.add(save);
        file.add(compile);
        file.add(exit);
        edit.add(undo);
        edit.add(redo);
        
        
        
        // Panel setup: The GUI uses three JPanels. A main JPanel, and two JPanels within:
        
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.GREEN);
        mainPanel.setLayout(new BorderLayout());
        
        JPanel editor = new JPanel();
        editor.setBackground(Color.BLACK);
        JLabel editorTitle = new JLabel("Map Editor:");
        editor.add(editorTitle);

        
        JPanel palette = new JPanel();
        palette.setBackground(Color.RED);
        palette.setLayout(new BoxLayout(palette, BoxLayout.Y_AXIS));
        JLabel paletteTitle = new JLabel("Palette:");
        palette.add(paletteTitle);
        
        // Initialise Icons:
        wallIcon = new ImageIcon(LevelBuilder.class.getResource("/tileset01/WALL.png"), "Wall");
        goalIcon = new ImageIcon(LevelBuilder.class.getResource("/tileset01/GOAL.png"), "Goal");
        playerIcon = new ImageIcon(LevelBuilder.class.getResource("/tileset01/PLAYER.png"), "Player");
        boxIcon = new ImageIcon(LevelBuilder.class.getResource("/tileset01/BOX.png"), "Box");

        // Initialise JLabels;
        wall = new JLabel(wallIcon);
        goal = new JLabel(goalIcon);
        player = new JLabel(playerIcon);
        box = new JLabel(boxIcon);
        
        // Add JLabels to Palette frame
        palette.add(wall);
        palette.add(goal);
        palette.add(player);
        palette.add(box);

        
        
        // Setup Jpanel's grids layout:
        mainPanel.add(editor, BorderLayout.WEST);
        mainPanel.add(palette, BorderLayout.EAST);
        frame.add(mainPanel);
        
        
        
        // Always set visible to true at the end, otherwise GUI may not display all elements:
        frame.setVisible(true);
        

        
	}
	
}
