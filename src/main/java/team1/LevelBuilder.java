package team1;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintWriter;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class LevelBuilder extends JPanel{

    private static final long serialVersionUID = 1L;
    private TilePalette tilePalette;
    private String fileName;
    private SpriteMap spriteMap;
    private PrintWriter txtFile;
    public static SokobanObject state = SokobanObject.SPACE;

    public LevelBuilder(SpriteMap spriteMap) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(Color.RED);
        add(spriteMap);
        tilePalette = new TilePalette(spriteMap);
        add(new TilePalette(spriteMap));
    }

    public static void setState(SokobanObject object) {
        state = object;
    }
}
