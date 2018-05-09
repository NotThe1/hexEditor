package hexEditor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/* @formatter:off */

/**
 * 
 * @author Frank Martyn. This is a text box that only accepts either decimal or hexadecimal numbers.
 *         It supports a DefaultBoundedRangeModel to manage the values range of input data.
 *         The value input is limited byte the DefaultBoundedRangeModel. 
 *         If value is greater than Max the value will be fixed to Max Value.
 *         Similarly if less than Min, the value will be fixed at Min Value.
 * 
 *         The range of the value is Integer.Min to Integer.MAX.
 * 
 *         This class supports a HDNumberValueChangeListener that will fire a HDNumberValueChangeEvent
 *         when the value changes
 *        
 *         The method mute(boolean state) disables/enables the fireSeekValueChanged() method,
 *         so values can be changed without triggering events
 *         
 *			2018-03-01 - added setValueQuiet(int value);
 */
/* @formatter:on  */

public class HDNumberBox extends JPanel {
	private static final long serialVersionUID = 1L;

	DefaultBoundedRangeModel rangeModel = new DefaultBoundedRangeModel();

	int currentValue, priorValue;
	JFormattedTextField txtValueDisplay;
	EventListenerList hdNumberValueChangeListenerList;
	String decimalDisplayFormat = "%d";
	String hexDisplayFormat = "%X";
	boolean showDecimal = true;
	SeekDocument displayDoc;
	boolean muteNumberChangeEvent;

	public int getValue() {
		return currentValue;
	}// getValue

	public void setValue(int newValue) {
		setNewValue(newValue);
		return;
	}// setValue

	public void setValueQuiet(int newValue) {
		muteNumberChangeEvent = true;
		setNewValue(newValue);
		muteNumberChangeEvent = false;
	}// setValueQuiet

	public void setMaxValue(int newMaxValue) {
		rangeModel.setMaximum(newMaxValue);
	}// setMaxValue

	public void setMinValue(int newMinValue) {
		rangeModel.setMinimum(newMinValue);
	}// setMinValue

	public void setDecimalDisplay() {
		setDecimalDisplay(true);
	}// setDecimalDisplay

	public void setHexDisplay() {
		setDecimalDisplay(false);
	}// setHexDisplay

	public void setDecimalDisplay(boolean displayDecimal) {
		String tipText = "";
		showDecimal = displayDecimal;
		if (displayDecimal) {
			displayDoc.displayDecimal();
			tipText = "Display is Decimal";
		} else {
			displayDoc.displayHex();
			tipText = "Display is Hex";
		} // if
		displayValue();
		txtValueDisplay.setToolTipText(tipText);
	}// setHexDisplay

	public boolean isDecimalDisplay() {
		return showDecimal;
	}// isDecimalDisplay

	// ---------------------------------------

	private void displayValue() {
		String displayFormat = showDecimal ? decimalDisplayFormat : hexDisplayFormat;
		currentValue = (int) rangeModel.getValue();

		String stringValue = String.format(displayFormat, currentValue);
		txtValueDisplay.setText(stringValue);
		txtValueDisplay.repaint();
	}// showValue

	void setNewValue(int newValue) {
		newValue = Math.min(newValue, (int) rangeModel.getMaximum()); // upper
		newValue = Math.max(newValue, (int) rangeModel.getMinimum()); // lower

		priorValue = (int) rangeModel.getValue();
		currentValue = (newValue);
		rangeModel.setValue(currentValue);
		displayValue();
		if (muteNumberChangeEvent) {
			return;
		} // if
		if (priorValue != currentValue) {
			fireSeekValueChanged();
		} // if
	}// newValue


	// -------------------------------------------------------


	/* <><><><> */

	public HDNumberBox() {
		this(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, false);
	}// Constructor


	public HDNumberBox(boolean decimalDisplay) {
		this(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, decimalDisplay);
	}// Constructor

	public HDNumberBox(int minValue, int maxValue, int initValue, boolean decimalDisplay) {
		this.rangeModel.setMinimum(minValue);
		this.rangeModel.setMaximum(maxValue);
		this.rangeModel.setValue(initValue);
		// this.rangeModel.setExtent(maxValue - initValue);

		displayDoc = new SeekDocument(true);

		Initialize();
		appInit();

		if (decimalDisplay) {
			setDecimalDisplay();
		} else {
			setHexDisplay();
		} // if

	}// Constructor

	/* <><><><> */

	private void appInit() {
		currentValue = (int) rangeModel.getValue();
		txtValueDisplay.setDocument(displayDoc);
		txtValueDisplay.setPreferredSize(new Dimension(100, 23));
		hdNumberValueChangeListenerList = new EventListenerList();
		muteNumberChangeEvent = false;
	}// appInit

	private void Initialize() {
		setPreferredSize(new Dimension(286, 35));

		setBorder(UIManager.getBorder("TextField.border"));

		txtValueDisplay = new JFormattedTextField();
		txtValueDisplay.setMaximumSize(new Dimension(0, 0));
		txtValueDisplay.setMinimumSize(new Dimension(75, 20));
		txtValueDisplay.setBackground(UIManager.getColor("TextArea.background"));
		txtValueDisplay.setFont(new Font("Courier New", Font.PLAIN, 13));
		txtValueDisplay.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				if (txtValueDisplay.getText().equals("")) {
					return;
				} // if null
				int radix = showDecimal ? 10 : 16;
				try {
					setNewValue(Integer.valueOf(txtValueDisplay.getText(), radix));
				} catch (Exception e) {
					setNewValue(rangeModel.getValue());
				} // try
			}// focusLost
		});
		setLayout(new GridLayout(0, 1, 0, 0));

		txtValueDisplay.setHorizontalAlignment(SwingConstants.RIGHT);
		txtValueDisplay.setPreferredSize(new Dimension(100, 23));
		add(txtValueDisplay);
	}// Constructor

	// ---------------------------
	public void addHDNumberValueChangedListener(HDNumberValueChangeListener seekValueChangeListener) {
		hdNumberValueChangeListenerList.add(HDNumberValueChangeListener.class, seekValueChangeListener);
	}// addSeekValueChangedListener

	public void removeHDNumberValueChangedListener(HDNumberValueChangeListener seekValueChangeListener) {
		hdNumberValueChangeListenerList.remove(HDNumberValueChangeListener.class, seekValueChangeListener);
	}// addSeekValueChangedListener

	protected void fireSeekValueChanged() {
		Object[] listeners = hdNumberValueChangeListenerList.getListenerList();
		// process
		HDNumberValueChangeEvent hdNumberValueChangeEvent = new HDNumberValueChangeEvent(this, priorValue,
				currentValue);

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == HDNumberValueChangeListener.class) {
				((HDNumberValueChangeListener) listeners[i + 1]).valueChanged(hdNumberValueChangeEvent);
			} // if
		} // for

	}// fireSeekValueChanged

	// ---------------------------
	class SeekDocument extends PlainDocument {
		private static final long serialVersionUID = 1L;

		private String inputPattern;

		SeekDocument(boolean decimalDisplay) {
			if (decimalDisplay == true) {
				displayDecimal();
			} else {
				displayHex();
			} // if
		}// Constructor

		public void displayDecimal() {
			inputPattern = "-??[0-9]*";

		}// displayDecimal

		public void displayHex() {
			inputPattern = "[A-F|a-f|0-9]+";
		}// displayHex

		public void insertString(int offSet, String string, AttributeSet attributeSet) throws BadLocationException {
			if (string == null) {
				return;
			} // if

			if (!string.matches(inputPattern)) {
				return;
			} // for

			super.insertString(offSet, string, attributeSet);
		}// insertString
	}// class SeekDocument
		// ______________________________

}// class HDNumberBox
