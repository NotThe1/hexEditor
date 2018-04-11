package hexEditor;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;


public class AsciiDocumentFilter extends DocumentFilter {

	public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
			throws BadLocationException {
		System.out.printf("[AsciiDocumentFilter.replace] offset: %d,length: %d ,text: %s%n", offset, length, text);
		String string;
		byte theByte = text.getBytes()[0];
		if ((theByte >= 0X20) && (theByte <= 126)) {
			string = text;
		} else {
			string = UNPRINTABLE;
		} // if printable

		System.out.printf("    byte = %02X%n", theByte);
		// Matcher m = onehexPattern.matcher(text);
		// if ((length==0) && m.matches()) { // replace one Hex digit char
		// int position = offset % COLUMNS_PER_LINE; // Calculate the column position
		// fb.replace(offset, 1, text.toUpperCase(), attrs);
		// }else {
		// // TODO - a real replace
		// }
		fb.replace(offset, length + 1, string, attrs);
	}// replace

	private static final int BYTES_PER_LINE = AsciiNavigation.BYTES_PER_LINE; // Number of bytes from source file to
																				// display on each line
	private static final int CHARS_PER_LINE = AsciiNavigation.CHARS_PER_LINE; // Number of chars displayed in the Hex
																				// text pane
	private static final int COLUMNS_PER_LINE = AsciiNavigation.COLUMNS_PER_LINE; // actual length of the line< includes
																					// /n/r
	private static final int MID_LINE_SPACE = AsciiNavigation.MID_LINE_SPACE; // place where extra space is placed on
																				// the line

	private static final String UNPRINTABLE = ".";

}// class AsciiDocumentFilter
