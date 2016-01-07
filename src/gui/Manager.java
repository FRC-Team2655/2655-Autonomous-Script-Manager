package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class Manager extends JFrame implements WindowListener, ActionListener, ItemListener {

	private static final long serialVersionUID = 1078660512871634148L;
	private JPanel mainPanel;
	private JPanel filesPanel;
	private JPanel editorPanel;
	private JPanel buttonsPanel;
	private JComboBox<String> fileSelector;
	private JPanel fileButtonsPanel;
	private JButton newFileButton;
	private JButton deleteFileButton;
	private JButton renameFileButton;
	private JButton saveButton;
	private JButton testButton;
	private JPanel commandsPanel;
	private JPanel tablePanel;
	private JButton commandDriveButton;
	private JButton commandRotateButton;
	private JButton commandWaitButton;
	private JButton commandStopButton;
	private JButton commandResetgyroButton;
	private JButton helpButton;
	private JScrollPane tableScrollPane;
	private JTable table;
	private JPanel tableButtons;
	private JButton tableUpButton;
	private JButton tableDownButton;
	private JButton tableDeleteButton;
	
	private File routinesFolder = new File(System.getProperty("user.home") + "/Desktop/Autonomous/");
	private File deleteBackupsFolder = new File(System.getProperty("user.home") + "/Desktop/Autonomous/Backup/");
	private File autoBackupFolder = new File(System.getProperty("user.home") + "/Autonomous-BAK/");
	private JButton refreshButton;
	private JLabel spacerlabel;
	
	private String lastFileSelected = "";
	
	public Manager(){
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.addWindowListener(this);
		
		mainPanel = new JPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 0));
		
		filesPanel = new JPanel();
		filesPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Scripts", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mainPanel.add(filesPanel, BorderLayout.NORTH);
		filesPanel.setLayout(new BorderLayout(0, 0));
		
		fileSelector = new JComboBox<String>();
		filesPanel.add(fileSelector, BorderLayout.CENTER);
		
		fileButtonsPanel = new JPanel();
		filesPanel.add(fileButtonsPanel, BorderLayout.EAST);
		fileButtonsPanel.setLayout(new GridLayout(0, 5, 0, 0));
		
		refreshButton = new JButton("Refresh");
		fileButtonsPanel.add(refreshButton);
		
		spacerlabel = new JLabel("");
		fileButtonsPanel.add(spacerlabel);
		
		newFileButton = new JButton("New");
		fileButtonsPanel.add(newFileButton);
		
		renameFileButton = new JButton("Rename");
		fileButtonsPanel.add(renameFileButton);
		
		deleteFileButton = new JButton("Delete");
		fileButtonsPanel.add(deleteFileButton);
		
		editorPanel = new JPanel();
		mainPanel.add(editorPanel, BorderLayout.CENTER);
		editorPanel.setLayout(new BorderLayout(0, 0));
		
		commandsPanel = new JPanel();
		commandsPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Pallete", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		editorPanel.add(commandsPanel, BorderLayout.WEST);
		commandsPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		commandDriveButton = new JButton("DRIVE");
		commandsPanel.add(commandDriveButton);
		
		commandRotateButton = new JButton("ROTATE");
		commandsPanel.add(commandRotateButton);
		
		commandWaitButton = new JButton("WAIT");
		commandsPanel.add(commandWaitButton);
		
		commandStopButton = new JButton("STOP");
		commandsPanel.add(commandStopButton);
		
		commandResetgyroButton = new JButton("RESET_GYRO");
		commandsPanel.add(commandResetgyroButton);
		
		tablePanel = new JPanel();
		tablePanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Script", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		editorPanel.add(tablePanel, BorderLayout.CENTER);
		tablePanel.setLayout(new BorderLayout(0, 0));
		
		tableScrollPane = new JScrollPane();
		tablePanel.add(tableScrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		table.setPreferredScrollableViewportSize(new Dimension(750, 350));
		tableScrollPane.setViewportView(table);
		
		tableButtons = new JPanel();
		tablePanel.add(tableButtons, BorderLayout.EAST);
		tableButtons.setLayout(new GridLayout(3, 1, 0, 0));
		
		tableUpButton = new JButton("Up");
		tableButtons.add(tableUpButton);
		
		tableDownButton = new JButton("Down");
		tableButtons.add(tableDownButton);
		
		tableDeleteButton = new JButton("Delete");
		tableButtons.add(tableDeleteButton);
		
		buttonsPanel = new JPanel();
		mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
		buttonsPanel.setLayout(new GridLayout(0, 3, 0, 0));
		
		saveButton = new JButton("Save");
		buttonsPanel.add(saveButton);
		
		testButton = new JButton("Save and Check");
		testButton.setEnabled(false);
		buttonsPanel.add(testButton);
		
		helpButton = new JButton("Help");
		helpButton.setEnabled(false);
		buttonsPanel.add(helpButton);
		
		fileSelector.addItemListener(this);
		
		setupTable();
		scanFiles();
		setupButtons();
		
		lastFileSelected = (String) fileSelector.getSelectedItem();
						
	}
	
	private void setupTable(){
		
		DefaultTableModel model = new DefaultTableModel(){
			
			private static final long serialVersionUID = 4835831320207780958L;

			@Override
			public boolean isCellEditable(int row, int column) {
				
				if(column == 0 || column == 2){
					
					return false;
					
				}else{
					
					return true;
					
				}
				
			}
			
		};
		
		table.setModel(model);
		
	    model.addColumn("Command");
	    model.addColumn("Argument");
	    model.addColumn("Argument In:");
	    
	    table.getTableHeader().setFont( new Font( "Arial" , Font.BOLD, 15 ));
	    table.getColumnModel().getColumn(0).setPreferredWidth(300);
	    table.getColumnModel().getColumn(1).setPreferredWidth(300);
	    table.getColumnModel().getColumn(2).setPreferredWidth(150);
	    
	    table.getTableHeader().setReorderingAllowed(false);
	    table.getTableHeader().setResizingAllowed(false);
	    
	    table.setSelectionModel(new ForcedListSelectionModel());
	    	    		
	}
	
	private void scanFiles(){
		
		if(!routinesFolder.exists()){
			
			routinesFolder.mkdirs();
						
		}
		
		if(!deleteBackupsFolder.exists()){
			
			deleteBackupsFolder.mkdirs();
			
		}

		if(!autoBackupFolder.exists()){
			
			autoBackupFolder.mkdirs();
			
		}
		
		File[] files = routinesFolder.listFiles();
		
		for(File file : files){
			
			try {
				
				BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
				BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(new File(autoBackupFolder.getAbsolutePath() + "/" + file.getName())));
				
				while(reader.available() > 0){
					
					writer.write(reader.read());
					
				}
				
				reader.close();
				writer.close();
				
			} catch (Exception e) {
				

				
			}
			
		}
		
		String[] fileNames = routinesFolder.list();
		
		for(String name : fileNames){
			
			if(name.endsWith(".csv")){
				
				fileSelector.addItem(name.substring(0, name.lastIndexOf(".csv")));
				
			}
			
		}
		
		
		
	}
	
	public void rescanFiles(){
		
		String currentlySelected = (String) fileSelector.getSelectedItem();
		
		if(!routinesFolder.exists()){
			
			routinesFolder.mkdirs();
						
		}
		
		if(!deleteBackupsFolder.exists()){
			
			deleteBackupsFolder.mkdirs();
			
		}

		if(!autoBackupFolder.exists()){
			
			autoBackupFolder.mkdirs();
			
		}
		
		File[] files = routinesFolder.listFiles();
		
		for(File file : files){
			
			try {
				
				BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
				BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(new File(autoBackupFolder.getAbsolutePath() + "/" + file.getName())));
				
				while(reader.available() > 0){
					
					writer.write(reader.read());
					
				}
				
				reader.close();
				writer.close();
				
			} catch (Exception e) {
				

				
			}
			
		}
		
		String[] fileNames = routinesFolder.list();
		
		fileSelector.removeAllItems();
		fileSelector.repaint();
		fileSelector.revalidate();
		
		for(String name : fileNames){
			
			if(name.endsWith(".csv")){
				
				fileSelector.addItem(name.substring(0, name.lastIndexOf(".csv")));
				
			}
			
		}
		
		for(int i = 0; i < fileSelector.getItemCount(); i++){
			
			if(((String) fileSelector.getItemAt(i)).equals(currentlySelected)){
				
				fileSelector.setSelectedIndex(i);
				
			}
			
		}
		
		if(!((String) fileSelector.getSelectedItem()).equals(currentlySelected)){
			
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			
			for (int i = model.getRowCount() - 1; i >= 0; i--) {
		    	
		        model.removeRow(i);
		    
		    }
			
			table.repaint();
			
		}
		
	}
	
	
	public void rescanFiles(String toSelect){
		
		String currentlySelected = toSelect;
		
		if(!routinesFolder.exists()){
			
			routinesFolder.mkdirs();
						
		}
		
		if(!deleteBackupsFolder.exists()){
			
			deleteBackupsFolder.mkdirs();
			
		}

		if(!autoBackupFolder.exists()){
			
			autoBackupFolder.mkdirs();
			
		}
		
		File[] files = routinesFolder.listFiles();
		
		for(File file : files){
			
			try {
				
				BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
				BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(new File(autoBackupFolder.getAbsolutePath() + "/" + file.getName())));
				
				while(reader.available() > 0){
					
					writer.write(reader.read());
					
				}
				
				reader.close();
				writer.close();
				
			} catch (Exception e) {
				

				
			}
			
		}
		
		String[] fileNames = routinesFolder.list();
		
		fileSelector.removeAllItems();
		fileSelector.repaint();
		fileSelector.revalidate();
		
		for(String name : fileNames){
			
			if(name.endsWith(".csv")){
				
				fileSelector.addItem(name.substring(0, name.lastIndexOf(".csv")));
				
			}
			
		}
		
		for(int i = 0; i < fileSelector.getItemCount(); i++){
			
			if(((String) fileSelector.getItemAt(i)).equals(currentlySelected)){
				
				fileSelector.setSelectedIndex(i);
				
			}
			
		}
		
		if(!((String) fileSelector.getSelectedItem()).equals(currentlySelected)){
			
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			
			for (int i = model.getRowCount() - 1; i >= 0; i--) {
		    	
		        model.removeRow(i);
		    
		    }
			
			table.repaint();
			
		}
		
	}
	
	private void setupButtons(){
		
		deleteFileButton.addActionListener(this);
		newFileButton.addActionListener(this);
		refreshButton.addActionListener(this);
		renameFileButton.addActionListener(this);
		
		commandDriveButton.addActionListener(this);
		commandRotateButton.addActionListener(this);
		commandWaitButton.addActionListener(this);
		commandStopButton.addActionListener(this);
		commandResetgyroButton.addActionListener(this);
		
		tableUpButton.addActionListener(this);
		tableDownButton.addActionListener(this);
		tableDeleteButton.addActionListener(this);
		
		saveButton.addActionListener(this);
		testButton.addActionListener(this);
		helpButton.addActionListener(this);
		
	}
	
	public static void main(String[] args){
		
		try{
			
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
		}catch(Exception e){
			

			
		}
		
		Manager manager = new Manager();
		
		try{			
			manager.setIconImage(ImageIO.read(new File("./img/icon.png")));
			
		}catch(Exception e){
			
			
			
		}
		
		manager.pack();
		manager.setTitle("Autonomous Routine Manager");
		manager.setLocationRelativeTo(null);
		manager.setVisible(true);
		
	}

	public void windowActivated(WindowEvent arg0) {}

	public void windowClosed(WindowEvent arg0) {}

	public void windowClosing(WindowEvent arg0) {
		
		final JDialog dialog = new JDialog();
		JButton yesBtn = new JButton("Yes");
		JButton noBtn = new JButton("No");
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(1, 2));
		buttons.add(yesBtn);
		buttons.add(noBtn);
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add("Center", new JLabel("Save before exiting?"));
		dialog.getContentPane().add("South", buttons);
		dialog.setAlwaysOnTop(true);
		dialog.pack();
		dialog.setTitle("Save?");
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		dialog.requestFocus();
		
		yesBtn.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
				
				saveToCSV();
				dialog.dispose();
				
			}
			
		});
		
		noBtn.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
				
				dialog.dispose();
				
			}
			
		});
		
		dialog.addWindowListener(new WindowListener(){

			@Override
			public void windowActivated(WindowEvent arg0) {}

			@Override
			public void windowClosed(WindowEvent arg0) {
				
				File[] files = routinesFolder.listFiles();
				
				for(File file : files){
					
					try {
										
						BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
						BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(new File(autoBackupFolder.getAbsolutePath() + "/" + file.getName())));
						
						while(reader.available() > 0){
							
							writer.write(reader.read());
							
						}
						
						reader.close();
						writer.close();
						
						System.exit(0);
						
					} catch (Exception e) {
						
						System.exit(-1);
						
					}
					
				}
				
			}

			@Override
			public void windowClosing(WindowEvent arg0) {}

			@Override
			public void windowDeactivated(WindowEvent arg0) {}

			@Override
			public void windowDeiconified(WindowEvent arg0) {}

			@Override
			public void windowIconified(WindowEvent arg0) {}

			@Override
			public void windowOpened(WindowEvent arg0) {}
			
			
			
		});
		
	}

	public void windowDeactivated(WindowEvent arg0) {}

	public void windowDeiconified(WindowEvent arg0) {}

	public void windowIconified(WindowEvent arg0) {}

	public void windowOpened(WindowEvent arg0) {}
	
	public void actionPerformed(ActionEvent e){
		
		Object src = e.getSource();
		
		if(src == deleteFileButton){
			
			try {
				
				File srcFile = new File(routinesFolder.getAbsolutePath() + "/" + ((String)fileSelector.getSelectedItem()) + ".csv");
				File destFile = new File(deleteBackupsFolder.getAbsolutePath() + "/" + srcFile.getName().substring(0, srcFile.getName().lastIndexOf(".csv")) + new SimpleDateFormat("MM-dd-YYYY_hh-mm-ss").format(Calendar.getInstance().getTime()) + ".csv");
				
				BufferedInputStream reader = new BufferedInputStream(new FileInputStream(srcFile));
				BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(destFile));
				
				while(reader.available() > 0){
					
					writer.write(reader.read());
					
				}
								
				reader.close();
				writer.close();
				
				srcFile.delete();
				
				rescanFiles();
				
			} catch (Exception ex) {
				
				
				
			}
			
		}else if(src == newFileButton){
			
			new NewRoutineDialog(this);
				
		}else if(src == refreshButton){
			
			rescanFiles();
			
		}else if(src == renameFileButton){
			
			new RenameRoutineDialog(this, ((String) fileSelector.getSelectedItem()));
			
		}else if(src == commandDriveButton){
			
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			
			model.addRow(new String[]{"DRIVE", "6", "Inches"});
			
		}else if(src == commandRotateButton){
			
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			
			model.addRow(new String[]{"ROTATE", "90", "Degrees"});
			
		}else if(src == commandWaitButton){
			
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			
			model.addRow(new String[]{"WAIT", "5", "Seconds"});
			
		}else if(src == commandStopButton){
			
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			
			model.addRow(new String[]{"STOP", "", "None"});
			
		}else if(src == commandResetgyroButton){
			
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			
			model.addRow(new String[]{"RESET_GYRO", "", "NONE"});
			
		}else if(src == tableUpButton){
			
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			
			int i = table.getSelectedRow();
			
			model.moveRow(i, i, i - 1);
			
			table.setRowSelectionInterval(i - 1, i - 1);
			
		}else if(src == tableDownButton){
			
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			
			int i = table.getSelectedRow();
			
			model.moveRow(i, i, i + 1);
			
			table.setRowSelectionInterval(i + 1, i + 1);
			
		}else if(src == tableDeleteButton){
			
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			
			int i = table.getSelectedRow();
			
			model.removeRow(i);
			
			table.setRowSelectionInterval(i - 1, i - 1);
			
		}else if(src == saveButton){
			
			saveToCSV();
			
		}else if(src == testButton){
			
			saveToCSV();
			checkCSV();
			
		}else if(src == helpButton){
			
			showHelp();
			
		}
		
	}
	
	private void checkCSV(){
		
		//TODO check
		
	}
	
	private void showHelp(){
		
		//TODO show help
		
	}
	
	private void saveToCSV(){
		
		try{
			
			String fileName = (String) fileSelector.getSelectedItem();
			
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			
			File outFile = new File(routinesFolder.getAbsolutePath() + "/" + fileName + ".csv");
						
			outFile.delete();
			
			outFile.createNewFile();
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
			
			for(int row = 0; row < model.getRowCount(); row++){
				
				for(int column = 0; column < model.getColumnCount(); column++){
					
					writer.write(model.getValueAt(row, column).toString());
					
					if(column != model.getColumnCount() - 1){
						
						writer.write(",");
					
					}
					
				}
				
				if(row != model.getRowCount() - 1){
					
					writer.newLine();
					
				}
				
			}
			
			writer.close();
			
		}catch(Exception e){
			
			
			
		}
		
	}
	
	private void loadFromCSV(){
		
		try{
			
			String fileName = fileSelector.getSelectedItem().toString();
			
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			
			File inFile = new File(routinesFolder.getAbsolutePath() + "/" + fileName + ".csv");
			
			BufferedReader reader = new BufferedReader(new FileReader(inFile));
			
			ArrayList<String> lines = new ArrayList<String>();
			
			while(reader.ready()){
				
				lines.add(reader.readLine());
				
			}
			
			for(int row = 0; row < lines.size(); row++){
				
				String[] columns = lines.get(row).split(",");
				
				model.addRow(columns);
				
			}
			
			reader.close();
					
			
		}catch(Exception e){
			
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			
		}
		
	}
	
	@Override
    public void itemStateChanged(ItemEvent e) {
		
		Object src = e.getSource();
		
       if(src == fileSelector){
    	   
    	   if (e.getStateChange() == ItemEvent.SELECTED) {
        	   
    		   String newItem = (String) fileSelector.getSelectedItem();
    		   
    		   if(!newItem.equals(lastFileSelected)){
    			       			   
    			   DefaultTableModel model = (DefaultTableModel) table.getModel();
    			   
    			   for (int i = model.getRowCount() - 1; i >= 0; i--) {
    			   	
    				   model.removeRow(i);
    			    
    			   }
    			    
    			   loadFromCSV();
    			    
    			   lastFileSelected = newItem;    				
    			   
    		   }
    	          
           }
    	   
       }
       
    }  
	
	public class ForcedListSelectionModel extends DefaultListSelectionModel {

		private static final long serialVersionUID = -7073835059132006928L;

		public ForcedListSelectionModel () {
	    	
	        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        
	    }

	    @Override
	    public void clearSelection() {
	    	
	    	
	    	
	    }

	    @Override
	    public void removeSelectionInterval(int index0, int index1) {
	    	
	    	
	    	
	    }

	}
	
}
