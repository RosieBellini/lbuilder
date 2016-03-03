package levelBuilder;

import javax.swing.*;
import java.awt.*;

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
        JFrame frame = new JFrame("Level Builder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(250,250);
        frame.setVisible(true);
        frame.setResizable(false);
	}
	
}
