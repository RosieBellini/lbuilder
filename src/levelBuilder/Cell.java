package levelBuilder;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

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
		setIcon(new ImageIcon(LevelBuilder.class.getResource("/tileset01/WALL.png"), "Wall"));
		
		// Mouse listener:
		addMouseListener(new MouseAdapter(){
			
			public void mouseClicked(MouseEvent me)
			{
				// Change icon and TileType depending on current palette state:
				
				if(LevelBuilder.state == 'b')
				{
				setIcon(new ImageIcon(LevelBuilder.class.getResource("/tileset01/BOX.png"), "Box"));
				
				}
				else if(LevelBuilder.state == 'n')
				{
					
				}
				else if(LevelBuilder.state == 'w')
				{
					
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
