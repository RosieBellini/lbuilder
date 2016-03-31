package levelBuilder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.awt.event.WindowAdapter;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;


public class LevelBuilder extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private static JMenuBar menuBar;
	private static JMenu file, edit, help;
	private static JMenuItem newMap, open, save, compile, exit, undo, redo;
	private static String fileName;
	private static int x, y;
	private static GridMap gridMap;
	private static PrintWriter txtFile;
	protected static char state = 'z';
	
	public static void main(String[] args)
	{
		// Setup ActionListeners:
        class SaveAction implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent arg0) {
		         fileName = JOptionPane.showInputDialog("Please input a name for your map. \nIf file name already exists, it will be overwritten.");
		         try{
		         saveMap(gridMap.getCells(), x, y, fileName);
		         }
		         catch(IOException e)
		         {
		        	 System.out.print("IOEXception found");
		         }
			}
        	
        }
		
		// Initialise GUI:
		GUI frame = new GUI();
		frame.setSize(new Dimension(700, 500));
		frame.setResizable(false);
		
		// Set up new WindowListener
		frame.addWindowListener(new WindowListen());
		
		// Setup menu bar:
		// Setup Menu Bar:
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        // Setup Top Menu Items:
        file = new JMenu("File");
        edit = new JMenu("Edit");
        help = new JMenu("Help");
        menuBar.add(file);
        menuBar.add(edit);
        menuBar.add(help);
        
        /*
         * Setup Sub-Menu Items:
         */
        
        // File Sub-Menu Items:
        newMap = new JMenuItem("New", KeyEvent.VK_N);
        newMap.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        newMap.setToolTipText("Start a new map design");

        
        open = new JMenuItem("Open", KeyEvent.VK_O);
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        open.setToolTipText("Open a saved map design");

        
        save = new JMenuItem("Save", KeyEvent.VK_S);
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        save.setToolTipText("Save current map design to file");
        save.addActionListener(new SaveAction());
        
        compile = new JMenuItem("Compile Map", KeyEvent.VK_C);
        compile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        compile.setToolTipText("Compile your map");
        
        exit = new JMenuItem("Exit", KeyEvent.VK_Q);
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        exit.setToolTipText("Exit Level Builder");
        
        // Edit Sub-Menu Items:
        undo = new JMenuItem("Undo", KeyEvent.VK_Z);
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        undo.setToolTipText("Undo block placements");
        
        redo = new JMenuItem("Redo", KeyEvent.VK_Y);
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        redo.setToolTipText("Redo block placements");
        
        file.add(newMap);
        file.add(open);
        file.add(save);
        file.add(compile);
        file.add(exit);
        edit.add(undo);
        edit.add(redo);
        
        

        
        // Setup Main Panel:
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setBackground(Color.RED);
        
        // Setup GridMap
        x = 20;
        y = 20;
        mainPanel.add(gridMap = new GridMap(x,y));
        
        // Setup TilePalette
        mainPanel.add(new TilePalette());
        
        
        frame.add(mainPanel);
        frame.pack();
		frame.setVisible(true);
		
		}
	
	public static void saveMap(Cell[][] tile, int rows, int cols, String fileName) throws IOException
	{
		FileWriter writer = new FileWriter(new File(".").getAbsolutePath()+"//wip//"+fileName+".txt", true);
		txtFile = new PrintWriter(writer);
		for(int y=0; y<rows; y++)
		{
			for(int x=0; x<cols; x++)
			{
			char tileType = tile[y][x].getTileType();
			switch(tileType){
				case 'z': txtFile.print(" ");
				break;
				case 'b': txtFile.print("$");
				break;
				case 's': txtFile.print("_");
				break;
				case 'p': txtFile.print(".");
				break;
				case 'q': txtFile.print("@");
				break;
				case 'w': txtFile.print("#");
				break;
			}
			}
		}
	}
	
	}

	// Setup Window Listener
	
	class WindowListen implements WindowListener {

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowClosing(WindowEvent e) {
		final JOptionPane optionPane = new JOptionPane(
		"Are you sure you want to quit level builder?\n",
		JOptionPane.QUESTION_MESSAGE,
		JOptionPane.YES_NO_OPTION);
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}
	
	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}
}