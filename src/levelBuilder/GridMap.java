package levelBuilder;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class GridMap extends JPanel 
{

	private static final long serialVersionUID = 1L;
	private int rows;
	private int cols;
	private Cell[][] cells;
	
	public GridMap(int rows, int cols)
	{
		super(new GridLayout(cols, rows));
		setBackground(Color.GREEN);
		JLabel test = new JLabel("hello");
		JLabel test1 = new JLabel("hello1");
		JLabel test2 = new JLabel("hello2");

		add(test);
		add(test1);
		add(test2);
		this.rows = rows;
		this.cols = cols;
		cells = new Cell[rows][cols];		
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
	
	// Methods:
	
	public void drawCells()
	{
		for(int i=0; i<cols; i++)
		{
			System.out.print("i = " +i);
			for(int j=0; j<rows; j++)
			{
			System.out.println("j = " +j);
			cells[j][i] = new Cell(j, i);
			add(cells[j][i]);
			}
		}
		
			
	}
	}
	
	
	

