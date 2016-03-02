import java.awt.GridLayout;
import javax.imageio.*;
import javax.swing.*;
import java.io.*;

public class SpriteMap extends JPanel{
	private ImageIcon space;
	private ImageIcon wall;
	private ImageIcon goal;
	private ImageIcon box;
	private ImageIcon box_on_goal;
	private ImageIcon player;
	private ImageIcon player_on_goal;
	private static final long serialVersionUID = 1L;
	private SokobanMap map;

	public SpriteMap(SokobanMap map){
		super(new GridLayout(map.getYSize(),map.getXSize()));
		this.map=map;
		loadSprites();
		setVisible(true);
		placeSprites();
	}

	public void placeSprites(){
		removeAll();
		String mapString = map.toString();
		for (char sp: mapString.toCharArray()){
			switch(sp){
			case '@':   add(new JLabel(player));
			break;
			case '$':   add(new JLabel(box));
			break;
			case '.':   add(new JLabel(goal));
			break;
			case '#':   add(new JLabel(wall));
			break;
			case '+':   add(new JLabel(player_on_goal));
			break;
			case '*':   add(new JLabel(box_on_goal));
			break;
			case ' ':   add(new JLabel(space));
			break;
			}
		}
		win();
		revalidate();
		repaint();
	}

	public void win(){
		if(map.isDone())
		{	removeAll();
			add (new JLabel("YOU WON!!"));}
	}

	public void loadSprites(){
		try{
			space = new ImageIcon(ImageIO.read(new File("src/tileset01/SPACE.png")));
			wall = new ImageIcon(ImageIO.read(new File("src/tileset01/WALL.png")));
			goal = new ImageIcon(ImageIO.read(new File("src/tileset01/GOAL.png")));
			box = new ImageIcon(ImageIO.read(new File("src/tileset01/BOX.png")));
			box_on_goal = new ImageIcon(ImageIO.read(new File("src/tileset01/BOX_ON_GOAL.png")));
			player = new ImageIcon(ImageIO.read(new File("src/tileset01/PLAYER.png")));
			player_on_goal = new ImageIcon(ImageIO.read(new File("src/tileset01/PLAYER_ON_GOAL.png")));
		}catch (IOException e){e.printStackTrace();}
	}
}
