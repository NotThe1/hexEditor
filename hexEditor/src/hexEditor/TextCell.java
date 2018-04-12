package hexEditor;

public class TextCell {

	private TextCell() {
	}// Constructor

	private static int getDot(int dot, String type) {
		int bytesPerLine = AsciiNavigation.BYTES_PER_LINE;
		int columnsPerLineSource;
		int midLineSpaceSource;
		int charsPerByteSource;
		int charPerLineOther;
		int charsPerByteOther;
		int columnsPerLineOther;
		
		if (type.equals(TYPE_HEX)) {
			columnsPerLineSource = AsciiNavigation.COLUMNS_PER_LINE;
			midLineSpaceSource = AsciiNavigation.MID_LINE_SPACE;
			charsPerByteSource = AsciiNavigation.CHARS_PER_BYTE;

			charPerLineOther = HexNavigation.CHARS_PER_LINE;
			charsPerByteOther = HexNavigation.CHARS_PER_BYTE;
			columnsPerLineOther = HexNavigation.COLUMNS_PER_LINE;

		} else if (type.equals(TYPE_ASCII)) {
			columnsPerLineSource = HexNavigation.COLUMNS_PER_LINE;
			midLineSpaceSource = HexNavigation.MID_LINE_SPACE;
			charsPerByteSource = HexNavigation.CHARS_PER_BYTE;

			charPerLineOther = AsciiNavigation.CHARS_PER_LINE;
			charsPerByteOther = AsciiNavigation.CHARS_PER_BYTE;
			columnsPerLineOther = AsciiNavigation.COLUMNS_PER_LINE;

		} else {
			return 0;
		}	

			int position = dot % columnsPerLineSource; // Calculate the column position
			int lineNumber = dot / columnsPerLineSource;
			int rawByteIndex = position >= midLineSpaceSource ? position - 1 : position;
			int byteIndex = rawByteIndex / charsPerByteSource;

			int otherLineStart = lineNumber * columnsPerLineOther;
			int otherByteIndex = byteIndex * charsPerByteOther;
			otherByteIndex = rawByteIndex >= (bytesPerLine / 2) ? otherByteIndex + 1 : otherByteIndex;

			return otherLineStart + otherByteIndex;
	}



	

	public static int getAsciiDot(int dot ) {
		return getDot(dot, TYPE_ASCII);
	}// Factory for ascii text cell

	public static int getHexDot(int dot ){
		return getDot(dot, TYPE_HEX);
	}//  Factory for hex text cell

	private static final String TYPE_HEX = "hex";
	private static final String TYPE_ASCII = "ascii";

}// Class TextCell
