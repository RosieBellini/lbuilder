package levelBuilder;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Cell extends JLabel{

	private static final long serialVersionUID = 1L;
	private boolean inUse;
	private char tileType;
	// Coordinate values: Named i and j so they don't affect LayoutManager:
	
	private int i;
	private int j;
	
	public Cell(int i, int j)
	{
		super();
		this.i = i;
		this.j = j;
		inUse = false;
		tileType = 'z';
		setIcon(new ImageIcon(LevelBuilder.class.getResource("/tileset01/WALL.png"), "Wall"));
	}
	
	// Accessor methods:
	

	
	public boolean getInUse()
	{
		return inUse;
	}
	
	public char getTileType()
	{
		return tileType;
	}
	
	
	
}
