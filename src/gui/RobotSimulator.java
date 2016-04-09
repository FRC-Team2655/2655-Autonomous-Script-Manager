package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.SwingConstants;

public class RobotSimulator extends JDialog /*implements ActionListener*/ {
	
	DrawFieldPanel field = new DrawFieldPanel(1);
	//private final JButton btnApply = new JButton("Apply");
	//private final JPanel zoompanel = new JPanel();
	//private final JTextField zoom = new JTextField();
	
	public RobotSimulator(){
		
		try{	
			
			this.setIconImage(ImageIO.read(new File("./img/icon.png"))); //Set the icon
			
		}catch(Exception e){
			
			
			
		}
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(field, "Center");
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		/*btnApply.addActionListener(this);
		zoompanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Zoom", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		panel.add(zoompanel);
		zoompanel.setLayout(new BorderLayout(0, 0));
		zoom.setText("1");
		zoom.setHorizontalAlignment(SwingConstants.TRAILING);
		zoom.setColumns(10);
		
		zoompanel.add(zoom, BorderLayout.NORTH);
		
		panel.add(btnApply);*/
		this.pack();
		this.setResizable(false);
		this.setTitle("Robot Simulator");
		this.setLocationRelativeTo(null);		
				
	}

	/*@Override
	public void actionPerformed(ActionEvent e) {
		
		Object src = e.getSource();
		
		if(src == btnApply){
			
			this.setVisible(false);
			field = new DrawFieldPanel(Integer.parseInt(zoom.getText()));
			this.pack();
			this.setVisible(true);
			field.repaint();
			
		}
		
	}*/
	
	
	
}
