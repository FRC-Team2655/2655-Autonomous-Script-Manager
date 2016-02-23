package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
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
import java.io.IOException;
import java.nio.file.Files;
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
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import engine.CSVCheckEngine;
import engine.RowNumberTable;

public class Manager extends JFrame implements WindowListener, ActionListener, ItemListener, PopupMenuListener {

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
	private JButton refreshButton;
	private JLabel spacerlabel;
	private JButton discardButton;
	
	private static GyroSim gyroSim;
	
	private String[] allCommands = {"DRIVE", "ROTATE_TO", "WAIT", "STOP", "RESET_GYRO", "BALL_INTAKE_DOWN", "BALL_INTAKE_UP", "BALL_INTAKE_UP", "BALL_INTAKE_DOWN", "BALL_OUT", "BALL_STOP", "DEFENSE_DESTROYER_DOWN", "DEFENSE_DESTROYER_UP"};
	private String[] argumentTypes = {CSVCheckEngine.ARGUMENT_TYPE_INTEGER, CSVCheckEngine.ARGUMENT_TYPE_INTEGER, CSVCheckEngine.ARGUMENT_TYPE_INTEGER, CSVCheckEngine.ARGUMENT_TYPE_NONE, CSVCheckEngine.ARGUMENT_TYPE_NONE, CSVCheckEngine.ARGUMENT_TYPE_NONE, CSVCheckEngine.ARGUMENT_TYPE_NONE, CSVCheckEngine.ARGUMENT_TYPE_NONE, CSVCheckEngine.ARGUMENT_TYPE_NONE, CSVCheckEngine.ARGUMENT_TYPE_NONE, CSVCheckEngine.ARGUMENT_TYPE_NONE, CSVCheckEngine.ARGUMENT_TYPE_NONE, CSVCheckEngine.ARGUMENT_TYPE_NONE};
	
	private File routinesFolder = new File(System.getProperty("user.home") + "/Desktop/Autonomous/"); //folder with CSV routines
	private File deleteBackupsFolder = new File(System.getProperty("user.home") + "/Desktop/Autonomous/Backup/"); // Folder to move to when deleted
	private File autoBackupFolder = new File(System.getProperty("user.home") + "/Autonomous-BAK/"); //Folder that all scripts are backed up to on run and on close  WARNING may not work 100% correctly
	
	private String lastFileSelected = "";  //Stores the name of the previously selected file if the refresh button is pressed
	
	private boolean canSave = true;
	private JButton commandBallDownButton;
	private JButton commandBallUpButton;
	private JButton commandBallOutButton;
	private JButton commandBallStopButton;
	private JButton commandDefenseDownButton;
	private JButton commandDefenseUpButton;
	
	//Build the UI (done with eclipse windowbuilder)
	public Manager(){
		
		//Use window listener to show a dialog before the frame is closed
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(this);
		
		//add components
		
		//main panel (used to avoid using frame.getContentPane())
		mainPanel = new JPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 0));
		
		//Top panel with all file management tasks
		filesPanel = new JPanel();
		filesPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Scripts", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mainPanel.add(filesPanel, BorderLayout.NORTH);
		filesPanel.setLayout(new BorderLayout(0, 0));
		
		//File selector drop-down
		fileSelector = new JComboBox<String>();
		fileSelector.addPopupMenuListener(this);
		filesPanel.add(fileSelector, BorderLayout.CENTER);
		
		//Holds all file management buttons
		fileButtonsPanel = new JPanel();
		filesPanel.add(fileButtonsPanel, BorderLayout.EAST);
		fileButtonsPanel.setLayout(new GridLayout(0, 5, 0, 0));
		
		//Refresh the list of files
		refreshButton = new JButton("Refresh");
		fileButtonsPanel.add(refreshButton);
		
		//Separate refresh and the other buttons
		spacerlabel = new JLabel("");
		fileButtonsPanel.add(spacerlabel);
		
		//Create new File
		newFileButton = new JButton("New");
		fileButtonsPanel.add(newFileButton);
		
		//Rename a file
		renameFileButton = new JButton("Rename");
		fileButtonsPanel.add(renameFileButton);
		
		//Delete(Backup) a file
		deleteFileButton = new JButton("Delete");
		fileButtonsPanel.add(deleteFileButton);
		
		//Panel for table and commands
		editorPanel = new JPanel();
		mainPanel.add(editorPanel, BorderLayout.CENTER);
		editorPanel.setLayout(new BorderLayout(0, 0));
		
		//Panel for the command buttons
		commandsPanel = new JPanel();
		commandsPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Pallete", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		editorPanel.add(commandsPanel, BorderLayout.WEST);
		commandsPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		//Drive command
		commandDriveButton = new JButton("DRIVE");
		commandsPanel.add(commandDriveButton);
		
		//Rotate command
		commandRotateButton = new JButton("ROTATE_TO");
		commandsPanel.add(commandRotateButton);
		
		//Wait command
		commandWaitButton = new JButton("WAIT");
		commandsPanel.add(commandWaitButton);
		
		//Stop Command
		commandStopButton = new JButton("STOP");
		commandsPanel.add(commandStopButton);
		
		//Reset Gyro command
		commandResetgyroButton = new JButton("RESET_GYRO");
		commandsPanel.add(commandResetgyroButton);
		
		commandBallDownButton = new JButton("BALL_INTAKE_DOWN");
		commandsPanel.add(commandBallDownButton);
		
		commandBallUpButton = new JButton("BALL_INTAKE_UP");
		commandsPanel.add(commandBallUpButton);
		
		commandBallOutButton = new JButton("BALL_OUT");
		commandsPanel.add(commandBallOutButton);
		
		commandBallStopButton = new JButton("BALL_STOP");
		commandsPanel.add(commandBallStopButton);
		
		commandDefenseDownButton = new JButton("DEFENSE_DESTROYER_DOWN");
		commandsPanel.add(commandDefenseDownButton);
		
		commandDefenseUpButton = new JButton("DEFENSE_DESTROYER_UP");
		commandsPanel.add(commandDefenseUpButton);
		
		//Panel for table to show CSV data
		tablePanel = new JPanel();
		tablePanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Script", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		editorPanel.add(tablePanel, BorderLayout.CENTER);
		tablePanel.setLayout(new BorderLayout(0, 0));
		
		//Make table scrollable
		tableScrollPane = new JScrollPane();
		tablePanel.add(tableScrollPane, BorderLayout.CENTER);
		
		//Table to show data
		table = new JTable();
		table.setPreferredScrollableViewportSize(new Dimension(750, 350));
		tableScrollPane.setViewportView(table);
		
		JTable rowTable = new RowNumberTable(table);
		tableScrollPane.setRowHeaderView(rowTable);
		tableScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,
		    rowTable.getTableHeader());
		
		//Panel for table action buttons
		tableButtons = new JPanel();
		tablePanel.add(tableButtons, BorderLayout.EAST);
		tableButtons.setLayout(new GridLayout(3, 1, 0, 0));
		
		//Move Row Up
		tableUpButton = new JButton("Up");
		tableButtons.add(tableUpButton);
		
		//Move row down
		tableDownButton = new JButton("Down");
		tableButtons.add(tableDownButton);
		
		//Delete row
		tableDeleteButton = new JButton("Delete");
		tableButtons.add(tableDeleteButton);
		
		//Panel for buttons on bottom of window
		buttonsPanel = new JPanel();
		mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
		buttonsPanel.setLayout(new GridLayout(0, 4, 0, 0));
		
		//Save current file
		saveButton = new JButton("Save");
		buttonsPanel.add(saveButton);
		
		//discard changes
		discardButton = new JButton("Discard all Changes");
		buttonsPanel.add(discardButton);
		
		//test the file (Not working at all)
		testButton = new JButton("Save and Check");
		buttonsPanel.add(testButton);
		
		//Show built in help
		helpButton = new JButton("Help");
		buttonsPanel.add(helpButton);
		
		//watch for when the user changes the file
		fileSelector.addItemListener(this);
		
		setupTable(); //Create table columns and properties
		scanFiles(); //Get a list of files
		setupButtons(); //add the action listeners
		
		lastFileSelected = (String) fileSelector.getSelectedItem(); //keep track of the selected file
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				rescanAngle();
				
			}
			
		});
		
		table.getModel().addTableModelListener(new TableModelListener() {
			
	        public void tableChanged(TableModelEvent e) {
	        	
	            if(e.getType() == TableModelEvent.UPDATE){
	                
	            	rescanAngle();
	            	
	            }
	            
	        }
	        
	    });
								
	}
	
	//Create columns and set properties
	private void setupTable(){
		
		DefaultTableModel model = new DefaultTableModel(){
			
			private static final long serialVersionUID = 4835831320207780958L;
			//Only argument is editable
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
		
		//add three columns
	    model.addColumn("Command");
	    model.addColumn("Argument");
	    model.addColumn("Argument In:");
	    
	    //Table style
	    table.getTableHeader().setFont( new Font( "Arial" , Font.BOLD, 15 )); //Bold column headers
	    table.getColumnModel().getColumn(0).setPreferredWidth(300); //width
	    table.getColumnModel().getColumn(1).setPreferredWidth(300); //width
	    table.getColumnModel().getColumn(2).setPreferredWidth(150); //width
	    
	    table.getTableHeader().setReorderingAllowed(false); //User cant drag columns
	    table.getTableHeader().setResizingAllowed(false); //user cant resize columns
	    
	    table.setSelectionModel(new ForcedListSelectionModel()); //only select one row at a time
	    	    		
	}
	
	//Create a list of routine files
	private void scanFiles(){
		
		//Make sure folders exists
		if(!routinesFolder.exists()){
			
			routinesFolder.mkdirs();
						
		}
		
		if(!deleteBackupsFolder.exists()){
			
			deleteBackupsFolder.mkdirs();
			
		}

		if(!autoBackupFolder.exists()){
			
			autoBackupFolder.mkdirs();
			
		}
		
		File[] files = routinesFolder.listFiles(); //Get a list of files
		
		//Auto backup
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
		
		String[] fileNames = routinesFolder.list(); //list file names
		
		for(String name : fileNames){
			
			if(name.endsWith(".csv")){
				
				fileSelector.addItem(name.substring(0, name.lastIndexOf(".csv"))); //Add name without .csv
				
			}
			
		}
		
		
		
	}
	
	//scan files by refresh button (need to clear combobox and table)
	public void rescanFiles(){
		
		String currentlySelected = (String) fileSelector.getSelectedItem(); //Get currently selected name
		
		//Make sure folders exist (redundant)
		if(!routinesFolder.exists()){
			
			routinesFolder.mkdirs();
						
		}
		
		if(!deleteBackupsFolder.exists()){
			
			deleteBackupsFolder.mkdirs();
			
		}

		if(!autoBackupFolder.exists()){
			
			autoBackupFolder.mkdirs();
			
		}
		
		//Backup Files
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
		
		String[] fileNames = routinesFolder.list(); //List file Names
		
		//Clear Drop-down Items
		fileSelector.removeAllItems();
		fileSelector.repaint();
		fileSelector.revalidate();
		
		for(String name : fileNames){
			
			if(name.endsWith(".csv")){
				
				fileSelector.addItem(name.substring(0, name.lastIndexOf(".csv"))); //Add each csv file without .csv
				
			}
			
		}
		
		//Reselect currentlySelected if it exists
		for(int i = 0; i < fileSelector.getItemCount(); i++){
			
			if(((String) fileSelector.getItemAt(i)).equals(currentlySelected)){
				
				fileSelector.setSelectedIndex(i);
				
			}
			
		}
		
		//Create file and select if it does not exist
		if(!currentlySelected.equals(fileSelector.getSelectedItem().toString())){
			
			try{
				
				File file = new File(routinesFolder.getAbsolutePath() + "/" + currentlySelected + ".csv");
				file.createNewFile();
				rescanFiles(currentlySelected);
				
			}catch(Exception e){
				
				
				
			}
			
		}
		
	}
	
	//scan files after a delete
		public void deleteRescanFiles(){
			
			String currentlySelected = (String) fileSelector.getSelectedItem(); //Get currently selected name
			
			//Make sure folders exist (redundant)
			if(!routinesFolder.exists()){
				
				routinesFolder.mkdirs();
							
			}
			
			if(!deleteBackupsFolder.exists()){
				
				deleteBackupsFolder.mkdirs();
				
			}

			if(!autoBackupFolder.exists()){
				
				autoBackupFolder.mkdirs();
				
			}
			
			//Backup Files
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
			
			String[] fileNames = routinesFolder.list(); //List file Names
			
			//Clear Drop-down Items
			fileSelector.removeAllItems();
			fileSelector.repaint();
			fileSelector.revalidate();
			
			for(String name : fileNames){
				
				if(name.endsWith(".csv")){
					
					fileSelector.addItem(name.substring(0, name.lastIndexOf(".csv"))); //Add each csv file without .csv
					
				}
				
			}
			
			//Reselect currentlySelected if it exists
			for(int i = 0; i < fileSelector.getItemCount(); i++){
				
				if(((String) fileSelector.getItemAt(i)).equals(currentlySelected)){
					
					fileSelector.setSelectedIndex(i);
					
				}
				
			}
			
			//Clear table if it doesn't exist anymore
			if(!currentlySelected.equals(fileSelector.getSelectedItem().toString())){
				
			   //Clear the table
 			   DefaultTableModel model = (DefaultTableModel) table.getModel();
 			   
 			   for (int i = model.getRowCount() - 1; i >= 0; i--) {
 			   	
 				   model.removeRow(i);
 			    
 			   }
				
			}
			
			loadFromCSV();
			
		}
	
	
	public void rescanFiles(String toSelect){
		
		String currentlySelected = toSelect; //Which file to select
		
		//Create directories (Redundant)
		if(!routinesFolder.exists()){
			
			routinesFolder.mkdirs();
						
		}
		
		if(!deleteBackupsFolder.exists()){
			
			deleteBackupsFolder.mkdirs();
			
		}

		if(!autoBackupFolder.exists()){
			
			autoBackupFolder.mkdirs();
			
		}
		
		//Auto-Backup
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
		
		String[] fileNames = routinesFolder.list(); //List all file names
		
		//Clear drop-down
		fileSelector.removeAllItems();
		fileSelector.repaint();
		fileSelector.revalidate();
		
		for(String name : fileNames){
			
			if(name.endsWith(".csv")){
				
				fileSelector.addItem(name.substring(0, name.lastIndexOf(".csv"))); //Add all csv files without .csv
				
			}
			
		}
		
		//Select the file
		for(int i = 0; i < fileSelector.getItemCount(); i++){
			
			if(((String) fileSelector.getItemAt(i)).equals(currentlySelected)){
				
				fileSelector.setSelectedIndex(i);
				
			}
			
		}
		
		//Create file and select if it does not exist
		if(!currentlySelected.equals(fileSelector.getSelectedItem().toString())){
					
			try{
						
				File file = new File(routinesFolder.getAbsolutePath() + "/" + currentlySelected + ".csv");
				file.createNewFile();
				rescanFiles(currentlySelected);
						
			}catch(Exception e){
					
					
					
			}
					
		}
		
	}
	
	//Add action listeners
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
		commandBallDownButton.addActionListener(this);
		commandBallUpButton.addActionListener(this);
		commandBallOutButton.addActionListener(this);
		commandBallStopButton.addActionListener(this);
		commandDefenseDownButton.addActionListener(this);
		commandDefenseUpButton.addActionListener(this);
		
		tableUpButton.addActionListener(this);
		tableDownButton.addActionListener(this);
		tableDeleteButton.addActionListener(this);
		
		saveButton.addActionListener(this);
		testButton.addActionListener(this);
		helpButton.addActionListener(this);
		discardButton.addActionListener(this);
		
	}
	
	public static void main(String[] args){
		
		try{
			
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //Set theme (looks better on windows)
			
		}catch(Exception e){
			

			
		}
		
		Manager manager = new Manager(); //create the manager
		
		try{	
			
			manager.setIconImage(ImageIO.read(new File("./img/icon.png"))); //Set the icon
			
		}catch(Exception e){
			
			
			
		}
		
		manager.pack(); //Size only as big as it needs to be
		manager.setTitle("Autonomous Routine Manager"); //Set title for window
		manager.setLocationRelativeTo(null); //Center
		Point location = manager.getLocation();
		location.x -= 100;
		manager.setLocation(location);
		manager.setVisible(true); //Make window visible
		
		gyroSim = new GyroSim(manager.getHeight(), manager);
				
		manager.requestFocus();
		
		gyroSim.setGyroAngle(0);
		gyroSim.setAngleOffset(0);
		gyroSim.update();
		
	}

	//Window Listener functions	(Window closed)
	public void windowActivated(WindowEvent arg0) {}

	public void windowClosed(WindowEvent arg0) {}

	public void windowClosing(WindowEvent arg0) {
		
		//Setup Dialog
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
		dialog.requestFocus();
		
		if(canSave){
			
			dialog.setVisible(true);
			
		}else{
			
			System.exit(0);
			
		}
		
		//If yes pressed
		yesBtn.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
				
				saveToCSV();
				dialog.dispose();
				
			}
			
		});
		
		//If no pressed
		noBtn.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
				
				dialog.dispose();
				
			}
			
		});
		
		//backup when the dialog closes itself (dialog.dispose();)
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

	public void windowDeiconified(WindowEvent arg0) {
		
		gyroSim.setVisible(true);
		
	}

	public void windowIconified(WindowEvent arg0) {
		
		gyroSim.setVisible(false);
		
	}

	public void windowOpened(WindowEvent arg0) {}
	
	//ActionListener (Button Pressed)
	@Override
	public void actionPerformed(ActionEvent e){
		
		Object src = e.getSource(); //Get pressed item
		
		if(src == deleteFileButton){ //if delete file
			
			//Move to bakcups folder, but add time stamp to the name
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
				
				deleteRescanFiles();
				
			} catch (Exception ex) {
				
				
				
			}
			
		}else if(src == newFileButton){ //If new File
			
			new NewRoutineDialog(this); //Show New File Dialog
				
		}else if(src == refreshButton){ //If refresh
			
			rescanFiles(); //reload all files
			
		}else if(src == renameFileButton){ //If rename
			
			new RenameRoutineDialog(this, ((String) fileSelector.getSelectedItem())); // Rename dialog
			
		}else if(src == commandDriveButton){ //If drive command
			
			//Add table row with drive command
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			
			model.addRow(new String[]{"DRIVE", "6", "Inches"});
			table.clearSelection();
			
		}else if(src == commandRotateButton){ //If rotate command
			
			//Add table row with rotate
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			
			model.addRow(new String[]{"ROTATE_TO", "90", "Degrees"});
			table.clearSelection();
			
			rescanAngle();
			
		}else if(src == commandWaitButton){ //If wait command
			
			//Add table row with wait
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			table.clearSelection();
			
			model.addRow(new String[]{"WAIT", "5", "Seconds"});
			
		}else if(src == commandStopButton){ //If stop command
			
			//Add table row with stop
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			table.clearSelection();
			
			model.addRow(new String[]{"STOP", "", "None"});
			
		}else if(src == commandResetgyroButton){ //If reset gyro command
			
			//Add table row with reset gyro
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			table.clearSelection();
			
			model.addRow(new String[]{"RESET_GYRO", "", "None"});
			
		}else if(src == commandBallDownButton){ //If ball down command
			
			//Add table row with ball down
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			table.clearSelection();
			
			model.addRow(new String[]{"BALL_INTAKE_DOWN", "", "None"});
			
		}else if(src == commandBallUpButton){ //If ball up
			
			//Add table row with ball up
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			table.clearSelection();
			
			model.addRow(new String[]{"BALL_INTAKE_UP", "", "None"});
			
		}else if(src == commandBallOutButton){ //If ball out
			
			//Add table row with ball out
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			table.clearSelection();
			
			model.addRow(new String[]{"BALL_OUT", "", "None"});
			
		}else if(src == commandBallStopButton){ //If ball stop
			
			//Add table row with ball stop
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			table.clearSelection();
			
			model.addRow(new String[]{"BALL_STOP", "", "None"});
			
		}
		
		else if(src == commandDefenseDownButton){ //If defense down
			
			//Add table row with defense down
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			table.clearSelection();
			
			model.addRow(new String[]{"DEFENSE_DESTROYER_DOWN", "", "None"});
			
		}else if(src == commandDefenseUpButton){ //If defense up
			
			//Add table row with defense up
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			table.clearSelection();
			
			model.addRow(new String[]{"DEFENSE_DESTROYER_UP", "", "None"});
			
		}
		
		else if(src == tableUpButton){ //If up
			
			//Move row up
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			
			int i = table.getSelectedRow();
			
			model.moveRow(i, i, i - 1);
			
			table.setRowSelectionInterval(i - 1, i - 1);
			
		}else if(src == tableDownButton){ //If down
			
			//Move row down
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			
			int i = table.getSelectedRow();
			
			model.moveRow(i, i, i + 1);
			
			table.setRowSelectionInterval(i + 1, i + 1);
			
		}else if(src == tableDeleteButton){ //if delete
			
			//Add row for delete
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			
			int i = table.getSelectedRow();
			
			model.removeRow(i);
			
			table.setRowSelectionInterval(i - 1, i - 1);
			
		}else if(src == saveButton){ //if save
			
			saveToCSV(); //Save
			
		}else if (src == discardButton){ //If discard
			
			//comfirm dialog
			final JDialog dialog = new JDialog();
			JButton yesBtn = new JButton("Yes");
			JButton noBtn = new JButton("No");
			JPanel buttons = new JPanel();
			buttons.setLayout(new GridLayout(1, 2));
			buttons.add(yesBtn);
			buttons.add(noBtn);
			dialog.getContentPane().setLayout(new BorderLayout());
			dialog.getContentPane().add("Center", new JLabel("Discard all changes made to this file? WARNING:THIS CAN NOT BE UNDONE!"));
			dialog.getContentPane().add("South", buttons);
			dialog.setAlwaysOnTop(true);
			dialog.pack();
			dialog.setTitle("Discard?");
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
			dialog.requestFocus();
			
			yesBtn.addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent e){
					
					//Clear table
					DefaultTableModel model = (DefaultTableModel) table.getModel();
	    			   
	    			   for (int i = model.getRowCount() - 1; i >= 0; i--) {
	    			   	
	    				   model.removeRow(i);
	    			    
	    			   }
	    			    
	    			   loadFromCSV(); //reload the file
	    			   
	    			   dialog.dispose();
					
				}
				
			});
			
			noBtn.addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent e){
					
					dialog.dispose();
					
				}
				
			});
			
		}else if(src == testButton){
			
			saveToCSV();
			checkCSV();
			
		}else if(src == helpButton){
			
			showHelp();
			
		}
		
	}
	
	private void checkCSV(){
		
		CSVCheckEngine checker = new CSVCheckEngine(allCommands, argumentTypes);
		
		try{
			
			String errorData = checker.checkFile(new File(routinesFolder.getAbsolutePath() + "/" + fileSelector.getSelectedItem().toString() + ".csv"));
			
			if(errorData.isEmpty()){
				
				//Message Dialog
				final JDialog dialog = new JDialog();
				JButton okBtn = new JButton("OK");
				dialog.getContentPane().setLayout(new BorderLayout());
				dialog.getContentPane().add("Center", new JLabel("File OK."));
				dialog.getContentPane().add("South", okBtn);
				dialog.setAlwaysOnTop(true);
				dialog.pack();
				dialog.setTitle("File Check");
				dialog.setLocationRelativeTo(null);
				dialog.setVisible(true);
				dialog.requestFocus();
				okBtn.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						
						dialog.dispose();
						
					}
					
				});
				
			}else{
				
				//Message Dialog
				final JDialog dialog = new JDialog();
				JButton okBtn = new JButton("OK");
				dialog.getContentPane().setLayout(new BorderLayout());
				dialog.getContentPane().add("Center", new JScrollPane(new JTextArea("Errors Found in File:\n\n" + errorData)));
				dialog.getContentPane().add("South", okBtn);
				dialog.setAlwaysOnTop(true);
				dialog.pack();
				dialog.setTitle("File Check");
				dialog.setLocationRelativeTo(null);
				dialog.setVisible(true);
				dialog.requestFocus();
				okBtn.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						
						dialog.dispose();
						
					}
					
				});
				
			}
		
		}catch(Exception e){
			
			
			
		}
		
	}
	
	private void showHelp(){
		
		try{
			
			Desktop.getDesktop().browse(new File("html/help.html").toURI());
		
		}catch(Exception e){
			
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			
		}
		
	}
	
	private void saveToCSV(){
		
		if(canSave){
			
			if(!fileSelector.getSelectedItem().toString().equals("null")){
				
				table.clearSelection();
				table.getSelectionModel().clearSelection();
				
				try{
					
					String fileName = (String) fileSelector.getSelectedItem(); //Get file name
					
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					
					File outFile = new File(routinesFolder.getAbsolutePath() + "/" + fileName + ".csv"); //Create file
					
					//Clear the file
					outFile.delete(); 			
					outFile.createNewFile();
					
					BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
					
					//Write data to the file from the table
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
			
		}
		
	}
	
	private void loadFromCSV(){
		
		try{
			
			String fileName = fileSelector.getSelectedItem().toString(); //get file name
			
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			
			File inFile = new File(routinesFolder.getAbsolutePath() + "/" + fileName + ".csv"); //create file
			
			if(!(Files.isWritable(inFile.toPath()))){
				
				throw new IOException();
				
			}
			
			BufferedReader reader = new BufferedReader(new FileReader(inFile));
			
			ArrayList<String> lines = new ArrayList<String>();
			
			//Read file by lines into ArrayList
			while(reader.ready()){
				
				lines.add(reader.readLine());
				
			}
			
			//Separate the lines by ','s
			for(int row = 0; row < lines.size(); row++){
				
				String[] columns = lines.get(row).split(",");
				
				model.addRow(columns); //Add row from array of strings
				
			}
			
			reader.close();
					
			saveButton.setEnabled(true);
			testButton.setEnabled(true);
			discardButton.setEnabled(true);
			canSave = true;
			
		}catch(Exception e){
			
			final JDialog dialog = new JDialog();
			JButton okBtn = new JButton("OK");
			dialog.getContentPane().setLayout(new BorderLayout());
			dialog.getContentPane().add("Center", new JLabel("File is not accessible. Make sure driver station is not running."));
			dialog.getContentPane().add("South", okBtn);
			dialog.setAlwaysOnTop(true);
			dialog.pack();
			dialog.setTitle("File Error");
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
			dialog.requestFocus();
			okBtn.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					
					dialog.dispose();
					
				}
				
			});
			
			saveButton.setEnabled(false);
			testButton.setEnabled(false);
			discardButton.setEnabled(false);
			canSave = false;
			
		}
		
	}
	
	//ItemStateChangedListener (When drop-down changed)
	@Override
    public void itemStateChanged(ItemEvent e) {
		
		Object src = e.getSource();
		
       if(src == fileSelector){ //If file selector
    	   
    	   if (e.getStateChange() == ItemEvent.SELECTED) { //if something selected
        	   
    		   String newItem = (String) fileSelector.getSelectedItem();
    		   
    		   if(!newItem.equals(lastFileSelected)){ //if it is not the same
    			   
    			   //Clear the table
    			   DefaultTableModel model = (DefaultTableModel) table.getModel();
    			   
    			   for (int i = model.getRowCount() - 1; i >= 0; i--) {
    			   	
    				   model.removeRow(i);
    			    
    			   }
    			   
    			   //load the data
    			   loadFromCSV();
    			   
    			   //change last file
    			   lastFileSelected = newItem;
    			   
    			   if(gyroSim != null){
    				   
    				   gyroSim.setAngleOffset(0);
        			   gyroSim.setGyroAngle(0);
    				   
    			   }
    			   
    		   }
    	          
           }
    	   
       }
       
    }  
	
	//Allow only one row to be selected at a time
	public class ForcedListSelectionModel extends DefaultListSelectionModel {

		private static final long serialVersionUID = -7073835059132006928L;

		public ForcedListSelectionModel () {
	    	
	        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        
	    }

	    @Override
	    public void clearSelection() {
	    	
	    	super.clearSelection();
	    	
	    }

	    @Override
	    public void removeSelectionInterval(int index0, int index1) {
	    	
	    	super.removeSelectionInterval(index0, index1);
	    	
	    }

	}
	
	
	//Listen for drop-down opened
	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		
		if (!fileSelector.getSelectedItem().toString().equals("null"))
			saveToCSV(); //save before pop-up is visible
		
	}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {}
	
	private void rescanAngle(){
		
		int selectedRow = table.getSelectedRow();
		int minHeight = 0;
		boolean set = false;
		
		if(selectedRow == -1)
			selectedRow = table.getRowCount() - 1;
		
		int offset = 0;
		
		for(int i = selectedRow; i >= 0; i--){
			
			if(table.getModel().getValueAt(i, 0).toString().equals("RESET_GYRO")){
				
				minHeight = i;
				
				for(int j = i; j >= 0; j--){
					
					if(table.getModel().getValueAt(j, 0).toString().equals("ROTATE_TO")){
						
						set = true;
						offset = Integer.parseInt(table.getModel().getValueAt(j, 1).toString());
						gyroSim.setAngleOffset(offset);
						gyroSim.setGyroAngle(0);
						break;
						
					}
					
				}
				
			}
			
		}
		
		for(int i = selectedRow; i >= minHeight; i--){
			
			if(table.getModel().getValueAt(i, 0).toString().equals("ROTATE_TO") && !set){
				
				set = true;
				gyroSim.setGyroAngle(Integer.parseInt(table.getModel().getValueAt(i, 1).toString()));
				break;
				
			}
			
		}
			
		if(!set){
			
			gyroSim.setGyroAngle(0);
			
		}
		
	}
	
}
