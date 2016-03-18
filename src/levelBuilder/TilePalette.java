package levelBuilder;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class TilePalette extends JPanel {
	

	private static final long serialVersionUID = 1L;

	public TilePalette()
	{
		super();
		setBackground(Color.GREEN);
		add(new JLabel("Test"));
	}

}
