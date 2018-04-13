package hexEditor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class HexFilter extends DocumentFilter {
	HexEditDisplayPanel host;
	
	public HexFilter() {		
	}//Constructor
	public HexFilter(HexEditDisplayPanel host) {
		this.host = host;
	}//Constructor

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

		Matcher m = onehexPattern.matcher(text);
		if ((length == 0) && m.matches()) { // replace one Hex digit char
			fb.replace(offset, 1, text.toUpperCase(), attrs);
			int sourceIndex = HEUtility.getHexSourceIndex(offset);
			int bias = host.getCurrentLineStart() * HEUtility.BYTES_PER_LINE_HEX;
			System.out.printf("[HexFilter.replace] sourceIndex: %04X, bias: %04X, location: %04X%n ",
					sourceIndex,bias,sourceIndex+bias);
		} // if
	}// replace

	Pattern onehexPattern = Pattern.compile("[0123456789ABCDEFabcdef]{1}");

}// class HexDocumentFilter
