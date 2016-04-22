package team1;

import java.awt.Color;
import java.io.PrintWriter;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * LevelBuilder class. Used to create and display the LevelBuilder GUI.
 * All of the fields & methods are static.
 *
 */

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
