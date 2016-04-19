package team1;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.Color;

import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


@SuppressWarnings("serial")
public class NewGame extends GUI {

	@SuppressWarnings("unused")
	private JPanel contentPane;
	@SuppressWarnings("unused")
	private JTextField textField;
	
	

	/**
	 * Launch the application.
	 */
	

	
	public static void main(String[] args) {
		JFrame frame=new JFrame("Create New Game!");
		ImageIcon bg = new ImageIcon("C:\\Users\\Administrator\\Desktop\\SokobanGame\\SokobanGame\\src\\terminator-2.jpg");
		JLabel label=new JLabel(bg);
		frame.getLayeredPane().add(label,new Integer(Integer.MIN_VALUE));
        label.setBounds(0, 0, bg.getIconWidth(), bg.getIconHeight());
		JPanel jp=(JPanel)frame.getContentPane();
		jp.setOpaque(false);
		JPanel contentPane = new JPanel();
		
		
		

		frame.setContentPane(contentPane);
		contentPane.setOpaque(false);
		contentPane.setLayout(null);
		JLabel lblCreateNewGame = new JLabel("Create New Game");
		lblCreateNewGame.setHorizontalAlignment(SwingConstants.CENTER);
		lblCreateNewGame.setForeground(Color.WHITE);
		lblCreateNewGame.setBackground(Color.WHITE);
		lblCreateNewGame.setFont(new Font("Tahoma", Font.BOLD, 17));
		lblCreateNewGame.setBounds(111, 24, 210, 44);
		contentPane.add(lblCreateNewGame);
		
		JLabel lblNewPlayerName = new JLabel("New Player Name");
		lblNewPlayerName.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewPlayerName.setForeground(Color.WHITE);
		lblNewPlayerName.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblNewPlayerName.setBounds(32, 110, 158, 14);
		contentPane.add(lblNewPlayerName);
		
		JTextField textField = new JTextField();
		textField.setBounds(249, 109, 118, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		
		JButton btnReayGo = new JButton("Reay!   GO!");
		btnReayGo.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnReayGo.setBounds(50, 196, 118, 23);
		contentPane.add(btnReayGo);
		btnReayGo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(textField.getText().isEmpty()){
					JOptionPane.showMessageDialog(null, "You need to input a player name!");
				}
				else{
					PlayerName=textField.getText();
					BoxTerm.main(PlayerName);
					frame.dispose();
				}
			}
		});
		
		JButton button = new JButton("Leave");
		button.setFont(new Font("Tahoma", Font.BOLD, 14));
		button.setBounds(266, 196, 89, 23);
		contentPane.add(button);
		button.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				int i=JOptionPane.showConfirmDialog(null, "Exit Now?", "Cancel", JOptionPane.YES_NO_OPTION);
		         if(i==JOptionPane.YES_OPTION){
		        	 frame.setVisible(false);
		         }
			}
		});

		frame.setSize(400,300);
		frame.setVisible(true);
		
		

		
	}
	
	

	/**
	 * Create the frame.
	 */
	public NewGame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
	}

}
