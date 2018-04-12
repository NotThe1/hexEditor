package hexEditor;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.NavigationFilter;
import javax.swing.text.Position;

public class AsciiNavigation extends NavigationFilter {

//	public AsciiNavigation(JTextComponent textComponent) {
//		this.highlighter = textComponent.getHighlighter();
//		this.highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
//	}// Constructor

	public void setDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias) {
		int position = dot % COLUMNS_PER_LINE; // Calculate the column position
//		System.out.println("[AsciiNavigation.setDot] Col position = " + position);
		if (position == CHARS_PER_LINE) {// the last display character in the line.
			dot += (COLUMNS_PER_LINE - position);
		} else if (position == MID_LINE_SPACE) {// at position
			dot += 1;
		} // if before/after mid line Break


		
		fb.setDot(dot, bias);
	}// setDot

	public void moveDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias) {
		System.out.printf("[AsciiNavigation.moveDot] dot = %d, bias = %s,", dot, bias);
		if ((dot % 3) == 0) {
			dot = dot + 1;
		}
		System.out.printf("  dot = %d%n", dot);

		fb.moveDot(dot, bias);
	}// moveDot

	public int getNextVisualPositionFrom(JTextComponent text, int pos, Position.Bias bias, int direction,
			Position.Bias[] biasRet) throws BadLocationException {

		return 0;
	}// getNextVisualPositionFrom

	public static final int BYTES_PER_LINE = 16; // Number of bytes from source file to display on each line
	public static final int CHARS_PER_BYTE = 1; // Number of chars used  to display on each byte on a line
	public static final int CHARS_PER_LINE = (BYTES_PER_LINE) + 1; // Number of chars displayed in the Hex text pane
	public static final int COLUMNS_PER_LINE = CHARS_PER_LINE + 2; // actual length of the line< includes /n/r
	public static final int MID_LINE_SPACE = BYTES_PER_LINE / 2; // place where extra space is placed on the line
}// AsciiDocumentNavigationFilter
