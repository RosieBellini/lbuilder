package levelBuilder;

import javax.swing.*;
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
		
        JFrame frame = new JFrame("");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700,600);
        frame.setVisible(true);
        frame.setResizable(false);
        
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
        JMenuItem quit = new JMenuItem("Quit", KeyEvent.VK_Q);
        quit.setToolTipText("Exit Level Builder");
        file.add(quit);
        
        // Edit Sub-Menu Items:
        
	}
	
}
