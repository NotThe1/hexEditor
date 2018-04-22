package hexEditor;

import javax.swing.text.NavigationFilter;
import javax.swing.text.Position;

public class HexNavigation extends NavigationFilter {
	int position;
	boolean inFirstHalf = true;
	boolean inData, inAscii;
	NavigationFilter.FilterBypass fb;
	int dot;
	Position.Bias bias;

	public void setDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias) {
		this.fb = fb;
		this.dot = dot;
		this.bias = bias;
		setDataOrAscii(dot);
		if (inData) {
			doData();
		} else if (inAscii) {
			doAscii();
		} else {
			return; // do nothing
		} // if data v Ascii
	}// setDot

	private void doData() {
//		System.out.println("[HexNavigation.doData] dot: " + dot);

		int position = dot % COLUMNS_PER_LINE; // Calculate the column position
		inFirstHalf = position < MID_LINE_SPACE_DATA ? true : false;
		if (position == MID_LINE_SPACE_DATA - 1) {// actually the mid line break 23
			dot += 2;
		} else if (position == LAST_COLUMN_DATA) {// the last display character in the line.
			dot += (COLUMNS_PER_LINE - (position));
		} else if (inFirstHalf) {// before
			if (position % 3 == 2) {
				dot += 1;
			} // if in space
		} else {
			if (position % 3 == 0) {// after
				dot += 1;
			} // if in space
		} // if before/after mid line Break

		fb.setDot(dot, bias);
	}// doData

	private void doAscii() {
//		System.out.println("[HexNavigation.doAscii] dot: " + dot);
		int position = (dot % COLUMNS_PER_LINE);
		inFirstHalf = position < MID_LINE_SPACE_ASCII ? true : false;

		if (position == LAST_COLUMN_ASCII) {// the last display character in the line.
			dot += (COLUMNS_PER_LINE - position)+ASCII_COL_START;
		} else if (position == MID_LINE_SPACE_ASCII) {
			dot += 1;
		}//if
		fb.setDot(dot, bias);
	}// doAscii

	private void setDataOrAscii(int dot) {
		int linePosition = dot % COLUMNS_PER_LINE;
		if (linePosition <= LAST_COLUMN_DATA) {
			inData = true;
			inAscii = false;
		} else {
			inData = false;
			inAscii = true;
		} // if
		return;
	}// InData

	public static final int BYTES_PER_LINE = HEUtility.BYTES_PER_LINE;

	public static final int CHARS_PER_BYTE_DATA = HEUtility.CHARS_PER_BYTE_DATA;
	public static final int MID_LINE_SPACE_DATA = HEUtility.MID_LINE_SPACE_DATA;
	public static final int LAST_COLUMN_DATA = HEUtility.LAST_COLUMN_DATA;

	public static final int ASCII_COL_START = HEUtility.ASCII_COL_START;
	public static final int CHARS_PER_BYTE_ASCII = HEUtility.CHARS_PER_BYTE_ASCII;
	public static final int MID_LINE_SPACE_ASCII = HEUtility.MID_LINE_SPACE_ASCII;
	public static final int LAST_COLUMN_ASCII = HEUtility.LAST_COLUMN_ASCII;

	public static final int COLUMNS_PER_LINE = HEUtility.COLUMNS_PER_LINE;// CR,LF

}// class HexDocumentNavigation
