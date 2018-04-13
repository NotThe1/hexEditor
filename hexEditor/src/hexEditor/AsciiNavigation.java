package hexEditor;

import javax.swing.text.NavigationFilter;
import javax.swing.text.Position;

public class AsciiNavigation extends NavigationFilter {


	public void setDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias) {
		int position = dot % COLUMNS_PER_LINE; // Calculate the column position
		if (position == CHARS_PER_LINE) {// the last display character in the line.
			dot += (COLUMNS_PER_LINE - position);
		} else if (position == MID_LINE_SPACE) {// at position
			dot += 1;
		} // if before/after mid line Break

	
		fb.setDot(dot, bias);
	}// setDot

//	private static final int BYTES_PER_LINE = HEUtility.BYTES_PER_LINE_ASCII; // Number of bytes from source file to display on each line
//	private static final int CHARS_PER_BYTE = HEUtility.CHARS_PER_BYTE_ASCII; // Number of chars used  to display on each byte on a line
	private static final int CHARS_PER_LINE = HEUtility.CHARS_PER_LINE_ASCII; // Number of chars displayed in the Hex text pane
	private static final int COLUMNS_PER_LINE = HEUtility.COLUMNS_PER_LINE_ASCII; // actual length of the line< includes /n/r
	private static final int MID_LINE_SPACE = HEUtility.MID_LINE_SPACE_ASCII; // place where extra space is placed on the line
}// AsciiDocumentNavigationFilter
