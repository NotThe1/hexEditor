package hexEditor;

/* HexEditor Utility */
public class HEUtility {

	public static int getAsciiDot(int hexDot) {
		int position = hexDot % COLUMNS_PER_LINE_HEX; // Calculate the column position
		boolean pastMidLine = position > MID_LINE_SPACE_HEX ;
		int lineNumber = hexDot / COLUMNS_PER_LINE_HEX;
		int rawByteIndex = pastMidLine? position - 1 : position; // >= 
		int byteIndex = rawByteIndex / CHARS_PER_BYTE_HEX;

		int otherLineStart = lineNumber * COLUMNS_PER_LINE_ASCII;
		int otherByteIndex = byteIndex * CHARS_PER_BYTE_ASCII;
		otherByteIndex = pastMidLine ? otherByteIndex + 1 : otherByteIndex;// >=

		return otherLineStart + otherByteIndex;
	}// getAsciiDot

	public static int getHexDot(int asciiDot) {
		int position = asciiDot % COLUMNS_PER_LINE_ASCII; // Calculate the column position
		boolean pastMidLine = position > MID_LINE_SPACE_ASCII;
		int lineNumber = asciiDot / COLUMNS_PER_LINE_ASCII;
		int rawByteIndex = pastMidLine? position - 1 : position; // >= 
		int byteIndex = rawByteIndex / CHARS_PER_BYTE_ASCII;

		int otherLineStart = lineNumber * COLUMNS_PER_LINE_HEX;
		int otherByteIndex = byteIndex * CHARS_PER_BYTE_HEX;
		otherByteIndex = pastMidLine ? otherByteIndex + 1 : otherByteIndex;// >=

		return otherLineStart + otherByteIndex;
	}// getHexDot
	
	public static int getAddressDot(int asciiDot) {
//		int position = asciiDot % COLUMNS_PER_LINE_ASCII; // Calculate the column position
		int lineNumber = asciiDot / COLUMNS_PER_LINE_ASCII;
		
		return  lineNumber * COLUMNS_PER_LINE_ADDR;	
	}//getAddressDot
	
	public static int getIndexDot(int asciiDot) {
		int position = asciiDot % COLUMNS_PER_LINE_ASCII; // Calculate the column position
		boolean pastMidLine = position > MID_LINE_SPACE_ASCII;
		int rawByteIndex = pastMidLine? position - 1 : position; // >= 
		int byteIndex =   rawByteIndex / CHARS_PER_BYTE_ASCII;	
		int otherByteIndex = byteIndex * CHARS_PER_BYTE_ADDR;
		otherByteIndex = pastMidLine ? otherByteIndex + 1 : otherByteIndex;// >=

		return  otherByteIndex;
	}//getIndexDot
	
	public static int getAsciiSourceIndex(int asciiDot) {
		int position = asciiDot % COLUMNS_PER_LINE_ASCII; // Calculate the column position
		boolean pastMidLine = position > MID_LINE_SPACE_ASCII;
		int lineNumber = asciiDot / COLUMNS_PER_LINE_ASCII;
		int rawByteIndex = pastMidLine? position - 1 : position; // >= 
		int byteIndex = rawByteIndex / CHARS_PER_BYTE_ASCII;
	
		return (lineNumber * BYTES_PER_LINE_ASCII) + byteIndex;
		
	}//getAsciiSourceIndex

	public static int getHexSourceIndex(int hexDot) {
		int position = hexDot % COLUMNS_PER_LINE_HEX; // Calculate the column position
		boolean pastMidLine = position > MID_LINE_SPACE_HEX;
		int lineNumber = hexDot / COLUMNS_PER_LINE_HEX;
		int rawByteIndex = pastMidLine? position - 1 : position; // >= 
		int byteIndex = rawByteIndex / CHARS_PER_BYTE_HEX;
	
		return (lineNumber * BYTES_PER_LINE_HEX) + byteIndex;
		
	}//getAsciiSourceIndex

	/* Constants */
	
	/* Hex Text Constants */
	public static final int BYTES_PER_LINE_HEX = 16; // Number of bytes from source file to display on each line
	public static final int CHARS_PER_BYTE_HEX = 3; // Number of chars used to display on each byte on a line
	public static final int CHARS_PER_LINE_HEX = (BYTES_PER_LINE_HEX * CHARS_PER_BYTE_HEX); // Number of chars displayed in the Hex text pane
	public static final int COLUMNS_PER_LINE_HEX = CHARS_PER_LINE_HEX + 2; // actual length of the line< includes /n/r
	public static final int MID_LINE_SPACE_HEX = (BYTES_PER_LINE_HEX / 2) * CHARS_PER_BYTE_HEX ; // place where extra space is placed on the line
//	public static final int MID_LINE_SPACE_HEX = (CHARS_PER_LINE_HEX / 2); // place where extra space is placed on the line

	/* Ascii Text Constants */
	public static final int BYTES_PER_LINE_ASCII = 16; // Number of bytes from source file to display on each line
	public static final int CHARS_PER_BYTE_ASCII = 1; // Number of chars used  to display on each byte on a line
	public static final int CHARS_PER_LINE_ASCII = BYTES_PER_LINE_ASCII + 1; // Number of chars displayed in the Ascii text pane
	public static final int COLUMNS_PER_LINE_ASCII = CHARS_PER_LINE_ASCII + 2; // actual length of the line< includes /n/r
	public static final int MID_LINE_SPACE_ASCII = (BYTES_PER_LINE_ASCII / 2) * CHARS_PER_BYTE_ASCII; // place where extra space is placed on the line
//	public static final int MID_LINE_SPACE_ASCII = BYTES_PER_LINE_ASCII / 2; // place where extra space is placed on the line
	
	/* Address Text Constants */
	public static final int BYTES_PER_LINE_ADDR = 8; // Number of bytes from source file to display on each line
	public static final int CHARS_PER_BYTE_ADDR = 3; // Number of chars used  to display on each byte on a line
	public static final int CHARS_PER_LINE_ADDR = (BYTES_PER_LINE_ADDR  ) + 1; // Number of chars displayed in the Address text pane
	public static final int COLUMNS_PER_LINE_ADDR = CHARS_PER_LINE_ADDR + 2; // actual length of the line< includes /n/r
//	public static final int MID_LINE_SPACE_ADDR = BYTES_PER_LINE_ADDR / 2; // place where extra space is placed on the line
}// Class TextCell
