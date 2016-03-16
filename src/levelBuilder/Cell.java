package levelBuilder;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Cell extends JLabel{

	private static final long serialVersionUID = 1L;
	private int x;
	private int y;
	private boolean inUse;
	private char tileType;
	
	public Cell(int x, int y)
	{
		super("HELLO");
		this.x = x;
		this.y = y;
		inUse = false;
		tileType = 'z';
		setIcon(new ImageIcon(LevelBuilder.class.getResource("/tileset01/WALL.png"), "Wall"));
		setVisible(true);

	
		
	}
	
	// Accessor methods:
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
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
