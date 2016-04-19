package levelBuilder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class TextArea extends JPanel {
	
	private static final long serialVersionUID = 1L;

	public TextArea() {
		
		super();
		
		// Generate JFrame
		
		JFrame textEditor = new JFrame();
		setSize(new Dimension(500, 500));
		textEditor.setResizable(false);
		
		// Generate JPanels 
		
		JPanel editFrame = new JPanel();
		JPanel infoFrame = new JPanel();
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

		editFrame.setSize(new Dimension(250, 250));
		infoFrame.setSize(new Dimension(250, 250));
		
		// Generate Text Areas
		
		JTextArea editArea = new JTextArea(20,20);
			editArea.setFont(new Font("MONOSPACED", Font.PLAIN, 15)); 
			editArea.setLineWrap(true);
			editArea.setWrapStyleWord(true);
			editArea.setEditable(true);
			editArea.setVisible(true);
			editArea.setSize(new Dimension(180, 180));
			
		JTextArea infoArea = new JTextArea(
					"#\n" +
					"Wall\n" +
					"@\n" +
					"Player\n" +
					"+\n" +
					"Player on goal square\n" +
					"$\n" +
					"Box\n" +
					"*\n" +
					"Box on goal square\n" +
					".\n" +
					"Goal square\n" +
					"_\n" +
					"Inside floor\n" +
					" \n" +
					"Outside floor"
					);
			infoArea.setFont(new Font("MONOSPACED", Font.PLAIN, 10)); 
			infoArea.setEditable(false);
			infoArea.setVisible(true);
			infoArea.setSize(new Dimension(250, 250));
			
		editFrame.add(editArea);
		infoFrame.add(infoArea);
		editFrame.setBackground(Color.WHITE);
		
		textEditor.add(mainPanel);
		
		editFrame.setVisible(true);
		infoFrame.setVisible(true);
		
		mainPanel.add(editFrame);
		mainPanel.add(infoFrame);
		mainPanel.setBackground(Color.RED);
		mainPanel.setVisible(true);
		textEditor.pack();
		textEditor.setVisible(true);
		
		String content = editArea.getText();
	}

	
	public static void main(String [] args)
	{
		TextArea ta = new TextArea();
		
	}
	
}
