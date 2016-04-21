package team1;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JPanel;

public class GridMap extends JPanel 
{

	private static final long serialVersionUID = 1L;
	private int rows;
	private int cols;
	private Cell[][] cells;
	
	public GridMap(int cols, int rows)
	{
		super();
		this.rows = rows;
		this.cols = cols;
		setLayout(new GridLayout(rows, cols, 0, 0));
		setBackground(Color.BLACK);
		cells = new Cell[rows][cols];
		drawCells();
			
	}
	
	// Accessor Methods:
	
	public int getRows()
	{
		return rows;
	}
	
	public int getCols()
	{
		return cols;
	}
	
	public Cell[][] getCells()
	{
		return cells;
	}
	
	// Methods:
	
	public void drawCells()
	{
		for(int y=0; y<rows; y++)
		{
			for(int x=0; x<cols; x++)
			{
			cells[y][x] = new Cell(x, y);
			add(cells[y][x]);
			}
		}
		
			
	}
	}
	
	
	

