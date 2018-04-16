package hexEditor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class HexFilter extends DocumentFilter {
	HexEditDisplayPanel host;
	
	public HexFilter() {		
	}//Constructor
	public HexFilter(HexEditDisplayPanel host) {
		this.host = host;
	}//Constructor
	
//	public DocumentFilter.FilterBypass getByPass(){
//		return DocumentFilter.FilterBypass;
//	}//

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
			int location = sourceIndex+bias;
			Document doc = fb.getDocument();
			String subject = doc.getText(offset-1, 3).trim();
			int newValue = Integer.valueOf(subject, 16);
			byte value = (byte)newValue;
			host.updateSource(location, value);
			System.out.printf("[HexFilter.replace] value: %02X, text: %s, offset: %d,  location: %04X%n ",
					value,subject.trim(),offset,location);
			host.updateAscii(0, "X");
		} // if
	}// replace

	Pattern onehexPattern = Pattern.compile("[0123456789ABCDEFabcdef]{1}");

}// class HexDocumentFilter
