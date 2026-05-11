package main;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import frame.FrameBase;
import frame.FrameMain;
import frame.StartPage;

public class Main extends JPanel {

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				FrameBase.getInstance(new StartPage());

			}
		});

	}
}
