package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class GyroSim extends JDialog {
	
	private static final long serialVersionUID = 2731536364300341670L;
	
	private JLabel currentGyroLabel;
	private DrawGyroAnglePanel drawGyroAnglePanel;

	public GyroSim(int windowHeight, JFrame mainWindow) {
		
		currentGyroLabel = new JLabel("0°");
		currentGyroLabel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Current Gyro Angle", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		currentGyroLabel.setHorizontalAlignment(SwingConstants.CENTER);
		currentGyroLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		getContentPane().add(currentGyroLabel, BorderLayout.SOUTH);
		
		drawGyroAnglePanel = new DrawGyroAnglePanel();
		getContentPane().add(drawGyroAnglePanel, BorderLayout.CENTER);
		
		setGyroAngle(0);
		
		try{	
			
			this.setIconImage(ImageIO.read(new File("./img/icon.png"))); //Set the icon
			
		}catch(Exception e){
			
			
			
		}
		
		this.setTitle("Gyro");
		this.pack();
		this.setSize(this.getWidth(), windowHeight);
		this.setLocation(mainWindow.getX() + mainWindow.getWidth(), mainWindow.getY());
		this.setAlwaysOnTop(true);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);

	}
	
	public void setGyroAngle(int angle){
		
		currentGyroLabel.setText(String.valueOf(angle) + "°");
		drawGyroAnglePanel.setAngle(angle);
		
	}
	
	public int getGyroAngle(){
		
		return drawGyroAnglePanel.getAngle();
		
	}
	
	public void setAngleOffset(int offset){
		
		drawGyroAnglePanel.setOffset(offset);
		
	}

}
