package team1;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class GUITransparentBackground extends JComponent { 
    @SuppressWarnings("unused")
	private JFrame frame; 
    private Image background;

public GUITransparentBackground(JFrame frame) {
    this.frame = frame;
    updateBackground( );
}

public void updateBackground( ) {
    try {
        Robot rbt = new Robot( );
        Toolkit tk = Toolkit.getDefaultToolkit( );
        Dimension dim = tk.getScreenSize( );
        background = rbt.createScreenCapture(
        new Rectangle(0,0,(int)dim.getWidth( ),
                          (int)dim.getHeight( )));
    } catch (Exception ex) {
        
        ex.printStackTrace( );
    }
}

public void paintComponent(Graphics g) {
    Point pos = this.getLocationOnScreen( );
    Point offset = new Point(-pos.x,-pos.y);
    g.drawImage(background,offset.x,offset.y,null);
}

@SuppressWarnings("deprecation")
public static void main(String[] args) {
    JFrame frame = new JFrame("Welcome to Box Terminor!!!!");
    GUITransparentBackground bg = new GUITransparentBackground(frame);
    bg.setLayout(new BorderLayout( ));
    JButton button = new JButton("This is a button");
    bg.add("North",button);
        JLabel label = new JLabel("This is a label");
    bg.add("South",label);
    frame.getContentPane( ).add("Center",bg);
    frame.pack( );
    frame.setSize(150,100);
    frame.show( );
}

}

