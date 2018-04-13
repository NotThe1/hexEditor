package hexEditor;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;


public class AsciiFilter extends DocumentFilter {
	HexEditDisplayPanel host;
	
	public AsciiFilter(HexEditDisplayPanel host) {
		this.host = host;
	}//Constructor

	public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
			throws BadLocationException {
		String string;
		byte theByte = text.getBytes()[0];
		if ((theByte >= 0X20) && (theByte <= 126)) {
			string = text;
		} else {
			return;
//			string = UNPRINTABLE;
		} // if printable

		fb.replace(offset, length + 1, string, attrs);
		int sourceIndex = HEUtility.getAsciiSourceIndex(offset);
		int bias = host.getCurrentLineStart() * HEUtility.BYTES_PER_LINE_ASCII;
		System.out.printf("[AsciiFilter.replace] sourceIndex: %04X, bias: %04X, location: %04X%n ",
				sourceIndex,bias,sourceIndex+bias);

	}// replace

	private static final String UNPRINTABLE = ".";

}// class AsciiDocumentFilter
