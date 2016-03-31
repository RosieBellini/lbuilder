package levelBuilder;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import levelBuilder.TilePalette.ListListener;

public class Cell extends JLabel{

	private static final long serialVersionUID = 1L;
	private boolean inUse;
	private char tileType;
	// Coordinate values: Named i and j so they don't affect LayoutManager.
	private int i; // X Coordinate
	private int j; // Y Coordinate
	
	public Cell(int i, int j)
	{
		super();
		this.i = i;
		this.j = j;
		inUse = false;
		tileType = 'z';
		setIcon(new ImageIcon(LevelBuilder.class.getResource("/tileset01/DEFAULT.png"), "Default"));
		
		// Mouse listener:
		addMouseListener(new MouseAdapter(){
			

			public void mousePressed(MouseEvent me)
			{
				// If left mouse button is clicked
				if (me.getButton() == MouseEvent.BUTTON1) {
				// Change icon and TileType depending on current palette state:
				
				int choice = LevelBuilder.state;
				
				switch(choice) {
				case 'w': setIcon(new ImageIcon(LevelBuilder.class.getResource("/tileset01/WALL.png"), "Wall"));
				tileType = 'w';
				break;
				case 'b': setIcon(new ImageIcon(LevelBuilder.class.getResource("/tileset01/BOX.png"), "Box"));
				TilePalette.boxCount = TilePalette.boxCount + 1;
				TilePalette.boxCounter.setText("" + TilePalette.boxCount);	// increment box count
				tileType = 'b';
				break;
				case 's': setIcon(new ImageIcon(LevelBuilder.class.getResource("/tileset01/SPACE.png"), "Space"));
				tileType = 's';
				break;
				case 'p': setIcon(new ImageIcon(LevelBuilder.class.getResource("/tileset01/PRESSURE_PAD.png"), "Pressure Pad"));
				TilePalette.pressureCount = TilePalette.pressureCount + 1;
				TilePalette.pressureCounter.setText("" + TilePalette.pressureCount); // increment pressure pad count
				tileType = 'p';
				break;
				case 'q': setIcon(new ImageIcon(LevelBuilder.class.getResource("/tileset01/PLAYER.png"), "Player"));
				TilePalette.playerCount = TilePalette.playerCount + 1; //	increment player count
				tileType = 'q';
				break;
				case 'h': setIcon(new ImageIcon(LevelBuilder.class.getResource("/tileset01/GRASS.png"), "Grass"));
				tileType = 'h';
				break;
				
				
				}
				inUse = true; // tile is in use if clicked
				}
				
				
				// If right mouse is clicked, undo 
				if (me.getButton() == MouseEvent.BUTTON3) {
					LevelBuilder.state = 'z';
					setIcon(new ImageIcon(LevelBuilder.class.getResource("/tileset01/DEFAULT_HOVER.png"), "Default"));
					tileType = 'z';
					inUse = false;
				}
			}
			
			//	Mouse hover
			
			public void mouseEntered(MouseEvent me) {
				if (!inUse) {
				setIcon(new ImageIcon(LevelBuilder.class.getResource("/tileset01/DEFAULT_HOVER.png")));
				}
			}
			
			public void mouseExited(MouseEvent me) {
				if (!inUse) {
				setIcon(new ImageIcon(LevelBuilder.class.getResource("/tileset01/DEFAULT.png")));
				}
			}	

			
		});
	
	}
	

	// Accessor methods:
	public int getI()
	{
		return i;
	}

	public int getJ()
	{
		return j;
	}
	
	public boolean getInUse()
	{
		return inUse;
	}
	
	public char getTileType()
	{
		return tileType;
	}
	
	
	
}
