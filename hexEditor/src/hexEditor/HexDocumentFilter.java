package hexEditor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class HexDocumentFilter extends DocumentFilter {

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
		System.out.printf("[HexDocumentFilter.replace] offset: %d,length: %d ,text: %s%n",offset,length,text);

		Matcher m = onehexPattern.matcher(text);
		if ((length==0) && m.matches()) { // replace one Hex digit char
			int position = offset % COLUMNS_PER_LINE; // Calculate the column position		
			fb.replace(offset, 1, text.toUpperCase(), attrs);
		}else {
			// TODO - a real replace
		}
//		fb.replace(offset, length, text, attrs);
	}// replace
	
	Pattern onehexPattern = Pattern.compile("[0123456789ABCDEFabcdef]{1}");
	
	private static final int BYTES_PER_LINE = HexDocumentNavigation.BYTES_PER_LINE; // Number of bytes from source file to display on each line

	private static final int CHARS_PER_LINE = HexDocumentNavigation.CHARS_PER_LINE; // Number of chars displayed in the Hex text pane
	private static final int COLUMNS_PER_LINE = HexDocumentNavigation.COLUMNS_PER_LINE; // actual length of the line< includes /n/r
//	private static final int MID_LINE_SPACE = HexDocumentNavigation.MID_LINE_SPACE; // place where extra space is placed on the line


}// class HexDocumentFilter
