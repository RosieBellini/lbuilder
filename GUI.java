package team1;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Container;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.awt.Font;
import java.awt.Color;
import java.awt.BorderLayout;


public class GUI extends JFrame {

	
	private static final long serialVersionUID = 2597327293501718729L;
	public static  String PlayerName;
	 @SuppressWarnings("unused")
	private static SokobanMap map;
	 @SuppressWarnings("unused")
	private static SpriteMap spriteMap;
	 @SuppressWarnings("unused")
	private static int tileSetNo=1;


	/**
	 * Launch the application.
	 */

	
	public static void main(String[] args) {
		new GUI();
	
		
			
	}
		

	

	/**
	 * Create the frame.
	 */
	public GUI() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		JFrame frame=new JFrame("Welcome To BOX TERMINTORRRRR!"+" "+"Player:"+" "+getName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ImageIcon img = new ImageIcon("C:\\Users\\Administrator\\Desktop\\SokobanGame\\SokobanGame\\src\\terminator_genisys___genesis_by_davidcreativedesigns-d8msxm0.jpg");
		JLabel imgLabel = new JLabel(img);
		frame.getLayeredPane().add(imgLabel, new Integer(Integer.MIN_VALUE));
		imgLabel.setBounds(0,0,img.getIconWidth(), img.getIconHeight());
		Container cp=frame.getContentPane();
		
		
		
		ImageIcon img2 = new ImageIcon("C:\\Users\\Administrator\\Desktop\\SokobanGame\\SokobanGame\\src\\team1\\web-logo.png");
		JButton btnNewButton = new JButton(img2);
		btnNewButton.setBackground(Color.BLACK);
		btnNewButton.setText("<===Music ON");
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnNewButton.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("deprecation")
			@Override
			public void mouseClicked(MouseEvent e) {
				try { 
					URL cb; 
					File f = new File("C:\\Users\\Administrator\\Desktop\\SokobanGame\\SokobanGame\\src\\terminator.wav"); 
					cb = f.toURL(); 
					AudioClip aau; 
					aau = Applet.newAudioClip(cb); 
					//aau.play();
					aau.loop();
				
			} catch (MalformedURLException a) { 
				a.printStackTrace(); 
				} 
			}
		});
		frame.getContentPane().add(btnNewButton,BorderLayout.SOUTH);
		
	
		
	
		
		

		((JPanel)cp).setOpaque(false); 
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("Game");
		menuBar.add(mnFile);
		
		JMenuItem mntmNewGame = new JMenuItem("New Game");
		mnFile.add(mntmNewGame);
		mntmNewGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				NewGame.main(null);
				frame.dispose();
			}
		});
		
		
		
		
		
		JMenuItem mntmRestart = new JMenuItem("Restart");
		mnFile.add(mntmRestart);
		mntmRestart.addActionListener(new ActionListener() {
	         

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			
				BoxTerm.main(PlayerName);
		         }
			
	    });
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mntmExit.addActionListener(new ActionListener() {
			         

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						int i=JOptionPane.showConfirmDialog(null, "Exit Now?", "Cancel", JOptionPane.YES_NO_OPTION);
				         if(i==JOptionPane.YES_OPTION){
				          System.exit(0);
				         }
					}
			    });

			


		
		JMenu mnNewMenu_1 = new JMenu("Play");
		menuBar.add(mnNewMenu_1);
		
		JMenuItem mntmChangeControlMethod_1 = new JMenuItem("Change Control Method");
		mnNewMenu_1.add(mntmChangeControlMethod_1);
		mntmChangeControlMethod_1.addActionListener(new ActionListener() {
	         

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Object[] obj2 ={ "W/A/S/D MODE", "UP/DOWN/LEFT/RIGHT MODE" };  
				String s = (String) JOptionPane.showInputDialog(null,"Please choose a control method:\n", "Control Method", JOptionPane.PLAIN_MESSAGE, new ImageIcon("C:\\Users\\Administrator\\Desktop\\SokobanGame\\SokobanGame\\src\\start.jpg"), obj2, "W/A/S/D MODE");
				if(s=="UP/DOWN/LEFT/RIGHT MODE"){
					
					BoxTermMODE2.main(PlayerName);
				}
			}
	    });
		
		
		JMenu mnNewMenu = new JMenu("Level Builder");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmOpenLevelBuilder = new JMenuItem("Open Level Builder");
		mnNewMenu.add(mntmOpenLevelBuilder);
		
		JMenuItem mntmOpenLevelSolver = new JMenuItem("Open Level Solver");
		mnNewMenu.add(mntmOpenLevelSolver);
		
		JMenu mnNewMenu_2 = new JMenu("Options");
		menuBar.add(mnNewMenu_2);
		
		JMenuItem mntmChangeSkin = new JMenuItem("Change Skin");
		mnNewMenu_2.add(mntmChangeSkin);
	    mntmChangeSkin.addActionListener(new ActionListener() {
	         

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Object[] obj3 = {"Skin 1","Skin 2","Skin 3"};
				String s = (String) JOptionPane.showInputDialog(null,"Please choose a Skin:\n", "Skin", JOptionPane.PLAIN_MESSAGE, null, obj3, "Skin 1");
				if(s=="Skin 2"){
				ImageIcon img3 = new ImageIcon("C:\\Users\\Administrator\\Desktop\\SokobanGame\\SokobanGame\\src\\terminator-400x300.jpg");
				imgLabel.setIcon(img3);
				imgLabel.setBounds(0,0,img3.getIconWidth(), img3.getIconHeight());
				frame.setSize(400,300);
				}
				if(s=="Skin 3"){
					ImageIcon img3 = new ImageIcon("C:\\Users\\Administrator\\Desktop\\SokobanGame\\SokobanGame\\src\\JyTCM.jpg");
					imgLabel.setIcon(img3);
					imgLabel.setBounds(0,0,img3.getIconWidth(), img3.getIconHeight());
					frame.setSize(1920,1258);
				}
				}
			
	    });
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Change Music");
		mnNewMenu_2.add(mntmNewMenuItem_2);
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				 JFileChooser jfc=new JFileChooser();
			        FileNameExtensionFilter filter = new FileNameExtensionFilter(
			        		"wav File", "wav");
			            jfc.setFileFilter(filter);
			        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );  
			       jfc.showDialog(new JLabel(), "Choose");  
			        File file=jfc.getSelectedFile();
			       
			        while(file.exists()){  
			        	try { 
			        		File new1= new File(file.getPath());
							URL cd; 
							 
							cd = new1.toURL(); 
							AudioClip aau; 
							aau = Applet.newAudioClip(cd); 
							aau.play();
						
					} catch (MalformedURLException c) { 
						c.printStackTrace(); 
						} 
					    
			        
			          
			    }  
			}
		});

		frame.setSize(550,410);
		frame.setVisible(true);
		
	

		
	}
	
	
	
	public String Rename(String s){
		PlayerName=s;
		return PlayerName;
		
	}
	
	public String getName(){
		return PlayerName;
	}
}
