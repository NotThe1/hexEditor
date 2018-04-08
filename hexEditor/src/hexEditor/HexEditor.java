package hexEditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
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
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class HexEditor {

	ApplicationAdapter applicationAdapter = new ApplicationAdapter();
	AppLogger log = AppLogger.getInstance();
	UndoManager undoManager = new UndoManager();
	AbstractAction actionUndo;
	AbstractAction actionRedo;

	File activeFile;
	String currentPath;

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

	private void clearFile() {
		displayFileName(NO_FILE_SELECTED, NO_FILE_SELECTED);
		setAllActivityButtons(false);
		setAllMenuActivity(false, mnuRemoveRecentFiles, mnuFileExit);

		mnuFileNew.setEnabled(true);
		btnFileNew.setEnabled(true);
		mnuFileOpen.setEnabled(true);
		btnFileOpen.setEnabled(true);
	}// ClearFile

	private void setupFile() {
		displayFileName(activeFile);
		setAllActivityButtons(true);
		setAllMenuActivity(true);

		mnuFileNew.setEnabled(false);
		btnFileNew.setEnabled(false);
		mnuFileOpen.setEnabled(false);
		btnFileOpen.setEnabled(false);

	}// setupFile()

	private void displayFileName(String fileName, String filePath) {
		lblFileName.setText(fileName);
		lblFileName.setToolTipText(filePath);
	}// displayFileName

	private void displayFileName(File file) {
		displayFileName(file.getName(), file.getAbsolutePath());
	}// displayFileName

	private void setActivityStates(String activity) {
		switch (activity) {
		case NO_FILE:
			clearFile();
			break;
		case FILE_ACTIVE:
			setupFile();
			break;
		default:
			log.addError("Bad Activity State -> " + activity);
		}// switch
	}// setActivityStates

	/* sets each menu to the passed state, and sets menuItems sent to opposite state) */
	private void setAllMenuActivity(boolean state, JMenuItem... menuItems) {
		Component[] menus = menuBar.getComponents();
		for (Component menu : menus) {
			if (menu instanceof JMenu) {
				Component[] items = ((JMenu) menu).getMenuComponents();
				for (Component mi : items) {
					if (mi instanceof AbstractButton) {// recent file entries names are not set
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

	private void loadFile(File file) {
		activeFile = file;
		currentPath = activeFile.getParent();
		log.addInfo("Loading File -> " + file.toString());
		setActivityStates(FILE_ACTIVE);
	}// loadFile

	private Action findAction(Action[] actions, String key) {
		for (Action action : actions) {
			if (action.getValue(Action.NAME).equals(key)) {
				return action;
			} // if
		} // for
		return null;
	}// findAction

	// ---------------------------------------------------------

	private void doFileNew() {
		log.addInfo("** [doFileNew] **");

		// JMenuItem mi1 = MenuUtility.addFileItem(mnuFile, new File("C:\\rat\\asjdfajf\\AddedFile.txt"));
		// JMenuItem mi2 = MenuUtility.addFileItem(mnuFile, new File("C:\\Users\\admin\\Dropbox\\My
		// Scans\\Misc\\US.bmp"));
		// JMenuItem mi3 = MenuUtility.addFileItem(mnuFile, new File("C:\\My\\Sysinternals\\accesschk.exe"));
		// int a = 1;
	}// doFileNew

	private void doFileOpen() {
		log.addInfo("** [doFileOpen] **");

		JFileChooser chooser = new JFileChooser(currentPath);
		if (chooser.showOpenDialog(frameBase) != JFileChooser.APPROVE_OPTION) {
			return; // just get out
		} // if open
		MenuUtility.addFileItem(mnuFile, chooser.getSelectedFile(), applicationAdapter);
		loadFile(chooser.getSelectedFile());
	}// doFileOpen

	private void doFileClose() {
		log.addInfo("** [doFileClose] **");
		System.out.println("** [doFileClose] **");
		setActivityStates(NO_FILE);
	}// doFileSave

	private void doFileSave() {
		log.addInfo("** [doFileSave] **");
		System.out.println("** [doFileSave] **");
	}// doFileSave

	private void doFileSaveAs() {
		log.addInfo("** [doFileSaveAs] **");

	}// doFileSaveAs

	private void doFilePrint() {
		log.addInfo("** [doFilePrint] **");

	}// doFilePrint

	private void doFileExit() {
		if (activeFile != null) {
			String message = String.format("File: %s has outstanding changes.%nDo you want to save it before exiting?",
					activeFile.getName());
			int answer = JOptionPane.showConfirmDialog(frameBase.getContentPane(), message, "Exit Hex Editor",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (answer == JOptionPane.CANCEL_OPTION) {
				return;
			} else if (answer == JOptionPane.YES_OPTION) {
				doFileSave();
			} else if (answer == JOptionPane.NO_OPTION) {
				/* do nothing special */
			} // if answer
		} // if active file
		appClose();
		System.exit(0);
	}// doFileExit

	////////////////////////////////////////////////////////////////////////////////////////
	private void appClose() {
		Preferences myPrefs = Preferences.userNodeForPackage(HexEditor.class).node(this.getClass().getSimpleName());
		Dimension dim = frameBase.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frameBase.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs.putInt("DividerLocationMajor", splitPaneMajor.getDividerLocation());
		myPrefs.putInt("DividerLocationMinor", splitPaneMinor.getDividerLocation());
		myPrefs.put("CurrentPath", currentPath);
		MenuUtility.saveRecentFileList(myPrefs, mnuFile);
		myPrefs = null;
		if (activeFile != null) {
			doFileClose();
		} // if
		
		System.out.println("Divider Location = " + splitPaneMajor.getDividerLocation());
	}// appClose

	private void appInit() {
		/* setup action for standard edit behaviors */
		initActions();

		/* Reestablish state info */

		Preferences myPrefs = Preferences.userNodeForPackage(HexEditor.class).node(this.getClass().getSimpleName());
		frameBase.setSize(myPrefs.getInt("Width", 761), myPrefs.getInt("Height", 693));
		frameBase.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		splitPaneMajor.setDividerLocation(800);
		splitPaneMinor.setDividerLocation(myPrefs.getInt("DividerLocationMinor", 100));
		currentPath = myPrefs.get("CurrentPath", DEFAULT_DIRECTORY);
		MenuUtility.loadRecentFileList(myPrefs, mnuFile, applicationAdapter);
		myPrefs = null;

		log.setDoc(textPaneLog.getStyledDocument());

		clearFile();
		log.addInfo("Starting .........");
	}// appInit

	private void initActions() {
		/* setup action for standard edit behaviors */

		btnEditCut.addActionListener(new DefaultEditorKit.CutAction());
		mnuEditCut.addActionListener(new DefaultEditorKit.CutAction());

		btnEditCopy.addActionListener(new DefaultEditorKit.CopyAction());
		mnuEditCut.addActionListener(new DefaultEditorKit.CopyAction());

		btnEditPaste.addActionListener(new DefaultEditorKit.PasteAction());
		mnuEditPaste.addActionListener(new DefaultEditorKit.PasteAction());

		//////////////////////////////////////////////////////////
		actionUndo = new AbstractAction("Undo") {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent actionEvent) {
				try {
					if (undoManager.canUndo()) {
						undoManager.undo();
					} // if
				} catch (CannotUndoException e) {
				} // try
			}// actionPerformed
		};// new AbstractAction

		actionRedo = new AbstractAction("Redo") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent actionEvent) {
				try {
					if (undoManager.canRedo()) {
						undoManager.redo();
					} // if
				} catch (CannotRedoException e) {
				} // try
			}// actionPerformed
		};// new AbstractAction

		// Bind the undo action to ctl-Z
		textPaneLog.getActionMap().put("Undo", actionUndo);
		textPaneLog.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");

		// Bind the undo action to ctl-y
		textPaneLog.getActionMap().put("Redo", actionRedo);
		textPaneLog.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");

//		textPaneLog.getInputMap().put(KeyStroke.getKeyStroke("control M"), "cut");

	}// initActions

	public HexEditor() {
		initialize();
		appInit();
	}// Constructor

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frameBase = new JFrame();
		frameBase.setTitle("Hex Editor    0.1");
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
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		frameBase.getContentPane().setLayout(gridBagLayout);

		toolBar = new JToolBar();
		GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.anchor = GridBagConstraints.WEST;
		gbc_toolBar.fill = GridBagConstraints.VERTICAL;
		gbc_toolBar.insets = new Insets(0, 0, 5, 0);
		gbc_toolBar.gridx = 0;
		gbc_toolBar.gridy = 0;
		frameBase.getContentPane().add(toolBar, gbc_toolBar);

		btnFileNew = new JButton("");
		btnFileNew.setToolTipText("New");
		btnFileNew.setName(BTN_FILE_NEW);
		btnFileNew.addActionListener(applicationAdapter);
		btnFileNew.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/new.png")));

		toolBar.add(btnFileNew);

		btnFileOpen = new JButton("");
		btnFileOpen.setName(BTN_FILE_OPEN);
		btnFileOpen.addActionListener(applicationAdapter);
		btnFileOpen.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/open.png")));
		btnFileOpen.setToolTipText("Open");
		toolBar.add(btnFileOpen);

		JSeparator separator = new JSeparator();
		separator.setPreferredSize(new Dimension(5, 0));
		separator.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator);

		btnFileSave = new JButton("");
		btnFileSave.setName(BTN_FILE_SAVE);
		btnFileSave.addActionListener(applicationAdapter);

		btnFileClose = new JButton("");
		btnFileClose.setName(BTN_FILE_CLOSE);
		btnFileClose.addActionListener(applicationAdapter);
		btnFileClose.setToolTipText("Close");
		btnFileClose.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/close.png")));
		toolBar.add(btnFileClose);

		separator_7 = new JSeparator();
		separator_7.setPreferredSize(new Dimension(5, 0));
		separator_7.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator_7);
		btnFileSave.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/save.png")));
		btnFileSave.setToolTipText("Save");
		btnFileSave.setSelectedIcon(null);
		toolBar.add(btnFileSave);

		btnEditSaveAs = new JButton("");
		btnEditSaveAs.setName(BTN_FILE_SAVE_AS);
		btnEditSaveAs.addActionListener(applicationAdapter);
		btnEditSaveAs.setToolTipText("Save As");
		btnEditSaveAs.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/saveAs.png")));
		toolBar.add(btnEditSaveAs);

		JSeparator separator_3 = new JSeparator();
		separator_3.setPreferredSize(new Dimension(5, 0));
		separator_3.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator_3);

		btnFilePrint = new JButton("");
		btnFilePrint.setToolTipText("Print");
		btnFilePrint.setName(BTN_FILE_PRINT);
		btnFilePrint.addActionListener(applicationAdapter);
		btnFilePrint.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/print.png")));
		toolBar.add(btnFilePrint);

		JSeparator separator_4 = new JSeparator();
		separator_4.setPreferredSize(new Dimension(5, 0));
		separator_4.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator_4);

		btnEditCut = new JButton("");
		btnEditCut.setToolTipText("Cut");
		btnEditCut.setName(BTN_EDIT_CUT);
		btnEditCut.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/cut.png")));
		toolBar.add(btnEditCut);

		btnEditCopy = new JButton("");
		btnEditCopy.setName(BTN_EDIT_COPY);
		btnEditCopy.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/copy.png")));
		btnEditCopy.setToolTipText("Copy");
		toolBar.add(btnEditCopy);

		btnEditPaste = new JButton("");
		btnEditPaste.setName(BTN_EDIT_PASTE);
		btnEditPaste.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/paste.png")));
		btnEditPaste.setToolTipText("Paste");
		toolBar.add(btnEditPaste);

		JSeparator separator_5 = new JSeparator();
		separator_5.setPreferredSize(new Dimension(10, 0));
		separator_5.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator_5);

		btnEditUndo = new JButton("");
		btnEditUndo.setName(BTN_EDIT_UNDO);
		btnEditUndo.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/undo.png")));
		btnEditUndo.setToolTipText("Undo");
		toolBar.add(btnEditUndo);

		btnEditRedo = new JButton("");
		btnEditRedo.setName(BTN_EDIT_REDO);
		btnEditRedo.setToolTipText("Redo");
		btnEditRedo.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/redo.png")));
		toolBar.add(btnEditRedo);

		lblFileName = new JLabel("New label");
		GridBagConstraints gbc_lblFileName = new GridBagConstraints();
		gbc_lblFileName.insets = new Insets(0, 0, 5, 0);
		gbc_lblFileName.gridx = 0;
		gbc_lblFileName.gridy = 1;
		frameBase.getContentPane().add(lblFileName, gbc_lblFileName);

		splitPaneMajor = new JSplitPane();
		splitPaneMajor.setAlignmentX(Component.CENTER_ALIGNMENT);
		GridBagConstraints gbc_splitPaneMajor = new GridBagConstraints();
		gbc_splitPaneMajor.insets = new Insets(0, 0, 5, 0);
		gbc_splitPaneMajor.fill = GridBagConstraints.BOTH;
		gbc_splitPaneMajor.gridx = 0;
		gbc_splitPaneMajor.gridy = 2;
		frameBase.getContentPane().add(splitPaneMajor, gbc_splitPaneMajor);

		splitPaneMinor = new JSplitPane();
		splitPaneMinor.setOneTouchExpandable(true);
		splitPaneMinor.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPaneMajor.setRightComponent(splitPaneMinor);

		panelTop = new JPanel();
		splitPaneMinor.setLeftComponent(panelTop);
		GridBagLayout gbl_panelTop = new GridBagLayout();
		gbl_panelTop.columnWidths = new int[] { 0 };
		gbl_panelTop.rowHeights = new int[] { 0 };
		gbl_panelTop.columnWeights = new double[] { Double.MIN_VALUE };
		gbl_panelTop.rowWeights = new double[] { Double.MIN_VALUE };
		panelTop.setLayout(gbl_panelTop);

		scrollPaneLog = new JScrollPane();
		splitPaneMinor.setRightComponent(scrollPaneLog);

		textPaneLog = new JTextPane();
		textPaneLog.setEditable(false);
		scrollPaneLog.setViewportView(textPaneLog);

		lblNewLabel = new JLabel("Application Log");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
		scrollPaneLog.setColumnHeaderView(lblNewLabel);
		splitPaneMinor.setDividerLocation(100);
		
		panelMainDisplay = new JPanel();
		panelMainDisplay.setPreferredSize(new Dimension(0, 0));
		panelMainDisplay.setMinimumSize(new Dimension(804, 0));
		panelMainDisplay.setMaximumSize(new Dimension(0, 0));
		panelMainDisplay.setBackground(Color.GRAY);
		splitPaneMajor.setLeftComponent(panelMainDisplay);
		GridBagLayout gbl_panelMainDisplay = new GridBagLayout();
		gbl_panelMainDisplay.columnWidths = new int[]{0, 0};
		gbl_panelMainDisplay.rowHeights = new int[]{0, 0};
		gbl_panelMainDisplay.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panelMainDisplay.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panelMainDisplay.setLayout(gbl_panelMainDisplay);
		
		panelEditPane = new HexEditDisplayPanel();
		panelEditPane.setMaximumSize(new Dimension(786, 2147483647));
		GridBagConstraints gbc_panelEditPane = new GridBagConstraints();
		gbc_panelEditPane.fill = GridBagConstraints.BOTH;
		gbc_panelEditPane.gridx = 0;
		gbc_panelEditPane.gridy = 0;
		panelMainDisplay.add(panelEditPane, gbc_panelEditPane);
		
		JPanel panelStatus = new JPanel();
		panelStatus.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panelStatus = new GridBagConstraints();
		gbc_panelStatus.fill = GridBagConstraints.BOTH;
		gbc_panelStatus.gridx = 0;
		gbc_panelStatus.gridy = 3;
		frameBase.getContentPane().add(panelStatus, gbc_panelStatus);

		menuBar = new JMenuBar();
		frameBase.setJMenuBar(menuBar);

		mnuFile = new JMenu("File");
		menuBar.add(mnuFile);

		mnuFileNew = new JMenuItem("New");
		mnuFileNew.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/new.png")));
		mnuFileNew.setName(MNU_FILE_NEW);
		mnuFileNew.addActionListener(applicationAdapter);
		mnuFile.add(mnuFileNew);

		mnuFileOpen = new JMenuItem("Open...");
		mnuFileOpen.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/open.png")));
		mnuFileOpen.setName(MNU_FILE_OPEN);
		mnuFileOpen.addActionListener(applicationAdapter);
		mnuFile.add(mnuFileOpen);

		JSeparator separator99 = new JSeparator();
		mnuFile.add(separator99);

		mnuFileSave = new JMenuItem("Save...");
		mnuFileSave.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/save.png")));
		mnuFileSave.setName(MNU_FILE_SAVE);
		mnuFileSave.addActionListener(applicationAdapter);

		mnuFileClose = new JMenuItem("Close");
		mnuFileClose.setName(MNU_FILE_CLOSE);
		mnuFileClose.addActionListener(applicationAdapter);
		mnuFileClose.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/close.png")));
		mnuFile.add(mnuFileClose);

		separator_1 = new JSeparator();
		mnuFile.add(separator_1);
		mnuFile.add(mnuFileSave);

		mnuFileSaveAs = new JMenuItem("Save As...");
		mnuFileSaveAs.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/saveAs.png")));
		mnuFileSaveAs.setName(MNU_FILE_SAVE_AS);
		mnuFileSaveAs.addActionListener(applicationAdapter);
		mnuFile.add(mnuFileSaveAs);

		JSeparator separator_2 = new JSeparator();
		mnuFile.add(separator_2);

		mnuFilePrint = new JMenuItem("Print...");
		mnuFilePrint.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/print.png")));
		mnuFilePrint.setName(MNU_FILE_PRINT);
		mnuFilePrint.addActionListener(applicationAdapter);
		mnuFile.add(mnuFilePrint);

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

		mnuEditCut = new JMenuItem("Cut");
		mnuEditCut.setName(MNU_EDIT_CUT);
		mnuEditCut.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/cut.png")));
		mnuEdit.add(mnuEditCut);

		mnuEditCopy = new JMenuItem("Copy");
		mnuEditCopy.setName(MNU_EDIT_COPY);
		mnuEditCopy.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/copy.png")));
		mnuEdit.add(mnuEditCopy);

		mnuEditPaste = new JMenuItem("Paste");
		mnuEditPaste.setName(MNU_EDIT_PASTE);
		mnuEditPaste.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/paste.png")));
		mnuEdit.add(mnuEditPaste);

		JSeparator separator_6 = new JSeparator();
		mnuEdit.add(separator_6);

		mnuEditUndo = new JMenuItem("Undo");
		mnuEditUndo.setName(MNU_EDIT_UNDO);
		mnuEditUndo.addActionListener(applicationAdapter);
		mnuEditUndo.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/undo.png")));
		mnuEdit.add(mnuEditUndo);

		mnuEditRedo = new JMenuItem("Redo");
		mnuEditRedo.setName(MNU_EDIT_REDO);
		mnuEditRedo.addActionListener(applicationAdapter);
		mnuEditRedo.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/redo.png")));
		mnuEdit.add(mnuEditRedo);

	}// initialize

	//////////////////////////////////////////////////////////////////////////
	private static final String EMPTY_STRING = "";

	private static final String NO_FILE_SELECTED = "<No File Selected>";
	private static final String DEFAULT_DIRECTORY = ".";

	/* constants for menu & button states */
	private static final String NO_FILE = "no File";
	private static final String FILE_ACTIVE = "File Active";

	/* constants for menus */
	private static final String MNU_FILE_NEW = "mnuFileNew";
	private static final String MNU_FILE_OPEN = "mnuFileOpen";
	private static final String MNU_FILE_CLOSE = "mnuFileclose";
	private static final String MNU_FILE_SAVE = "mnuFileSave";
	private static final String MNU_FILE_SAVE_AS = "mnuFileSaveAs";
	private static final String MNU_FILE_PRINT = "mnuFilePrint";
	private static final String MNU_FILE_EXIT = "mnuFileExit";

	private static final String MNU_EDIT_CUT = "mnuEditCut";
	private static final String MNU_EDIT_COPY = "mnuEditCopy";
	private static final String MNU_EDIT_PASTE = "mnuEditPaste";
	private static final String MNU_EDIT_UNDO = "mnuEditUndo";
	private static final String MNU_EDIT_REDO = "mnuEditREDO";

	/* constants for buttons */
	private static final String BTN_FILE_NEW = "btnFileNew";
	private static final String BTN_FILE_OPEN = "btnFileOpen";
	private static final String BTN_FILE_CLOSE = "btnFileclose";
	private static final String BTN_FILE_SAVE = "btnFileSave";
	private static final String BTN_FILE_SAVE_AS = "btnFileSaveAs";
	private static final String BTN_FILE_PRINT = "btnFilePrint";
	// private static final String BTN_FILE_EXIT = "btnFileExit";

	private static final String BTN_EDIT_CUT = "btnEditCut";
	private static final String BTN_EDIT_COPY = "btnEditCopy";
	private static final String BTN_EDIT_PASTE = "btnEditPaste";
	private static final String BTN_EDIT_UNDO = "btnEditUndo";
	private static final String BTN_EDIT_REDO = "btnEditREDO";

	//////////////////////////////////////////////////////////////////////////
	private JFrame frameBase;

	private JSplitPane splitPaneMajor;
	private JMenuItem mnuFileNew;
	private JButton btnFileNew;
	private JLabel lblFileName;
	private JToolBar toolBar;
	private JMenuBar menuBar;
	private JMenuItem mnuFileExit;
	private JMenuItem mnuFileOpen;
	private JMenuItem mnuFileSave;
	private JMenuItem mnuFileSaveAs;
	private JMenuItem mnuFilePrint;
	private JButton btnFileOpen;
	private JButton btnFileSave;
	private JButton btnEditSaveAs;
	private JButton btnFilePrint;
	private JButton btnEditCut;
	private JButton btnEditCopy;
	private JButton btnEditPaste;
	private JButton btnEditUndo;
	private JButton btnEditRedo;
	private JMenuItem mnuRemoveRecentFiles;
	private JMenu mnuFile;
	private JSplitPane splitPaneMinor;
	private JPanel panelTop;
	private JScrollPane scrollPaneLog;
	private JTextPane textPaneLog;
	private JLabel lblNewLabel;
	private JMenuItem mnuFileClose;
	private JSeparator separator_1;
	private JButton btnFileClose;
	private JSeparator separator_7;
	private JMenu mnuEdit;
	private JMenuItem mnuEditCopy;
	private JMenuItem mnuEditPaste;
	private JMenuItem mnuEditUndo;
	private JMenuItem mnuEditRedo;
	private JMenuItem mnuEditCut;
	private JPanel panelMainDisplay;
	private HexEditDisplayPanel panelEditPane;
	//////////////////////////////////////////////////////////////////////////

	class ApplicationAdapter implements ActionListener {// , ListSelectionListener
		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			String name = ((Component) actionEvent.getSource()).getName();

			if (name == null) {
				loadFile(new File(actionEvent.getActionCommand()));
			} else {
				switch (name) {
				case MNU_FILE_NEW:
				case BTN_FILE_NEW:
					doFileNew();
					break;
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
				case MNU_FILE_PRINT:
				case BTN_FILE_PRINT:
					doFilePrint();
					break;
				case MNU_FILE_EXIT:
					doFileExit();
					break;
				default:
					log.addSpecial(actionEvent.getActionCommand());
				}// switch
			} // if
		}// actionPerformed
	}// class AdapterAction

	class AdapterUndoRedo implements UndoableEditListener {
		@Override
		public void undoableEditHappened(UndoableEditEvent undoableEditEvent) {
			undoManager.addEdit(undoableEditEvent.getEdit());
		}// undoableEditHappened
	}// class AdapterUndoRedo

}// class GUItemplate