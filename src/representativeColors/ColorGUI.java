package representativeColors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JScrollPane;

import java.awt.FlowLayout;

import javax.swing.JPasswordField;

import com.jcraft.jsch.JSchException;

import javax.swing.JSeparator;

public class ColorGUI {

	private JFrame frame;
	private JTextField vtTrackingStartField;
	private JTextField vtTrackingEndField;
	private JTextField directoryField;
	private JTextField sqlPathField;
	private JFileChooser chooser;
	
	private boolean debug = true;
	private DebugUtil debugUtil;
	
	int indexOfVisualOutput =0;
	int indexOfRGBOutput =0;
	int indexOfDebugOutput=0;

	final private LinkedList<String> selectedImages = new LinkedList<String>();
	private JTextField dbHostField;
	private JTextField dbNameField;
	private JTextField dbUsernameField;
	private JPasswordField dbPasswordField;
	private JTextField sshHostField;
	private JTextField sshUserField;
	private JTextField sshPortField;
	private JPasswordField sshPasswordField;
	private JTextField dbPortField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ColorGUI window = new ColorGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void recurseDirectory(File path, LinkedList<File> list) {
        if (!path.isDirectory()) {
            list.add(path);
            debugUtil.debug("Adding file: " + path.getAbsolutePath(), debug);
        }
        else {
            File[] subFiles = path.listFiles();
            for (File file : subFiles) {
                debugUtil.debug("Descending into directory: " + file.getAbsolutePath(), debug);
                recurseDirectory(file, list);
            }
        }
    }

	/**
	 * Create the application.
	 */
	public ColorGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		
		
		
		
		/*Initialize all Swing components */
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setSize(800,  500);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel selectImagesPanel = new JPanel();
		tabbedPane.addTab("Select Images", null, selectImagesPanel, null);
		selectImagesPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_3 = new JPanel();
		selectImagesPanel.add(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("Please select source of images");
		lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
		panel_3.add(lblNewLabel, BorderLayout.NORTH);
		
		JPanel panel_6 = new JPanel();
		panel_3.add(panel_6, BorderLayout.CENTER);
		panel_6.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_7 = new JPanel();
		panel_6.add(panel_7);
		panel_7.setLayout(new GridLayout(0, 1, 0, 0));
		
		ButtonGroup selectImportMethodGroup = new ButtonGroup();
		final JRadioButton databaseImportRadioButton = new JRadioButton("Import images directly from database");
		selectImportMethodGroup.add(databaseImportRadioButton);
		//panel_7.add(databaseImportRadioButton);
		
		JLabel lblNewLabel_1 = new JLabel("Limit VT_TRACKING ID to a specific range (Leave blank to show all):");
		panel_7.add(lblNewLabel_1);
		
		JPanel panel_9 = new JPanel();
		panel_7.add(panel_9);
		
		JLabel lblNewLabel_2 = new JLabel("Start");
		panel_9.add(lblNewLabel_2);
		
		vtTrackingStartField = new JTextField();
		

		
		panel_9.add(vtTrackingStartField);
		vtTrackingStartField.setColumns(15);
		
		JPanel panel_10 = new JPanel();
		panel_7.add(panel_10);
		
		JLabel lblNewLabel_3 = new JLabel("End");
		panel_10.add(lblNewLabel_3);
		
		vtTrackingEndField = new JTextField();
		
		
		
		panel_10.add(vtTrackingEndField);
		vtTrackingEndField.setColumns(15);
		
		JPanel panel_27 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_27.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel_7.add(panel_27);
		
		final JButton applyTrackingLimitButton = new JButton("Apply VT_TRACKING Limit");
		applyTrackingLimitButton.setHorizontalAlignment(SwingConstants.RIGHT);
		applyTrackingLimitButton.setEnabled(true);
		panel_27.add(applyTrackingLimitButton);
		
		JPanel panel_8 = new JPanel();
		//panel_6.add(panel_8);
		panel_8.setLayout(new GridLayout(0, 1, 0, 0));
		
		JRadioButton directoryImportRadioButton = new JRadioButton("Import images from folder on file system");
		selectImportMethodGroup.add(directoryImportRadioButton);
		directoryImportRadioButton.setSelected(true);
		panel_8.add(directoryImportRadioButton);
		
		
		
		
		
		JLabel lblNewLabel_4 = new JLabel("Select folder to import images from:");
		panel_8.add(lblNewLabel_4);
		
		JPanel panel_11 = new JPanel();
		panel_8.add(panel_11);
		
		JLabel lblNewLabel_5 = new JLabel("Folder path");
		panel_11.add(lblNewLabel_5);
		
		directoryField = new JTextField();
		panel_11.add(directoryField);
		directoryField.setColumns(10);
		
		final JButton directoryBrowseButton = new JButton("browse");
		panel_11.add(directoryBrowseButton);
		
		JPanel panel_12 = new JPanel();
		panel_8.add(panel_12);
		
		final JButton processImagesButton = new JButton("Process images");
		processImagesButton.setEnabled(false);
		
		
		
		// panel_12.add(processImagesButton);
		
		JPanel panel_4 = new JPanel();
		
		
		selectImagesPanel.add(panel_4);
		
		
		final DefaultListModel<String> imageListModel = new DefaultListModel();
		final JList imageList = new JList(imageListModel);
		imageList.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		final JScrollPane imageListScrollPane = new JScrollPane(imageList);
		
		imageListScrollPane.setPreferredSize(new Dimension(500, 200));
		
		
		JPanel selectAllPanel = new JPanel(new GridLayout(0, 1, 0, 0));
		
		final JButton selectAllButton = new JButton("Select All");
		selectAllButton.setEnabled(true);
		final JButton deselectAllButton = new JButton("Deselect All");
		deselectAllButton.setEnabled(true);
		
		selectAllPanel.add(selectAllButton);
		selectAllPanel.add(deselectAllButton);
		
		
		panel_4.add(selectAllPanel);
		
		
		
		panel_4.add(imageListScrollPane);
		panel_4.add(processImagesButton);
		
		
		JPanel outputPrefPanel = new JPanel();
		tabbedPane.addTab("Output Preferences", null, outputPrefPanel, null);
		outputPrefPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel = new JPanel();
		outputPrefPanel.add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel_6 = new JLabel("Select output location:");
		panel.add(lblNewLabel_6, BorderLayout.NORTH);
		
		JPanel panel_15 = new JPanel();
		panel.add(panel_15, BorderLayout.CENTER);
		panel_15.setLayout(new GridLayout(0, 1, 0, 0));
		
		
		ButtonGroup selectOutputRadioGroup = new ButtonGroup();
		final JRadioButton databaseOutputRadioButton = new JRadioButton("Output generated colors directly to database");
		selectOutputRadioGroup.add(databaseOutputRadioButton);
		panel_15.add(databaseOutputRadioButton);
		
		
		
		final JRadioButton sqlOutputRadioButton = new JRadioButton("Output generated colors to SQL file for future import");
		selectOutputRadioGroup.add(sqlOutputRadioButton);
		


		
		
		panel_15.add(sqlOutputRadioButton);
		
		JRadioButton sqlAndDatabaseRadioButton = new JRadioButton("Output to database and SQL file");
		selectOutputRadioGroup.add(sqlAndDatabaseRadioButton);
		panel_15.add(sqlAndDatabaseRadioButton);
		
		
		
		
		
		
		JPanel panel_19 = new JPanel();
		panel_15.add(panel_19);
		
		JLabel lblNewLabel_7 = new JLabel("SQL file path");
		panel_19.add(lblNewLabel_7);
		
		sqlPathField = new JTextField();
		
		
		
		panel_19.add(sqlPathField);
		sqlPathField.setColumns(20);
		
		final JButton sqlBrowseButton = new JButton("browse");
		panel_19.add(sqlBrowseButton);
		
		JPanel panel_14 = new JPanel();
		outputPrefPanel.add(panel_14);
		panel_14.setLayout(new BorderLayout(0, 0));
		
		JLabel lblOutputDisplaySettings = new JLabel("Output Display Settings");
		panel_14.add(lblOutputDisplaySettings, BorderLayout.NORTH);
		
		JPanel panel_18 = new JPanel();
		panel_14.add(panel_18, BorderLayout.CENTER);
		panel_18.setLayout(new GridLayout(0, 1, 0, 0));
		
		final JCheckBox showColorCheckbox = new JCheckBox("Visually show generated colors");
		panel_18.add(showColorCheckbox);
		
		final JCheckBox printColorCheckbox = new JCheckBox("Print RGB representation of colors");
		panel_18.add(printColorCheckbox);
		
		final JCheckBox debugOutputCheckbox = new JCheckBox("Debug output");
		panel_18.add(debugOutputCheckbox);
		
		
		
		
		
		
		
		JPanel panel_13 = new JPanel();
		outputPrefPanel.add(panel_13);
		panel_13.setLayout(new GridLayout(2, 1, 0, 0));
		
		JPanel panel_16 = new JPanel();
		panel_13.add(panel_16);
		panel_16.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel lblNumberOfRepresentative = new JLabel("Number of Representative Colors to generate per image:");
		panel_16.add(lblNumberOfRepresentative);
		
		JPanel spinnerPanel = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) spinnerPanel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		
		final JSpinner numColorSpinner = new JSpinner();
		numColorSpinner.setValue(10);
		
		
		
		numColorSpinner.setPreferredSize(new Dimension(100, 20));
		
		spinnerPanel.add(numColorSpinner);
		
		panel_16.add(spinnerPanel);
		
		JPanel panel_17 = new JPanel();
		panel_13.add(panel_17);
		panel_17.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel lblNewLabel_11 = new JLabel("Resize image to the following dimensions (Leave 0 for no resize): ");
		panel_17.add(lblNewLabel_11);
		
		JPanel widthPanel = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) widthPanel.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		JPanel heightPanel = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) heightPanel.getLayout();
		flowLayout_3.setAlignment(FlowLayout.LEFT);
		
		panel_17.add(widthPanel);
		
		JLabel lblHeiht = new JLabel("Width:");
		widthPanel.add(lblHeiht);
		
		final JSpinner widthSpinner = new JSpinner();
		widthSpinner.setValue(0);
		
		
		
		widthSpinner.setPreferredSize(new Dimension(100, 20));
		widthPanel.add(widthSpinner);
		panel_17.add(heightPanel);
		
		JLabel lblHeight = new JLabel("Height");
		heightPanel.add(lblHeight);
		
		final JSpinner heightSpinner = new JSpinner();
		heightSpinner.setValue(0);
		
	
		heightSpinner.setPreferredSize(new Dimension(100, 20));
		heightPanel.add(heightSpinner);
		
		Component horizontalStrut_3 = Box.createHorizontalStrut(400);
		heightPanel.add(horizontalStrut_3);
		
		JButton saveButton = new JButton("Save Current Settings");
		
		heightPanel.add(saveButton);
		
		JPanel colorCorrectPanel = new JPanel();
		tabbedPane.addTab("Color Correction", null, colorCorrectPanel, null);
		colorCorrectPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_21 = new JPanel();
		colorCorrectPanel.add(panel_21);
		panel_21.setLayout(new BorderLayout(0, 0));
		
		JLabel lblApplyTheFollowing = new JLabel("Apply the following color correction to all images");
		panel_21.add(lblApplyTheFollowing, BorderLayout.NORTH);
		
		JPanel panel_22 = new JPanel();
		panel_21.add(panel_22, BorderLayout.CENTER);
		panel_22.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_23 = new JPanel();
		panel_22.add(panel_23);
		
		JLabel lblNewLabel_8 = new JLabel("Hue");
		panel_23.add(lblNewLabel_8);
		
		final JSlider hueSlider = new JSlider();
		hueSlider.setValue(0);
		hueSlider.setMaximum(128);
		hueSlider.setMinimum(-128);
		
		
		
		hueSlider.setPreferredSize(new Dimension(300, 50));
		panel_23.add(hueSlider);
		
		final JSpinner hueSpinner = new JSpinner();
		hueSpinner.setValue(0);
		
		
		
		
		
		hueSpinner.setPreferredSize(new Dimension(300, 50));
		panel_23.add(hueSpinner);
		
		Component horizontalStrut = Box.createHorizontalStrut(200);
		panel_23.add(horizontalStrut);
		
		JPanel panel_24 = new JPanel();
		panel_22.add(panel_24);
		
		JLabel lblNewLabel_9 = new JLabel("Saturation");
		panel_24.add(lblNewLabel_9);
		
		final JSlider satSlider = new JSlider();
		satSlider.setValue(0);
		satSlider.setMaximum(128);
		satSlider.setMinimum(-128);
		satSlider.setPreferredSize(new Dimension(300, 50));
		panel_24.add(satSlider);
		
		final JSpinner satSpinner = new JSpinner();
		satSpinner.setPreferredSize(new Dimension(300, 50));
		
		
		
		panel_24.add(satSpinner);
		
		
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(241);
		panel_24.add(horizontalStrut_1);
		
		JPanel panel_25 = new JPanel();
		panel_22.add(panel_25);
		
		JLabel lblNewLabel_10 = new JLabel("Value");
		panel_25.add(lblNewLabel_10);
		
		final JSlider valSlider = new JSlider();
		valSlider.setValue(0);
		valSlider.setMaximum(128);
		valSlider.setMinimum(-128);
		valSlider.setPreferredSize(new Dimension(300, 50));
		panel_25.add(valSlider);
		
		final JSpinner valSpinner = new JSpinner();
		valSpinner.setPreferredSize(new Dimension(300, 50));
		

		
		panel_25.add(valSpinner);
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(215);
		panel_25.add(horizontalStrut_2);
		
		JPanel panel_20 = new JPanel();
		colorCorrectPanel.add(panel_20);
		
		JPanel dbConfigPanel = new JPanel();
		tabbedPane.addTab("Database Configuration", null, dbConfigPanel, null);
		dbConfigPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel sshInfo = new JPanel();
		dbConfigPanel.add(sshInfo);
		sshInfo.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_2 = new JPanel();
		sshInfo.add(panel_2);
		
		JLabel lblPleaseEnterYour = new JLabel("Please enter your SSH information:");
		panel_2.add(lblPleaseEnterYour);
		
		JPanel panel_32 = new JPanel();
		sshInfo.add(panel_32);
		
		JLabel lblSshHost = new JLabel("SSH Host:");
		panel_32.add(lblSshHost);
		
		JSeparator separator = new JSeparator();
		panel_32.add(separator);
		
		sshHostField = new JTextField();
		//sshHostField.setText(configMap.get(0));
		

		
		panel_32.add(sshHostField);
		sshHostField.setColumns(10);
		
		JPanel panel_34 = new JPanel();
		sshInfo.add(panel_34);
		
		JLabel lblSshPort = new JLabel("SSH Port: ");
		panel_34.add(lblSshPort);
		
		sshPortField = new JTextField();
		//sshPortField.setText(configMap.get(1));
		

		
		
		
		panel_34.add(sshPortField);
		sshPortField.setColumns(10);
		
		JPanel panel_31 = new JPanel();
		sshInfo.add(panel_31);
		
		JLabel lblSshUsername = new JLabel("SSH Username:");
		panel_31.add(lblSshUsername);
		
		sshUserField = new JTextField();
		//sshUserField.setText(configMap.get(2));
		
	
		
		
		panel_31.add(sshUserField);
		sshUserField.setColumns(10);
		
		JPanel panel_33 = new JPanel();
		sshInfo.add(panel_33);
		
		JLabel lblSshPassword = new JLabel("SSH Password: ");
		panel_33.add(lblSshPassword);
		
		sshPasswordField = new JPasswordField();
		sshPasswordField.setColumns(10);
		//sshPasswordField.setText(configMap.get(3));
		

		
		panel_33.add(sshPasswordField);
		
		JPanel panel_1 = new JPanel();
		dbConfigPanel.add(panel_1);
		panel_1.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_5 = new JPanel();
		panel_1.add(panel_5);
		
		JLabel lblPleaseEnterDatabase = new JLabel("Please enter Database information:");
		panel_5.add(lblPleaseEnterDatabase);
		
		JPanel panel_26 = new JPanel();
		panel_1.add(panel_26);
		
		JLabel lblHost = new JLabel("Host:");
		panel_26.add(lblHost);
		
		dbHostField = new JTextField();
		//dbHostField.setText(configMap.get(4));
		

		
		
		panel_26.add(dbHostField);
		dbHostField.setColumns(10);
		
		JPanel panel_35 = new JPanel();
		panel_1.add(panel_35);
		
		JLabel lblPort = new JLabel("Port:");
		panel_35.add(lblPort);
		
		dbPortField = new JTextField();
		//dbPortField.setText(configMap.get(5));
		


		
		
		panel_35.add(dbPortField);
		dbPortField.setColumns(10);
		
		JPanel panel_28 = new JPanel();
		panel_1.add(panel_28);
		
		JLabel lblDbName = new JLabel("DB Name:");
		panel_28.add(lblDbName);
		
		dbNameField = new JTextField();
		//dbNameField.setText(configMap.get(6));
		


		
		
		panel_28.add(dbNameField);
		dbNameField.setColumns(10);
		
		JPanel panel_29 = new JPanel();
		panel_1.add(panel_29);
		
		JLabel lblUsername = new JLabel("Username: ");
		panel_29.add(lblUsername);
		
		dbUsernameField = new JTextField();
		//dbUsernameField.setText(configMap.get(7));
		


		
		
		panel_29.add(dbUsernameField);
		dbUsernameField.setColumns(10);
		
		JPanel panel_30 = new JPanel();
		panel_1.add(panel_30);
		
		JLabel lblPassword = new JLabel("Password:");
		panel_30.add(lblPassword);
		
		dbPasswordField = new JPasswordField();
		//dbPasswordField.setText(configMap.get(8));
		


		
		
		dbPasswordField.setColumns(10);
		panel_30.add(dbPasswordField);
		
		final JPanel viewColorPanel = new JPanel();
		
		
		
		
		
		
		viewColorPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		
		
		
		//panel_26.add(outputColorScrollPane);
	
		
		final JPanel debugPanel = new JPanel();
		
		
		final JTextArea debugTextArea = new JTextArea();
		debugPanel.add(debugTextArea);
		
		JButton clearButton = new JButton("Clear Debug Text");
		debugPanel.add(clearButton);
		
		 debugUtil = new DebugUtil(debugTextArea);
		 
		 final JPanel rgbOutputPanel = new JPanel();
		 

		 
		 final JTextArea textOutputArea = new JTextArea();
		
		 final JScrollPane outputScrollPane = new JScrollPane(textOutputArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		 outputScrollPane.setPreferredSize(new Dimension(450, 350));
		 
		 JButton rgbClearButton = new JButton("Clear RGB text");
		 rgbOutputPanel.add(rgbClearButton);
		 rgbOutputPanel.add(outputScrollPane);
		 
		 
		 final JScrollPane debugScrollPane = new JScrollPane(debugTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		 debugScrollPane.setPreferredSize(new Dimension(450, 350));
		 debugPanel.add(debugScrollPane);
		 
		 
		 
		 
		 /*We see if there's a file with saved configuration settings and load it in if there is*/
		 /*Saved settings will be available in configMap hashmap */
		 HashMap<String, String> configMap = new HashMap();
		 try {
			FileInputStream dbConf = new FileInputStream("config.txt");
			BufferedReader dbConfReader = new BufferedReader(new InputStreamReader(dbConf));
			String line;
			String[] parts;
			String name;
			String value;
			while ((line = dbConfReader.readLine()) != null) {
				if (line.split(":").length>1) {
					parts = line.split(":");
					name = parts[0];
					value = parts[1];
					configMap.put(name, value);
					
				
				}
			}
			
			dbConfReader.close();
			

			
		 } catch (FileNotFoundException e1) {
			/*If the file doesn't exist we don't have to do anything*/
			debugUtil.debug("No config file fount...", debug);

		 } catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "Config file is not formatted correctly. Ignoring...");


		 }
		 
		 
		 /*Now we apply all of the settings that were in the file*/
	 
		 if (configMap.get("end")!=null) {
			vtTrackingEndField.setText(configMap.get("end"));
		 }
		 
		 if (configMap.get("output")!=null) {
			String output = configMap.get("output");
			if (output.equals("sql")) {
				sqlOutputRadioButton.setSelected(true);
			}
			else if (output.equals("db")) {
				databaseOutputRadioButton.setSelected(true);
			}
			else if (output.equals("both")) {
				sqlAndDatabaseRadioButton.setSelected(true);
			}
			else {
				databaseOutputRadioButton.setSelected(true);
			}
		 } else databaseOutputRadioButton.setSelected(true);
		 
		 if (configMap.get("sqlPath")!=null) {
			sqlPathField.setText(configMap.get("sqlPath"));
		 }
		 
		 if (configMap.containsKey("numColors")) {
			numColorSpinner.setValue(Integer.parseInt(configMap.get("numColors")));
		 }
		 
		 if (configMap.containsKey("width")) {
			widthSpinner.setValue(Integer.parseInt(configMap.get("width")));
		 }
		 
		 if (configMap.containsKey("height")) {
			heightSpinner.setValue(Integer.parseInt(configMap.get("height")));
		 }
			
		 
		 if (configMap.containsKey("hue")) {
			hueSpinner.setValue(Integer.parseInt(configMap.get("hue")));
			hueSlider.setValue(Integer.parseInt(configMap.get("hue")));
		 }
		 
		 if (configMap.containsKey("sat")) {
			satSlider.setValue(Integer.parseInt(configMap.get("sat")));
			satSpinner.setValue(Integer.parseInt(configMap.get("sat")));
		 }
		 
		 if (configMap.containsKey("val")) {
		   	valSpinner.setValue(Integer.parseInt(configMap.get("val")));
			valSlider.setValue(Integer.parseInt(configMap.get("val")));
		 }
		
		 if (configMap.get("sshHost")!=null) {
			sshHostField.setText(configMap.get("sshHost"));
		 }
		
		 if (configMap.get("sshPort")!=null) {
			sshPortField.setText(configMap.get("sshPort"));
		 }
		
		 if (configMap.get("sshUser")!=null) {
			sshUserField.setText(configMap.get("sshUser"));
		 }
		
		 if (configMap.get("sshPassword")!=null) {
			sshPasswordField.setText(configMap.get("sshPassword"));
		 }
		
		 if (configMap.get("dbHost")!=null) {
			dbHostField.setText(configMap.get("dbHost"));
		 }
		
		 if (configMap.get("dbPort")!=null) {
			dbPortField.setText(configMap.get("dbPort"));
		 }
		
		 if (configMap.get("dbName")!=null) {
			dbNameField.setText(configMap.get("dbName"));
		 }
		
		 if (configMap.get("dbUser")!=null) {
			dbUsernameField.setText(configMap.get("dbUser"));
		 }
		
		 if (configMap.get("dbPassword")!=null) {
			dbPasswordField.setText(configMap.get("dbPassword"));
		 }
		
		 if (configMap.get("start")!=null) {
			vtTrackingStartField.setText(configMap.get("start"));
		 }
		 
		 /*For RGB, visual and debug - if we check them off, we must also add them as tabs to*/
		 /*the interface and remember their position so that we may remove them later if we have to*/
		 if (configMap.get("rgb")!=null) {
				if (configMap.get("rgb").equals("1")){
					printColorCheckbox.setSelected(true);
					indexOfRGBOutput = tabbedPane.getTabCount();
					tabbedPane.addTab("RGB Output", null, rgbOutputPanel, null);
				}
				
				else
					printColorCheckbox.setSelected(false);
			}
			if (configMap.get("visual")!=null) {
				if (configMap.get("visual").equals("1")) {
						showColorCheckbox.setSelected(true);
						indexOfVisualOutput = tabbedPane.getTabCount();
						tabbedPane.addTab("Visual Output", null, viewColorPanel, null);
				}
				else
					showColorCheckbox.setSelected(false);
			}
			if (configMap.get("debug")!=null) {
				if (configMap.get("debug").equals("1")) {
					debugOutputCheckbox.setSelected(true);
					indexOfDebugOutput = tabbedPane.getTabCount();
					tabbedPane.addTab("Debug", null, debugPanel, null);
					debug = true;
				}
				else
					debugOutputCheckbox.setSelected(false);
			}
		 
		 
		/*Add a listener to clear the debug listener*/
		 clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				debugTextArea.setText("");
				
			}
			 
		 });
		 
		 rgbClearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				textOutputArea.setText("");
				
			}
			 
		 });
		 
		 
		
		
		/*Open a file system browser window to choose where to save the sql file*/
		sqlBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooser = new JFileChooser();
                int retval = chooser.showSaveDialog(null);
                if (retval == JFileChooser.APPROVE_OPTION) {
                	sqlPathField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
		
		
		
		
		//If we want to output directly to the database, we do not need to input an sql filename
		databaseOutputRadioButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sqlPathField.setEnabled(false);
				sqlBrowseButton.setEnabled(false);
				
			}
			
		});
		
		//If we want to output to an SQL file, we need to put an sql filename
		sqlOutputRadioButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sqlPathField.setEnabled(true);
				sqlBrowseButton.setEnabled(true);
				
			}
			
		});
		
		//If we want to output to an SQL file, we need to put an sql filename
		sqlAndDatabaseRadioButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sqlPathField.setEnabled(true);
				sqlBrowseButton.setEnabled(true);
			}
			
		});
		
		
		//Make sure hue/sat/val sliders match their respective spinners
		hueSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				hueSpinner.setValue(hueSlider.getValue());
				
			}
			
		});
		
		hueSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				hueSlider.setValue((int) hueSpinner.getValue());
			}
		});
		
		satSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				satSpinner.setValue(satSlider.getValue());
				
			}
			
		});
		
		satSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				satSlider.setValue((int) satSpinner.getValue());
			}
		});
		
		valSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				valSpinner.setValue(valSlider.getValue());
				
			}
			
		});
		
		valSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				valSlider.setValue((int) valSpinner.getValue());
			}
		});
		
		
		//We make sure that the 'process' button is only active if there are images selected
		imageList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				while (!selectedImages.isEmpty()) {
			        selectedImages.removeFirst();
			    }
				
				for (Object trackNum : imageList.getSelectedValuesList()) {
					selectedImages.add((String)trackNum);
				}
				
				if (!selectedImages.isEmpty()) {
					processImagesButton.setEnabled(true);
				}
				else {
					processImagesButton.setEnabled(false);
				}
			}
			
		});
		
		
		
		//Retrieve all vt tracking numbers within the given limits
		applyTrackingLimitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String trackingID;
				
				vtTrackingStartField.setEnabled(true);
				vtTrackingEndField.setEnabled(true);
				
				directoryField.setEnabled(false);
				directoryBrowseButton.setEnabled(false);
				
								
				ImageProc proc;
				try {
					proc = new ImageProc(sshHostField.getText(), sshPortField.getText(), sshUserField.getText(), 
							new String(sshPasswordField.getPassword()), dbHostField.getText(), dbPortField.getText(), dbNameField.getText(), 
							dbUsernameField.getText(), new String(dbPasswordField.getPassword()), 
							"textile_img", debug, debugUtil);
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, "Could not connect to MySQL!");
					e1.printStackTrace();
					return;
				} catch (JSchException e1) {
					JOptionPane.showMessageDialog(null, "SSH failed. Could not connect to server!");
					e1.printStackTrace();
					return;
				}
				proc.fetchImageIds("img_detail", vtTrackingStartField.getText(), vtTrackingEndField.getText());
			
				
				imageListModel.clear();
				while (proc.hasNextImageID()) {
					try {
						trackingID = proc.nextImageID();
						imageListModel.addElement(trackingID);
					} catch (SQLException e1) {
						debugUtil.error(e1.getMessage());
					}
					
				}
				
				try {
					proc.endConnection();
				} catch(NullPointerException e1) {
					
				}
				
				
			}
			
		});
		
		
		//If we want to visually see colors, we need to add a new tab to do this
		showColorCheckbox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (showColorCheckbox.isSelected()) {
					indexOfVisualOutput = tabbedPane.getTabCount();
					tabbedPane.addTab("Visual Output", null, viewColorPanel, null);
				}
				else {
					tabbedPane.removeTabAt(indexOfVisualOutput);
					
					if (indexOfRGBOutput>indexOfVisualOutput) {
						indexOfRGBOutput--;
					}
					if (indexOfDebugOutput>indexOfVisualOutput) {
						indexOfDebugOutput--;
					}
					
				}
				
			}
			
		});
		
		
		//If we want RGB values - make a new tab to show this
		printColorCheckbox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (printColorCheckbox.isSelected()) {
					indexOfRGBOutput = tabbedPane.getTabCount();
					tabbedPane.addTab("RGB Output", null, rgbOutputPanel, null);
				}
				else {
					tabbedPane.removeTabAt(indexOfRGBOutput);
					
					if (indexOfVisualOutput>indexOfRGBOutput) {
						indexOfVisualOutput--;
					}
					if (indexOfDebugOutput>indexOfRGBOutput) {
						indexOfDebugOutput--;
					}
					
				}
				
			}
			
		});
		
		
		//If we want to see the debug info, add a new tab for this
		debugOutputCheckbox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (debugOutputCheckbox.isSelected()) {
					indexOfDebugOutput = tabbedPane.getTabCount();
					tabbedPane.addTab("Debug", null, debugPanel, null);
					debug = true;
				}
				else {
					debug=false;
					tabbedPane.removeTabAt(indexOfDebugOutput);
					
					if (indexOfVisualOutput>indexOfDebugOutput) {
						indexOfVisualOutput--;
					}
					
					if (indexOfRGBOutput>indexOfDebugOutput) {
						indexOfRGBOutput--;
					}
					
					
				}
				
			}
			
		});
		
		
		/*Button to select all images in the currently visible list*/
		selectAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultListModel model = (DefaultListModel) imageList.getModel();
				int start=0;
				int end = model.getSize()-1;
				
				if (end>0) {
					imageList.setSelectionInterval(start, end);
				}
				
			}
			
		});
		
		/*Button to deselect all images in the currently visible list*/
		deselectAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				imageList.clearSelection();
				
			}
			
		});
		
		/*Button to save all current settings*/
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					//Simply write all settings to 'config.txt'
					PrintWriter writer = new PrintWriter("config.txt", "UTF-8");
					writer.println("sshHost:"+sshHostField.getText());
					writer.println("sshPort:"+sshPortField.getText());
					writer.println("sshUser:"+sshUserField.getText());
					writer.println("sshPassword:"+new String(sshPasswordField.getPassword()));
					
					writer.println("dbHost:"+dbHostField.getText());
					writer.println("dbPort:"+dbPortField.getText());
					writer.println("dbUser:"+dbUsernameField.getText());
					writer.println("dbName:"+dbNameField.getText());
					writer.println("dbPassword:"+new String(dbPasswordField.getPassword()));
					
					if (databaseOutputRadioButton.isSelected()) {
						writer.println("output:db");
					}
					else if (sqlOutputRadioButton.isSelected()) {
						writer.println("output:sql");
					}
					else {
						writer.println("output:both");
					}
					
					
					if(showColorCheckbox.isSelected()) {
						writer.println("visual:1");
					}
					if(printColorCheckbox.isSelected()) {
						writer.println("rgb:1");
					}
					if (debugOutputCheckbox.isSelected()) {
						writer.println("debug:1");
					}
					
					writer.println("width:"+widthSpinner.getValue());
					writer.println("height:"+heightSpinner.getValue());
					
					writer.println("start:"+vtTrackingStartField.getText());
					writer.println("end:"+vtTrackingEndField.getText());
					
					writer.println("sqlPath:"+sqlPathField.getText());
					
					writer.println("numColors:"+ numColorSpinner.getValue());
					
					writer.println("hue:"+hueSlider.getValue());
					writer.println("sat:"+satSlider.getValue());
					writer.println("val:"+valSlider.getValue());
					
					writer.close();
					
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(null, "Could not create settings file!");
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					JOptionPane.showMessageDialog(null, "UTF-8 not supported!");
					e1.printStackTrace();
				}
			}
		});
		
		
		
		//Process button will process the images with all of the selected options
		processImagesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//If we want to generate an SQL file but haven't given a path, then abort
				if (!databaseOutputRadioButton.isSelected() && sqlPathField.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please input a path to generate a .sql file in Output Preferences");
					return;
				}
				
				
				
				
				int hOffset, sOffset, vOffset;
                hOffset = hueSlider.getValue();
                sOffset = satSlider.getValue();
                vOffset = valSlider.getValue();
                
                debug = debugOutputCheckbox.isSelected();
                RepColors col = new RepColors(debugUtil);
                col.debug = debug;
               
                ImageProc proc;
				try {
					proc = new ImageProc(sshHostField.getText(), sshPortField.getText(), sshUserField.getText(), 
							new String(sshPasswordField.getPassword()), dbHostField.getText(), dbPortField.getText(), dbNameField.getText(), 
							dbUsernameField.getText(), new String(dbPasswordField.getPassword()), 
							"textile_img", debug, debugUtil);
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, "Could not connect to MySQL!");
					e1.printStackTrace();
					return;
				} catch (JSchException e1) {
					JOptionPane.showMessageDialog(null, "SSH failed. Could not connect to server!");
					e1.printStackTrace();
					return;
				}
                col.refColors = proc.loadRefColors("color_detail");
                int numColors = (int) numColorSpinner.getValue();

                //Fetch images from database or directory
                LinkedList<ImageItem> images = new LinkedList<>();
                // if (databaseImportRadioButton.isSelected()) {
                    proc.fetchImageList("img_detail", selectedImages);
                    while (proc.hasNextImage()) {
                        try {
							images.add(proc.nextImage((Integer)widthSpinner.getValue(), (Integer)heightSpinner.getValue()));
						} catch (SQLException e1) {
							debugUtil.error(e1.getMessage());
						}
                    }
 
                
                textOutputArea.setText("");
                
                
                //Process images and insert into database or write to file
                try {
                	JPanel colorPanel;
                	BufferedWriter out;
                	if (!databaseOutputRadioButton.isSelected()) {
                			out = new BufferedWriter(new FileWriter(sqlPathField.getText()));
                	}
                	else
                		out = null;
                    
                    viewColorPanel.removeAll();
                    
                    //Insert the files or create the dump
                    for (ImageItem imageItem : images) {
                    	if (imageItem!=null && imageItem.image!=null) {
                    		debugUtil.debug("Processing image " + imageItem.vt_tracking, debug);
                    		LinkedList<ColorCount> colors = col.processImage(imageItem, hOffset, sOffset, vOffset);
                    		
                    		
                    		//Print out RGB values of colors
                    		if (printColorCheckbox.isSelected()) {
                    			textOutputArea.append("Textile: " + imageItem.vt_tracking + "\n");
                    			for (int i=0; i<numColors; i++) {
                    				textOutputArea.append(colors.get(i).toString() + ", \n");
                    			}
                    		}
                    		
                    		//Visually display the colors
                    		if (showColorCheckbox.isSelected()) {
                        		ColorCount curColor;
                        		JPanel colorRow = new JPanel();
                        	
                        		colorRow.setLayout(new GridLayout());
                        		JLabel colorLabel = new JLabel(imageItem.vt_tracking);
                        		colorRow.add(colorLabel);
 
                        		
                        		for (int i=0; i<Math.min(numColors, 10); i++) {
                        			curColor = colors.get(i);
	                            	colorPanel = new JPanel();
	                            	colorPanel.setLayout(new GridLayout(1, 0, 0, 0));
	                            	colorPanel.setBackground(curColor.c.color);
	                            	colorPanel.setPreferredSize(new Dimension(50, 50));
	                            	colorRow.add(colorPanel);
	                            	
	                            }
	                            viewColorPanel.add(colorRow);
                    		}
                            
                            
                            if (debug) {
                                String colorString = "\t";
                                int i = 0;
                                for (ColorCount colorCount: colors) {
                                    colorString += "\t" + colorCount.toString() + "\n";
                                    i++;
                                    if (i > numColors) break;
                                }
                                debugUtil.debug("Found colors:\n" + colorString, debug);
                            }
                            
                            
                            //Do whatever the user asked in their input options - make SQL file and insert
                            // directly into DB
                            if (!databaseOutputRadioButton.isSelected()) {
                            	col.writeColors(proc, numColors, colors, out);
                            }
                            if (!sqlOutputRadioButton.isSelected()) {
                            	col.insertColors(proc, numColors, colors);
                            }
                           
                    	}
                    	
                    	
                    }
                    if (!databaseOutputRadioButton.isSelected() && out!=null) {
                    	out.close();
                    	proc.endConnection();
                    }
                }
                catch (IOException er ) {
                    debugUtil.error("Could not open SQL script file for writing.");
                    JOptionPane.showMessageDialog(null, "Could not open SQL script file for writing");
                    proc.endConnection();
                    if (debug) er.printStackTrace();
                }
                
                try {
                	proc.endConnection();
                } catch(NullPointerException e1) {
                	
                }

                
                
			}
		});
				
	}

}
