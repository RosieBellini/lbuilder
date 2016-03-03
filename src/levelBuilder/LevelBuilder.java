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
		
		 
		// Frame:
		
        JFrame frame = new JFrame("Level Builder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(750, 550);
        
        // Menu Bar:
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        // Top Menu Items:
        JMenu file = new JMenu("File");
        JMenu edit = new JMenu("Edit");
        JMenu help = new JMenu("Help");
        menuBar.add(file);
        menuBar.add(edit);
        menuBar.add(help);
        
        // Sub-Menu Items:
        
        // File Sub-Menu Items:
        JMenuItem exit = new JMenuItem("Exit", KeyEvent.VK_Q);
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        exit.setToolTipText("Exit Level Builder");
        file.add(exit);
        
        // Edit Sub-Menu Items:
        JMenuItem undo = new JMenuItem("Undo", KeyEvent.VK_Z);
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        undo.setToolTipText("Undo block placements");
        
        JMenuItem redo = new JMenuItem("Redo", KeyEvent.VK_Y);
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        redo.setToolTipText("Redo block placements");
        
        edit.add(undo);
        edit.add(redo);
        
        
        
        // Panel setup: The GUI uses three JPanels. A main JPanel, and two JPanels within named editor & palette
        
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
        JButton wall = new JButton("Wall");
        palette.add(wall);
        JButton box = new JButton("Box");
        palette.add(box);
        JButton pressurePad = new JButton("Pressure Pad");
        palette.add(pressurePad);
        JButton playerPosition = new JButton("Starting Position");
        palette.add(playerPosition);
        

        mainPanel.add(editor, BorderLayout.WEST);
        mainPanel.add(palette, BorderLayout.EAST);
        frame.add(mainPanel);
        
        frame.setVisible(true);
        

        
	}
	
}
