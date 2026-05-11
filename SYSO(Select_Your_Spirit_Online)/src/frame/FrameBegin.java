package frame;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FrameBegin extends JPanel { // 확인

	public FrameBegin() {

		// Jpanel 구조
		setBackground(new Color(55, 248, 205));
		setLayout(null);
		setSize(600, 800);

		// 포스터 이미지
		ImageIcon icon = new ImageIcon("07.png");
		JLabel jla = new JLabel(icon);
		jla.setSize(200, 400);
		add(jla);

	}

}
