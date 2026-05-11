//package frame;
//
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.Image;
//import java.awt.RenderingHints;
//import java.awt.geom.Ellipse2D;
//
//import javax.swing.ImageIcon;
//import javax.swing.JLabel;
//
//// 이미지를 원형으로 잘라서 표시
//public class CircleLabel extends JLabel {
//
//	private Image image;
//
//	public CircleLabel(ImageIcon icon) {
//		this.image = icon.getImage();
//	}
//
//	protected void paintComponent(Graphics g) {
//		if (image != null) {
//			Graphics2D g2 = (Graphics2D) g.create();
//			// 테두리 부드럽게
//			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//			int diameter = Math.min(getWidth(), getHeight());
//
//			// 원형 클리핑 영역 설정
//			g2.setClip(new Ellipse2D.Float(0, 0, diameter, diameter));
//
//			// 이미지 그리기 (라벨 크기에 맞춰서)
//			g2.drawImage(image, 0, 0, diameter, diameter, null);
//
//			g2.dispose();
//		}
//	}
//
//}
