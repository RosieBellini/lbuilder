package team1;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;






public class GUI extends JFrame {



		
		// All variables & references for GUI:

		private static ImageIcon wallIcon, goalIcon, playerIcon, boxIcon;
		
		
		public GUI()
		{
			super("Map Editor");
		}
		
		

	        
//	        
//	        
//	        // Panel setup: The GUI uses three JPanels. A main JPanel, and two JPanels within:
//	        
//	        mainPanel = new JPanel();
//	        mainPanel.setBackground(Color.GREEN);
//	        mainPanel.setLayout(new BorderLayout());
//	        
//	        editor = new JPanel();
//	        editor.setBackground(Color.BLACK);
//	        editor.setSize(20, 3);
//	        editor.setLayout(new GridLayout(y, x));
//	        // Setup the 2d Array grid:
//	        createGrid(editor, x, y);
//
//	        // Setup palette for selecting 'brush' for designing map:
//	        palette = new JPanel();
//	        palette.setBackground(Color.RED);
//	        palette.setLayout(new BoxLayout(palette, BoxLayout.Y_AXIS));
//	        JLabel paletteTitle = new JLabel("Palette:");
//	        palette.add(paletteTitle);
//	        
//	        // Initialise Icons:
//	        wallIcon = new ImageIcon(LevelBuilder.class.getResource("/tileset01/WALL.png"), "Wall");
//	        goalIcon = new ImageIcon(LevelBuilder.class.getResource("/tileset01/GOAL.png"), "Goal");
//	        playerIcon = new ImageIcon(LevelBuilder.class.getResource("/tileset01/PLAYER.png"), "Player");
//	        boxIcon = new ImageIcon(LevelBuilder.class.getResource("/tileset01/BOX.png"), "Box");
//
//	        // Initialise JLabels;
//	        wall = new JLabel(wallIcon);
//	        goal = new JLabel(goalIcon);
//	        player = new JLabel(playerIcon);
//	        box = new JLabel(boxIcon);
//	        
//	        // Add JLabels to Palette frame
//	        palette.add(wall);
//	        palette.add(goal);
//	        palette.add(player);
//	        palette.add(box);
//
//	        
//	        // Setup Jpanel's grids layout:
//	        mainPanel.add(editor, BorderLayout.WEST);
//	        mainPanel.add(palette, BorderLayout.EAST);
//	        frame.add(mainPanel);
//	        
//	        
//	        
//	        // Always set visible to true at the end, otherwise GUI may not display all elements:
//	        frame.setVisible(true);
//	        
//
//	        
//		}
//		
//		private static void createGrid(JPanel panel, int x, int y)
//		{		
//	        wallIcon = new ImageIcon(LevelBuilder.class.getResource("/tileset01/WALL.png"), "Wall");
//
//			// Outer loop for moving along the y axis:
//			for(int i=0; i<y; i++)
//			{
//				// Inner loop which is ran before the outer loop, moving along the x axis:
//				for(int j=0; j<x; j++)
//				{
//					// Save coordinates into a String:
//					String coords = ""+j+i;
//					// Add JLabel along with its coordinates into a HashMap:
//					LevelBuilder.gridElements.put(coords, new JLabel(wallIcon));
//					// Get it from HashMap and add it to the panel:
//					JLabel tempLabel = LevelBuilder.gridElements.get(coords);
//					tempLabel.addMouseListener(new MouseAdapter()
//					{   
//
//				        public void mouseClicked(MouseEvent e)   
//				        {   
//				        	tempLabel.setText("Hello");
//				        }   
//					});	
//					panel.add(tempLabel);
//
//				}
//			}	
		}

		

