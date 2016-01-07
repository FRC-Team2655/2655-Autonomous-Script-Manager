package gui;

import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JLabel;

public class HelpDialog extends JDialog {
	private JPanel mainPanel;
	private JPanel Command;
	private JPanel panel_1;

	public HelpDialog() {
		setTitle("Help");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
		setAlwaysOnTop(true);
		
		mainPanel = new JPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 0));
		
		Command = new JPanel();
		mainPanel.add(Command, BorderLayout.WEST);
		
		panel_1 = new JPanel();
		mainPanel.add(panel_1, BorderLayout.CENTER);
		
		
		
	}

}
