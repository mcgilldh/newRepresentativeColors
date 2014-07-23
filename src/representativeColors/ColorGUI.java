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
		//Get dbconfig info:
		LinkedList<String> dbInfo = new LinkedList<String>();
		try {
			FileInputStream dbConf = new FileInputStream("dbconfig.txt");
			BufferedReader dbConfReader = new BufferedReader(new InputStreamReader(dbConf));
			String line;
			
			while ((line = dbConfReader.readLine()) != null) {
				dbInfo.add(line);
			}
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "Could not find dbconfig.txt. Aborting!");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "Could not read dbconfig.txt. Aborting!");
		}
		
		
		
		
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
		panel_6.setLayout(new GridLayout(0, 2, 0, 0));
		
		JPanel panel_7 = new JPanel();
		panel_6.add(panel_7);
		panel_7.setLayout(new GridLayout(0, 1, 0, 0));
		
		ButtonGroup selectImportMethodGroup = new ButtonGroup();
		final JRadioButton databaseImportRadioButton = new JRadioButton("Import images directly from database");
		selectImportMethodGroup.add(databaseImportRadioButton);
		panel_7.add(databaseImportRadioButton);
		
		JLabel lblNewLabel_1 = new JLabel("Limit VT_TRACKING ID to a specific range:");
		panel_7.add(lblNewLabel_1);
		
		JPanel panel_9 = new JPanel();
		panel_7.add(panel_9);
		
		JLabel lblNewLabel_2 = new JLabel("Start");
		panel_9.add(lblNewLabel_2);
		
		vtTrackingStartField = new JTextField();
		vtTrackingStartField.setEnabled(false);
		panel_9.add(vtTrackingStartField);
		vtTrackingStartField.setColumns(15);
		
		JPanel panel_10 = new JPanel();
		panel_7.add(panel_10);
		
		JLabel lblNewLabel_3 = new JLabel("End");
		panel_10.add(lblNewLabel_3);
		
		vtTrackingEndField = new JTextField();
		vtTrackingEndField.setEnabled(false);
		panel_10.add(vtTrackingEndField);
		vtTrackingEndField.setColumns(15);
		
		JPanel panel_27 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_27.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel_7.add(panel_27);
		
		final JButton applyTrackingLimitButton = new JButton("Apply VT_TRACKING Limit");
		applyTrackingLimitButton.setHorizontalAlignment(SwingConstants.RIGHT);
		applyTrackingLimitButton.setEnabled(false);
		panel_27.add(applyTrackingLimitButton);
		
		JPanel panel_8 = new JPanel();
		panel_6.add(panel_8);
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
		sqlOutputRadioButton.setSelected(true);
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
		
		final JSpinner numColorSpinner = new JSpinner();
		numColorSpinner.setValue(10);
		panel_16.add(numColorSpinner);
		
		JPanel panel_17 = new JPanel();
		panel_13.add(panel_17);
		
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
		dbHostField.setText(dbInfo.get(0));
		panel_26.add(dbHostField);
		dbHostField.setColumns(10);
		
		JPanel panel_28 = new JPanel();
		panel_1.add(panel_28);
		
		JLabel lblDbName = new JLabel("DB Name:");
		panel_28.add(lblDbName);
		
		dbNameField = new JTextField();
		dbNameField.setText(dbInfo.get(1));
		panel_28.add(dbNameField);
		dbNameField.setColumns(10);
		
		JPanel panel_29 = new JPanel();
		panel_1.add(panel_29);
		
		JLabel lblUsername = new JLabel("Username: ");
		panel_29.add(lblUsername);
		
		dbUsernameField = new JTextField();
		dbUsernameField.setText(dbInfo.get(2));
		panel_29.add(dbUsernameField);
		dbUsernameField.setColumns(10);
		
		JPanel panel_30 = new JPanel();
		panel_1.add(panel_30);
		
		JLabel lblPassword = new JLabel("Password:");
		panel_30.add(lblPassword);
		
		dbPasswordField = new JPasswordField();
		dbPasswordField.setText(dbInfo.get(3));
		dbPasswordField.setColumns(10);
		panel_30.add(dbPasswordField);
		
		JPanel panel_2 = new JPanel();
		dbConfigPanel.add(panel_2);
		
		final JPanel viewColorPanel = new JPanel();
		
		
		
		
		
		
		viewColorPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		
		
		
		//panel_26.add(outputColorScrollPane);
	
		
		final JPanel debugPanel = new JPanel();
		
		
		JTextArea debugTextArea = new JTextArea();
		debugPanel.add(debugTextArea);
		
		 debugUtil = new DebugUtil(debugTextArea);
		 
		 final JPanel rgbOutputPanel = new JPanel();
		 

		 
		 final JTextArea textOutputArea = new JTextArea();
		
		 final JScrollPane outputScrollPane = new JScrollPane(textOutputArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		 outputScrollPane.setPreferredSize(new Dimension(450, 350));
		 rgbOutputPanel.add(outputScrollPane);
		 
		 
		 final JScrollPane debugScrollPane = new JScrollPane(debugTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		 debugScrollPane.setPreferredSize(new Dimension(450, 350));
		 debugPanel.add(debugScrollPane);
		 
		 
		 
		 
		 
		//Create the browse windows for choosing a directory to import or an SQL file to save to
		directoryBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                int retval = chooser.showOpenDialog(null);
                if (retval == JFileChooser.APPROVE_OPTION) {
                    directoryField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
                
                LinkedList<File> files = new LinkedList<>();
                recurseDirectory(new File(directoryField.getText()), files);
                
                if (!files.isEmpty()) {
                	processImagesButton.setEnabled(true);
                }
                else {
                	processImagesButton.setEnabled(false);
                }
            }
        });
		sqlBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooser = new JFileChooser();
                int retval = chooser.showSaveDialog(null);
                if (retval == JFileChooser.APPROVE_OPTION) {
                	sqlPathField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
		
		
		//If we are importing a directory, disable the useless components
		directoryImportRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				directoryField.setEnabled(true);
				directoryBrowseButton.setEnabled(true);
				
				vtTrackingStartField.setEnabled(false);
				vtTrackingEndField.setEnabled(false);
				applyTrackingLimitButton.setEnabled(false);
				DefaultListModel listModel = (DefaultListModel) imageList.getModel();
				listModel.removeAllElements();
			}
		});
		
		
		//If we are importing from a database, disable useless components and populate the image list
		databaseImportRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImageItem image;
				
				vtTrackingStartField.setEnabled(true);
				vtTrackingEndField.setEnabled(true);
				applyTrackingLimitButton.setEnabled(true);
				
				directoryField.setEnabled(false);
				directoryBrowseButton.setEnabled(false);
				
				
				
				
				//Get all of the images that we can actually find files for and add them to the image
				//selection list
				ImageProc proc = new ImageProc(dbHostField.getText(), dbNameField.getText(), dbUsernameField.getText(),
						new String(dbPasswordField.getPassword()), "textile_img", debug, debugUtil);
				
				proc.fetchImages("img_detail", "", "");
			
				
				imageListModel.clear();
				while (proc.hasNextImage()) {
					image = proc.nextImage();
					if (image!=null) {
						imageListModel.addElement(image.vt_tracking);
					}
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
		
		
		//Make sure hue sliders match hue spinners
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
		
		
		//If we select images from our list, enable the processing button, otherwise disable it
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
				ImageItem image;
				
				vtTrackingStartField.setEnabled(true);
				vtTrackingEndField.setEnabled(true);
				
				directoryField.setEnabled(false);
				directoryBrowseButton.setEnabled(false);
				
				
				//ImageProc proc = new ImageProc("localhost:8889", "vtmast_dev", "root", "root", "Textile_img", debug, debugUtil);
				
				ImageProc proc = new ImageProc(dbHostField.getText(), dbNameField.getText(), dbUsernameField.getText(),
						new String(dbPasswordField.getPassword()), "textile_img", debug, debugUtil);
				proc.fetchImages("img_detail", vtTrackingStartField.getText(), vtTrackingEndField.getText());
				int count = 0;
				
				imageListModel.clear();
				while (proc.hasNextImage()) {
					image = proc.nextImage();
					if (image!=null) {
						imageListModel.addElement(image.vt_tracking);
						System.out.println(image.vt_tracking);
						count++;
					}
				}
				System.out.println(count);
				
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
		
		
		
		//Process button will process the images with all of the selected options
		processImagesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int hOffset, sOffset, vOffset;
                hOffset = hueSlider.getValue();
                sOffset = satSlider.getValue();
                vOffset = valSlider.getValue();
                
                debug = debugOutputCheckbox.isSelected();
                RepColors col = new RepColors(debugUtil);
                col.debug = debug;
               
                ImageProc proc = new ImageProc(dbHostField.getText(), dbNameField.getText(), dbUsernameField.getText(),
						new String(dbPasswordField.getPassword()), "textile_img", debug, debugUtil);
                col.refColors = proc.loadRefColors("Color_detail");
                int numColors = (int) numColorSpinner.getValue();

                //Fetch images from database or directory
                LinkedList<ImageItem> images = new LinkedList<>();
                if (databaseImportRadioButton.isSelected()) {
                    proc.fetchImageList("img_detail", selectedImages);
                    while (proc.hasNextImage()) {
                        images.add(proc.nextImage());
                    }
                }
                else { 
                    LinkedList<File> files = new LinkedList<>();
                    recurseDirectory(new File(directoryField.getText()), files);
                    
                   

                    //Process all files in the list
                    for (File file : files) {
                        images.add(proc.readImageFromFile(file.getAbsolutePath()));
                        
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
                    }
                }
                catch (IOException er ) {
                    debugUtil.error("Could not open SQL script file for writing.");
                    if (debug) er.printStackTrace();
                }

                

			}
		});
				
	}

}
