package hexEditor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class HexFilter extends DocumentFilter {
	HexEditDisplayPanel host;
	// DocumentFilter.FilterBypass fb;
	// int offset;
	// int length;
	// String text;
	// AttributeSet attrs;

	public HexFilter() {
	}// Constructor

	public HexFilter(HexEditDisplayPanel host) {
		this.host = host;
		HEUtility.makeStyles();
	}// Constructor

	@Override
	public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr)
			throws BadLocationException {
		fb.insertString(offset, string.toUpperCase(), attr);
	}// insertString

	public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
		fb.remove(offset, length);
	}// remove

	public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
			throws BadLocationException {

		if ((offset % COLUMNS_PER_LINE) < LAST_COLUMN_DATA) { // data
			Matcher m = onehexPattern.matcher(text);
			if ((length == 0) && m.matches()) { // replace one Hex digit char
				fb.replace(offset, 1, text.toUpperCase(), HEUtility.dataAttributes);
//				fb.replace(offset, 1, text.toUpperCase(), attrs);
			}else {
				return; //do nothing
			}//if data
		} else {// Ascii
			String string;
			byte theByte = text.getBytes()[0];
			if ((theByte >= 0X20) && (theByte <= 126)) {
				string = text;
			} else {
				return;
//				string = UNPRINTABLE;
			} // if printable

			fb.replace(offset, length + 1, string, HEUtility.asciiAttributes);
//			fb.replace(offset, length + 1, string, attrs);
		} // if
	}// replace

	Pattern onehexPattern = Pattern.compile("[0123456789ABCDEFabcdef]{1}");
	private static final String UNPRINTABLE = ".";
	private static final int LAST_COLUMN_DATA = HEUtility.LAST_COLUMN_DATA;
	private static final int COLUMNS_PER_LINE = HEUtility.COLUMNS_PER_LINE;
}// class HexDocumentFilter
