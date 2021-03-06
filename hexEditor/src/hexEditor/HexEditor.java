package hexEditor;

/*
 *    2019-09-02 Tested on both Unix and Windows
 *    2019-09-02  Added LINE_SEPARATOR and LINE_SEPARATOR_SIZE (HEUtility.java)
 */


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.prefs.Preferences;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import appLogger.AppLogger;
import hexEditDisplayPanel.HexEditDisplayPanel;
import menuUtility.MenuUtility;

public class HexEditor {
	String title = "Hex Editor    2.0";
	/* @formatter:off */
	Image classImage = Toolkit.getDefaultToolkit().getImage(HexEditor.class.getResource("/resources/hexEdit.png"));
	Icon openIcon = new ImageIcon(HexEditor.class.getResource("/resources/open.png"));
	Icon closeIcon = new ImageIcon(HexEditor.class.getResource("/resources/close.png"));
	Icon saveIcon = new ImageIcon(HexEditor.class.getResource("/resources/save.png"));
	Icon saveAsIcon = new ImageIcon(HexEditor.class.getResource("/resources/saveAs.png"));
	Icon unDoIcon = new ImageIcon(HexEditor.class.getResource("/resources/undo.png"));
	Icon reDoIcon = new ImageIcon(HexEditor.class.getResource("/resources/redo.png"));
	/* @formatter:on */
	
	ApplicationAdapter applicationAdapter = new ApplicationAdapter();
	AppLogger log = AppLogger.getInstance();

	Font myCourierFont;

	String activeFileName;
	String activeFilePath;
	String activeFileAbsolutePath;

	File workingFile;

	boolean dataChanged;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HexEditor window = new HexEditor();
					window.frameBase.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				} // try
			}// run
		});
	}// main

	private void setDataChange(boolean state) {
		this.dataChanged = state;
		hexEditDisplay.setDataChanged(state);
	}// setDataChange

	private void setUIasNoFile() {
		displayFileName(NO_FILE_SELECTED, NO_FILE_SELECTED);
		setAllActivityButtons(false);
		setAllMenuActivity(false, mnuRemoveRecentFiles, mnuFileExit);

		mnuFileOpen.setEnabled(true);
		btnFileOpen.setEnabled(true);
		hexEditDisplay.clear();
	}// ClearFile

	private void setUIasFileActive() {
		setAllActivityButtons(true);
		setAllMenuActivity(true);

		mnuFileOpen.setEnabled(false);
		btnFileOpen.setEnabled(false);

	}// setupFile()

	private void displayFileName(String fileName, String filePath) {
		lblFileName.setText(fileName);
		lblFileName.setToolTipText(filePath);
	}// displayFileName

	private void setActivityStates(String activity) {
		switch (activity) {
		case NO_FILE:
			setUIasNoFile();
			break;
		case FILE_ACTIVE:
			setUIasFileActive();
			break;
		default:
			log.errorf("Bad Activity State -> %s", activity);
		}// switch
		setDataChange(false);
		lblFileName.setForeground(Color.BLACK);
	}// setActivityStates

	/*
	 * sets each menu to the passed state, and sets menuItems sent to opposite
	 * state)
	 */
	private void setAllMenuActivity(boolean state, JMenuItem... menuItems) {
		Component[] menus = menuBar.getComponents();
		for (Component menu : menus) {
			if (menu instanceof JMenu) {
				Component[] items = ((JMenu) menu).getMenuComponents();
				for (Component mi : items) {
					if (mi instanceof AbstractButton) {// recent file entries
														// names are not set
						mi.setEnabled(mi.getName() == null ? !state : state);
					} // if menuItem
				} // for all menu items
			} // if a menu
		} // for all menus

		for (JMenuItem menuItem : menuItems) {
			menuItem.setEnabled(!state);
		} // for - enable

	}// disableAllActivity

	private void setAllActivityButtons(boolean state) {
		Component[] buttons = toolBar.getComponents();
		for (Component b : buttons) {
			if (b instanceof AbstractButton) {
				b.setEnabled(state);
			} // if a menu
		} // for all menus

	}// disableAllActivity

	private void setActiveFileInfo(File currentActiveFile) {
		activeFileAbsolutePath = currentActiveFile.getAbsolutePath();
		activeFilePath = currentActiveFile.getParent();
		activeFileName = currentActiveFile.getName();
		displayFileName(activeFileName, activeFilePath);

	}// setActiveFileInfo

	private void loadFile(File subjectFile) {

		closeFile();

		long fileLength = subjectFile.length();
		if (fileLength >= Integer.MAX_VALUE) {
			Toolkit.getDefaultToolkit().beep();
			log.warnf("[HexEditPanelFile : loadData] file too large %,d%n", fileLength);
			return;
		} // if

		if (fileLength <= 0) {
			Toolkit.getDefaultToolkit().beep();
			log.warnf("[HexEditPanelFile : loadData] file is empty %,d%n", fileLength);
			return;
		} // if

		workingFile = makeWorkingFile();
		setActiveFileInfo(subjectFile);

		log.info("Loading File:");
		log.infof("       Path : %s%n", activeFileAbsolutePath);
		log.infof("       Size : %1$,d bytes  [%1$#X]%n%n", fileLength);
		setActivityStates(FILE_ACTIVE);

		// log.info("activeFile: %s%n", activeFileAbsolutePath);
		// log.info("workingFile: %s%n", workingFile.getAbsolutePath());
		/////////////////////////////////////////////

		try {
			Path source = Paths.get(activeFileAbsolutePath);
			Path target = Paths.get(workingFile.getAbsolutePath());
			Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			log.errorf("Failed to copy %s to %s", activeFileAbsolutePath, workingFile.getAbsolutePath());
			e.printStackTrace();
		} // try

		////////////////////////////////////////////

		FileChannel fileChannel;
		MappedByteBuffer fileMap = null;

		try (RandomAccessFile raf = new RandomAccessFile(workingFile, "rw")) {

			fileChannel = raf.getChannel();
			fileMap = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileChannel.size());// this.totalBytesOnDisk);
			fileChannel.close();
		} catch (IOException ioe) {
			Toolkit.getDefaultToolkit().beep();
			log.errorf("[loadFile]: %s", ioe.getMessage());
		} // try

		hexEditDisplay.setData(fileMap);
		hexEditDisplay.run();
		fileMap = null;
		// hexEditDisplay.run();
	}// loadFile

	// ---------------------------------------------------------
	static class TempFilter implements FilenameFilter {

		@Override
		public boolean accept(File dir, String name) {
			if (name.startsWith(TEMP_PREFIX) && name.endsWith(TEMP_SUFFIX)) {
				return true;
			} else {
				return false;
			} // if
		}// accept

	}// class TempFilter

	private void removeAllWorkingFiles() {
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		File[] tempFiles = tempDir.listFiles(new TempFilter());

		if (tempFiles == null) {
			return;
		} // if
		for (File file : tempFiles) {
			log.infof("Deleting file: %s", file.getName());
			if (!file.delete()) {
				log.errorf("Bad Delete %s", file.getName());
			} // if bad delete
		} // if not null
	}// removeAllTempFiles

	private File makeWorkingFile() {
		File result = null;
		try {
			result = File.createTempFile(TEMP_PREFIX, TEMP_SUFFIX);
			// log.addInfo("[HexEditor.makeWorkingFile] Working file = " +
			// result.getAbsolutePath());
		} catch (IOException e) {
			log.errorf("Failed to make WorkingFile: %s", e.getMessage());
			e.printStackTrace();
		} // try
		return result;
	}// makeWorkingFile

	private void doFileOpen() {
		JFileChooser chooser = new JFileChooser(activeFilePath);
		if (chooser.showOpenDialog(frameBase) != JFileChooser.APPROVE_OPTION) {
			return; // just get out
		} // if open
		MenuUtility.addFileItem(mnuFile, chooser.getSelectedFile(), applicationAdapter);
		loadFile(chooser.getSelectedFile());
	}// doFileOpen

	private void doFileClose() {
		log.info("** [doFileClose] **");
		if (checkForDataChange() == JOptionPane.CANCEL_OPTION) {
			return; // get out
		} // if

		closeFile();
		setActivityStates(NO_FILE);
	}// doFileSave

	private void doFileSave() {
		log.info("[HexEditor.doFileSave]");

		Path originalPath = Paths.get(activeFileAbsolutePath);
		Path workingPath = Paths.get(workingFile.getAbsolutePath());
		try {
			Files.copy(workingPath, originalPath, StandardCopyOption.REPLACE_EXISTING);
			setDataChange(false);
			lblFileName.setForeground(Color.BLACK);
		} catch (IOException e) {
			log.errorf("Failed to Save %s to %s", workingFile.getAbsolutePath(), activeFileAbsolutePath);
			e.printStackTrace();
		} // try
	}// doFileSave

	private void doFileSaveAs() {
		log.info("** [doFileSaveAs] **");
		JFileChooser chooser = new JFileChooser(activeFilePath);
		if (chooser.showOpenDialog(frameBase) != JFileChooser.APPROVE_OPTION) {
			return; // just get out
		} // if open
		File newActiveFile = chooser.getSelectedFile();
		setActiveFileInfo(newActiveFile);
		MenuUtility.addFileItem(mnuFile, chooser.getSelectedFile(), applicationAdapter);
		doFileSave();
	}// doFileSaveAs

	private void doFileExit() {
		appClose();
	}// doFileExit

	/* if data changed save i,t if user wants to save it */
	private int checkForDataChange() {
		int result = JOptionPane.NO_OPTION;
		if (dataChanged) {
			String message = String.format("File: %s has outstanding changes.%nDo you want to save it before exiting?",
					activeFileName);
			result = JOptionPane.showConfirmDialog(frameBase.getContentPane(), message, "Exit Hex Editor",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (result == JOptionPane.CANCEL_OPTION) {
				// return cancel result
			} else if (result == JOptionPane.YES_OPTION) {
				doFileSave();
			} else if (result == JOptionPane.NO_OPTION) {
				/* do nothing special */
			} // if answer
		} // DataChanged
		return result;
	}// checkForDataChange

	private void doUndo() {
		hexEditDisplay.undo();
	}// doUndo

	private void doRedo() {
		hexEditDisplay.redo();
	}// doRedo

	public void closeFile() {
		workingFile = null;
		hexEditDisplay.clear();
	}// closeFile

	////////////////////////////////////////////////////////////////////////////////////////

	private void makeCourierFont() {
		try (InputStream in = this.getClass().getResourceAsStream("/resources/myCourierNew.ttf");) {
			Font myCourierFont = Font.createFont(Font.TRUETYPE_FONT, in);

			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(myCourierFont);
			// labelSouth.setFont(new Font(myCourierFont.getName(), Font.BOLD,
			// 48));
		} catch (FontFormatException e) {
			System.err.println("Font Format Exception");
			log.errorf("FontFormatException  - Failed to create create \"Courier New\" Font%n", "");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO Exception");
			log.errorf("IOException  - Failed to create create \"Courier New\" Font%n", "");
			e.printStackTrace();
		}

	}// setCourierFont

	private void appClose() {
		if (checkForDataChange() == JOptionPane.CANCEL_OPTION) {
			return; // get out
		} // if

		Preferences myPrefs = Preferences.userNodeForPackage(HexEditor.class).node(this.getClass().getSimpleName());
		Dimension dim = frameBase.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frameBase.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs.putInt("DividerLocationMain", splitPaneMain.getDividerLocation());
		myPrefs.put("CurrentPath", activeFilePath);
		MenuUtility.saveRecentFileList(myPrefs, mnuFile);
		myPrefs = null;

		frameBase.dispose();
		// System.exit(0);
	}// appClose

	private void appInit() {
		// makeCourierFont();
		log.setTextPane(textLog, "HexEditor Log");
		/* setup action for standard edit behaviors */

		/* Reestablish state info */

		Preferences myPrefs = Preferences.userNodeForPackage(HexEditor.class).node(this.getClass().getSimpleName());
		frameBase.setSize(myPrefs.getInt("Width", 761), myPrefs.getInt("Height", 693));
		frameBase.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		splitPaneMain.setDividerLocation(myPrefs.getInt("DividerLocationMain", 500));
		activeFilePath = myPrefs.get("CurrentPath", DEFAULT_DIRECTORY);
		MenuUtility.loadRecentFileList(myPrefs, mnuFile, applicationAdapter);
		myPrefs = null;

		setActivityStates(NO_FILE);

		log.info("Starting .........");
		removeAllWorkingFiles();
		hexEditDisplay.setName("Hex editor");
		hexEditDisplay.addChangeListener(applicationAdapter);
	}// appInit

	public HexEditor() {
		initialize();
		appInit();
	}// Constructor

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		makeCourierFont();
		frameBase = new JFrame();
		frameBase.setTitle(title);
		frameBase.setIconImage(classImage);
		frameBase.setBounds(100, 100, 450, 300);
		frameBase.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameBase.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				appClose();
			}
		});
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		frameBase.getContentPane().setLayout(gridBagLayout);

		toolBar = new JToolBar();
		GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.anchor = GridBagConstraints.WEST;
		gbc_toolBar.fill = GridBagConstraints.VERTICAL;
		gbc_toolBar.insets = new Insets(0, 0, 5, 0);
		gbc_toolBar.gridx = 0;
		gbc_toolBar.gridy = 0;
		frameBase.getContentPane().add(toolBar, gbc_toolBar);

		btnFileOpen = new JButton(openIcon);
		btnFileOpen.setName(BTN_FILE_OPEN);
		btnFileOpen.addActionListener(applicationAdapter);
		btnFileOpen.setToolTipText("Open");
		toolBar.add(btnFileOpen);

		JSeparator separator = new JSeparator();
		separator.setPreferredSize(new Dimension(5, 0));
		separator.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator);

		btnFileSave = new JButton(saveIcon);
		btnFileSave.setName(BTN_FILE_SAVE);
		btnFileSave.addActionListener(applicationAdapter);

		btnFileClose = new JButton(closeIcon);
		btnFileClose.setName(BTN_FILE_CLOSE);
		btnFileClose.addActionListener(applicationAdapter);
		btnFileClose.setToolTipText("Close");
		toolBar.add(btnFileClose);

		separator_7 = new JSeparator();
		separator_7.setPreferredSize(new Dimension(5, 0));
		separator_7.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator_7);
		btnFileSave.setToolTipText("Save");
		btnFileSave.setSelectedIcon(null);
		toolBar.add(btnFileSave);

		btnEditSaveAs = new JButton(saveAsIcon);
		btnEditSaveAs.setName(BTN_FILE_SAVE_AS);
		btnEditSaveAs.addActionListener(applicationAdapter);
		btnEditSaveAs.setToolTipText("Save As");
		toolBar.add(btnEditSaveAs);

		JSeparator separator_3 = new JSeparator();
		separator_3.setPreferredSize(new Dimension(5, 0));
		separator_3.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator_3);

		btnEditUndo = new JButton(unDoIcon);
		btnEditUndo.setName(BTN_EDIT_UNDO);
		btnEditUndo.addActionListener(applicationAdapter);
		btnEditUndo.setToolTipText("Undo");
		toolBar.add(btnEditUndo);

		btnEditRedo = new JButton(reDoIcon);
		btnEditRedo.setName(BTN_EDIT_REDO);
		btnEditRedo.addActionListener(applicationAdapter);
		btnEditRedo.setToolTipText("Redo");
		toolBar.add(btnEditRedo);

		lblFileName = new JLabel("New label");
		GridBagConstraints gbc_lblFileName = new GridBagConstraints();
		gbc_lblFileName.insets = new Insets(0, 0, 5, 0);
		gbc_lblFileName.gridx = 0;
		gbc_lblFileName.gridy = 2;
		frameBase.getContentPane().add(lblFileName, gbc_lblFileName);

		splitPaneMain = new JSplitPane();
		splitPaneMain.setOneTouchExpandable(true);
		GridBagConstraints gbc_splitPaneMain = new GridBagConstraints();
		gbc_splitPaneMain.insets = new Insets(0, 0, 5, 0);
		gbc_splitPaneMain.fill = GridBagConstraints.BOTH;
		gbc_splitPaneMain.gridx = 0;
		gbc_splitPaneMain.gridy = 3;
		frameBase.getContentPane().add(splitPaneMain, gbc_splitPaneMain);

		hexEditDisplay = new HexEditDisplayPanel();
		splitPaneMain.setLeftComponent(hexEditDisplay);
		hexEditDisplay.setPreferredSize(new Dimension(780, 0));
		hexEditDisplay.setMinimumSize(new Dimension(780, 0));
		hexEditDisplay.setMaximumSize(new Dimension(780, 2147483647));

		scrollPane_1 = new JScrollPane();
		splitPaneMain.setRightComponent(scrollPane_1);

		textLog = new JTextPane();
		scrollPane_1.setViewportView(textLog);
		textLog.setEditable(false);

		JPanel panelStatus = new JPanel();
		panelStatus.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panelStatus = new GridBagConstraints();
		gbc_panelStatus.fill = GridBagConstraints.BOTH;
		gbc_panelStatus.gridx = 0;
		gbc_panelStatus.gridy = 5;
		frameBase.getContentPane().add(panelStatus, gbc_panelStatus);

		menuBar = new JMenuBar();
		frameBase.setJMenuBar(menuBar);

		mnuFile = new JMenu("File");
		menuBar.add(mnuFile);

		mnuFileOpen = new JMenuItem("Open...", openIcon);
		mnuFileOpen.setName(MNU_FILE_OPEN);
		mnuFileOpen.addActionListener(applicationAdapter);
		mnuFile.add(mnuFileOpen);

		JSeparator separator99 = new JSeparator();
		mnuFile.add(separator99);

		mnuFileSave = new JMenuItem("Save...", saveIcon);
		mnuFileSave.setName(MNU_FILE_SAVE);
		mnuFileSave.addActionListener(applicationAdapter);

		mnuFileClose = new JMenuItem("Close", closeIcon);
		mnuFileClose.setName(MNU_FILE_CLOSE);
		mnuFileClose.addActionListener(applicationAdapter);
		mnuFile.add(mnuFileClose);

		separator_1 = new JSeparator();
		mnuFile.add(separator_1);
		mnuFile.add(mnuFileSave);

		mnuFileSaveAs = new JMenuItem("Save As...", saveAsIcon);
		mnuFileSaveAs.setName(MNU_FILE_SAVE_AS);
		mnuFileSaveAs.addActionListener(applicationAdapter);
		mnuFile.add(mnuFileSaveAs);

		JSeparator separatorFileStart = new JSeparator();
		separatorFileStart.setName(MenuUtility.RECENT_FILES_START);
		mnuFile.add(separatorFileStart);

		JSeparator separatorFileEnd = new JSeparator();
		separatorFileEnd.setName(MenuUtility.RECENT_FILES_END);
		separatorFileEnd.setVisible(false);
		mnuFile.add(separatorFileEnd);

		mnuRemoveRecentFiles = new JMenuItem("Remove Recent Files");
		mnuRemoveRecentFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				MenuUtility.clearList(mnuFile);
			}// action Performed
		});// addActionListener

		mnuFile.add(mnuRemoveRecentFiles);

		mnuFileExit = new JMenuItem("Exit");
		mnuFileExit.setName(MNU_FILE_EXIT);
		mnuFileExit.addActionListener(applicationAdapter);
		mnuFile.add(mnuFileExit);

		mnuEdit = new JMenu("Edit");
		menuBar.add(mnuEdit);

		mnuEditUndo = new JMenuItem("Undo", unDoIcon);
		mnuEditUndo.setName(MNU_EDIT_UNDO);
		mnuEditUndo.addActionListener(applicationAdapter);
		mnuEdit.add(mnuEditUndo);

		mnuEditRedo = new JMenuItem("Redo", reDoIcon);
		mnuEditRedo.setName(MNU_EDIT_REDO);
		mnuEditRedo.addActionListener(applicationAdapter);
		mnuEdit.add(mnuEditRedo);

	}// initialize

	//////////////////////////////////////////////////////////////////////////
	// private static final String EMPTY_STRING = "";

	private static final String NO_FILE_SELECTED = "<No File Selected>";
	private static final String DEFAULT_DIRECTORY = ".";

	/* constants for menu & button states */
	private static final String NO_FILE = "no File";
	private static final String FILE_ACTIVE = "File Active";

	/* constants for menus */
	private static final String MNU_FILE_OPEN = "mnuFileOpen";
	private static final String MNU_FILE_CLOSE = "mnuFileclose";
	private static final String MNU_FILE_SAVE = "mnuFileSave";
	private static final String MNU_FILE_SAVE_AS = "mnuFileSaveAs";
	private static final String MNU_FILE_EXIT = "mnuFileExit";

	private static final String MNU_EDIT_UNDO = "mnuEditUndo";
	private static final String MNU_EDIT_REDO = "mnuEditREDO";

	/* constants for buttons */
	private static final String BTN_FILE_OPEN = "btnFileOpen";
	private static final String BTN_FILE_CLOSE = "btnFileclose";
	private static final String BTN_FILE_SAVE = "btnFileSave";
	private static final String BTN_FILE_SAVE_AS = "btnFileSaveAs";

	private static final String BTN_EDIT_UNDO = "btnEditUndo";
	private static final String BTN_EDIT_REDO = "btnEditREDO";

	private static final String TEMP_PREFIX = "HexEdit";
	private static final String TEMP_SUFFIX = ".tmp";

	//////////////////////////////////////////////////////////////////////////
	private JFrame frameBase;
	private JLabel lblFileName;
	private JToolBar toolBar;
	private JMenuBar menuBar;
	private JMenuItem mnuFileExit;
	private JMenuItem mnuFileOpen;
	private JMenuItem mnuFileSave;
	private JMenuItem mnuFileSaveAs;
	private JButton btnFileOpen;
	private JButton btnFileSave;
	private JButton btnEditSaveAs;
	private JButton btnEditUndo;
	private JButton btnEditRedo;
	private JMenuItem mnuRemoveRecentFiles;
	private JMenu mnuFile;
	private JMenuItem mnuFileClose;
	private JSeparator separator_1;
	private JButton btnFileClose;
	private JSeparator separator_7;
	private JMenu mnuEdit;
	private JMenuItem mnuEditUndo;
	private JMenuItem mnuEditRedo;
	private HexEditDisplayPanel hexEditDisplay;
	private JTextPane textLog;
	private JSplitPane splitPaneMain;
	private JScrollPane scrollPane_1;
	//////////////////////////////////////////////////////////////////////////

	class ApplicationAdapter implements ActionListener, ChangeListener {// ,
																		// ListSelectionListener
		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			String name = ((Component) actionEvent.getSource()).getName();

			if (name == null) {
				loadFile(new File(actionEvent.getActionCommand()));
			} else {
				switch (name) {
				case MNU_FILE_OPEN:
				case BTN_FILE_OPEN:
					doFileOpen();
					break;
				case MNU_FILE_CLOSE:
				case BTN_FILE_CLOSE:
					doFileClose();
					break;
				case MNU_FILE_SAVE:
				case BTN_FILE_SAVE:
					doFileSave();
					break;
				case MNU_FILE_SAVE_AS:
				case BTN_FILE_SAVE_AS:
					doFileSaveAs();
					break;
				case MNU_FILE_EXIT:
					doFileExit();
					break;

				// Undo/Redo
				case BTN_EDIT_UNDO:
				case MNU_EDIT_UNDO:
					doUndo();
					break;
				case BTN_EDIT_REDO:
				case MNU_EDIT_REDO:
					doRedo();
					break;

				default:
					log.special(actionEvent.getActionCommand());
				}// switch
			} // if
		}// actionPerformed

		/* Change Listener */
		@Override
		public void stateChanged(ChangeEvent changeEvent) {
			dataChanged = true;
			log.infof("Change event from %s%n%n", ((HexEditDisplayPanel) changeEvent.getSource()).getName());
			lblFileName.setForeground(Color.RED);
		}// stateChanged
	}// class AdapterAction

}// class GUItemplate