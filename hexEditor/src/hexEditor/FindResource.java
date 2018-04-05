package hexEditor;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class FindResource {
	
	private static final String TARGET = "open.png";

	public static void main(String[] args) {
		Runnable runner = new Runnable() {
			public void run() {
				JFrame frame = new JFrame("FindResource ");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				URL url = getClass().getResource(TARGET);
				
				JLabel label = new JLabel(new ImageIcon(url));	
				frame.add(label,BorderLayout.NORTH);
				
				JLabel label1URL = new JLabel(String.format("url1 = %s%n",url));
				frame.add(label1URL,BorderLayout.SOUTH);

				frame.setSize(500, 100);
				frame.setLocation(1400, 400);
				frame.setVisible(true);
			}// run
		};// Runnable runner
		EventQueue.invokeLater(runner);

	}// main

}// class FindResource
