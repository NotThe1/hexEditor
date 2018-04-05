package hexEditor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

public class HexEditor {

	ApplicationAdapter applicationAdapter = new ApplicationAdapter();



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
				}//try
			}//run
		});
	}// main
	
	//---------------------------------------------------------
	
	private void doFileNew(){
		System.out.println("** [doFileNew] **");
	}//doFileNew
	private void doFileOpen(){
		System.out.println("** [doFileOpen] **");

	}//doFileOpen
	private void doFileSave(){
		System.out.println("** [doFileSave] **");

	}//doFileSave
	private void doFileSaveAs(){
		System.out.println("** [doFileSaveAs] **");

	}//doFileSaveAs
	private void doFilePrint(){
		System.out.println("** [doFilePrint] **");

	}//doFilePrint
	
	private void doFileExit(){
		appClose();
		System.exit(0);
	}//doFileExit

////////////////////////////////////////////////////////////////////////////////////////
	private void appClose() {
		Preferences myPrefs =  Preferences.userNodeForPackage(HexEditor.class).node(this.getClass().getSimpleName());
		Dimension dim = frameBase.getSize();
		myPrefs.putInt("Height", dim.height);
		myPrefs.putInt("Width", dim.width);
		Point point = frameBase.getLocation();
		myPrefs.putInt("LocX", point.x);
		myPrefs.putInt("LocY", point.y);
		myPrefs.putInt("DividerLocation", splitPane.getDividerLocation());
		myPrefs = null;
	}//appClose

	private void appInit() {
		Preferences myPrefs =  Preferences.userNodeForPackage(HexEditor.class).node(this.getClass().getSimpleName());
		frameBase.setSize(myPrefs.getInt("Width", 761), myPrefs.getInt("Height", 693));
		frameBase.setLocation(myPrefs.getInt("LocX", 100), myPrefs.getInt("LocY", 100));
		splitPane.setDividerLocation(myPrefs.getInt("DividerLocation", 250));
		myPrefs = null;
	}// appInit

	public HexEditor() {
		initialize();
		appInit();
	}// Constructor
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frameBase = new JFrame();
		frameBase.setTitle("Base for GUI Application    0.0");
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
		
		JToolBar toolBar = new JToolBar();
		GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.anchor = GridBagConstraints.WEST;
		gbc_toolBar.fill = GridBagConstraints.VERTICAL;
		gbc_toolBar.insets = new Insets(0, 0, 5, 0);
		gbc_toolBar.gridx = 0;
		gbc_toolBar.gridy = 1;
		frameBase.getContentPane().add(toolBar, gbc_toolBar);
		
//		ImageIcon icon= new ImageIcon("C:\\Users\\admin\\git\\hexEditor\\hexEditor\\resources\\Computer.gif");
		ImageIcon icon= new ImageIcon("Ycomputer.gif");

		URL url = getClass().getClassLoader().getResource("YComputer.gif");
		System.out.printf("url = %s%n",url);

		btnFileNew = new JButton(icon);
		btnFileNew.setToolTipText("New");
		btnFileNew.setName(BTN_FILE_NEW);
		btnFileNew.setHorizontalAlignment(SwingConstants.LEFT);
		btnFileNew.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/new.png")));
		
		toolBar.add(btnFileNew);
		
		JButton btnFileOpen = new JButton("");
		btnFileOpen.setName(BTN_FILE_OPEN);
		btnFileOpen.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/open.png")));
		btnFileOpen.setToolTipText("Open");
		toolBar.add(btnFileOpen);
		
		JSeparator separator = new JSeparator();
		separator.setPreferredSize(new Dimension(5, 0));
		separator.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator);
		
		JButton btnFileSave = new JButton("");
		btnFileSave.setName(BTN_FILE_SAVE);
		btnFileSave.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/save.png")));
		btnFileSave.setToolTipText("Save");
		btnFileSave.setSelectedIcon(null);
		toolBar.add(btnFileSave);
		
		JSeparator separator_3 = new JSeparator();
		separator_3.setPreferredSize(new Dimension(5, 0));
		separator_3.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator_3);
		
		JButton btnFilePrint = new JButton("");
		btnFilePrint.setToolTipText("Print");
		btnFilePrint.setName(BTN_FILE_PRINT);
		btnFilePrint.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/print.png")));
		toolBar.add(btnFilePrint);
		
		JSeparator separator_4 = new JSeparator();
		separator_4.setPreferredSize(new Dimension(5, 0));
		separator_4.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator_4);
		
		JButton btnEditCut = new JButton("");
		btnEditCut.setToolTipText("Cut");
		btnEditCut.setName(BTN_EDIT_CUT);
		btnEditCut.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/cut.png")));
		toolBar.add(btnEditCut);
		
		JButton btnEditCopy = new JButton("");
		btnEditCopy.setName(BTN_EDIT_COPY);
		btnEditCopy.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/copy.png")));
		btnEditCopy.setToolTipText("Copy");
		toolBar.add(btnEditCopy);
		
		JButton btnEditPaste = new JButton("");
		btnEditPaste.setName(BTN_EDIT_PASTE);
		btnEditPaste.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/paste.png")));
		btnEditPaste.setToolTipText("Paste");
		toolBar.add(btnEditPaste);
		
		JSeparator separator_5 = new JSeparator();
		separator_5.setPreferredSize(new Dimension(10, 0));
		separator_5.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator_5);
		
		JButton btnEditUndo = new JButton("");
		btnEditUndo.setName(BTN_EDIT_UNDO);
		btnEditUndo.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/undo.png")));
		btnEditUndo.setToolTipText("Undo");
		toolBar.add(btnEditUndo);
		
		JButton btnEditRedo = new JButton("");
		btnEditRedo.setName(BTN_EDIT_REDO);
		btnEditRedo.setToolTipText("Redo");
		btnEditRedo.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/redo.png")));
		toolBar.add(btnEditRedo);
		
		splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.insets = new Insets(0, 0, 5, 0);
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 2;
		frameBase.getContentPane().add(splitPane, gbc_splitPane);
		
		JPanel panelStatus = new JPanel();
		panelStatus.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_panelStatus = new GridBagConstraints();
		gbc_panelStatus.fill = GridBagConstraints.BOTH;
		gbc_panelStatus.gridx = 0;
		gbc_panelStatus.gridy = 3;
		frameBase.getContentPane().add(panelStatus, gbc_panelStatus);

		JMenuBar menuBar = new JMenuBar();
		frameBase.setJMenuBar(menuBar);
		
		JMenu mnuFile = new JMenu("File");
		menuBar.add(mnuFile);
		
		mnuFileNew = new JMenuItem("New");
		mnuFileNew.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/new.png")));
		mnuFileNew.setName(MNU_FILE_NEW);
		mnuFileNew.addActionListener(applicationAdapter);
		mnuFile.add(mnuFileNew);
		
		JMenuItem mnuFileOpen = new JMenuItem("Open...");
		mnuFileOpen.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/open.png")));
		mnuFileOpen.setName(MNU_FILE_OPEN);
		mnuFileOpen.addActionListener(applicationAdapter);
		mnuFile.add(mnuFileOpen);
		
		JSeparator separator99 = new JSeparator();
		mnuFile.add(separator99);
		
		JMenuItem mnuFileSave = new JMenuItem("Save...");
		mnuFileSave.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/save.png")));
		mnuFileSave.setName(MNU_FILE_SAVE);
		mnuFileSave.addActionListener(applicationAdapter);
		mnuFile.add(mnuFileSave);
		
		JMenuItem mnuFileSaveAs = new JMenuItem("Save As...");
		mnuFileSaveAs.setName(MNU_FILE_SAVE_AS);
		mnuFileSaveAs.addActionListener(applicationAdapter);
		mnuFile.add(mnuFileSaveAs);
		
		JSeparator separator_2 = new JSeparator();
		mnuFile.add(separator_2);
		
		JMenuItem mnuFilePrint = new JMenuItem("Print...");
		mnuFilePrint.setIcon(new ImageIcon(HexEditor.class.getResource("/resources/print.png")));
		mnuFilePrint.setName(MNU_FILE_PRINT);
		mnuFilePrint.addActionListener(applicationAdapter);
		mnuFile.add(mnuFilePrint);
		
		
		JSeparator separator_1 = new JSeparator();
		mnuFile.add(separator_1);
		
		JMenuItem mnuFileExit = new JMenuItem("Exit");
		mnuFileExit.setName(MNU_FILE_EXIT);
		mnuFileExit.addActionListener(applicationAdapter);
		mnuFile.add(mnuFileExit);

		

	}// initialize
	static final String EMPTY_STRING = "";
	
	//////////////////////////////////////////////////////////////////////////
	private JFrame frameBase;

	//////////////////////////////////////////////////////////////////////////
	private static final String MNU_FILE_NEW = "mnuFileNew";
	private static final String MNU_FILE_OPEN = "mnuFileOpen";
	private static final String MNU_FILE_SAVE = "mnuFileSave";
	private static final String MNU_FILE_SAVE_AS = "mnuFileSaveAs";
	private static final String MNU_FILE_PRINT = "mnuFilePrint";
	private static final String MNU_FILE_EXIT = "mnuFileExit";
	
	private static final String MNU_EDIT_CUT = "mnuEditCut";
	private static final String MNU_EDIT_COPY = "mnuEditCopy";
	private static final String MNU_EDIT_PASTE = "mnuEditPaste";
	private static final String MNU_EDIT_UNDO = "mnuEditUndo";
	private static final String MNU_EDIT_REDO = "mnuEditREDO";

	
	
	
	private static final String BTN_FILE_NEW = "mnuFileNew";
	private static final String BTN_FILE_OPEN = "mnuFileOpen";
	private static final String BTN_FILE_SAVE = "mnuFileSave";
	private static final String BTN_FILE_SAVE_AS = "mnuFileSaveAs";
	private static final String BTN_FILE_PRINT = "mnuFilePrint";
	private static final String BTN_FILE_EXIT = "mnuFileExit";

	private static final String BTN_EDIT_CUT = "btnEditCut";
	private static final String BTN_EDIT_COPY = "btnEditCopy";
	private static final String BTN_EDIT_PASTE = "btnEditPaste";
	private static final String BTN_EDIT_UNDO = "btnEditUndo";
	private static final String BTN_EDIT_REDO = "btnEditREDO";

	
	private JSplitPane splitPane;
	private JMenuItem mnuFileNew;
	private JButton btnFileNew;
	//////////////////////////////////////////////////////////////////////////

	class ApplicationAdapter implements ActionListener {// , ListSelectionListener
		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			String name = ((Component) actionEvent.getSource()).getName();
			switch (name) {
			case MNU_FILE_NEW:
				doFileNew();
				break;
			case MNU_FILE_OPEN:
				doFileOpen();
				break;
			case MNU_FILE_SAVE:
				doFileSave();
				break;
			case MNU_FILE_SAVE_AS:
				doFileSaveAs();
				break;
			case MNU_FILE_PRINT:
				doFilePrint();
				break;
			case MNU_FILE_EXIT:
				doFileExit();
				break;
			}// switch
		}// actionPerformed
	}// class AdapterAction

}// class GUItemplate