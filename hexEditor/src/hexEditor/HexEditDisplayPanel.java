package hexEditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.ByteBuffer;
import java.util.SortedMap;

import javax.swing.BoundedRangeModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class HexEditDisplayPanel extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;

	private AppLogger log = AppLogger.getInstance();

	private AdapterHexEditDisplay adapterHexEditDisplay = new AdapterHexEditDisplay();
	protected ByteBuffer source;
	protected int dataSize;
	private int currentAddress;
	private int bias;
	private int dataIndex;

	private int currentMax;
	private int currentLineStart;
	private int currentExtent;

	protected StyledDocument addrDoc;// = new DefaultStyledDocument();
	protected StyledDocument indexDoc;// = new DefaultStyledDocument();

	protected StyledDocument hexDoc;// = new DefaultStyledDocument();
	protected HexFilter hexFilter = new HexFilter(this);
	protected HexNavigation hexNavigation;// = new HexDocumentNavigation();

	protected StyledDocument asciiDoc = new DefaultStyledDocument();
	protected AsciiFilter asciiFilter = new AsciiFilter(this);
	protected AsciiNavigation asciiNavigation; // = new AsciiNavigation();

	protected SortedMap<Integer, Byte> changes;

	private SimpleAttributeSet addressAttributes;
	private SimpleAttributeSet dataAttributes;
	private SimpleAttributeSet asciiAttributes;

	// public void test() {

	// }// test

	@Override
	public void run() {
		clearAllDocs();
		bias = 0;
		dataIndex = 0;
		currentAddress = 0;
		currentLineStart = 0;
		setUpScrollBar();

		fillPane();
	}// run
	
	public int getCurrentLineStart() {
		return this.currentLineStart;
	}//getCurrentLineStart

	void fillPane() {
		if (currentExtent == 0) {
			return;
		} // if nothing to display

		// clearFilters(); // suspend doc filter

		setTextPanesCaretListeners(false);
		clearAllDocs();

		int sourceIndex = currentLineStart * LINE_SIZE; // address to display

		byte[] activeLine = new byte[LINE_SIZE];
		String message = String.format("currentExtent: %04X, currentMax: %04X, currentLineStart: %04X, currentMax - currentLineStart: %04X%n",
				currentExtent, currentMax,currentLineStart,currentMax - currentLineStart);
		System.out.println(message);
		int linesToDisplay = Math.min(currentExtent, currentMax - currentLineStart);

		int bytesToRead = LINE_SIZE;
		source.position(sourceIndex);
		for (int i = 0; i < linesToDisplay; i++) {
			source.get(activeLine, 0, bytesToRead);
			byte[] processedData = applyChanges(activeLine, bytesToRead, sourceIndex);
			processLine(processedData, bytesToRead, sourceIndex);
			sourceIndex += bytesToRead;
			if (bytesToRead < LINE_SIZE) {
				// leave the byte count for the last sector set in bytesToRead
				break;
			} // if
			bytesToRead = Math.min(source.remaining(), LINE_SIZE);
			if (bytesToRead == 0) {
				bytesToRead = LINE_SIZE;
				break;
			} // if
		} // for
			// restoreFilters();
			// hexNavigationFilter.setLastLine(bytesToRead, linesToDisplay - 1);
		setTextPanesCaretListeners(true);
		textHex.setCaretPosition(0);
	}// fillPane

	private void processLine(byte[] rawData, int bytesRead, int bufferAddress) {//
		addHexLineToDocument(bufferAddress, bias, rawData);
		addAsciiLineToDocument(rawData);

		// StringBuilder sbData = new StringBuilder();
		// for (int i = 0; i < bytesRead; i++) {
		// if ((i % 8) == 0) {
		// sbData.append(SPACE);
		// } // if data extra space
		// sbData.append(String.format(hexCharacterFormat, rawData[i]));
		// } // for
		//
		// String bufferAddressStr = String.format(addressFormat, bufferAddress);
		// String dataStr = String.format(dataFormat, sbData.toString());
		// String asciiStr = getASCII(rawData, bytesRead);
		//
		// try {
		// doc.insertString(doc.getLength(), bufferAddressStr, addressAttributes);
		// doc.insertString(doc.getLength(), dataStr, dataAttributes);
		// doc.insertString(doc.getLength(), asciiStr, asciiAttributes);
		// } catch (BadLocationException e) {
		// e.printStackTrace();
		// } // try
		// return bufferAddress + bytesRead;
	}// processLine

	protected byte[] applyChanges(byte[] rawData, int bytesRead, int bufferAddress) {
		// byte[] ans = rawData.clone();
		// SortedMap<Integer, Byte> rowChanges = changes.subMap(bufferAddress, bufferAddress + bytesRead);
		//
		// if (rowChanges.size() != 0) {
		// rowChanges.forEach((k, v) -> ans[(int) k - bufferAddress] = (byte) v);
		// } // if need to update
		// return ans;
		return rawData;
	}// applyChanges

	public void clear() {
		clearAllDocs();
	}// clear

	private void clearAllDocs() {
		try {
			addrDoc.remove(0, addrDoc.getLength());
			hexDoc.remove(0, hexDoc.getLength());
			asciiDoc.remove(0, asciiDoc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		} // try

	}// clearAllDocs

	private void setTextPanesCaretListeners(boolean status) {
		/* status true = on/enables; false = off/disabled */

		if (status) {
			textAddr.addCaretListener(adapterHexEditDisplay);
			textHex.addCaretListener(adapterHexEditDisplay);
			textAscii.addCaretListener(adapterHexEditDisplay);
		} else {
			textAddr.removeCaretListener(adapterHexEditDisplay);
			textHex.removeCaretListener(adapterHexEditDisplay);
			textAscii.removeCaretListener(adapterHexEditDisplay);
		} //

	}// setTextPanesEnabled

	private void clearDoc(StyledDocument doc) {
		try {
			doc.remove(0, doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		} // try
	}// clearDoc

	private void addAsciiLineToDocument(byte[] data) {
		StringBuilder sb = new StringBuilder();

		byte b;
		for (int i = 0; i < LINE_SIZE; i++) {
			if (i == 8) {
				sb.append(SPACE);
			} //
			b = data[i];
			if ((b >= 0X20) && (b <= 126)) {
				sb.append((char) b);
			} else {
				sb.append(UNPRINTABLE);
			} // if printable
		} // for
		sb.append(System.lineSeparator());
		String strData = sb.toString();
		try {
			asciiDoc.insertString(asciiDoc.getLength(), strData, asciiAttributes);
		} catch (BadLocationException e) {
			e.printStackTrace();
		} // try
	}// addLineToDocument

	private void addHexLineToDocument(int currentAddress, int bias, byte[] data) {
		String strAddress = getAddress(currentAddress, bias);
		String strData = getData(data);

		try {
			// hexDoc.remove(0, hexDoc.getLength());
			addrDoc.insertString(addrDoc.getLength(), strAddress, addressAttributes);
			addrDoc.insertString(addrDoc.getLength(), System.lineSeparator(), null);

			hexDoc.insertString(hexDoc.getLength(), strData, dataAttributes);
			hexDoc.insertString(hexDoc.getLength(), System.lineSeparator(), null);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}// addLineToDocument

	private String getAddress(int currentAddress, int bias) {
		return String.format(FORMAT_ADDR, currentAddress + bias);
	}// getAddress

	private String getData(byte[] data) {
		return String.format(FORMAT_DATA, data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7],
				data[8], data[9], data[10], data[11], data[12], data[13], data[14], data[15]);
	}// getData

	public void setData(ByteBuffer data) {
		source = data.duplicate();
		// source = buffer.
	}// setData

	public void setData(byte[] data) {
		dataSize = data.length;
		int bufferSize = dataSize;
		if ((dataSize % LINE_SIZE) != 0) {
			bufferSize = (LINE_SIZE - (dataSize % LINE_SIZE)) + dataSize;
		} // buffer Size
		source = ByteBuffer.allocate(bufferSize);
		source.put(data);
		source.rewind();
	}// setData

	protected void setUpScrollBar() {
		if (source == null) {
			return;
		} // if
			// javax.swing.SwingUtilities.invokeLater(new Runnable() {
			// public void run() {
		currentMax = maximumNumberOfRows(textHex);
		currentExtent = calcExtent(textHex);

		BoundedRangeModel model = scrollBar.getModel();
		model.setMinimum(0);
		model.setMaximum(currentMax);
		model.setValue(0);
		model.setExtent(currentExtent);

		scrollBar.setBlockIncrement(currentExtent - 2);
		// }// run
		// });
		scrollBar.updateUI();
	}// setUpScrollBar

	private int calcFontHeight(JTextPane thisPane) {
		Font font = thisPane.getFont();
		return thisPane.getFontMetrics(font).getHeight();
	}// calcFontHeight

	private int calcUsableHeight(JTextPane thisPane) {
		Insets insets = thisPane.getInsets();
		Dimension dimension = thisPane.getSize();
		return dimension.height - (insets.bottom + insets.top);
	}// calcUsableHeight

	private int calcExtent(JTextPane thisPane) {
		int useableHeight = calcUsableHeight(thisPane);
		int fontHeight = calcFontHeight(thisPane);
		return (int) (useableHeight / fontHeight);
	}// calcExtent

	private int maximumNumberOfRows(JTextPane thisTextPane) {
		return (source.capacity() / LINE_SIZE) + 1;
	}// maximumNumberOfRows

	private void makeStyles() {
		SimpleAttributeSet baseAttributes = new SimpleAttributeSet();
		StyleConstants.setFontFamily(baseAttributes, "Courier New");
		StyleConstants.setFontSize(baseAttributes, 16);

		addressAttributes = new SimpleAttributeSet(baseAttributes);
		StyleConstants.setForeground(addressAttributes, Color.GRAY);

		dataAttributes = new SimpleAttributeSet(baseAttributes);
		StyleConstants.setForeground(dataAttributes, Color.BLACK);

		asciiAttributes = new SimpleAttributeSet(baseAttributes);
		StyleConstants.setForeground(asciiAttributes, Color.BLUE);

	}// makeStyles1
		//////////////////////////////////////////////////////////////////////////////////////

	public HexEditDisplayPanel() {
		setPreferredSize(new Dimension(802, 500));
		setMaximumSize(new Dimension(802, 32767));
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		// setAlignmentY(Component.TOP_ALIGNMENT);
		// setAlignmentX(Component.LEFT_ALIGNMENT);
		initialize();
		appInit();
	}// Constructor

	private void appInit() {

		hexDoc = textHex.getStyledDocument();
//		hexFilter = new HexFilter(this);

		((AbstractDocument) hexDoc).setDocumentFilter(hexFilter);
		hexNavigation = new HexNavigation();
		textHex.getCaret().setVisible(false);

		textHex.setNavigationFilter(hexNavigation);

		asciiDoc = textAscii.getStyledDocument();
//		asciiFilter = new AsciiFilter(this);
		((AbstractDocument) asciiDoc).setDocumentFilter(asciiFilter);
		asciiNavigation = new AsciiNavigation();
		// asciiNavigation = new AsciiNavigation(textAscii);

		textAscii.setNavigationFilter(asciiNavigation);

		makeStyles();

		addrDoc = textAddr.getStyledDocument();
		indexDoc = textIndex.getStyledDocument();
		try {
			indexDoc.remove(0, indexDoc.getLength());
			indexDoc.insertString(0, INDEX_DATA, addressAttributes);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}// appInit

	private void initialize() {
		// setPreferredSize(new Dimension(0, 0));
		setMinimumSize(new Dimension(802, 0));
		// setMaximumSize(new Dimension(0, 0));

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 92, 300, 170, 17 };
		gridBagLayout.rowHeights = new int[] { 21, 21 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0 };
		setLayout(gridBagLayout);

		lblNewLabel_1 = new JLabel("00000000:");
		lblNewLabel_1.setVisible(false);
		lblNewLabel_1.setPreferredSize(new Dimension(92, 14));
		lblNewLabel_1.setMinimumSize(new Dimension(92, 14));
		lblNewLabel_1.setMaximumSize(new Dimension(92, 14));
		lblNewLabel_1.setForeground(Color.RED);
		lblNewLabel_1.setFont(new Font("Courier New", Font.BOLD, 16));
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 0;
		add(lblNewLabel_1, gbc_lblNewLabel_1);

		textIndex = new JTextPane();
		textIndex.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		textIndex.setFont(new Font("Courier New", Font.BOLD, 16));
		GridBagConstraints gbc_textIndex = new GridBagConstraints();
		gbc_textIndex.anchor = GridBagConstraints.WEST;
		gbc_textIndex.insets = new Insets(0, 0, 5, 5);
		gbc_textIndex.gridx = 1;
		gbc_textIndex.gridy = 0;
		add(textIndex, gbc_textIndex);
		textIndex.setFont(new Font("Courier New", Font.BOLD, 16));

		lblCodeType = new JLabel("UTF-8");
		lblCodeType.setFont(new Font("Courier New", Font.PLAIN, 16));
		GridBagConstraints gbc_lblCodeType = new GridBagConstraints();
		gbc_lblCodeType.insets = new Insets(0, 0, 5, 5);
		gbc_lblCodeType.gridx = 2;
		gbc_lblCodeType.gridy = 0;
		add(lblCodeType, gbc_lblCodeType);

		textAddr = new JTextPane();
		textAddr.setName("textAddr");
		textAddr.setEditable(false);
		textAddr.addMouseWheelListener(adapterHexEditDisplay);
		textAddr.setMaximumSize(new Dimension(90, 2147483647));
		textAddr.setPreferredSize(new Dimension(92, 0));
		textAddr.setMinimumSize(new Dimension(92, 0));
		GridBagConstraints gbc_textAddr = new GridBagConstraints();
		gbc_textAddr.insets = new Insets(0, 0, 0, 5);
		gbc_textAddr.fill = GridBagConstraints.BOTH;
		gbc_textAddr.gridx = 0;
		gbc_textAddr.gridy = 1;
		add(textAddr, gbc_textAddr);

		textHex = new JTextPane();
		textHex.setName(TEXT_HEX);
		textHex.addCaretListener(adapterHexEditDisplay);
		textHex.addComponentListener(adapterHexEditDisplay);
		textHex.addMouseWheelListener(adapterHexEditDisplay);
		textHex.setPreferredSize(new Dimension(410, 0));
		textHex.setMinimumSize(new Dimension(410, 0));
		textHex.setFont(new Font("Courier New", Font.BOLD, 16));
		GridBagConstraints gbc_textHex = new GridBagConstraints();
		gbc_textHex.fill = GridBagConstraints.BOTH;
		gbc_textHex.insets = new Insets(0, 0, 0, 5);
		gbc_textHex.gridx = 1;
		gbc_textHex.gridy = 1;
		add(textHex, gbc_textHex);
		textHex.setBorder(null);

		textAscii = new JTextPane();
		textAscii.setName(TEXT_ASCII);
		textAscii.addCaretListener(adapterHexEditDisplay);
		textAscii.addMouseWheelListener(adapterHexEditDisplay);
		textAscii.setMinimumSize(new Dimension(170, 0));
		textAscii.setPreferredSize(new Dimension(170, 0));
		textAscii.setBorder(null);
		textAscii.setFont(new Font("Courier New", Font.BOLD, 16));
		// textAscii.setText("01234567 89ABCDEF");
		GridBagConstraints gbc_textASCII = new GridBagConstraints();
		gbc_textASCII.insets = new Insets(0, 0, 0, 5);
		gbc_textASCII.fill = GridBagConstraints.BOTH;
		gbc_textASCII.gridx = 2;
		gbc_textASCII.gridy = 1;
		add(textAscii, gbc_textASCII);

		scrollBar = new JScrollBar();
		scrollBar.addAdjustmentListener(adapterHexEditDisplay);
		scrollBar.addMouseWheelListener(adapterHexEditDisplay);
		GridBagConstraints gbc_scrollBar = new GridBagConstraints();
		gbc_scrollBar.fill = GridBagConstraints.VERTICAL;
		gbc_scrollBar.gridx = 3;
		gbc_scrollBar.gridy = 1;
		add(scrollBar, gbc_scrollBar);
	}// initialize

	private static final String FORMAT_DATA = "%02X %02X %02X %02X %02X %02X %02X %02X  %02X %02X %02X %02X %02X %02X %02X %02X";
	private static final String FORMAT_ADDR = "%08X:";

	private static final int LINE_SIZE = 16;
	private static final String UNPRINTABLE = ".";
	private static final String SPACE = " ";

	private static final String TEXT_HEX = "textHex";
	private static final String TEXT_ASCII = "textAscii";
	private static final String INDEX_DATA = "00 01 02 03 04 05 06 07  08 09 0A 0B 0C 0D 0E 0F";

	private JScrollBar scrollBar;
	private JLabel lblCodeType;
	private JTextPane textHex;
	private JTextPane textAscii;
	private JLabel lblNewLabel_1;
	private JTextPane textAddr;
	private JTextPane textIndex;

	///////////////////////////////////////////////////////////

	class AdapterHexEditDisplay implements AdjustmentListener, ComponentListener, MouseWheelListener, CaretListener {// ,ChangeListener{

		/* ----------------- AdjustmentListener --------------- */
		@Override
		public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
			if (adjustmentEvent.getValueIsAdjusting()) {
				return;
			} // if
			if (adjustmentEvent.getAdjustmentType() != AdjustmentEvent.TRACK) {
				return;
			} // if
			currentLineStart = adjustmentEvent.getValue();
			// spinnerAddress.setValue(LINE_SIZE * currentLineStart);
			fillPane();
		}// adjustmentValueChanged

		/* ----------------- ComponentListener --------------- */

		@Override
		public void componentResized(ComponentEvent componentEvent) {
			setUpScrollBar();
		}// componentResized

		@Override
		public void componentHidden(ComponentEvent componentEvent) {
			// TODO Auto-generated method stub

		}// componentHidden

		@Override
		public void componentMoved(ComponentEvent componentEvent) {
			// TODO Auto-generated method stub

		}// componentMoved

		@Override
		public void componentShown(ComponentEvent componentEvent) {
			// TODO Auto-generated method stub

		}// componentShown

		/* ----------------- MouseWheelListener --------------- */
		@Override
		public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
			// int increment = scrollBar.getUnitIncrement(1);
			scrollBar.setValue(scrollBar.getValue() + mouseWheelEvent.getWheelRotation());
		}// mouseWheelMoved

		// /* ----------------- CaretListener ---------------*/
		private Highlighter highlighterSource, highlighterOther;
		private Highlighter highlighterAddress;
		private Highlighter highlighterIndex;
		private Highlighter.HighlightPainter highlightPainterYellow = new DefaultHighlighter.DefaultHighlightPainter(
				Color.YELLOW);
		private Highlighter.HighlightPainter highlightPainterPink = new DefaultHighlighter.DefaultHighlightPainter(
				Color.PINK);

		Object tag;

		@Override
		public void caretUpdate(CaretEvent caretEvent) {
			int dotStart = caretEvent.getDot();
			int dotEnd = dotStart + 1;
			int otherDotStart, otherDotEnd;
			int asciiDot;
			int addressDotStart, addressDotEnd;
			int indexDotStart, indexDotEnd;
			String name = ((Component) caretEvent.getSource()).getName();
			String message = "no Message";
			highlighterIndex = textIndex.getHighlighter();
			highlighterAddress = textAddr.getHighlighter();
			highlighterSource = ((JTextComponent) caretEvent.getSource()).getHighlighter();
			if (name.equals(TEXT_HEX)) {
				highlighterOther = textAscii.getHighlighter();
				otherDotStart = HEUtility.getAsciiDot(dotStart);
				otherDotEnd = otherDotStart + 1;
				asciiDot = otherDotStart;
			} else if (name.equals(TEXT_ASCII)) {
				highlighterOther = textHex.getHighlighter();
				otherDotStart = HEUtility.getHexDot(dotStart);
				otherDotEnd = otherDotStart + 2;
				asciiDot = dotStart;
			} else {
				return; // no a good event
			} // if

			addressDotStart = HEUtility.getAddressDot(asciiDot);
			addressDotEnd = addressDotStart + HEUtility.CHARS_PER_LINE_ADDR;
			indexDotStart = HEUtility.getIndexDot(asciiDot);
			indexDotEnd = indexDotStart + 2;

			try {
				clearHighlights(highlighterSource);
				clearHighlights(highlighterOther);
				tag = highlighterSource.addHighlight(dotStart, dotEnd, highlightPainterYellow);
				tag = highlighterOther.addHighlight(otherDotStart, otherDotEnd, highlightPainterYellow);

				clearHighlights(highlighterAddress);
				clearHighlights(highlighterIndex);
				tag = highlighterAddress.addHighlight(addressDotStart, addressDotEnd, highlightPainterPink);
				tag = highlighterIndex.addHighlight(indexDotStart, indexDotEnd, highlightPainterPink);

			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // try

		}// caretUpdate

		private void clearHighlights(Highlighter highlighter) {
			Highlighter.Highlight[] highlights = highlighter.getHighlights();
			for (Highlighter.Highlight hightlight : highlights) {
				highlighter.removeHighlight(hightlight);
			} // for each highlighted
		}// clearHighlights

		// private Point hexToAsciiPosition(int row, int byteIndex) {
		//
		// }

		// /* ----------------- ChangeListener ---------------*/
		// @Override
		// public void stateChanged(ChangeEvent e) {
		// // TODO Auto-generated method stub
		//
		// }//stateChanged

	}// class adapterHexEditDisplay

	class Cell {
		int row, col;
	}// class Cell

}// class HexEditDisplayPanel
