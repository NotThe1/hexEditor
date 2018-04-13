package hexEditor;

import javax.swing.text.NavigationFilter;
import javax.swing.text.Position;

public class HexNavigation extends NavigationFilter {


	public void setDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias) {
		int position = dot % COLUMNS_PER_LINE; // Calculate the column position
		if (position == MID_LINE_SPACE - 1) {// actually the mid line break 23
			dot += 2;
		} else if (position == CHARS_PER_LINE) {// the last display character in the line.
			dot += (COLUMNS_PER_LINE - position);
		} else if (position <= MID_LINE_SPACE) {// before
			if (position % 3 == 2) {
				dot += 1;
			} // if in space
		} else {
			if (position % 3 == 0) {// after
				dot += 1;
			} // if in space
		} // if before/after mid line Break
			
		fb.setDot(dot, bias);

	}// setDot


	public static final int BYTES_PER_LINE = HEUtility.BYTES_PER_LINE_HEX; // Number of bytes from source file to display on each line
	public static final int CHARS_PER_BYTE = HEUtility.CHARS_PER_BYTE_HEX; // Number of chars used  to display on each byte on a line
	public static final int CHARS_PER_LINE = HEUtility.CHARS_PER_LINE_HEX; // Number of chars displayed in the Hex text pane
	public static final int COLUMNS_PER_LINE = HEUtility.COLUMNS_PER_LINE_HEX; // actual length of the line< includes /n/r
	public static final int MID_LINE_SPACE = HEUtility.MID_LINE_SPACE_HEX; // place where extra space is placed on the line

//	public static final int BYTES_PER_LINE = 16; // Number of bytes from source file to display on each line
//	public static final int CHARS_PER_BYTE = 3; // Number of chars used  to display on each byte on a line
//	public static final int CHARS_PER_LINE = (BYTES_PER_LINE * 3); // Number of chars displayed in the Hex text pane
//	public static final int COLUMNS_PER_LINE = CHARS_PER_LINE + 2; // actual length of the line< includes /n/r
//	public static final int MID_LINE_SPACE = (CHARS_PER_LINE / 2); // place where extra space is placed on the line

}// class HexDocumentNavigation
