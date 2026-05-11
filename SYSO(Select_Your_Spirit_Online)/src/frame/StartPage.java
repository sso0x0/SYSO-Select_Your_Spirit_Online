package frame;

import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class StartPage extends JPanel {

	public StartPage() {
		setBackground(new Color(225, 215, 200));
		setLayout(null);
		setSize(600, 800);

		ImageIcon icon = new ImageIcon("ui_img/SYSOLogo.png");
		Image img = icon.getImage().getScaledInstance(320, 190, Image.SCALE_SMOOTH);

		JLabel titleLogo = new JLabel(new ImageIcon(img));
		titleLogo.setBounds((600 - 320) / 2, 80, 320, 190);
		add(titleLogo);

		// intro 화면 역할, 페이드 효과 후 자동 로그인 화면 전환
		Timer introDelay = new Timer(300, e -> FrameBase.getInstance(new LoginPage(), true));
		introDelay.setRepeats(false);
		introDelay.start();
	}
}
